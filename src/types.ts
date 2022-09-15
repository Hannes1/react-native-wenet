export enum Event {
  /** Fired when a result is received. */
  Result = 'onResponse',
}

export interface ResultResponse {
  /** Currently only a json string containing the result is sent back. */
  result: string | null;
}

export interface EventPayloadByEvent {
  [Event.Result]: ResultResponse;
}

export interface SpeechOptions {
  /**
   * Language of the speaker.
   * @default 'en-US'
   */
  language?: string;

  /**
   * Enable timestamps in the result.
   * @default false
   */
  timestamps?: boolean;

  /**
   * Return interim results.
   * @default true
   */
  interimResults?: boolean;
}
