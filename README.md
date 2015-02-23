# easygcm

Android Library for easy GCM integration. Including push notifications sender task for Gradle.

This library is based on sample client code provided by Android documentation at http://developer.android.com/google/gcm/client.html and https://code.google.com/p/gcm/

It includes all the common code from the sample including AndroidManifest.xml that is automatically merged into your application. Also content of sample's DemoActivity is included in this library extracted in GcmHelper class.

### How to use:

**WARNING** Current version on Maven Central contains bug and will not work properly. We are [working on fix](https://github.com/inloop/easygcm/issues/12).

* add this to your build.gradle: `compile 'eu.inloop:easygcm:1.2.3@aar'`
* in your `Application` implement [GcmListener](http://github.com/inloop/easygcm/blob/master/easygcm/src/main/java/eu/inloop/easygcm/GcmListener.java) interface with two methods: 
 * `onMessage()` (don't forget to call wakeLockRelease.release() after you are done)
 * `sendRegistrationIdToBackend()`
* in your MainActivity `onCreate()`, call `GcmHelper.init(this, "your-google-dev-project-id");`

That's it. You might also want to use Gradle task for sending push notifications to your device:

* add Gradle dependency to buildscript section: `classpath 'eu.inloop:easygcm-tasks:1.2.3'`
* edit push notification data, apiKey and registration ID in build.gradle
* call `./gradlew push`

### How to build locally:

So far, it is not possible to use project dependencies in Gradle's buildscript section (needed for our plugin). That means you have to build the library first (and install in local Maven repo) and just then build the sample application.

    ./gradlew clean uploadArchives
    ./gradlew -p easygcm-sample clean assembleDebug
