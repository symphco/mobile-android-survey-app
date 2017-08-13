# Mobile Android Survey App

### Mobile Android Survey App is a mobile application to download web surveys, allow offline entry and upload collected submissions to a Drupal Survey Platform [https://github.com/symph-team/open-survey-platform](https://github.com/symph-team/open-survey-platform)

## Android Version

The app supports devices running [Android 4.0 (API 14)](http://developer.android.com/about/versions/android-4.0.html) and higher.

## Android Permissions

The app uses the following Android features:

* com.google.android.maps
* android.hardware.camera
* android.hardware.camera.autofocus

The app requires the following Android permissions:

* android.permission.CAMERA
* android.permission.INTERNET
* android.permission.READ_CONTACTS
* android.permission.WRITE_SETTINGS
* android.permission.READ_EXTERNAL_STORAGE
* android.permission.WRITE_EXTERNAL_STORAGE
* android.permission.CHANGE_WIFI_STATE
* android.permission.ACCESS_WIFI_STATE
* android.permission.ACCESS_NETWORK_STATE
* android.permission.ACCESS_FINE_LOCATION
* android.permission.ACCESS_COARSE_LOCATION

These permissions and features are defined in project's [AndroidManifest.xml](app/src/main/AndroidManifest.xml) file.

## Login Tab

Before the user can begin collecting surveys, they are required to login to the app using their OpenEd account credentials.

![login](doc/1.0/armm_login.png?raw=true)

If the user needs to create an OpenEd account or forgots their password, they can visit [/user/login](/user/login).

## Survey Tab

The *Survey* tab lists the available surveys that the user can input results for.

![surveys](doc/1.0/armm_surveys.png?raw=true)

Pulling down on the Survey list or clicking the Refresh button in the action bar will download new or modified surveys from the server.

## Incomplete Tab

The *Incomplete* tab lists all the surveys not currently completed.

![incomplete](doc/1.0/armm_incomplete.png?raw=true)

Note, surveys in the *Incomplete* list can not be submitted until all the required questions have been answered.

## Completed Tab

The *Completed* tab lists all surveys that have been completed but not yet submitted or have been submitted.

![completed](doc/1.0/armm_completed.png?raw=true)

## Question Types

The app supports the following question types:

### textfield - plain text
![textfield](doc/1.0/armm_textfield.png?raw=true)

### textarea - multiline text
![textarea](doc/1.0/armm_textarea.png?raw=true)

### number - numerical input
![number](doc/1.0/armm_number.png?raw=true)

### email - email address input
![email](doc/1.0/armm_email.png?raw=true)

### select - single choice select
![select](doc/1.0/armm_select.png?raw=true)

### grid - collection of single choice select
![grid](doc/1.0/armm_grid.png?raw=true)

### date - date in format *yyyy-MM-dd*
![date](doc/1.0/armm_date.png?raw=true)

### time - time in format *HH:mm:ss*
![time](doc/1.0/armm_time.png?raw=true)

### markup - read only HTML message
![textarea](/doc/1.0/armm_textarea.png?raw=true)

### geofield - location detection
![geofield](doc/1.0/armm_geofield.png?raw=true)

### file - upload of photos
![file](doc/1.0/armm_file.png?raw=true)

### fieldset - collection of sub questions
![fieldset](doc/1.0/armm_fieldset.png?raw=true)

## Secondary Menu

Each question type has a secondary menu to provide contextual help, for example changing the keyboard type or clearing the current result.

![fieldset](doc/1.0/armm_menu.png?raw=true)

## Required Questions

Surveys with questions marked as *required*, will require the user to provide an answer before they can continue moving to the next question of the survey.

![fieldset](doc/1.0/armm_required.png?raw=true)

Surveys with required questions will not be allowed to be submitted until all the required questions have a valid answer.

## Survey Details

You can view collected survey results by clicking an item in the Incomplete or Complete tab.

![fieldset](doc/1.0/armm_details.png?raw=true)

## App Logout

The app currently only supports single user usage.
For another user to use the app, the current user must first logout, by clicking *Logout* from the secondary menu in the action bar.
Note, this will delete the current items from the database as well as remove any user information, requiring login again.

## Style Resources

The style resources are defined in the [styles.xml](app/src/main/res/values/styles.xml) file.
You can customize these styles following the Android [style resource](http://developer.android.com/guide/topics/resources/style-resource.html) conventions.

## String Resources

The string resources are defined in the [strings.xml](app/src/main/res/values/strings.xml) file.
You can change these string values following the Android [string resource](http://developer.android.com/guide/topics/resources/style-resource.html) conventions.
To add translations to the app, please following the Android [Supporting Different Languages](http://developer.android.com/training/basics/supporting-devices/languages.html) guidelines.

## Color Resources

The color resources are defined in the [colors.xml](app/src/main/res/values/colors.xml) file.
These colors can be customized by changing the hexcode values following the Android [color resource](http://developer.android.com/guide/topics/resources/more-resources.html#Color) conventions.

## ActiveAndroid Database

The app uses [ActiveAndroid](http://www.activeandroid.com) library has a database [ORM](http://en.wikipedia.org/wiki/Object-relational_mapping), learn more at [https://github.com/pardom/ActiveAndroid](https://github.com/pardom/ActiveAndroid).

## Fabric Crashlytics

[Fabric](https://get.fabric.io) [Crashlytics](https://get.fabric.io/crashlytics) is a free error reporting service developed by the team at [Twitter](https://www.twitter.com).
The API key is specified under *com.crashlytics.ApiKey* in the [AndroidManifest.xml](app/src/main/AndroidManifest.xml) file.

## Google Maps API

The app uses [Google Maps Android API v2](https://developers.google.com/maps/documentation/android/), which requires an API key from Google.
The API key is specified under *com.google.android.maps.v2.API_KEY* in the [AndroidManifest.xml](app/src/main/AndroidManifest.xml) file.

## Library Dependencies

The app requires the following external libraries:

* com.android.support:support-v4:21.0.3
* com.android.support:support-v13:21.0.2
* com.android.support:appcompat-v7:21.0.3
* com.android.support:recyclerview-v7:21.0.2
* com.android.support:cardview-v7:21.0.2

The app requires the following local libariries:

* libs/ActiveAndroid.jar
* libs/httpcore-4.3.3.jar
* libs/httpmime-4.3.6.jar

Note, both the external and local libraries are defined in the [build.gradle](app/build.gradle) file.

#### For additional questions about this and project and other Open Government Projects, please visit:  [https://github.com/symph-team](https://github.com/symph-team).
