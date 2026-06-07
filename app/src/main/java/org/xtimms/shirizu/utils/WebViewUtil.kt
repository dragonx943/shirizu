package org.xtimms.shirizu.utils

import android.content.Context
import androidx.webkit.WebViewCompat

object WebViewUtil {

    fun getVersion(context: Context): String {
        val webView = WebViewCompat.getCurrentWebViewPackage(context) ?: return "o_O"
        val pm = context.packageManager
        val label = webView.applicationInfo?.loadLabel(pm) ?: "WebView"
        val version = webView.versionName
        return "$label $version"
    }

}