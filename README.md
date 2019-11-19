# react-native-appmetrica-yandex
##### Install
```sh 
yarn add react-native-appmetrica-yandex
```
1. **IOS**

    **RN<0.60**:

    ```sh
    react-native link react-native-appmetrica-yandex
    ```

    With [CocoaPods](https://guides.cocoapods.org/using/getting-started.html), add the following line to
    your `Podfile`:

    ```sh
    pod 'react-native-appmetrica-yandex', :path => '../node_modules/react-native-appmetrica-yandex'
    pod 'YandexMobileMetrica', '3.8.2'
    ```

    Then run `pod install`.

    **RN>=0.60**:
    With React Native 0.60 and later, linking of pods is done automatically

    ```sh
    cd ios
    pod install
    ```
2. **Android**

    **RN<0.60**:
    
    2.1. Open up `android/app/src/main/java/[...]/MainApplication.java`
      - Add `import com.codeard.yandexmetrica.YandexAppmetricaPackage;` to the imports at the top of the file
      - Add `new YandexAppmetricaPackage()` to the list returned by the `getPackages()` method
      
    2.2. Append the following lines to `android/settings.gradle`:
      	```
      	include ':react-native-appmetrica-yandex'
      	project(':react-native-appmetrica-yandex').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-appmetrica-yandex/android')
      	```
    
    2.3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
      	```
        implementation project(':react-native-appmetrica-yandex')
      	```
       
    **RN>=0.60**:
    With React Native 0.60 and later, linking is done automatically
## Usage

```js
import { YandexMetrica } from 'react-native-appmetrica-yandex';

// Initialize
YandexMetrica.activateWithApiKey('KEY');

// Sends a custom event message and additional parameters (optional).
YandexMetrica.reportEvent('My event');
YandexMetrica.reportEvent('My event', 'Test');
YandexMetrica.reportEvent('My event', { foo: 'bar' });

// Send a custom error event and additional parameters (optional).
YandexMetrica.reportError('My error');
YandexMetrica.reportError('My error', 'Test');
YandexMetrica.reportError('My error', { foo: 'bar' });
YandexMetrica.reportError('My error', new Error('test'));
```
