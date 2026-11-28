package com.example.neart_project.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

// Reference: GenAi see prompt(s) (8) in report - whole file

/**
 * ShakeDetector - listens to the accelerometer and calls onShake() when the phone is shaken.
 *
 * The accelerometer measures how hard the phone is being pushed in each direction (x, y, z).
 * When the phone is still, the total is about 1 g (just gravity). A shake pushes it much higher.
 *
 * MainActivity registers this with the SensorManager while the app is open,
 * and onShake() opens the rest timer (RestTimerDialog).
 */
class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {

    // When the last shake happened, so one shake doesn't trigger lots of times in a row
    private var lastShakeTime = 0L

    // Called every time the accelerometer has a new reading (many times a second)
    override fun onSensorChanged(event: SensorEvent) {
        // How hard the phone is being pushed left/right (x), up/down (y) and forwards/back (z)
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Combine the three into one total force, then divide by gravity.
        // Result: about 1.0 when the phone is still, higher when it's shaken
        val gForce = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH

        val now = System.currentTimeMillis()
        // Strong enough to be a shake, and at least 1 second since the last one
        if (gForce > SHAKE_THRESHOLD && now - lastShakeTime > 1000) {
            lastShakeTime = now
            onShake()
        }
    }

    // Required by SensorEventListener, but we don't need it
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    companion object {
        // How strong a movement counts as a shake (1.0 = still).
        // Raise it if it triggers too easily, lower it if shakes aren't detected
        const val SHAKE_THRESHOLD = 1.8f
    }
}
