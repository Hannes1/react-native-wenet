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

This is a react native module of the amazing [Wenet](https://github.com/wenet-e2e/wenet) framework. We at [Writtan](https://www.writtan.com) absolutely love the simplicity of wenet and plan on integrating it into our own app. We would appreciate any help with this module please see the todo at the bottom.

## Android

You are required to provide a wenet model to use this package. In your file tree it should be located in `android\app\src\main\assets`.

## IOS

No support for ios yet :(

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
- [ ] Reduce package size (maybe move to pytorch-android-light, see what c++ can be removed)
- [ ] Convert module to send data with JSI
- [ ] Add timestamps to the final results
- [ ] Add ability to download new models inside the app
- [ ] Create ios version (also not implemented in wenet yet)

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
