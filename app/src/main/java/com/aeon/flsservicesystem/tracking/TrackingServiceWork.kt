package com.aeon.flsservicesystem.tracking

import android.content.Context
import android.location.LocationManager
import androidx.room.Room
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aeon.flsservicesystem.URL_WEB_PAGE
import com.aeon.flsservicesystem.user_manager.DeviceDatabase
import com.aeon.flsservicesystem.user_manager.UserDatabase
import com.aeon.mci.syncadapter.DefaultBackendVolley
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale

class TrackingServiceWork (appContext: Context, trackingParams: WorkerParameters):
    Worker(appContext, trackingParams) {

    private val dataBaseUser = "database-user"
    private val dataBaseDevice = "database-device"
    private val dataBaseTracking = "database-tracking"

    companion object {
        private const val NETWORK_PROVIDER_DECIMAL_PLACES = 10
        private const val TRACKING_WEBSERVICE_URL = "$URL_WEB_PAGE/TrackingLocation"

    }
    override fun doWork(): Result {
        val dbTracking = connectTrackingDB()

        val trackingDao = dbTracking.trackingDao()
        val listTracking = trackingDao.loadAllNotSend()

        if (listTracking != null) {
            listTracking.forEach { trackingData ->
                val speedInMetersPerSec = trackingData.speed
                val speedInKmPerHr = speedInMetersPerSec?.times(3.6f)
                val status: Int =  if (speedInKmPerHr!! > 0) 22 else 20
                val provider = trackingData.provider
                var latitude = trackingData.latitude
                var longitude = trackingData.longitude
                if (provider == LocationManager.NETWORK_PROVIDER) {
                    latitude += 0.000000009901
                    longitude += 0.000000009901
                    latitude = BigDecimal(latitude)
                        .setScale(NETWORK_PROVIDER_DECIMAL_PLACES, RoundingMode.DOWN)
                        .toDouble()
                    longitude = BigDecimal(longitude)
                        .setScale(NETWORK_PROVIDER_DECIMAL_PLACES, RoundingMode.DOWN)
                        .toDouble()
                }

                var employeeCode = ""
                val time = trackingData.createdAt
                val batteryPct = trackingData.battery
                val now = datetimeFormat().format(Date(time* 1000L))
                var deviceId = ""
                val db = Room.databaseBuilder(
                    applicationContext,
                    UserDatabase::class.java, dataBaseUser
                ).build()

                val userData = db.userDao()
                val listUser = userData.getAll()
                if(listUser.isNotEmpty())
                {
                    employeeCode = listUser[0].uid
                }

                val dbDevice = Room.databaseBuilder(
                    applicationContext,
                    DeviceDatabase::class.java, dataBaseDevice
                ).build()

                val deviceData = dbDevice.deviceDao()
                val listDevice = deviceData.getAll()
                if(listDevice.isNotEmpty())
                {
                    deviceId = listDevice[0].imei
                }



                val params = JSONObject().apply {
                    put("device_imei", deviceId)
                    put("created_at", now)
                    put("staff_code", employeeCode)
                    put("latitude", latitude)
                    put("longitude", longitude)
                    put("battery", batteryPct)
                    put("status", status)
                    put("speed", speedInKmPerHr)
                    put("created_at", now)
                }
                requestToWebService(trackingData, params)

            }
        }
        deleteData()
        return Result.success()
    }

    private fun connectTrackingDB() = Room.databaseBuilder(
        applicationContext,
        TrackingDatabase::class.java, dataBaseTracking
    ).allowMainThreadQueries().build()


    private fun datetimeFormat() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    private fun requestToWebService(tracking: TrackingData, params: JSONObject) {
        val method = Request.Method.POST
        val request: JsonObjectRequest = object : JsonObjectRequest(method,
            TRACKING_WEBSERVICE_URL, params,
            { updateFlagToDatabase(tracking) },
            {}
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    "Content-Type" to "application/json"
                }
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                if (response != null) {
                    if (response.statusCode == HttpURLConnection.HTTP_CREATED) {
                        if (response.data.isEmpty()) {
                            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response))
                        }
                    }
                }
                return super.parseNetworkResponse(response)
            }
        }
        DefaultBackendVolley.getInstance(applicationContext).addToRequestQueue(request)
    }

    private fun updateFlagToDatabase(trackingData: TrackingData) {
        val now = (System.currentTimeMillis() / 1000L).toInt()
        val trackingDb = connectTrackingDB()
        val trackingDao = trackingDb.trackingDao()

        trackingData.update_at = now
        trackingData.updateFlag = 0

        trackingDao.updateSend(trackingData)
    }

    private fun deleteData()
    {
        val trackingDb = connectTrackingDB()
        val trackingDao = trackingDb.trackingDao()
        trackingDao.deleteSendData()
    }

}