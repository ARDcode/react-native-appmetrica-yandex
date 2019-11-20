import {NativeModules, Platform} from 'react-native';

const {YandexAppmetrica} = NativeModules;

export default YandexAppmetrica;

export class YandexMetrica {
    static activateWithApiKey(apiKey) {
        YandexAppmetrica.activateWithApiKey(apiKey);
    }

    /**
     * Starts the statistics collection process using config.
     * @param {object} params
     */
    static activateWithConfig(params: Object) {
        YandexAppmetrica.activateWithConfig(params);
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
     * @param {object || string} exception
     */
    static reportError(name: string, exception: string | Object) {
        YandexAppmetrica.reportError(name, exception);
    }

    /**
     * Sets the ID of the user profile.
     * @param {string} userProfileId
     */
    setUserProfileID(userProfileId: string) {
        YandexAppmetrica.setUserProfileID(userProfileId);
    }

    /**
     * Sets attributes of the user profile.
     * @param {object} attributes
     */
    static setUserProfileAttributes(attributes: Object) {
        const readyAttributes = {};
        Object.keys(attributes).forEach(key => {
            if (
              key === 'birthDate' &&
              typeof attributes.birthDate === 'object' &&
              typeof attributes.birthDate.getTime === 'function'
            ) {
                readyAttributes.birthDate = attributes.birthDate.getTime();
            } else {
                readyAttributes[key] = attributes[key];
            }
        });
        YandexAppmetrica.setUserProfileAttributes(readyAttributes);
    }
}
