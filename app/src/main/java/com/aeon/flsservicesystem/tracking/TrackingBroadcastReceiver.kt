package com.aeon.flsservicesystem.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class TrackingBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {


        if(!TrackingUpdateService.isRunning)
        {
            val trackingServiceIntent = Intent(context, TrackingUpdateService::class.java)
            context.startForegroundService(trackingServiceIntent)
        }


    }
}