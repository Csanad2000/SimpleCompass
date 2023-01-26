package com.csanad.simplecompass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class CompassSensorModule(
    private val manager: SensorManager,
    private val pin: ImageView,
    private val incompatible: TextView
) : SensorEventListener {
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    fun resume() {
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer == null || magnetometer == null) {
            pin.visibility = View.INVISIBLE
            incompatible.visibility = View.VISIBLE
        } else {
            pin.visibility = View.VISIBLE
            incompatible.visibility = View.INVISIBLE

            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            manager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun pause() {
        manager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            }
            updateOrientationAngles()
            pin.rotation = -orientationAngles[0] * 180 / Math.PI.toFloat()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO figure out a way to efficiently inform user, maybe at redraw?
    }

    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }
}