package com.example.projectpolygondiver.Sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log

class ShakeSensorManager(private val context: Context, private val onShakeDetected: () -> Unit) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometerValues = FloatArray(3)

    private var lastShakeTime: Long = 0
    private val shakeThreshold = 30f
    private val shakeCooldown = 2000L

    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    init {
        startListening()
    }

    fun startListening() {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values.clone()
            detectShake(event)
        }
    }

    private fun detectShake(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat() - SensorManager.GRAVITY_EARTH
        val currentTime = System.currentTimeMillis()
        //Log.d ("Shake" , "Acceleration: ${acceleration}")
        if (acceleration > shakeThreshold && currentTime - lastShakeTime > shakeCooldown) {
            lastShakeTime = currentTime
            triggerShakeAction()
            Log.d("Shake","Shake")
        }
    }

    private fun triggerShakeAction() {
        vibratePhone()
        onShakeDetected() //
    }

    private fun vibratePhone() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
