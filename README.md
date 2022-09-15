# react-native-wenet

React Native Speech to Text using wenet

## Installation

```sh
npm install react-native-wenet
```

### Example App

To run the example app just fork the repository and run the following

```sh
yarn
yarn example android
```

## About

This is a react native module of the amazing [Wenet](https://github.com/wenet-e2e/wenet) framework. Currently wenet only supports android, this this package will only work on android devices at this moment

## Usage

```js
import Wenet, { Event } from 'react-native-wenet';

React.useEffect(() => {
  Wenet.setupSTT();
  //Need to request audio permission here
}, []);

const handleStart = async () => {
  Wenet.startSTT(); //Start the service
  Wenet.addEventListener(Event.Result, (data) => {
    setResult(data); //Returns the results
  });
};

const handleStop = async () => {
  Wenet.stopSTT();
};
```

## Todo

- [ ] Create documentation
- [ ] Reduce package size
- [ ] Convert module to use JSI
- [ ] Create ios version (also not implemented in wenet yet)

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
