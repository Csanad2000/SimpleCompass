package com.csanad.simplecompass

import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var sensorModule: CompassSensorModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorModule = CompassSensorModule(
            getSystemService(SensorManager::class.java),
            findViewById<ImageView>(R.id.pinImage),
            findViewById<TextView>(R.id.incompatible)
        )
    }

    override fun onResume() {
        super.onResume()
        sensorModule.resume()
    }

    override fun onPause() {
        super.onPause()
        sensorModule.pause()
    }
}