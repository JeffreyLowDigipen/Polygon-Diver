package com.example.projectpolygondiver.Sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.example.projectpolygondiver.Managers.InputManager

class TiltSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometerValues = FloatArray(3)
    private var magnetometerValues = FloatArray(3)

    // New flags for independent axis tilt detection
    private var tiltDetectedRoll = false // Left / Right detection (X-axis)
    private var tiltDetectedPitch = false // Forward / Backward detection (Y-axis)

    // Separate last tilt direction variables for each axis
    private var lastTiltDirectionRoll = "" // "left" or "right"
    private var lastTiltDirectionPitch = "" // "forward" or "backward"

    // Store initial orientation values
    var initialAzimuth = 0f
    var initialPitch = 0f
    var initialRoll = 0f

    // Store relative orientation values
    var relativeAzimuth = 0f
    var relativePitch = 0f
    var relativeRoll = 0f

    private var initialOrientationSet = false
    private var lastTiltTime = 0L
    private val tiltCooldown = 300f // 1-second cooldown

    private var tiltDetected = false // Flag to check if a tilt has already been detected
    private val recenterThreshold = 15f // Threshold for re-centering the phone
    private var lastTiltDirection: String? = null // Stores the last tilt direction

    // New flag for resetting orientation
    var resetRequested = false

    init {
        // Register sensors
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
    }

    // Unregister listener
    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerValues = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> magnetometerValues = event.values.clone()
        }

        if (!InputManager.isDetectingTilt) return

        // Calculate rotation matrix and orientation
        if (accelerometerValues.isNotEmpty() && magnetometerValues.isNotEmpty()) {
            val rotationMatrix = FloatArray(9)
            val orientationAngles = FloatArray(3)

            val success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues)
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)

                // Convert from radians to degrees
                val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat() // Z-axis rotation
                val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()   // X-axis rotation
                val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()    // Y-axis rotation

                // Store relative orientation
                if (initialOrientationSet) {
                    relativeAzimuth = azimuth - initialAzimuth
                    relativePitch = pitch - initialPitch
                    relativeRoll = roll - initialRoll
                    detectTilt(relativePitch, relativeRoll)
                }
            }
        }
    }

    // Function to reset orientation
    fun ResetOrientation() {
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)
        val success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues)
        if (success) {
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // Convert from radians to degrees
            initialAzimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            initialPitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
            initialRoll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
            relativeRoll=0f
            relativePitch=0f
            relativeAzimuth=0f
            initialOrientationSet = true
            resetRequested = false
            Log.d("TiltDetection", "Orientation reset -> Azimuth: $initialAzimuth°, Pitch: $initialPitch°, Roll: $initialRoll°")
        }
    }



    private fun detectTilt(relativePitch: Float, relativeRoll: Float) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTiltTime < tiltCooldown) return

        val aggressiveTiltThreshold = 30f // Require larger tilts for detection

        // 🔄 Re-centering check for Roll (Left / Right)
        if (tiltDetectedRoll) {
            if (Math.abs(relativeRoll) <= recenterThreshold) {
                tiltDetectedRoll = false
                lastTiltDirectionRoll = "" // Reset last tilt direction for Roll
                Log.d("Tilt", "Re-centered on X-axis (Roll). Ready for next tilt.")
            }
        }

        // 🔄 Re-centering check for Pitch (Forward / Backward)
        if (tiltDetectedPitch) {
            if (Math.abs(relativePitch) <= recenterThreshold) {
                tiltDetectedPitch = false
                lastTiltDirectionPitch = "" // Reset last tilt direction for Pitch
                Log.d("Tilt", "Re-centered on Y-axis (Pitch). Ready for next tilt.")
            }
        }

        // Detect LEFT / RIGHT based on relativeRoll
        if (!tiltDetectedRoll) {
            if (relativeRoll > aggressiveTiltThreshold && lastTiltDirectionRoll != "right") {
                Log.d("Tilt", "Aggressive tilt to the right detected")
                tiltDetectedRoll = true
                lastTiltDirectionRoll = "right"
                lastTiltTime = currentTime
            } else if (relativeRoll < -aggressiveTiltThreshold && lastTiltDirectionRoll != "left") {
                Log.d("Tilt", "Aggressive tilt to the left detected")
                tiltDetectedRoll = true
                lastTiltDirectionRoll = "left"
                lastTiltTime = currentTime
            }
        }

        // Detect FORWARD / BACKWARD based on relativePitch
        if (!tiltDetectedPitch) {
            if (relativePitch > aggressiveTiltThreshold && lastTiltDirectionPitch != "forward") {
                Log.d("Tilt", "Aggressive tilt forward detected")
                tiltDetectedPitch = true
                lastTiltDirectionPitch = "forward"
                lastTiltTime = currentTime
            } else if (relativePitch < -aggressiveTiltThreshold && lastTiltDirectionPitch != "backward") {
                Log.d("Tilt", "Aggressive tilt backward detected")
                tiltDetectedPitch = true
                lastTiltDirectionPitch = "backward"
                lastTiltTime = currentTime
            }
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle changes in sensor accuracy if needed
    }
}
