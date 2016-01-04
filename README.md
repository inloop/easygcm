# easygcm

Android Library for easy GCM integration. Including push notifications sender task for Gradle.

This library is based on sample client code provided by Android documentation at http://developer.android.com/google/gcm/client.html and https://code.google.com/p/gcm/

It includes all the common code from the sample including AndroidManifest.xml that is automatically merged into your application. Also content of sample's DemoActivity is included in this library extracted in GcmHelper class. This library further improves original sample client in several ways, see [changelog](https://github.com/inloop/easygcm/blob/master/CHANGELOG.md).

### How to use:

* The project uses Google Play Services, so make sure you have:
 * `classpath 'com.google.gms:google-services:1.5.0` in your project root gradle file
 * `apply plugin: 'com.google.gms.google-services'` in your module gradle file
 * `compile 'com.google.android.gms:play-services-gcm:8.3.0'` in your module gradle file dependencies

* Add this to your build.gradle: `compile 'eu.inloop:easygcm:1.6.1@aar'`
* In your `Application` implement [GcmListener](https://github.com/inloop/easygcm/blob/master/easygcm-lib/src/main/java/eu/inloop/easygcm/GcmListener.java) interface with two methods:
  * `onMessage()`
  * `sendRegistrationIdToBackend()`

> Note: Both methods are executed asynchronously in background thread and the wake lock is held until the method is executed. Don't start other asynchronous tasks here unless needed - the wake lock would be released and the tasks would not be guaranteed to run. Put the blocking code right into these methods.

* In your `MainActivity` `onCreate()`, call `EasyGcm.init(this);`
* Define GCM Sender ID adding `google-service.json` configuration and setting up Google Services Gradle plugin as described in [Implementing GCM Client on Android ](https://developers.google.com/cloud-messaging/android/client)

* Alternatively, instead of being implemented by the `Application` class, you can have your own instance of [GcmListener](https://github.com/inloop/easygcm/blob/master/easygcm-lib/src/main/java/eu/inloop/easygcm/GcmListener.java) and set it in the application with `EasyGcm.setGcmListener(GcmListener listener)`

If you need ```GET_ACCOUNTS``` permission, add this to you application's manifest:

    <uses-permission
        android:name="android.permission.GET_ACCOUNTS"
        tools:remove="android:maxSdkVersion"/>

That's it. You might also want to use Gradle task for sending push notifications to your device:

* add Gradle dependency to buildscript section: `classpath 'eu.inloop:easygcm-tasks:1.3.0'`
* add following code to your build.gradle and edit push notification data, apiKey and registration ID
```groovy
task push(type: eu.inloop.easygcm.PushTask) {
    data = '{"myMessage":"Hello World"}'
    apiKey = 'AIzaSyDt0uzm74Un5qsoEyQcvE-5lhDB3n3m9XX' // Server API Key
    registrationIds = [
            'mt4gNCjtlRs:APA91bHiu_7X_CzA_IbSk9FiR_wk9b6sIvAuR6t-zoE5ujSx0D5Obql5dZiQ9DrEK8YsJm4lBr2nNLKWGRKSr2GZJSgdm_jxOWO4nBCE2QYNzJTS2bCTPRgrIH7cra0t1BEU7g2dwnYY'
    ]
}
```
* call `./gradlew push`

### How to build locally:

So far, it is not possible to use project dependencies in Gradle's buildscript section (needed for our plugin). That means you have to build the library first (and install in local Maven repo) and just then build the sample application.

    ./gradlew clean uploadArchives
    ./gradlew -p easygcm-sample clean assembleDebug
