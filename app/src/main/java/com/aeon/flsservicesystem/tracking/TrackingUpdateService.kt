package com.aeon.flsservicesystem.tracking

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.aeon.flsservicesystem.MainActivity
import com.aeon.flsservicesystem.R
import com.aeon.flsservicesystem.user_manager.UserDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices



class TrackingUpdateService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val dataBaseUser = "database-user"
    private val dataBaseTracking = "database-tracking"

    companion object {
        const val CHANNEL_ID = "location"
        var isRunning = false
    }


    override fun onCreate() {

        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isRunning) {
            return START_NOT_STICKY
        }
        isRunning = true

        createNotificationChannel()
        val notificationIntent = Intent(this,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Fls Tracking Location")
            .setContentText("Start Tracking Service")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setOngoing(true)
            .setShowWhen(true).setContentIntent(pendingIntent).build()

        startForeground(1,notification)

        fetchLocation()
        return START_NOT_STICKY

    }



    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Handle the case where permissions are not granted
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                updateNotification(it)
            }
        }
        onDestroy()
    }

    private fun updateNotification(location : Location) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            this.registerReceiver(null, ifilter)
        }

        val batteryPct: Int? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toInt()
        }
        val latitude = location.latitude
        val longitude = location.longitude

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Latitude: $latitude, Longitude: $longitude, Battery: $batteryPct%")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setShowWhen(true)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notification)
        val lastLocation: Location? = Location(location)
        if (lastLocation != null) {
            saveLocationToDatabase(lastLocation)
        }
        updateToWebService()
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(
                NotificationManager ::class.java
            )
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onDestroy() {

        super.onDestroy()
        isRunning = false

    }


    private fun saveLocationToDatabase(location: Location) {
            val batteryPct: Float? = getBatteryStatus()?.let { intent ->
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                level * 100 / scale.toFloat()
            }
            val locationBattery = (batteryPct ?: -1F).toInt()
            var employeeCode = ""
            val db = Room.databaseBuilder(
                applicationContext,
                UserDatabase::class.java, dataBaseUser
            ).allowMainThreadQueries().build()



            val userData = db.userDao()
            val listUser = userData.getAll()
            if (listUser.isNotEmpty()) {
                employeeCode = listUser[0].uid
            }
            val locationTime = (System.currentTimeMillis() / 1000L).toInt()
            val updatedDate = (System.currentTimeMillis() / 1000L).toInt()

            val dbTracking = Room.databaseBuilder(
                applicationContext,
                TrackingDatabase::class.java, dataBaseTracking
            ).allowMainThreadQueries().build()

            val trackingDataDao = dbTracking.trackingDao()
            val trackingData = TrackingData(staffCode = employeeCode, updateFlag = 1
                , latitude = location.latitude
                , longitude = location.longitude
                , createdAt = locationTime
                , battery = locationBattery
                , speed = location.speed
                , provider = location.provider
                , update_at = updatedDate
            )

        val existCreateAt = trackingDataDao.checkDupCreateAt(trackingData.createdAt)
        if(existCreateAt == null)
        {trackingDataDao.insertAll(trackingData)}

    }

    private fun updateToWebService() {
        val trackingServiceWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<TrackingServiceWork>()
                .build()
        WorkManager.getInstance(this.applicationContext).enqueue(trackingServiceWorkRequest)
    }


    private fun getBatteryStatus(): Intent? {
        return registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }



}