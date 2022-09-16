package com.reactnativewenet.wenet;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class OfflineRecognition {

  Context context;
  DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;
  private static final String LOG_TAG = "WENET";
  private static final int SAMPLE_RATE = 16000; // The sampling rate
  private static final int MAX_QUEUE_SIZE = 2500; // 100 seconds audio, 1 / 0.04 * 100
  private static final List<String> resource = Arrays.asList(
      "final.zip", "units.txt", "ctc.ort", "decoder.ort", "encoder.ort");

  private boolean startRecord = false;
  private AudioRecord record = null;
  private int miniBufferSize = 0; // 1280 bytes 648 byte 40ms, 0.04s
  private final BlockingQueue<short[]> bufferQueue = new ArrayBlockingQueue<>(MAX_QUEUE_SIZE);

  public static void assetsInit(Context context) throws IOException {
    AssetManager assetMgr = context.getAssets();
    // Unzip all files in resource from assets to context.
    // Note: Uninstall the APP will remove the resource files in the context.
    for (String file : assetMgr.list("")) {
      if (resource.contains(file)) {
        File dst = new File(context.getFilesDir(), file);
        if (!dst.exists() || dst.length() == 0) {
          Log.i(LOG_TAG, "Unzipping " + file + " to " + dst.getAbsolutePath());
          InputStream is = assetMgr.open(file);
          OutputStream os = new FileOutputStream(dst);
          byte[] buffer = new byte[4 * 1024];
          int read;
          while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
          }
          os.flush();
        }
      }
    }
  }

  public OfflineRecognition(Context c, DeviceEventManagerModule.RCTDeviceEventEmitter emit) {
    eventEmitter = emit;
    this.context = c; // Get context from react
    initModule(c);
  }

  private void initModule(Context context) {
    initRecoder();
    try {
      assetsInit(context);
    } catch (IOException e) {
      Log.e(LOG_TAG, "Error process asset files to file path");
    }
    Recognize.init(context.getFilesDir().getPath());
  }

  public void toggleRecording(boolean shouldRecord) {
    startRecord = shouldRecord;
    if (startRecord) {
      startRecord = true;
      Recognize.reset();
      startRecordThread();
      startAsrThread();
      Recognize.startDecode();
    } else {
      startRecord = false;
      Recognize.setInputFinished();
    }
  }

  private void initRecoder() {
    // buffer size in bytes 1280
    miniBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT);
    if (miniBufferSize == AudioRecord.ERROR || miniBufferSize == AudioRecord.ERROR_BAD_VALUE) {
      Log.e(LOG_TAG, "Audio buffer can't initialize!");
      return;
    }
    record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        miniBufferSize);
    if (record.getState() != AudioRecord.STATE_INITIALIZED) {
      Log.e(LOG_TAG, "Audio Record can't initialize!");
      return;
    }
    Log.i(LOG_TAG, "Record init okay");
  }

  private void startRecordThread() {
    new Thread(() -> {
      record.startRecording();
      Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
      while (startRecord) {
        short[] buffer = new short[miniBufferSize];
        int read = record.read(buffer, 0, buffer.length);
        try {
          if (AudioRecord.ERROR_INVALID_OPERATION != read) {
            bufferQueue.put(buffer);
          }
        } catch (InterruptedException e) {
          Log.e(LOG_TAG, e.getMessage());
        }
      }
      record.stop();
    }).start();
  }

  private void startAsrThread() {
    new Thread(() -> {
      // Send all data
      while (startRecord || bufferQueue.size() > 0) {
        try {
          short[] data = bufferQueue.take();
          // 1. add data to C++ interface
          Recognize.acceptWaveform(data);
          // 2. get partial result

          // TODO: Make it Final Result and Partial Result not partials the whole time
          eventEmitter.emit("onResponse", Recognize.getResult());
        } catch (InterruptedException e) {
          Log.e(LOG_TAG, e.getMessage());
        }
      }

      // Wait for final result
      while (true) {
        // get result
        if (!Recognize.getFinished()) {
          Recognize.getResult();
          eventEmitter.emit("onResponse", Recognize.getResult());
          // Reset here? not sure how to distinguish from final and partial
          Log.i(LOG_TAG, Recognize.getResult());
        } else {
          // button.setEnabled(true);
          break;
        }
      }
    }).start();
  }
}