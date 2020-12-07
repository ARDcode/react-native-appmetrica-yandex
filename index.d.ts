declare module 'react-native-appmetrica-yandex' {
    import React from 'react';

    type ActivationConfig = {
        apiKey: string,
        sessionTimeout?: number,
        firstActivationAsUpdate?: boolean,
    };

    type UserProfileConfig = {
        name?: string,
        gender?: 'female' | 'male' | string | void,
        age?: number,
        birthDate?: Date | [number] | [number, number] | [number, number, number] | void,
        notificationsEnabled?: boolean,
    } & {
        [key: string]: string | number | boolean,
    }

    export class YandexMetrica extends React.Component {
        public static activateWithApiKey(apiKey: string): void;

        public static activateWithConfig(params: ActivationConfig): void;

        public static reportEvent(message: string): void;

        public static reportEvent(message: string, params: object): void;

        public static reportError(name: string, exception?: string | object): void;

        public static setUserProfileAttributes(params: UserProfileConfig): void;

        public static setUserProfileID(userProfileId: string): void;

        public static sendEventsBuffer(): void;
    }
}
