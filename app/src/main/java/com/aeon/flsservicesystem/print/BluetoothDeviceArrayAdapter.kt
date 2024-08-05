/***********************************************
 * CONFIDENTIAL AND PROPRIETARY
 *
 * The source code and other information contained herein is the confidential and the exclusive property of
 * ZIH Corp. and is subject to the terms and conditions in your end user license agreement.
 * This source code, and any other information contained herein, shall not be copied, reproduced, published,
 * displayed or distributed, in whole or in part, in any medium, by any means, for any purpose except as
 * expressly permitted under such license agreement.
 *
 * Copyright ZIH Corp. 2015 - 2022
 *
 * ALL RIGHTS RESERVED
 */
/**
 * Created by BWai on 7/7/2015.
 */
package com.zebra.isv.tapbluetoothwifi

import android.bluetooth.BluetoothDevice
import android.content.Context

//Implements the Bluetooth Device Array List View
class BluetoothDeviceArrayAdapter(
     val context: Context,
     val values: ArrayList<BluetoothDevice>
)

