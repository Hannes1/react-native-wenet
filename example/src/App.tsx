import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { multiply, init, start } from 'react-native-wenet';
import STT from 'react-native-wenet';
import Wenet from './screens/wenet';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();

  React.useEffect(() => {
    multiply(3, 7).then(setResult);
  }, []);

  return (
    <View style={styles.container}>
      {/* <Wenet /> */}
      <Text onPress={() => init()}>Result: {result}</Text>
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
