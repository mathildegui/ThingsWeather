package com.mathilde.thingsweather

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.DynamicSensorCallback
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay
import java.io.IOException


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private val TAG = MainActivity::class.java.simpleName as String

    private var bmp280Driver: Bmx280SensorDriver? = null
    private lateinit var mSensorManager: SensorManager
    private lateinit var alphanumericDisplay: AlphanumericDisplay

    fun initSensorManager() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensorManager.registerDynamicSensorCallback(mDynamicSensorCallback)
    }

    private val mDynamicSensorCallback = object : DynamicSensorCallback() {
        override fun onDynamicSensorConnected(sensor: Sensor) {
            if (sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                Log.i(TAG, "Temperature sensor connected")
                mSensorManager.registerListener(object: SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        val value = event.values[0]

                        if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
//                    final float value = event.values[0];
                            updateTemperatureDisplay(value.toDouble())
                            findViewById<TextView>(R.id.temp).text = value.toString()
                            Log.d(TAG, "temperature: $value")
                        }
                        if (event.sensor.type == Sensor.TYPE_PRESSURE) {
//                            updateBarometerDisplay(value)
                        }
                    }

                    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                        Log.d(TAG, "accuracy changed: $accuracy")
                    }
                }, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    fun registeTemp () {
        try {
            bmp280Driver = Bmx280SensorDriver(BoardDefaults.getI2CPort())

            // Register the drivers with the framework
            bmp280Driver?.registerTemperatureSensor()
            bmp280Driver?.registerHumiditySensor()

            Log.d(TAG, "Initialized I2C BMP280")
        } catch (e: IOException) {
            throw RuntimeException("Error initializing BMP280", e)
        }
    }

    fun setupalphanumericDisplay() {
        try {
            alphanumericDisplay = AlphanumericDisplay(BoardDefaults.getI2CPort())
            alphanumericDisplay.setEnabled(true)
            alphanumericDisplay.clear()
            Log.d(TAG, "Initialized I2C Display")
        } catch (e: IOException) {
            Log.e(TAG, "Error initializing display", e)
            Log.d(TAG, "Display disabled")
//            alphanumericDisplay = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)



        initSensorManager()
        registeTemp()
        setupalphanumericDisplay()
    }

    fun updateTemperatureDisplay(temp : Double) {
        if(alphanumericDisplay != null ) {
            try {

                alphanumericDisplay.display(temp)
            } catch (e : IOException) {
                Log.e(TAG, "Error updating display", e)
            }
        }
    }
}