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

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var manager: SensorManager
    private var rotation: Sensor? = null
    private var georotation: Sensor? = null
    private var used = -1
    private var chooseable = false
    private var changed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manager = getSystemService(SensorManager::class.java)
        rotation = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        georotation = manager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)

        if (rotation != null) {
            used = Sensor.TYPE_ROTATION_VECTOR //refactor so used is a reference to a sensor
        }
        if (georotation != null) {
            if (used == Sensor.TYPE_ROTATION_VECTOR) chooseable = true
            used = Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR
        }
        if (used == -1) {
            findViewById<ImageView>(R.id.pinImage).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.incompatible).visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        when (used) {
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                manager.registerListener(this, georotation, SensorManager.SENSOR_DELAY_UI)
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
                manager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        manager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            //event.values
            //TODO draw the compass
        }
    }

    //intent is to use the not unreliable option TODO give user the option to switch
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            when (used) {
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                    findViewById<TextView>(R.id.magnetoUnreliable).visibility = View.VISIBLE
                }
                Sensor.TYPE_ROTATION_VECTOR -> {
                    findViewById<TextView>(R.id.gyroUnreliable).visibility = View.VISIBLE
                }
            }

            if (chooseable&&!changed) { //TODO make sure this is not called repeatedly, currently bootleg
                changed = true
                switch()
            }
        } else {
            when (used) {
                Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                    findViewById<TextView>(R.id.magnetoUnreliable).visibility = View.INVISIBLE
                }
                Sensor.TYPE_ROTATION_VECTOR -> {
                    findViewById<TextView>(R.id.gyroUnreliable).visibility = View.INVISIBLE
                }
            }
            changed = false
        }
    }

    private fun switch() {
        when (used) {
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                used = Sensor.TYPE_ROTATION_VECTOR
                manager.unregisterListener(this, georotation)
                manager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_UI)
            }
            Sensor.TYPE_ROTATION_VECTOR -> {
                used = Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR
                manager.unregisterListener(this, rotation)
                manager.registerListener(this, georotation, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }
}