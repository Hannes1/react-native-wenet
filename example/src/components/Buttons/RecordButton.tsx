import React from 'react';
import { TouchableOpacity, StyleSheet, Dimensions } from 'react-native';

import { Feather } from 'react-native-vector-icons';
const width = Dimensions.get('screen').width;
const height = Dimensions.get('screen').height;

const RecordButton = ({ isRecording, toggleRecording }) => {
  const handlePress = () => {
    toggleRecording();
  };

  return (
    <TouchableOpacity
      style={[styles.recordButton, { backgroundColor: '#000' }]}
      onPress={() => handlePress()}
      disabled={false}
    >
      {isRecording ? (
        <Feather name={'mic-off'} size={width * 0.075} color={'#fff'} />
      ) : (
        <Feather name={'mic'} size={width * 0.075} color={'#fff'} />
      )}
    </TouchableOpacity>
  );
};

export default RecordButton;

const styles = StyleSheet.create({
  recordButton: {
    width: width * 0.2,
    height: width * 0.2,
    zIndex: 999,
    position: 'absolute',
    bottom: height * 0.075,
    right: width * 0.1,
    borderRadius: width * 0.1,
    borderWidth: 0,
    alignItems: 'center',
    justifyContent: 'center',
  },
  recordButtonOutside: {
    width: width * 0.21,
    height: width * 0.21,
    borderRadius: width * 0.105,
    alignItems: 'center',
    justifyContent: 'center',
  },
  pulse: {
    position: 'absolute',
    bottom: height * 0.025,
    left: width * 0.6,
  },
});
