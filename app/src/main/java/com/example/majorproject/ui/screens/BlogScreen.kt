package com.example.majorproject.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OpenUrlScreen(navController: NavController) {
    val url = "https://dayam8696.github.io/Information_on_Disease/"

    // Remember the WebView instance to control it for back navigation
    var webView by remember { mutableStateOf<WebView?>(null) }

    // Handle back press in a composable context
    BackHandler(enabled = webView != null) {
        webView?.let { wv ->
            if (wv.canGoBack()) {
                wv.goBack()
            } else {
                navController.popBackStack()
            }
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // Configure WebView settings
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true

                // Set a WebViewClient to handle page navigation within the WebView
                webViewClient = WebViewClient()

                // Load the URL
                loadUrl(url)

                // Store the WebView instance
                webView = this
            }
        },
        update = { wv ->
            // Update the WebView reference if needed
            webView = wv
        }
    )
}