package com.eusoft;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.eusoft.loopj.CacheAsyncHttpClient;
import com.eusoft.loopj.core.CacheBaseGsonHttpResponseHandler;
import com.eusoft.utils.JniApi;
import com.eusoft.R;
import com.loopj.android.http.RequestHandle;

public class MyActivity extends Activity {
    private class Entity{
        private String somePro;
    }
    private CacheAsyncHttpClient asyncHttpClient;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JniApi.appcontext = this;
        setContentView(R.layout.activity_main);
        initClient();
        demoRequest("you url here");
    }

    /**
     * init client
     */
    private void initClient() {
        asyncHttpClient = new CacheAsyncHttpClient();
        asyncHttpClient.setTimeout(30000);
        asyncHttpClient.setConnectTimeout(50000);
        asyncHttpClient.setEnableRedirects(true);
    }


    /**
     * do request some url data
     * @param targetUrl url
     */
    private void demoRequest(String targetUrl) {
        getResponseCache(this, targetUrl, false, new CacheBaseGsonHttpResponseHandler<Entity>(Entity.class) {
            @Override
            public void onCacheSuccess(Entity response) {

            }

            @Override
            public void onGsonSuccess(Entity result) {

            }

            @Override
            public void onGsonFail(int statusCode) {

            }
        });

    }

    /**
     * get response
     *
     * @param url             target url
     * @param responseHandler callback
     */
    private RequestHandle getResponseCache(Context context, String url, boolean findCacheBreak, CacheBaseGsonHttpResponseHandler responseHandler) {
        return asyncHttpClient.getCache(context, url, null, null, findCacheBreak, responseHandler);
    }
}
