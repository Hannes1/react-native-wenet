import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

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

type STTMODULE = {
  start: () => void;
  stop: () => Promise<string>;
  init: () => void;
  deleteAudio: (filePath: string) => Promise<string>;
  getAudioDuration: (filePath: string) => Promise<number>;
  on: (event: string, callback: (...args: string[]) => void) => void;
};

// interface Response {
//   data: string;
//   onConnectionStateChange: string;
// }

// const eventsMap: Response = {
//   data: 'onResponse',
//   onConnectionStateChange: 'onConnectionStateChange',
// };

export const STT: STTMODULE = {
  start: () => Wenet.start(),
  stop: () => Wenet.stop(),
  init: () => Wenet.init(),
  deleteAudio: (filePath) => Wenet.deleteAudio(filePath),
  getAudioDuration: (filePath) => Wenet.getAudioDuration(filePath),
  on: (event, callback) => {
    const nativeEvent = event;
    if (!nativeEvent) {
      throw new Error('Invalid event');
    }
    EventEmitter.removeAllListeners(nativeEvent);
    return EventEmitter.addListener(nativeEvent, callback);
  },
};

// STT.startOffline = (options) => Wenet.startOffline(options);
// STT.stop = () => Wenet.stop();
// STT.init = () => Wenet.init();
// STT.start = () => Wenet.start();
// STT.deleteAudio = (filePath) => Wenet.deleteAudio(filePath);
// STT.pause = () => Wenet.pause();
// STT.testOffline = () => Wenet.testOffline();

//export { STT };

// import { NativeModules, Platform, NativeEventEmitter } from 'react-native';

// const LINKING_ERROR =
//   `The package 'react-native-wenet' doesn't seem to be linked. Make sure: \n\n` +
//   Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
//   '- You rebuilt the app after installing the package\n' +
//   '- You are not using Expo managed workflow\n';

// const Wenet = NativeModules.Wenet
//   ? NativeModules.Wenet
//   : new Proxy(
//       {},
//       {
//         get() {
//           throw new Error(LINKING_ERROR);
//         },
//       }
//     );

// const EventEmitter = new NativeEventEmitter(Wenet);

// export function init() {
//   return Wenet.init();
// }
// export function start() {
//   return Wenet.start();
// }

// //Maybe need to change this to object and all it's contents
// const eventsMap = {
//   data: 'onResponse',
// };

// const STT = {};
// STT.on = (event, callback) => {
//   const nativeEvent = eventsMap[event];
//   if (!nativeEvent) {
//     throw new Error('Invalid event');
//   }
//   EventEmitter.removeAllListeners(nativeEvent);
//   return EventEmitter.addListener(nativeEvent, callback);
// };

// export default STT;

// export function addEventListener<T extends Event>(
//   event: T,
//   listener: EventPayloadByEvent[T] extends never
//     ? () => void
//     : (event: EventPayloadByEvent[T]) => void
// ) {
//   return EventEmitter.addListener(event, listener);
// }
