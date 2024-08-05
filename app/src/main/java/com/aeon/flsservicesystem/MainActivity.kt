package com.aeon.flsservicesystem

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.aeon.flsservicesystem.databinding.ActivityMainBinding
import com.aeon.flsservicesystem.tracking.TrackingBroadcastReceiver
import com.aeon.flsservicesystem.tracking.TrackingUpdateService
import com.aeon.flsservicesystem.user_manager.DeviceData
import com.aeon.flsservicesystem.user_manager.DeviceDatabase
import com.aeon.flsservicesystem.user_manager.User
import com.aeon.flsservicesystem.user_manager.UserDatabase
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    private val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 456
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private lateinit var binding: ActivityMainBinding

    private val dataBaseUser = "database-user"
    private val dataBaseDevice = "database-device"
    private val packageName = "com.android.chrome"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://$packageName"))
        intent.setPackage(packageName)
        enableEdgeToEdge()


        setContentView(R.layout.activity_main)

        val imeiEditText : EditText = findViewById(R.id.login_edittext_imei)
        val scrollView : ScrollView = findViewById(R.id.login_scrollview)

        // Request focus programmatically
        imeiEditText.requestFocus()
        imeiEditText.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(imeiEditText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)

        // Adjusting for keyboard with ViewTreeObserver
        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                scrollView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = scrollView.rootView.height

                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is open
                    val focusedView = currentFocus
                    focusedView?.let {
                        scrollView.post {
                            scrollView.smoothScrollTo(0, it.bottom)
                        }
                    }
                }
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        checkAndRequestLocationPermissions()

        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()

        if (Prefs.getBoolean(PREFS_KEY_IS_LOGIN, false)) {
            val token = getToken()
            if (token.isNotEmpty()) {
                checkToken(token)
            }

        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setupWidget()
        //scheduleAlarm()
    }

    private fun setupWidget() {

//For test
        /*
        binding.loginEdittextUsername.editText?.setText("19998")
        binding.loginEdittextPassword.editText?.setText("aeon*123")
        binding.loginEdittextImei.editText?.setText("357612101381136")
        */
        binding.loginButtonLogin.setOnClickListener {
            validate()
        }

        binding.loginScrollview.apply {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
        }

        //get default imei
        var imei = getImei()
        binding.loginEdittextImei.setText(imei)

    }

    private fun validate() {
        val username = binding.loginEdittextUsername.editText?.text.toString()
        val password = binding.loginEdittextPassword.editText?.text.toString()
        val imei = binding.loginEdittextImei.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            showToast("Username or Password is Empty")
            return
        }


        Prefs.putString(PREFS_KEY_USERNAME, username)
        Prefs.putString(PREFS_KEY_IMEI, imei)
        val md = MessageDigest.getInstance("SHA-256")
        val byteData = md.digest(password.toByteArray())

        fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

        val passwordEncode = byteData.toHexString()

        showProgress(true)

        requestLogin(username, passwordEncode, imei)

    }

    @SuppressLint("InvalidPeriodicWorkRequestInterval")
    private fun checkAndRequestLocationPermissions() {
        if (checkLocationPermission()) {
            if (checkBackGroundLocationPermission()) {
                val intent = Intent(this, TrackingUpdateService::class.java)
                startService(intent)
                scheduleAlarm()
            } else {
                requestBackGroundLocationPermission()
                finish()
            }

        } else {
            requestLocationPermission()
        }
    }

    private fun checkBackGroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        } else {
            return true
        }

    }

    private fun scheduleAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, TrackingBroadcastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 1 * 60 * 1000, // 1 minute from now
            5 * 60 * 1000, // 5 minute interval
            pendingIntent
        )
    }

    private fun requestBackGroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    private fun requestLogin(user: String, pass: String, imei: String) {
        val client = OkHttpClient()


        val httpUrl = HttpUrl.Builder().scheme(scheme).host(callurl)
            .addPathSegment(pathSeqment)
            .addPathSegment("TabletLogin")

        val requestBody = "{\"usr\":\"$user\",\"pwd\":\"$pass\",\"imei\":\"$imei\"}"
        val body = requestBody.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(httpUrl.build())
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
// Handle failure
                Prefs.putBoolean(PREFS_KEY_IS_LOGIN, false)
                showProgress(false)
                showToastResult("Error:Connection Cannot Connect")
            }

            override fun onResponse(call: Call, response: Response) {
                showProgress(false)
// Handle success
                val result = response.body?.string() ?: ""
// Process the response data

//insert user to db
                val resultJson = JSONObject(result)
                val status = resultJson.get("status").toString()
                var token = ""
                var msg = ""
                if (resultJson.has("token")) {
                    token = resultJson.get("token").toString()
                }
                if (resultJson.has("msg")) {
                    msg = resultJson.get("msg").toString()
                }
                if (status == "200" && token.isNotEmpty()) {
                    val db = connectDb()
                    val dbDevice = connectDbDevice()
                    var userData = User(user, imei, token)
                    var deviceData = DeviceData(imei)
                    val userDao = db.userDao()
                    val userDevice = dbDevice.deviceDao()
                    userDao.deleteAll()
                    userDao.insertAll(userData)
                    userDevice.deleteAll()
                    userDevice.insertAll(deviceData)
                    Prefs.putBoolean(PREFS_KEY_IS_LOGIN, true)
//call login by token
                    redirectToLogin(token)
                    finish()
                } else {

                    clearUserData()
                    Prefs.putBoolean(PREFS_KEY_IS_LOGIN, false)
                    showToastResult("$msg")
                }
            }
        })
    }

    private fun checkToken(token: String) {
        val client = OkHttpClient()
        val httpUrl = HttpUrl.Builder().scheme(scheme).host(callurl)
            .addPathSegment(pathSeqment)
            .addPathSegment("JwtToken")

        val requestBody = "{\"token\":\"$token\"}"
        val body = requestBody.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(httpUrl.build())
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
// Go to login page
                Prefs.putBoolean(PREFS_KEY_IS_LOGIN, false)
                showToastResult("Error:Connection Cannot Connect")
            }

            override fun onResponse(call: Call, response: Response) {

// Handle success
                val result = response.body?.string() ?: ""
// Process the response data
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
                if (status == "200") {
//call login by token
                    redirectToLogin(token)
                    finish()
                } else {
                    clearUserData()
                    Prefs.putBoolean(PREFS_KEY_IS_LOGIN, false)

                    showToastResult("$msg")
                }


            }
        })
    }

    private fun getToken(): String {
        var token = ""
        val db = connectDb()
        val userData = db.userDao()
        val listUser = userData.getAll()

        if (listUser.isNotEmpty()) {
            val user = listUser[0]
            token = user.token.toString()
        }

        return token

    }

    private fun clearUserData() {
        val db = connectDb()
        val userDao = db.userDao()
        userDao.deleteAll()
    }

    private fun redirectToLogin(token: String) {
        val httpUrl = HttpUrl.Builder().scheme(scheme).host(callurl)
            .addPathSegment(pathSeqment)
            .addPathSegment("Login")
            .addQueryParameter("token", token)

        val builder = CustomTabsIntent.Builder()
        val params = CustomTabColorSchemeParams.Builder()
        params.setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.white))
       builder.setShareState(CustomTabsIntent.SHARE_STATE_OFF)
        builder.setDefaultColorSchemeParams(params.build())
        builder.setShowTitle(false)
        val menuItemIntent = Intent(this, CustomTabMenuActivity::class.java)
        builder.setInstantAppsEnabled(true)
        val pendingIntent = PendingIntent.getActivity(this, 0, menuItemIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        //builder.addMenuItem("Custom Menu Item", pendingIntent)


        val customBuilder = builder.build()

        if (this.isPackageInstalled(packageName)) {
// if chrome is available use chrome custom tabs
           // customBuilder.intent.setPackage(packageName)
            customBuilder.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            customBuilder.launchUrl(this, Uri.parse(httpUrl.build().toString()))
        } else {
// if not available use WebView to launch the url
            val browserIntent =
                Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
            browserIntent.setData(Uri.parse(httpUrl.build().toString()))
            startActivity(browserIntent)


        }/**/

       /* val intent = Intent(this, WebActivity::class.java)
        // Pass the URL as an intent extra
        intent.putExtra("URL", Uri.parse(httpUrl.build().toString()).toString())
        // Start the activity
        startActivity(intent)*/



    }


    fun Context.isPackageInstalled(packageName: String): Boolean {
// check if chrome is installed or not
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun getImei() : String
    {
        var imei = ""
        val db = connectDbDevice()
        val userDevice = db.deviceDao()
        val listDevice = userDevice.getAll()

        if (listDevice.isNotEmpty()) {
            val device = listDevice[0]
            imei = device.imei.toString()
        }

        return imei

    }

    private fun connectDb() = Room.databaseBuilder(
        applicationContext,
        UserDatabase::class.java, dataBaseUser
    ).allowMainThreadQueries().build()

    private fun connectDbDevice() = Room.databaseBuilder(
        applicationContext,
        DeviceDatabase::class.java, dataBaseDevice
    ).allowMainThreadQueries().build()

    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        runOnUiThread {
// Stuff that updates the UI
            binding.loginButtonLogin.apply {
                visibility = if (show) View.GONE else View.VISIBLE
                animate().run {
                    duration = shortAnimTime.toLong()
                    alpha = if (show) 0f else 1f
                    setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            this@apply.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })
                }
            }

            binding.loginProgress.apply {
                visibility = if (show) View.VISIBLE else View.GONE
                animate().run {
                    duration = shortAnimTime.toLong()
                    alpha = if (show) 1f else 0f
                    setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            this@apply.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
                }
            }
        }

    }

    private fun showToastResult(text: String) {
        runOnUiThread {
            showToast(text)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                restartApp()
                // Permission granted, proceed with your task
            } else {
                // Permission denied, you can handle it or show a message
            }
            if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAndRequestLocationPermissions()
                }
            }

        }
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        Runtime.getRuntime().exit(0) // Ensure the app is fully restarted
    }


}