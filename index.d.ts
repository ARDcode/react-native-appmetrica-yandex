declare module 'react-native-appmetrica-yandex' {
    import React from 'react';

    type ActivationConfig = {
        apiKey: string,
        sessionTimeout?: number,
        firstActivationAsUpdate?: boolean,
    };

    type userProfileConfig = {
        userProfileId?: string,
        age?: number,
        name?: string,
        [key: string]: string | number | boolean
    }

    export class YandexMetrica extends React.Component {
        public static activateWithApiKey(apiKey: string);

        public static activateWithConfig(params: ActivationConfig);

        public static reportEvent(message: string);

        public static reportEvent(message: string, params: Object);

        public static reportError(name: string, exception?: string | Object);

        public static setUserProfileAttributes(params: userProfileConfig);
    }
}
