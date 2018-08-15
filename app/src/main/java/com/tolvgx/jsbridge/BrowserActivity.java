package com.tolvgx.jsbridge;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

public class BrowserActivity extends AppCompatActivity {

    private WebView mWebView;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_brower);

        mWebView = (WebView) findViewById(R.id.webview);

        Drawable drawable = getResources().getDrawable(R.drawable.progress_bar_states);
        progressbar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT, 5, 0, 0));
        progressbar.setProgressDrawable(drawable);
        mWebView.addView(progressbar);

        WebSettings settings = mWebView.getSettings();
        // 设置js支持
        settings.setJavaScriptEnabled(true);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        // 设置网页字体不跟随系统字体发生改变
        settings.setTextZoom(100);
        // 缩放至屏幕的大小
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // 是否支持缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        // 不缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //设置https支持http
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("tolvgx://")){
                    //读取到url后通过callJava分析调用
                    JSBridge.callJava(view, url);
                }else{
                    // 当有新连接时，使用当前的 WebView
                    view.loadUrl(url);
                }
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                result.confirm(JSBridge.callJava(view, message));
                return true;
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressbar.setVisibility(GONE);

                } else {
                    if (progressbar.getVisibility() == GONE){
                        progressbar.setVisibility(View.VISIBLE);
                    }
                    progressbar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });


        JSBridge.register("tolvgx_bridge", BridgeImpl.class);

        mWebView.loadUrl("file:///android_asset/index.html");

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("fromNative", "不回调");
                    Callback callback = new Callback(mWebView);
                    JSONObject object = new JSONObject();
                    object.put("handlerName", "testH5Func");
                    object.put("data", data);
                    callback.apply(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject data = new JSONObject();
                try {
                    data.put("fromNative", "回调");
                    Callback callback = new Callback(mWebView);
                    JSONObject object = new JSONObject();
                    object.put("handlerName", "testH5Func");
                    object.put("data", data);
                    object.put("callbackId", getCallbackId());
                    callback.apply(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private double getCallbackId(){
        return Math.floor(Math.random() * (1 << 30));
    }
}
