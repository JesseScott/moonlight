# 01 CREATE + CLONE

* create git repository
* clone git repository

# 02 CHECK NODE

* open terminal
  * type *node -v*
  * type *npm -v*
  * perform dark magick if necessary

# 03 INSTALL CORDOVA + PHONEGAP

* open terminal
  * type *sudo npm install -g cordova*
  * type *sudo npm install -g phonegap*

# 04 CREATE PHONEGAP PROJECT

* cd to directory
  * type *cd 'location-you-cloned-project'*
* create project
  * type *cordova create hello com.example.hello HelloWorld* or somesuch

# 05 ADD PLATFORMS

* cd to app
  * type *cordova platform add xxx*

# 06 ADD PLUGINS

* cd to app
  * type *cordova plugin add org.apache.cordova.splashscreen*
  * type *cordova plugin add org.apache.cordova.console*
  * type *cordova plugin add org.apache.cordova.device*
