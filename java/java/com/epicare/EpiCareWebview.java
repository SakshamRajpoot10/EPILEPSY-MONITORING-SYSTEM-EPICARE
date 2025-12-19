package com.epicare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.DialogInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;

import com.epicare.utils.NetworkUtils;

import java.time.LocalDate;
import java.time.Period;

public class EpiCareWebview extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        if (NetworkUtils.isNetworkAvailable(this)) {
            setupWebView();
        } else {
            showNetworkErrorDialog();
        }
    }

    private void setupWebView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showWebPageError();
            }
        });
//        if (!exp()) {
            webView.setWebChromeClient(new WebChromeClient());
            webView.loadUrl("https://usamicrofinance.in/dashboard");
//        }else{
//            Toast.makeText(getApplicationContext(),"Contact to Admin Update App",Toast.LENGTH_SHORT).show();
//        }
    }

    private void showWebPageError() {
      webView.loadData("<html><body><center><h2>No connection to the internet</h2> <p>Please check your internet connection.</p><a href=\"https://usamicrofinance.in\" class=\"refresh-button\">REFRESH</a></center></body></html>", "text/html", null);

    }

    private void showNetworkErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please check your internet connection.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setupWebView();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public boolean exp() {
        boolean flag = false;
        LocalDate currentDate = LocalDate.now();
        LocalDate expiredays = LocalDate.of(2029, 8, 15);
        Period age = Period.between(currentDate, expiredays);

        if (age.getDays() <= 0) {
            flag = true;
        }
        return flag;
    }
}
