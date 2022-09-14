import { NativeModules, Platform, NativeEventEmitter } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-wenet' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const Wenet = NativeModules.Wenet
  ? NativeModules.Wenet
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const EventEmitter = new NativeEventEmitter(Wenet);

export function multiply(a: number, b: number): Promise<number> {
  return Wenet.multiply(a, b);
}

export function init() {
  return Wenet.init();
}
export function start() {
  return Wenet.start();
}

//Maybe need to change this to object and all it's contents
const eventsMap = {
  data: 'onResponse',
};

const STT = {};
STT.on = (event, callback) => {
  const nativeEvent = eventsMap[event];
  if (!nativeEvent) {
    throw new Error('Invalid event');
  }
  EventEmitter.removeAllListeners(nativeEvent);
  return EventEmitter.addListener(nativeEvent, callback);
};

export default STT;

// export function addEventListener<T extends Event>(
//   event: T,
//   listener: EventPayloadByEvent[T] extends never
//     ? () => void
//     : (event: EventPayloadByEvent[T]) => void
// ) {
//   return EventEmitter.addListener(event, listener);
// }
