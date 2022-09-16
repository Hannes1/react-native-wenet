import { NativeModules, NativeEventEmitter, Platform } from 'react-native';
import type { Event, EventPayloadByEvent } from './types';

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

/**
 * Initializes the player with the specified options.
 */
// async function setupSTT(options: SpeechOptions = {}): Promise<void> {
//   return Wenet.setupSTT(options || {});
// }
async function setupSTT(): Promise<void> {
  return Wenet.setupSTT();
}

/**
 * Starts the speech recognition.
 */
function startSTT(): void {
  Wenet.start();
}

/**
 * Stops the speech recognition.
 * @returns the path to the audio file containing the speech.
 */
async function stopSTT(): Promise<string> {
  return Wenet.stop();
}

/**
 * Deletes the audio file at the specified path.
 * @param filePath the path to the audio file to delete.
 */
async function deleteAudio(filePath: string): Promise<void> {
  return Wenet.deleteAudio(filePath);
}

function addEventListener<T extends Event>(
  event: T,
  listener: EventPayloadByEvent[T] extends never
    ? () => void
    : (event: string) => void
) {
  return EventEmitter.addListener(event, listener);
}

export default {
  setupSTT,
  startSTT,
  stopSTT,
  deleteAudio,
  addEventListener,
};
