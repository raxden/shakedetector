Android ShakeDetector
==========

Android ShakeDetector is a library that allows us verify if device has an accelerometer, and listening for shakes. Works on Android 4.1 (API level 16) and upwards.

[![Release](https://img.shields.io/github/release/raxden/AndroidShakeDetector.svg?label=maven central)](https://jitpack.io/#raxden/AndroidShakeDetector/) [![API](https://img.shields.io/badge/API-16%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=16)

## Usage

In order to use the library, there are 3 options:

**1. Gradle dependency**

 - 	Add the following to your `build.gradle`:
 ```gradle
repositories {
	    maven { url "https://jitpack.io" }
}

dependencies {
	    compile 'com.github.raxden:AndroidShakeDetector:3.0.1@aar'
}
```

**2. Maven**
- Add the following to your `pom.xml`:
 ```xml
<repository>
       	<id>jitpack.io</id>
	    <url>https://jitpack.io</url>
</repository>

<dependency>
	    <groupId>com.github.raxden</groupId>
	    <artifactId>AndroidShakeDetector</artifactId>
	    <version>3.0.1</version>
</dependency>
```

**3. clone whole repository**
 - Open your **commandline-input** and navigate to your desired destination folder (where you want to put the library)
 - Use the command `git clone https://github.com/raxden/AndroidShakeDetector.git` to download the full AndroidShakeDetector repository to your computer (this includes the folder of the library project as well as the example project)
 
### Documentation 

For a **detailed documentation**, please have a look at the [**Wiki**](https://github.com/raxden/AndroidShakeDetector/wiki).

## LICENSE

    Copyright 2015 Ángel Gómez

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.