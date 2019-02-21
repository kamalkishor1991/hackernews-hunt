package com.hnhunt.hnhuntv2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class WebViewFragment extends Fragment {
    private View rootView;
    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         rootView = inflater.inflate(R.layout.web_view, container, false);

        return rootView;
    }

    public boolean canGoBack() {
        // System.out.println("Can go back: " + webView.canGoBack() + "," + webView.getUrl() + "," + webView.getOriginalUrl() + "," +  webView.copyBackForwardList().getSize());
        return webView.copyBackForwardList().getSize() > 2;
    }
    public void setUrl(final String url) {
        webView = rootView.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        try {
            ((TextView)rootView.findViewById(R.id.host)).setText(new URL(url).getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String u) {
                if (u.equals("about:blank")) {
                    webView.clearHistory(); // TODO: this does not work
                    webView.loadUrl(url);
                    super.onPageFinished(view, u);
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        progress/100.0f == 1 ? 0 : progress/100.0f
                );
                rootView.findViewById(R.id.loader).setLayoutParams(param);
                param = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1 - progress/100.0f == 0 ? 1 : 1 - progress/100.0f
                );
                rootView.findViewById(R.id.unloader).setLayoutParams(param);
            }

        });
        webView.loadUrl("about:blank");


    }
    public void goBack() {
        webView.goBack();
    }
}