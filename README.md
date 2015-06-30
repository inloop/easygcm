# easygcm

Android Library for easy GCM integration. Including push notifications sender task for Gradle.

This library is based on sample client code provided by Android documentation at http://developer.android.com/google/gcm/client.html and https://code.google.com/p/gcm/

It includes all the common code from the sample including AndroidManifest.xml that is automatically merged into your application. Also content of sample's DemoActivity is included in this library extracted in GcmHelper class. This library further improves original sample client in several ways, see [changelog](https://github.com/inloop/easygcm/blob/master/CHANGELOG.md).

### How to use:

1. Add this to your build.gradle: `compile 'eu.inloop:easygcm:1.4.0@aar'`
2. In your `Application` implement [GcmListener](http://github.com/inloop/easygcm/blob/master/easygcm/src/main/java/eu/inloop/easygcm/GcmListener.java) interface with two methods:
  - `onMessage()`
  - `sendRegistrationIdToBackend()`
> Note: Both methods are executed asynchronously in background thread and the wake lock is held until the method is executed. Don't start other asynchronous tasks here unless needed - the wake lock would be released and the tasks would not be guaranteed to run. Put the blocking code right into these methods.
3. In your `MainActivity` `onCreate()`, call `GcmHelper.init(this);`
4. Define GCM Sender ID adding `google-service.json` configuration and setting up Google Services Gradle plugin as described in [Implementing GCM Client on Android ](https://developers.google.com/cloud-messaging/android/client)

If you need ```GET_ACCOUNTS``` permission, add this to you application's manifest:

    <uses-permission
        android:name="android.permission.GET_ACCOUNTS"
        tools:remove="android:maxSdkVersion"/>

That's it. You might also want to use Gradle task for sending push notifications to your device:

* add Gradle dependency to buildscript section: `classpath 'eu.inloop:easygcm-tasks:1.3.0'`
* edit push notification data, apiKey and registration ID in build.gradle
* call `./gradlew push`

### How to build locally:

So far, it is not possible to use project dependencies in Gradle's buildscript section (needed for our plugin). That means you have to build the library first (and install in local Maven repo) and just then build the sample application.

    ./gradlew clean uploadArchives
    ./gradlew -p easygcm-sample clean assembleDebug
