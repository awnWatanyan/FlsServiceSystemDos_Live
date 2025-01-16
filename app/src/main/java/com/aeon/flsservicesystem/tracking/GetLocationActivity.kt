package com.aeon.flsservicesystem.tracking

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aeon.flsservicesystem.R
import com.aeon.flsservicesystem.tracking.GetLocationService
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

class GetLocationActivity : AppCompatActivity() {
    lateinit var btnBack : Button
    lateinit var textProgress: TextView

    private var uri: Uri? = null

    private lateinit var getLocation : GetLocationService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLocation = GetLocationService(this)
        setContentView(R.layout.activity_get_location)
        textProgress = findViewById(R.id.idTVGetLocationStatus)
        btnBack = findViewById(R.id.btn_back)
        intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.android.chrome"))
        uri = intent.data
        val token = uri!!.getQueryParameter("resultToken")
        var tokenData :String = "";
        if (token != null) {
            tokenData = token
        }
        btnBack.setOnClickListener{
            finish()
        }
        val context: Context = this
        lifecycleScope.launch {
            val locationData =  getLocation.getCurrentLocationData();
            if(locationData != null)
            {

                sendLocationResult(context,locationData.trackingLatitude.toString(),locationData.trackingLongitude.toString(),locationData.trackingBattery.toString(),locationData.trackingSpeed.toString(),
                    tokenData,"200","Success")
            }
            else{
                sendLocationResult(context,"0.0","0.0","0","0",
                    tokenData,"200","Failed")
            }

        }



    }

    private fun sendLocationResult(context: Context,
                                   trackingLatitude: String, trackingLongitude: String,
                                   trackingBattery: String, trackingSpeed: String,
                                   token:String, status:String, msg:String)
    {
        val client = OkHttpClient()
        val httpUrl = HttpUrl.Builder().scheme(com.aeon.flsservicesystem.scheme).host(com.aeon.flsservicesystem.callurl)
            .addPathSegment(com.aeon.flsservicesystem.pathSeqment)
            .addPathSegment("SubmitCollectorResult")

        val requestBody = "{\"status\":\"$status\",\"msg\":\"$msg\",\"resultToken\":\"$token\"," +
                "\"pntToken\":\"\"," +
                "\"trackingLatitude\":\"$trackingLatitude\"," +
                "\"trackingLongitude\":\"$trackingLongitude\"," +
                "\"trackingBattery\":\"$trackingBattery\"," +
                "\"trackingSpeed\":\"$trackingSpeed\"" +
                "}"
        val body = requestBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(httpUrl.build())
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                var status = "Error"
                var msg = e.cause
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    textProgress.text = "$status:$msg"
                    btnBack.visibility = View.VISIBLE;
                    //finish()
                }
            }

            override fun onResponse(call: Call, response: Response) {


                val result = response.body?.string() ?: ""

                var status = ""
                var msg = ""
                var resultJson: JSONObject? = null
                try {
                    resultJson = JSONObject(result)
                } catch (e: Exception) {
                    msg = e.cause.toString()
                }

                if (resultJson != null) {
                    if (resultJson.has("status")) {
                        status = resultJson.get("status").toString()
                    }
                    if (resultJson.has("msg")) {
                        msg = resultJson.get("msg").toString()
                    }
                }
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    Thread.sleep(3000)
                    if (status.equals("200"))
                    {
                        msg = context.getString(R.string.getting_location_finish)
                    }
                    else
                    {
                        msg = context.getString(R.string.getting_location_failed)
                    }

                    textProgress.text = "$msg"
                    btnBack.visibility = View.VISIBLE;
                    finish()
                }



            }
        })
    }
}