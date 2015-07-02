## 1.5.0 (2015-07-02)

This release brings incompatible changes. Please README and sample project for details on integration of the library in your project. Shortly:

 - onMessage is changed, no need to release a wake lock. Make sure, your blocking tasks are executed directly in GcmListener callback methods, as they run in background thread. Creating s new background task would not be guaranteed to run.
 - no need to add custom manifest placeholder `localApplicationId`

Features:

  - [#33 Support for Google Play Services 7.5](https://github.com/inloop/easygcm/issues/33)
  - [#34 Switch lib to use ${applicationId} in manifest once Android bug is fixed](https://github.com/inloop/easygcm/issues/34)

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

  - onMessage is changed, you need to call `wakeLockRelease.release()` after your job is done
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
