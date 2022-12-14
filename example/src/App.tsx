import * as React from 'react';

import { StyleSheet, View, Text, Button } from 'react-native';
import Wenet, { Event } from 'react-native-wenet';
import { request, PERMISSIONS } from 'react-native-permissions';

export default function App() {
  const [result, setResult] = React.useState<string | null>('No Results Yet');

  React.useEffect(() => {
    Wenet.setupSTT();
    request(PERMISSIONS.ANDROID.RECORD_AUDIO).then((r) => {
      console.log('🚀 ~ file: App.tsx ~ line 17 ~ request ~ result', r);
      // …
    });
  }, []);

  const handleStart = async () => {
    //await Audio.requestPermissionsAsync();
    setResult('');
    Wenet.startSTT();
    Wenet.addEventListener(Event.Result, (data) => {
      setResult(data);
    });
  };

  const handleStop = async () => {
    Wenet.stopSTT();
  };

  return (
    <View style={styles.container}>
      {/* <Wenet /> */}
      <Text style={styles.text} onPress={() => handleStart()}>
        Result: {result}
      </Text>
      <View style={styles.buttonContainer}>
        <Button title="Stop" onPress={() => handleStop()} />
        <Button title="Start" onPress={() => handleStart()} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: 'white',
    justifyContent: 'center',
  },
  text: {
    fontSize: 20,
    flex: 0.7,
    color: '#000',
  },
  buttonContainer: {
    flexDirection: 'row',
    width: '100%',
    justifyContent: 'space-around',
    marginTop: 20,
    marginBottom: 20,
  },
  buttons: {
    width: 100,
    height: 100,
    borderRadius: 15,
  },
});
