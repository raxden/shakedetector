package com.raxdenstudios.shake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agomez on 04/05/2015.
 */
public class ShakeDetectorHelper implements SensorEventListener {

    private static final String TAG = ShakeDetectorHelper.class.getSimpleName();

    /**
     * When the magnitude of total acceleration exceeds this
     * value, the phone is accelerating.
     */
    private static final int ACCELERATION_THRESHOLD = 13;

    /** Listens for shakes. */
    public static interface ShakeDetectorListener {
        /** Called on the main thread when the device is shaken. */
        public void shakeDetected();
    }

    private SampleQueue queue = new SampleQueue();
    private ShakeDetectorListener mListener;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private static ShakeDetectorHelper INSTANCE = null;

    private ShakeDetectorHelper() {}

    private synchronized static void createInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShakeDetectorHelper();
        }
    }

    public static ShakeDetectorHelper getInstance() {
        if (INSTANCE == null) createInstance();
        return INSTANCE;
    }

    /**
     * Starts listening for shakes on devices with appropriate hardware.
     * @returns true if the device supports shake detection.
     */
    public boolean startShakeDetector(Context context, ShakeDetectorListener listener) {
        if (mListener == null) {
            mListener = listener;
        }
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        if (mAccelerometer == null) {
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (mAccelerometer != null) {
            // If this phone has an accelerometer, listen to it.
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Stops listening.  Safe to call when already stopped.  Ignored on devices
     * without appropriate hardware.
     */
    public void stopShakeDetector() {
        if (mAccelerometer != null) {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager = null;
            mAccelerometer = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean accelerating = isAccelerating(event);
        long timestamp = event.timestamp;
        queue.add(timestamp, accelerating);
        if (queue.isShaking()) {
            queue.clear();
            mListener.shakeDetected();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /** Returns true if the device is currently accelerating. */
    private boolean isAccelerating(SensorEvent event) {
        float ax = event.values[0];
        float ay = event.values[1];
        float az = event.values[2];

        // Instead of comparing magnitude to ACCELERATION_THRESHOLD,
        // compare their squares. This is equivalent and doesn't need the
        // actual magnitude, which would be computed using (expesive) Math.sqrt().
        final double magnitudeSquared = ax * ax + ay * ay + az * az;
        return magnitudeSquared > ACCELERATION_THRESHOLD * ACCELERATION_THRESHOLD;
    }

    /** Queue of samples. Keeps a running average. */
    static class SampleQueue {

        /** Window size in ns. Used to compute the average. */
        private static final long MAX_WINDOW_SIZE = 500000000; // 0.5s
        private static final long MIN_WINDOW_SIZE = MAX_WINDOW_SIZE >> 1; // 0.25s

        /**
         * Ensure the queue size never falls below this size, even if the device
         * fails to deliver this many events during the time window. The LG Ally
         * is one such device.
         */
        private static final int MIN_QUEUE_SIZE = 4;

        private final SamplePool pool = new SamplePool();

        private Sample oldest;
        private Sample newest;
        private int sampleCount;
        private int acceleratingCount;

        /**
         * Adds a sample.
         *
         * @param timestamp    in nanoseconds of sample
         * @param accelerating true if > {@link #ACCELERATION_THRESHOLD}.
         */
        void add(long timestamp, boolean accelerating) {
            // Purge samples that proceed window.
            purge(timestamp - MAX_WINDOW_SIZE);

            // Add the sample to the queue.
            Sample added = pool.acquire();
            added.timestamp = timestamp;
            added.accelerating = accelerating;
            added.next = null;
            if (newest != null) {
                newest.next = added;
            }
            newest = added;
            if (oldest == null) {
                oldest = added;
            }

            // Update running average.
            sampleCount++;
            if (accelerating) {
                acceleratingCount++;
            }
        }

        /** Removes all samples from this queue. */
        void clear() {
            while (oldest != null) {
                Sample removed = oldest;
                oldest = removed.next;
                pool.release(removed);
            }
            newest = null;
            sampleCount = 0;
            acceleratingCount = 0;
        }

        /** Purges samples with timestamps older than cutoff. */
        void purge(long cutoff) {
            while (sampleCount >= MIN_QUEUE_SIZE && oldest != null && cutoff - oldest.timestamp > 0) {
                // Remove sample.
                Sample removed = oldest;
                if (removed.accelerating) {
                    acceleratingCount--;
                }
                sampleCount--;

                oldest = removed.next;
                if (oldest == null) {
                    newest = null;
                }
                pool.release(removed);
            }
        }

        /** Copies the samples into a list, with the oldest entry at index 0. */
        List<Sample> asList() {
            List<Sample> list = new ArrayList<Sample>();
            Sample s = oldest;
            while (s != null) {
                list.add(s);
                s = s.next;
            }
            return list;
        }

        /**
         * Returns true if we have enough samples and more than 3/4 of those samples
         * are accelerating.
         */
        boolean isShaking() {
            return newest != null && oldest != null && newest.timestamp - oldest.timestamp >= MIN_WINDOW_SIZE && acceleratingCount >= (sampleCount >> 1) + (sampleCount >> 2);
        }
    }

    /** An accelerometer sample. */
    static class Sample {
        /** Time sample was taken. */
        long timestamp;

        /** If acceleration > {@link #ACCELERATION_THRESHOLD}. */
        boolean accelerating;

        /** Next sample in the queue or pool. */
        Sample next;
    }

    /** Pools samples. Avoids garbage collection. */
    static class SamplePool {
        private Sample head;

        /** Acquires a sample from the pool. */
        Sample acquire() {
            Sample acquired = head;
            if (acquired == null) {
                acquired = new Sample();
            } else {
                // Remove instance from pool.
                head = acquired.next;
            }
            return acquired;
        }

        /** Returns a sample to the pool. */
        void release(Sample sample) {
            sample.next = head;
            head = sample;
        }
    }

}
