package com.reactnativewenet.wenet;

//Media recording
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

//Offline Imports:
import android.content.Context;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//Converting byte to short
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioSoftwarePollerOffline {
    public static final String TAG = "AudioSoftwarePoller";
    public static final int SAMPLE_RATE = 16000;
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int FRAMES_PER_BUFFER = 24; // 1 sec @ 1024 samples/frame (aac)
    public static long US_PER_FRAME = 0;
    public static boolean is_recording = false;
    public static boolean is_paused = true;
    final boolean VERBOSE = false;
    public RecorderTask recorderTask = new RecorderTask();

    // Offline variables
    Context c;
    DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;

    AudioEncoderOffline audioEncoder;

    public AudioSoftwarePollerOffline(Context c, DeviceEventManagerModule.RCTDeviceEventEmitter emit) {
        // Todo
        eventEmitter = emit;
        this.c = c; // Get context from react
    }

    public void setAudioEncoder(AudioEncoderOffline avcEncoder) {
        this.audioEncoder = avcEncoder;
    }

    /**
     * Set the number of samples per frame (Default is 1024). Call this before
     * startPolling().
     * The output of emptyBuffer() will be equal to, or a multiple of, this value.
     *
     * @param samples_per_frame The desired audio frame size in samples.
     */
    public void setSamplesPerFrame(int samples_per_frame) {
        if (!is_recording)
            recorderTask.samples_per_frame = samples_per_frame;
    }

    /**
     * Return the number of microseconds represented by each audio frame
     * calculated with the sampling rate and samples per frame
     * 
     * @return
     */
    public long getMicroSecondsPerFrame() {
        if (US_PER_FRAME == 0) {
            US_PER_FRAME = (SAMPLE_RATE / recorderTask.samples_per_frame) * 1000000;
        }
        return US_PER_FRAME;
    }

    public void recycleInputBuffer(byte[] buffer) {
        recorderTask.data_buffer.offer(buffer);
    }

    /**
     * Begin polling audio and transferring it to the buffer. Call this before
     * emptyBuffer().
     */
    public void startPolling() {
        new Thread(recorderTask).start();
    }

    /**
     * User pause polling audio.
     */
    public void togglePause() {
        is_paused = !is_paused; // will stop recording after next sample received
    }

    /**
     * Use offline decoding instead of server.
     */
    public void initOfflineDecoding() {
        initOfflineModule();
        Log.d(TAG, "Switching to offline decoding");
    }

    /**
     * Initialize and load the offline model.
     */
    private void initOfflineModule() {
        final String modelPath = new File(assetFilePath(c, "final.zip")).getAbsolutePath();
        final String dictPath = new File(assetFilePath(c, "words.txt")).getAbsolutePath();
        Recognize.init(c.getFilesDir().getPath());
        Recognize.reset();
    }

    /**
     * Unpacks the wenet model.
     */
    public static String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error process asset " + assetName + " to file path");
        }
        return null;
    }

    /**
     * Stop polling audio.
     */
    public void stopPolling() {
        is_paused = false;
        is_recording = false;
    }

    public class RecorderTask implements Runnable {
        public int buffer_size;
        public int samples_per_frame = 2048; // codec-specific
        public int buffer_write_index = 0; // last buffer index written to
        public int total_frames_written = 0;

        ArrayBlockingQueue<byte[]> data_buffer = new ArrayBlockingQueue<byte[]>(50);

        int read_result = 0;

        public void run() {
            int min_buffer_size = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

            buffer_size = samples_per_frame * FRAMES_PER_BUFFER;

            // Ensure buffer is adequately sized for the AudioRecord
            if (buffer_size < min_buffer_size)
                buffer_size = ((min_buffer_size / samples_per_frame) + 1) * samples_per_frame * 2;

            // data_buffer = new byte[samples_per_frame]; // filled directly by hardware
            for (int x = 0; x < 25; x++)
                data_buffer.add(new byte[samples_per_frame]);

            AudioRecord audio_recorder;
            audio_recorder = new AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION, // source
                    SAMPLE_RATE, // sample rate, hz
                    CHANNEL_CONFIG, // channels
                    AUDIO_FORMAT, // audio format
                    buffer_size); // buffer size (bytes)

            audio_recorder.startRecording();
            Recognize.startDecode();
            is_recording = true;
            // is_paused = true;
            Log.i("AudioSoftwarePoller", "SW recording begin");
            long audioPresentationTimeNs;
            while (is_recording) {
                audioPresentationTimeNs = System.nanoTime();
                byte[] this_buffer;
                short[] buffer = new short[samples_per_frame]; // try for offline
                if (data_buffer.isEmpty()) {
                    this_buffer = new byte[samples_per_frame];
                } else {
                    this_buffer = data_buffer.poll();
                }
                read_result = audio_recorder.read(this_buffer, 0, samples_per_frame);

                // Create short fom byteArray
                // TODO: Figure out how to use bytes instead of shorts for decoding
                short[] shorts = new short[this_buffer.length / 2];
                ByteBuffer.wrap(this_buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

                if (read_result > 0) {
                    // 1. add data to C++ interface
                    Recognize.acceptWaveform(shorts); // Figure out how to get required buffer
                    // 2. get partial result
                    // TODO: Make it Final Result and Partial Result not partials the whole time
                    eventEmitter.emit("onResponse", Recognize.getResult());
                }

                if (VERBOSE)
                    Log.i("AudioSoftwarePoller-FillBuffer", String.valueOf(buffer_write_index) + " - "
                            + String.valueOf(buffer_write_index + samples_per_frame - 1));
                if (read_result == AudioRecord.ERROR_BAD_VALUE || read_result == AudioRecord.ERROR_INVALID_OPERATION)
                    Log.e("AudioSoftwarePoller", "Read error");
                total_frames_written++;
                if (audioEncoder != null) {
                    audioEncoder.offerAudioEncoder(this_buffer, audioPresentationTimeNs);
                }
            }
            if (audio_recorder != null) {
                audio_recorder.setRecordPositionUpdateListener(null);
                audio_recorder.release();
                audio_recorder = null;
                Log.i("AudioSoftwarePoller", "stopped");
            }
        }
    }

}