# android-async-http-loopj-with-cache

#Easy to use
###1.create client
````
       asyncHttpClient = new CacheAsyncHttpClient();
        asyncHttpClient.setTimeout(30000);
        asyncHttpClient.setConnectTimeout(50000);
        asyncHttpClient.setEnableRedirects(true);
````
###2.getResponseCache
```
  private RequestHandle getResponseCache(Context context, String url, boolean findCacheBreak, CacheBaseGsonHttpResponseHandler responseHandler) {
        return asyncHttpClient.getCache(context, url, null, null, findCacheBreak, responseHandler);
    }
```
###3.do some request

```
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

```
