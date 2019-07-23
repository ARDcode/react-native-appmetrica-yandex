declare module 'react-native-appmetrica-yandex' {
    import React from 'react';

    export class YandexMetrica extends React.Component {
        public static activateWithApiKey(apiKey: string);

        public static reportEvent(message: string);

        public static reportEvent(message: string, params: Object);

        public static reportError(name: string, exception?: string | Object);

    }
}
