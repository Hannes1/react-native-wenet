import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { init, start } from 'react-native-wenet';
import STT from 'react-native-wenet';
import Wenet from './screens/wenet';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();

  React.useEffect(() => {
    init();
  }, []);

  const handleStart = async () => {
    //await Audio.requestPermissionsAsync();
    start();
    STT.on('data', (data) => {
      //console.log(data)
      setResult(data);
    });
  };

  return (
    <View style={styles.container}>
      {/* <Wenet /> */}
      <Text onPress={() => start()}>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
