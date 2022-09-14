import React, { useRef, useState } from 'react';
import {
  Button,
  KeyboardAvoidingView,
  Platform,
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  Dimensions,
  TouchableOpacity,
} from 'react-native';
import RecordButton from '../components/Buttons/RecordButton';

export default function Wenet() {
  const sound = useRef(null);
  const [audioFile, setAudioFile] = useState('');
  const [loaded, setLoaded] = useState(false);
  const [paused, setPaused] = useState(true);
  const [result, setResult] = useState('Chatable Offline STT');
  const [isRecording, setIsRecording] = useState(false);

  const getAudioPermission = async () => {
    //return await Audio.requestPermissionsAsync();
  };

  const handleDelete = async () => {
    // let result = await STT.deleteAudio(
    //   'file:///data/user/0/com.grpctest/files/hello.m4a'
    // );
    console.log(result);
  };

  const handlePause = () => {
    //STT.pause();
  };

  const handleRecording = async () => {
    //const { status } = await getAudioPermission();
    //if (status === 'granted') {
    //   STT.init('5545'); //Probable have to change offline init
    //   STT.testOffline();
    //   //STT.startOffline("hello.m4a");
    //   STT.on('data', (data) => {
    //     //console.log(data)
    //     setResult(data);
    //   });
    //}
  };

  const handleStop = async () => {
    //let filePath = await STT.stop();
    //console.log('audioFile', filePath);
    //setAudioFile(filePath);
  };

  const handleAudioLoad = () => {
    return new Promise((resolve, reject) => {
      if (!audioFile) {
        return reject('file path is empty');
      }

      //   sound.current = new Sound(audioFile, '', (error) => {
      //     if (error) {
      //       console.log('failed to load the file', error);
      //       return reject(error);
      //     }
      //     console.log('Loaded');
      //     setLoaded(true);
      //     return resolve();
      //   });
    });
  };

  const playAudio = async () => {
    if (!loaded) {
      try {
        await handleAudioLoad();
      } catch (error) {
        console.log(error);
      }
    }

    setPaused(false);
    // Sound.setCategory('Playback');
    // console.log(sound.current);
    // //sound.current.setSpeed(1.5);
    // sound.current.play((success) => {
    //   if (success) {
    //     console.log('successfully finished playing');
    //     setLoaded(false);
    //   } else {
    //     console.log('playback failed due to audio decoding errors');
    //   }
    //   setPaused(true);

    //   //sound.release();
    // });
  };

  const pauseAudio = () => {
    //sound.current.pause();
    setPaused(true);
  };

  const toggleRecording = () => {
    if (isRecording) {
      //handleStop();
      setIsRecording(false);
    } else {
      handleRecording();
      setIsRecording(true);
    }
  };

  return (
    <SafeAreaView style={[styles.container]}>
      <ScrollView keyboardDismissMode={'none'} style={[styles.textEditor]}>
        <Text style={[styles.text]}>{result}</Text>
      </ScrollView>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={[styles.buttonContainer]}
      >
        {/* <RecordButton
          isRecording={isRecording}
          toggleRecording={toggleRecording}
        /> */}
        {audioFile !== '' ? (
          <>
            <Button title="Pause Audio" color="#841584" onPress={handlePause} />
            <Button title="Play Audio" color="#841584" onPress={playAudio} />
          </>
        ) : null}
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#efefef',
  },
  textEditor: {
    flex: 0.6,
    padding: 50,
  },
  text: {
    fontSize: 16,
  },
  buttonContainer: {
    flex: 0.4,
    zIndex: 500,
    width: '100vw',
  },
  recordButton: {},
});
