import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class TiltDetector(context: Context) : SensorEventListener {

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    // Register the listener
    fun startListening() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // Unregister the listener when not needed
    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    // Handle sensor changes
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            detectTilt(event.values)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Detect tilt direction
    private fun detectTilt(values: FloatArray) {
        val x = values[0] // Left/Right tilt
        val y = values[1] // Up/Down tilt
        val z = values[2] // Forward/Backward motion (not needed here)

        // Detect left/right tilt (x-axis)
        when {
        //    x > 2 -> Log.d("TiltDetection", "Tilting Left")
            //x < -2 -> Log.d("TiltDetection", "Tilting Right")
        }

        // Detect up/down tilt (y-axis)
        when {
          //  y > 2 -> Log.d("TiltDetection", "Tilting Down")
           // y < -2 -> Log.d("TiltDetection", "Tilting Up")
        }
    }
}
