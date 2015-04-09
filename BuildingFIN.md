# Installation Steps #

Ensure that the Java JDK, Android SDK, and Apache Ant are installed properly on your machine.  For Ant instructions, see http://ant.apache.org/manual/install.html or the section below for installing Ant on Windows.

_Note that this assumes the JDK and ANT have been added to your PATH variable, so that you can call the functions from any directory.  If they are not, you can manually cd to the bin directory of the Java JDK to run the next step_

  1. In a command prompt, cd to the root directory of the project (where src and res are located) and run `keytool -list -keystore include\fin-release-key.keystore` (forward slash in linux)
  1. You will be prompted for a password.  Type 'recycle' (without the single-quotes) and hit enter.
  1. Copy and paste the Certificate Fingerprint code into the box at the following URL: http://code.google.com/android/maps-api-signup.html, agree to the terms of service and click Generate API Key
  1. An API key will be generated for you. Add this to the android:apiKey="" line of map.xml and addnew\_outdoor.xml, which are located in the res\layout sub-directory of the project.  Then save the files
  1. Now from the root of the project directory run 'ant release' (without the single-quotes).
    * _NOTE: If you receive an error such as_
```
BUILD FAILED
C:\Users\EricHare\Software\FindItNow\FindItNow\build.xml:46: taskdef class com.android.ant.SetupTask cannot be found using the classloader AntClassLoader[]
```
    * Then you should run the following command from the tools sub-directory of the Android SDK: `'android update project --target "Google Inc.:Google APIs:7" --path path-to-project-folder --subprojects'` Then try running 'ant release' from the project folder again
  1. When prompted for the passwords, type 'recycle' both times

You're done!  The compiled apk will be located in the bin subdirectory of the project, named FindItNow-release.apk.


---


# Installing Apache Ant for Windows #

  1. Go to http://ant.apache.org/bindownload.cgi
  1. Under "Current Release of Ant", download the .zip archive (`apache-ant-1.8.2-bin.zip`)
  1. Navigate to the folder where the .zip file downloaded.  Extract its contents.
  1. Open up your environment variables.
  1. Add new system variable with name ANT\_HOME and set the value to the directory you uncompressed Ant to (e.g. `C:\Users\username\Downloads\apache-ant-1.8.2`)
  1. Add a new system variable with name JAVA\_HOME and set the value to your Java environment directory (e.g. `C:\Program Files\Java\jdk1.6.0_23`)
  1. From your existing system variables, select PATH and click "Edit".  At the end of the existing variable value (make sure it is terminated with a semicolon), append: `%ANT_HOME%\bin;%JAVA_HOME%\bin`