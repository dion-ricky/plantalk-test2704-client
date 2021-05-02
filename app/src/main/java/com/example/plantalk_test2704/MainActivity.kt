package com.example.plantalk_test2704

import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {
    lateinit var webView : WebView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this);
        webView.settings.javaScriptEnabled = true;

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

        setContentView(webView);

        webView.loadUrl(PlantalkConfig.BASE_URL);
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
}