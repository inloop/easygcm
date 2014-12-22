# easygcm


Android Library for easy GCM integration. Including push notifications sender task for Gradle.

This library is based on sample client code provided by Android documentation at http://developer.android.com/google/gcm/client.html and https://code.google.com/p/gcm/

It includes all the common code from the sample including AndroidManifest.xml that is automatically merged into your application. Also content of sample's DemoActivity is included in this library extracted in GcmHelper class.

### Current status

We are working on the first public release of the library. Before we go live in public repos, we would like to get as much feedback as possible. For now, you'll have to build it yourself and deploy to local Maven repo (see below).

### How to use:

Have a look at sample project in this repository. Integration of the library is very simple:

* add Gradle dependency : `compile 'eu.inloop:easygcm:1.2.2@aar'`
* in your `Application` implement `GcmListener` interface with two methods: `onMessage()` and `sendRegistrationIdToBackend()`

That's it. You might also want to use Gradle task for sending push notifications to your device:

* add Gradle dependency to buildscript section: `classpath 'eu.inloop:easygcm-tasks:1.2.2'`
* edit push notification data, apiKey and registration ID in build.gradle
* call `./gradlew push`

### How to build:

So far, it is not possible to use project dependencies in Gradle's buildscript section (needed for our plugin). That means you have to build the library first (and install in local Maven repo) and just then build the sample application.

    ./gradlew clean uploadArchives
    ./gradlew -p easygcm-sample clean assembleDebug
