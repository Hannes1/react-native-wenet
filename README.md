# react-native-wenet

React Native Speech to Text using wenet

## Installation

```sh
#Not yet ready
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

## Disclaimer

This is a work in progress and the package might change drastically in the future!

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
- [ ] Convert module to send data with JSI
- [ ] Add timestamps to the final results
- [ ] Separate Final result from partial result (Not just 1 big final)
- [ ] Add ability to download new models inside the app
- [ ] Reduce package size (maybe move to pytorch-android-light, see what c++ can be removed)
- [ ] Create ios version (also not implemented in wenet yet)

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## Acknowledge

1. Most of the code is written by the good people at [Wenet](https://github.com/wenet-e2e/wenet)
2. I plan on using a lot of [Playtorch](https://github.com/facebookresearch/playtorch) code for writing jsi (linking c++ and react native)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
