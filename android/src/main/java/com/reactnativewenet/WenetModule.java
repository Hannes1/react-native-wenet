package com.reactnativewenet;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;

//Utils
import android.util.Log;
//Exceptions
import java.io.IOException;
import java.io.File;

//Wenet Utils:
import com.reactnativewenet.wenet.*;

@ReactModule(name = WenetModule.NAME)
public class WenetModule extends ReactContextBaseJavaModule {
    public static final String NAME = "Wenet";
    private final String TAG = "Wenet";
    private final ReactApplicationContext reactContext;
    private DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter;
    private Throwable failed;

    AudioEncoderOffline mEncoderOffline;
    AudioSoftwarePollerOffline audioPollerOffline;
    OfflineRecognition offlineSTT;

    public WenetModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    /**
     * Start The event emitter
     * See https://reactnative.dev/docs/native-modules-android
     */
    @ReactMethod
    public void setupSTT() {
        eventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    }

    /**
     * Easy mode for wenet-stt without any of the Audio Compression using media
     * recorder
     */
    @ReactMethod
    public void start() {
        offlineSTT = new OfflineRecognition(getReactApplicationContext(), eventEmitter);
        offlineSTT.toggleRecording(true);
    }

    @ReactMethod
    public void stop(Promise promise) {
        eventEmitter.emit("onRecordingStateChange", false);
        // Stops Encoder
        offlineSTT.toggleRecording(false);
        promise.resolve("TodoSendBackFilePath");
    }

    // @ReactMethod
    // public void stop(Promise promise) {
    // eventEmitter.emit("onRecordingStateChange", false);
    // // Stops Encoder
    // if (mEncoderOffline != null) {
    // audioPollerOffline.stopPolling();
    // String FilePath = mEncoderOffline.stop();
    // promise.resolve(FilePath);
    // }
    // }

    @ReactMethod
    public void pause() {
        audioPollerOffline.togglePause();
    }

    @ReactMethod
    public void deleteAudio(String FilePath, Promise promise) {
        FilePath = FilePath.replace("file://", ""); // Use getAbsolute url?
        File file = new File(FilePath);
        try {
            file.delete();
            promise.resolve("File was deleted successfully");
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
            promise.reject("Delete error:", e.getMessage());
        }
    }

    /**
     * Offline stt-wenet implementation for android
     *
     * @param Filename
     */
    @ReactMethod
    public void startOffline(String FileName) {
        eventEmitter.emit("onRecordingStateChange", true);
        // Start the recording
        mEncoderOffline = new AudioEncoderOffline(getReactApplicationContext(), FileName);
        audioPollerOffline = new AudioSoftwarePollerOffline(getReactApplicationContext(), eventEmitter);
        audioPollerOffline.setAudioEncoder(mEncoderOffline);
        audioPollerOffline.initOfflineDecoding(); // Can probably remove
        mEncoderOffline.setAudioSoftwarePoller(audioPollerOffline);
        audioPollerOffline.startPolling();
        eventEmitter.emit("onConnectionStateChange", "ready"); // Move to encoder maybe
    }

}
