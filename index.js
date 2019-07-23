import {NativeModules, Platform} from 'react-native';

const {YandexAppmetrica} = NativeModules;

export default YandexAppmetrica;

export class YandexMetrica {
    static activateWithApiKey(apiKey) {
        YandexAppmetrica.activateWithApiKey(apiKey);
    }

    /**
     * Sends a custom event message and additional parameters (optional).
     * @param {string} message
     */
    static reportEvent(message: string) {
        YandexAppmetrica.reportEvent(message);
    }

    /**
     * Sends a custom event message and additional parameters (optional).
     * @param {string} message
     * @param {object} [params=null]
     */
    static reportEvent(message: string, params: Object) {
        YandexAppmetrica.reportEvent(message, params);
    }

    /**
     * Sends error with reason.
     * @param {string} name
     * @param {object} exception
     */
    static reportError(name: string, exception: string) {
        YandexAppmetrica.reportError(name, exception);
    }
}
