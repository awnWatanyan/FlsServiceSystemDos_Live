package com.aeon.mci.syncadapter

import android.content.Context
import com.aeon.flsservicesystem.syncadapter.FakeX509TrustManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class DefaultBackendVolley constructor(context: Context) {
    companion object {
        private const val INITIAL_TIMEOUT_MS: Int = 15 * 1000
        private const val MAX_NUM_RETRIES: Int = 0
        private const val BACKOFF_MULTIPLIER: Float = 0f

        private val TAG = DefaultBackendVolley::class.java.simpleName

        @Volatile
        private var INSTANCE: DefaultBackendVolley? = null

        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: DefaultBackendVolley(context).also {
                        INSTANCE = it
                    }
                }
    }

    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext).also {
            FakeX509TrustManager.allowAllSSL()
        }
    }

    fun <T> addToRequestQueue(request: Request<T>) {
        request.retryPolicy = DefaultRetryPolicy(INITIAL_TIMEOUT_MS, MAX_NUM_RETRIES, BACKOFF_MULTIPLIER)
        requestQueue.add(request)
    }

    fun <T> addToRequestQueue(request: Request<T>, tag: String) {
        //request.tag = if (tag.isEmpty()) TAG else tag
        request.tag = tag.ifEmpty { TAG }
        addToRequestQueue(request)
    }
}