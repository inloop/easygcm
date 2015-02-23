## 1.3.0-SNAPSHOT

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

  - add option to disable logging, see `GcmHelper.setLoggingEnabled()`

## 1.2.3 (2014-12-22)

Bugfixes:

  - fixed min SDK - set to 9

## 1.2.2 (2014-12-22)

Features:

  - first public release

Bugfixes:

  - [#1 Create a destroy method to cancel in-flight AsyncTasks](https://github.com/inloop/easygcm/issues/1)
