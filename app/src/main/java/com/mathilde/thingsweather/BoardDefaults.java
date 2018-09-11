package com.mathilde.thingsweather;

import android.os.Build;

/**
 * @author mathilde
 * @version 24/08/2018
 */
public class BoardDefaults {
  private static final String DEVICE_RPI3 = "rpi3";
  private static final String DEVICE_IMX6UL_PICO = "imx6ul_pico";
  private static final String DEVICE_IMX7D_PICO = "imx7d_pico";

  /**
   * Return the preferred I2C port for each board.
   */
  public static String getI2CPort() {
    switch (Build.DEVICE) {
      case DEVICE_RPI3:
        return "I2C1";
      case DEVICE_IMX6UL_PICO:
        return "I2C2";
      case DEVICE_IMX7D_PICO:
        return "I2C1";
      default:
        throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
    }
  }
}
