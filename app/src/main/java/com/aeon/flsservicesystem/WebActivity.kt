package com.aeon.flsservicesystem

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class WebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val myWebView: WebView = findViewById(R.id.webview)


        // Enable JavaScript
        val webSettings: WebSettings = myWebView.settings
        webSettings.javaScriptEnabled = true

        // Enable Zoom controls
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        // Enable DOM Storage
        webSettings.domStorageEnabled = true

        // Load the URL passed from MainActivity
        val url = intent.getStringExtra("URL")
        if (url != null) {
            myWebView.loadUrl(url)
        }
    }
}