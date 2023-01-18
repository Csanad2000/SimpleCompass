package com.csanad.simplecompass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.lang.Math.toDegrees

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var manager: SensorManager
    private var accelerometer:Sensor?=null
    private var magnetometer:Sensor?=null

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private lateinit var pin:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manager = getSystemService(SensorManager::class.java)
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        pin = findViewById(R.id.pinImage)

        if (accelerometer == null || magnetometer == null) {
            pin.visibility = View.INVISIBLE
            findViewById<TextView>(R.id.incompatible).visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        if (accelerometer != null && magnetometer != null){
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            manager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
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
            pin.rotation = (-toDegrees(orientationAngles[0].toDouble())).toFloat()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO figure out a way to efficiently inform user, maybe at redraw?
    }

    //call before drawing for accurate info
    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }
}