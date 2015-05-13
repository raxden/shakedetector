Android ShakeDetector
==========

Android ShakeDetector is a library that allows us verify if device has an accelerometer, and listening for shakes.

## Usage

### build.gradle

```
compile 'com.raxdenstudios:androidcommons:2.0.+'
compile 'com.raxdenstudios:androidshakedetector:2.0.+'
```

### Activity or Fragment

```
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShakeDetectorHelper.getInstance().startShakeDetector(this, new ShakeDetectorHelper.ShakeDetectorListener() {
            public void shakeDetected() {
              // device is shaking!
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShakeDetectorHelper.getInstance().stopShakeDetector();
    }
```
