## 1.6.1 (2015-12-21)

Features:

  - Updated com.android.tools.build:gradle to 1.5.0
  - Updated com.google.gms:google-services to 1.5.0

## 1.6.0 (2015-12-02)

Features:

  - [#39 Gcm registration now handled by WakefulIntentService] (https://github.com/inloop/easygcm/issues/39)
  - [#59 Added a method to remove gcm registration ID.] (https://github.com/inloop/easygcm/issues/59)
  - GcmHelper class is now deprecated in favour of EasyGcm class.
  - The logging is now handled by EasyGcm.Logger class and can have its logging levels set.

Bugfixes:

  - [#23 Gcm registration will not start until the device connects online] (https://github.com/inloop/easygcm/issues/23)
  - [#29 Registration is invalidated and restarted on OS upgrade] (https://github.com/inloop/easygcm/issues/29)

## 1.5.5 (2015-11-26)

Features:

  - [#63 Updated to Google Play Services 8.3.0] (https://github.com/inloop/easygcm/issues/63)

## 1.5.4 (2015-10-29)

Bugfixes:

  - [#61 Catch for SecurityException in some exotic cases during GCM registration] (https://github.com/inloop/easygcm/issues/61)

## 1.5.3 (2015-10-13)

Bugfixes:

  - [#45 The package name of permission.C2D_MESSAGE still wrong] (https://github.com/inloop/easygcm/issues/45)

## 1.5.2 (2015-09-28)

Features:

  - [#56 Updated to Google Play Services 8.1.0] (https://github.com/inloop/easygcm/issues/56)

## 1.5.1 (2015-09-22)

Features:

  - [#47 Updated to Google Play Services 7.8.0] (https://github.com/inloop/easygcm/issues/47)
  - [#48 Used GoogleApiAvailibility instead of GooglePlayServicesUtil] (https://github.com/inloop/easygcm/issues/48)

Bugfixes:

  - [#43 Removed GET_ACCOUNTS permission] (https://github.com/inloop/easygcm/issues/43)

## 1.5.0 (2015-07-02)

This release brings incompatible changes. Please README and sample project for details on integration of the library in your project. Shortly:

  - `onMessage` is changed, no need to release a wake lock. Make sure, your blocking tasks are executed directly in `GcmListener` callback methods, as they run in background thread. Creating a new background task would not be guaranteed to run.
  - no need to add custom manifest placeholder `localApplicationId`

Features:

  - [#33 Support for Google Play Services 7.5](https://github.com/inloop/easygcm/issues/33)
  - [#34 Switch lib to use ${applicationId} in manifest once Android bug is fixed](https://github.com/inloop/easygcm/issues/34)
  - GCM sender ID is now defined using Google Services Gradle plugin via `google-services.json` as described in [Implementing GCM Client on Android ](https://developers.google.com/cloud-messaging/android/client)

Bugfixes:

  - [#32 BroadcastReceiver trying to return result during a non-ordered broadcast](https://github.com/inloop/easygcm/issues/32)
  - [#36 BroadcastReceiver trying to return result during a non-ordered broadcast](https://github.com/inloop/easygcm/issues/36)

## 1.4.0 (2015-05-14)

Features:

  - [#26  Allow custom Play Services handling and re-registration](https://github.com/inloop/easygcm/pull/26)
  - Update Google Play Services to 7.3
  
Bugfixes:

  - [#30 Fix build scripts to use local project instead of build from repository](https://github.com/inloop/easygcm/pull/30)
  
## 1.3.0 (2015-03-12)

This release brings incompatible changes. Please README and sample project for details on integration of the library in your project. Shortly:

  - `onMessage` is changed, you need to call `wakeLockRelease.release()` after your job is done
  - `GcmHelper.init()` is changed, sender ID is now configured in XML instead of passing as parameter
  - you need to add custom manifest placeholder `localApplicationId` in your build script to workaround Android bug

Bugfixes:

  - [#2 Don't call completeWakefulIntent right after onMessage](https://github.com/inloop/easygcm/issues/2)
  - [#12 ${applicationId} is still using "easygcm" but not project's appliication id](https://github.com/inloop/easygcm/issues/12)
  - [#13 SERVICE_NOT_AVAILABLE Error](https://github.com/inloop/easygcm/issues/13)
  - [#14 GET_ACCOUNTS permission is not needed on ICS+](https://github.com/inloop/easygcm/issues/14)
  - [#15 Re-register immediately after app update](https://github.com/inloop/easygcm/issues/15)

Features:

  - [Support for setting custom GcmListener](https://github.com/inloop/easygcm/pull/21)
  - [Retry if GCM registration fails](https://github.com/inloop/easygcm/pull/20)
  - add option to disable logging, see `GcmHelper.setLoggingEnabled()`

## 1.2.3 (2014-12-22)

Bugfixes:

  - fixed min SDK - set to 9

## 1.2.2 (2014-12-22)

Features:

  - first public release

Bugfixes:

  - [#1 Create a destroy method to cancel in-flight AsyncTasks](https://github.com/inloop/easygcm/issues/1)
