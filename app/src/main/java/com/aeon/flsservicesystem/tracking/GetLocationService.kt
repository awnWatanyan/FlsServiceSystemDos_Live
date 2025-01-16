package com.aeon.flsservicesystem.tracking

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.BatteryManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await


class GetLocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient
            = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocationData() : LocationData? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle the case where permissions are not granted
            return null
        }


        val location : Location? =
            try {
                fusedLocationClient.lastLocation.await()

            }catch (e : Exception)
            {
                println(e.stackTrace.toString())
                null
            }



        val batterPct = getBatteryPercent()
        return location?.let { LocationData(location.latitude.toString(),location.longitude.toString(),batterPct.toString(),location.speed.toString()) }
    }


    private fun getBatteryPercent() : Int? {

        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }

        val batteryPct: Int? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toInt()
        }

        return batteryPct
    }


}