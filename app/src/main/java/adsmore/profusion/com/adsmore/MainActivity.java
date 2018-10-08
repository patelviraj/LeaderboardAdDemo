package adsmore.profusion.com.adsmore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

public class MainActivity extends AppCompatActivity {

    protected PublisherAdView adView;
    private WebView webView;
    private RelativeLayout webViewContainer, progressdialog;
    private LinearLayout ll2;
    private ImageButton btnBack;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adView = findViewById(R.id.ad_view);
        webView = findViewById(R.id.web_view);
        webViewContainer = findViewById(R.id.webviewContainer);
        progressdialog = findViewById(R.id.progressdialog);
        ll2 = findViewById(R.id.ll2);
        btnBack = findViewById(R.id.btn_url_menu);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        webView.setScrollBarDefaultDelayBeforeFade(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setEnableSmoothTransition(true);

        Intent intent = getIntent();

        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action) && intent.getDataString() != null) {
            Toast.makeText(MainActivity.this, "onCreate DataURI Before: \n\n" + intent.getDataString(), Toast.LENGTH_LONG).show();

            String dataUri = intent.getDataString() != null ? intent.getDataString().substring(10) : "";
            Toast.makeText(MainActivity.this, "onCreate DataURI After: \n\n" + dataUri, Toast.LENGTH_LONG).show();

            if (!dataUri.isEmpty()) {
                Log.e("AdvertisementActivity", "Load Ad in Leaderboard " + dataUri);
                // Here received URL is loading in the WebView
                loadUrl(dataUri);
            }
        } else {
            Log.e("AdvertisementActivity", "Continue loading Advertisement Activity");
        }

        setupLeaderboardAd();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll2.setVisibility(View.VISIBLE);
                webViewContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    private void setupLeaderboardAd() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("AdListener", "onAdLoaded: AdLoading Finished");
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("AdListener", "onAdFailedToLoad: AdLoading Failed. ErrorCode: " + errorCode);
                adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
//                this.openInAppBrowser();
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.e("AdListener", "onAdOpened()");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.e("AdListener", "onAdLeftApplication()");
            }

            /*private void openInAppBrowser() {
                Intent intent1 = new Intent();
                intent1.setData(Uri.parse("grabdfp://http://adsmore.co"));
                startActivity(intent1);
            }*/

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.e("AdListener", "onAdClosed()");
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("New Intent", "called");
        String action = intent.getAction();
        if (intent.getDataString() != null) {
            Toast.makeText(MainActivity.this, "onNewIntent DataURI Before: \n\n" + intent.getDataString(), Toast.LENGTH_LONG).show();
            String dataUri = intent.getDataString() != null ? intent.getDataString().substring(10) : "";
            Toast.makeText(MainActivity.this, "onNewIntent DataURI After: \n\n" + dataUri, Toast.LENGTH_LONG).show();
            if (!dataUri.isEmpty()) {
                Log.e("MainActivity New Intent", "Load Ad in Leaderboard " + dataUri);
                loadUrl(dataUri);
            }
        }
    }

    private void loadUrl(String url) {
        shrinkView();
        if (!url.contains("http")) {
            url = "http://" + url;
        }
        webView.loadUrl(url);
    }

    private void shrinkView() {
        ll2.setVisibility(View.GONE);
        webViewContainer.setVisibility(View.VISIBLE);
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressdialog.setVisibility(View.VISIBLE);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressdialog.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            //Toast.makeText(AdvertisementActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
        }
    }


    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            Log.i("WebViewProgress", String.valueOf(newProgress));
            if (newProgress >= 80) {
                progressdialog.setVisibility(View.GONE);
            }
        }
    }
}