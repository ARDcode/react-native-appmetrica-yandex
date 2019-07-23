import { NativeModules, Platform  } from 'react-native';
const { YandexAppmetrica } = NativeModules;

export default YandexAppmetrica;

export class YandexMetrica {
    static activateWithApiKey(apiKey) {
        YandexAppmetrica.activateWithApiKey(apiKey)
    }

    /**
     * Sends a custom event message and additional parameters (optional).
     * @param {string} message
     * @param {object} [params=null]
     */
    static reportEvent(message: string, params: ?Object = null) {
        if (Platform.OS === 'ios') {
            YandexAppmetrica.reportEvent(message)
        } else {
            YandexAppmetrica.reportEvent(message, params)
        }
    }

    /**
     * Sends error with reason.
     * @param {string} error
     * @param {object} reason
     */
    static reportError(error: string, reason: Object) {
        YandexAppmetrica.reportError(error, reason)
    }

    static setDryRun(enabled) {
        YandexAppmetrica.setDryRun(enabled)
    }

    static isInitialized() {
        return YandexAppmetrica.isInitialized()
    }
}
