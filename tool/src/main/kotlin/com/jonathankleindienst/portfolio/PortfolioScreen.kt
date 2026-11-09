package com.jonathankleindienst.portfolio

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.thelightphone.sdk.InitialScreen
import com.thelightphone.sdk.LightScreen
import com.thelightphone.sdk.LightViewModel
import com.thelightphone.sdk.SealedLightActivity
import com.thelightphone.sdk.ui.LightBarButton
import com.thelightphone.sdk.ui.LightIcons
import com.thelightphone.sdk.ui.LightTheme
import com.thelightphone.sdk.ui.LightThemeController
import com.thelightphone.sdk.ui.LightThemeTokens
import com.thelightphone.sdk.ui.LightTopBar
import com.thelightphone.sdk.ui.LightTopBarCenter

/** Die einzige Seite, die dieses Tool jemals anzeigt. */
private const val START_URL = "https://kleindienstportfolio.netlify.app/#seite-5"

/** Nur Seiten auf dieser Domain dürfen geöffnet werden. Alles andere wird ignoriert. */
private const val ALLOWED_HOST = "kleindienstportfolio.netlify.app"

private fun isAllowed(uri: Uri?): Boolean =
    uri != null && uri.scheme == "https" && uri.host == ALLOWED_HOST

/**
 * Lässt Navigation nur innerhalb des Portfolios zu. Links zu anderen Webseiten,
 * mailto:, tel: usw. werden still blockiert, damit das Tool kein Browser wird.
 */
private class PortfolioOnlyClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean =
        !isAllowed(request.url)
}

class PortfolioViewModel : LightViewModel<Unit>()

@InitialScreen
class PortfolioScreen(sealedActivity: SealedLightActivity) :
    LightScreen<Unit, PortfolioViewModel>(sealedActivity) {

    override val viewModelClass: Class<PortfolioViewModel>
        get() = PortfolioViewModel::class.java

    override fun createViewModel(): PortfolioViewModel = PortfolioViewModel()

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    override fun Content() {
        val themeColors by LightThemeController.colors.collectAsState()

        LightTheme(colors = themeColors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightThemeTokens.colors.background)
            ) {
                // Schmale LightOS-Leiste: Zurück-Pfeil schließt das Tool.
                LightTopBar(
                    leftButton = LightBarButton.LightIcon(
                        icon = LightIcons.BACK,
                        onClick = { goBack() },
                        contentDescription = "Zurück",
                    ),
                    center = LightTopBarCenter.Text("Portfolio"),
                )

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    allowFileAccess = false
                                    allowContentAccess = false
                                    setSupportMultipleWindows(false)
                                    javaScriptCanOpenWindowsAutomatically = false
                                    mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                }
                                webViewClient = PortfolioOnlyClient()
                                loadUrl(START_URL)
                            }
                        },
                        onRelease = { webView -> webView.destroy() },
                    )
                }
            }
        }
    }
}
