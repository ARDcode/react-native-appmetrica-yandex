declare module 'react-native-appmetrica-yandex' {
    import React from 'react';

    type ActivationConfig = {
        apiKey: string,
        sessionTimeout?: number,
        firstActivationAsUpdate?: boolean,
    };

    type userProfileConfig = {
        name?: string,
        gender?: 'female' | 'male' | string | void,
        age?: number,
        birthDate?: Date | [number] | [number, number] | [number, number, number] | void,
        notificationsEnabled?: boolean,
        [key: string]: string | number | boolean,
    }

    export class YandexMetrica extends React.Component {
        public static activateWithApiKey(apiKey: string);

        public static activateWithConfig(params: ActivationConfig);

        public static reportEvent(message: string);

        public static reportEvent(message: string, params: Object);

        public static reportError(name: string, exception?: string | Object);

        public static setUserProfileAttributes(params: userProfileConfig);

        public static setUserProfileID(userProfileId: string);

        public static sendEventsBuffer();
    }
}
