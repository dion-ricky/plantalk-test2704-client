package com.example.plantalk_test2704

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity() {
    companion object {
        const val RC_CAMERA = 1
    }

    lateinit var webView : WebView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this);
        webView.settings.javaScriptEnabled = true;
        webView.settings.mediaPlaybackRequiresUserGesture = false;

        requestPermission();

        // Start browser if accessing unknown URL
        webView.webViewClient = object : WebViewClient() {
            @Override
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (Uri.parse(url).host == Uri.parse(PlantalkConfig.BASE_URL).host) {
                    return false
                }
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    startActivity(this)
                }
                return true;
            }

            @Override
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                when(error?.primaryError) {
                    SslError.SSL_UNTRUSTED -> Log.d("SSL_ERROR", "SSL Untrusted")
                    SslError.SSL_INVALID -> Log.d("SSL_ERROR", "SSL Invalid")
                }
//                handler?.proceed() //Ignore SSL Cert error
            }
        };

        webView.webChromeClient = MyWebChromeClient()

        setContentView(webView);

        webView.loadUrl(PlantalkConfig.BASE_URL);

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if (!it.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", it.exception)
                return@OnCompleteListener
            }

            val token = it.result
            Log.d("MainActivity", token!!)
        })
    }

    @AfterPermissionGranted(RC_CAMERA)
    private fun requestPermission() {
        val cameraPerms = Manifest.permission.CAMERA
        val micPerms = Manifest.permission.RECORD_AUDIO

        if (EasyPermissions.hasPermissions(this, cameraPerms, micPerms)) {

        } else {
            EasyPermissions.requestPermissions(this,
                    "Allow camera", RC_CAMERA, cameraPerms, micPerms)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    class MyWebChromeClient : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                request?.grant(request.resources)
            }
        }
    }
}