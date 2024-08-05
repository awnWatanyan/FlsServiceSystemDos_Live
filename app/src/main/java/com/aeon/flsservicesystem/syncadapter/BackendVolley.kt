package com.aeon.mci.syncadapter

import android.content.Context
import com.aeon.flsservicesystem.syncadapter.FakeX509TrustManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object BackendVolley {

    private val TAG = BackendVolley::class.java.simpleName

    private const val INITIAL_TIMEOUT_MS: Int = 15 * 1000

    private const val MAX_NUM_RETRIES: Int = 0

    private const val BACKOFF_MULTIPLIER: Float = 0f

    private var requestQueue: RequestQueue? = null

    fun init(context: Context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.applicationContext)
        }
        FakeX509TrustManager.allowAllSSL()
    }

    fun <T> addToRequestQueue(request: Request<T>, tag: String) {
        request.apply {
            this.tag = if (tag.isEmpty()) TAG else tag
            retryPolicy = DefaultRetryPolicy(INITIAL_TIMEOUT_MS, MAX_NUM_RETRIES, BACKOFF_MULTIPLIER)
        }
        requestQueue?.add(request)
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        request.tag = TAG
        requestQueue?.add(request)
    }

    fun cancelPendingRequests(tag: Any) {
        if (requestQueue != null) {
            requestQueue!!.cancelAll(tag)
        }
    }
}