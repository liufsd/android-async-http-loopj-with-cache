package com.eusoft.loopj;

import android.content.Context;
import android.util.Log;

import com.eusoft.loopj.core.CacheAsyncHttpRequest;
import com.eusoft.loopj.core.CacheTextResponseHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by liupeng on 2/10/15.
 */
public class CacheAsyncHttpClient extends AsyncHttpClient {
    public RequestHandle getCache(Context context, String url, RequestParams params,
                                  boolean onlyShowLocalCache,
                                  CacheTextResponseHandler responseHandler) {
        return sendRequest((DefaultHttpClient) getHttpClient(), getHttpContext(),
                new HttpGet(getUrlWithQueryString(isUrlEncodingEnabled(), url, params)), null,
                responseHandler, context, onlyShowLocalCache, url);
    }

    public RequestHandle getCache(Context context, String url, Header[] headers,
                                  RequestParams params, boolean onlyShowLocalCache, CacheTextResponseHandler responseHandler) {
        HttpUriRequest request = new HttpGet(getUrlWithQueryString(isUrlEncodingEnabled(), url, params));
        if (headers != null) request.setHeaders(headers);
        return sendRequest((DefaultHttpClient) getHttpClient(), getHttpContext(),
                request, null,
                responseHandler, context, onlyShowLocalCache, url);
    }


    private Map<Context, List<RequestHandle>> getRequestMap() {
        try {
            Field field = AsyncHttpClient.class.getDeclaredField("requestMap");
            field.setAccessible(true);
            return (Map<Context, List<RequestHandle>>) field.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Puts a new request in queue as a new thread in pool to be executed
     *
     * @param client          HttpClient to be used for request, can differ in single requests
     * @param contentType     MIME body type, for POST and PUT requests, may be null
     * @param context         Context of Android application, to hold the reference of request
     * @param httpContext     HttpContext in which the request will be executed
     * @param responseHandler ResponseHandler or its subclass to put the response into
     * @param uriRequest      instance of HttpUriRequest, which means it must be of HttpDelete,
     *                        HttpPost, HttpGet, HttpPut, etc.
     * @return RequestHandle of future request process
     */
    protected RequestHandle sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest,
                                        String contentType, CacheTextResponseHandler responseHandler, Context context, boolean onlyShowLocalCache, String cacheKey) {
        if (uriRequest == null) {
            throw new IllegalArgumentException("HttpUriRequest must not be null");
        }

        if (responseHandler == null) {
            throw new IllegalArgumentException("ResponseHandler must not be null");
        }

        if (responseHandler.getUseSynchronousMode()) {//&& !responseHandler.getUsePoolThread()
            throw new IllegalArgumentException("Synchronous ResponseHandler used in AsyncHttpClient. You should create your response handler in a looper thread or use SyncHttpClient instead.");
        }

        if (contentType != null) {
            if (uriRequest instanceof HttpEntityEnclosingRequestBase && ((HttpEntityEnclosingRequestBase) uriRequest).getEntity() != null) {
                Log.w(LOG_TAG, "Passed contentType will be ignored because HttpEntity sets content type");
            } else {
                uriRequest.setHeader(HEADER_CONTENT_TYPE, contentType);
            }
        }

        responseHandler.setRequestHeaders(uriRequest.getAllHeaders());
        responseHandler.setRequestURI(uriRequest.getURI());

        CacheAsyncHttpRequest request = newAsyncHttpCacheRequest(client, httpContext, uriRequest, contentType,
                responseHandler, context, onlyShowLocalCache, cacheKey);
        getThreadPool().submit(request);
        RequestHandle requestHandle = new RequestHandle(request);

        if (context != null) {
            // Add request to request map
            Map<Context, List<RequestHandle>> requestMapHolder = getRequestMap();
            List<RequestHandle> requestList = requestMapHolder.get(context);
            synchronized (requestMapHolder) {
                if (requestList == null) {
                    requestList = Collections.synchronizedList(new LinkedList<RequestHandle>());
                    requestMapHolder.put(context, requestList);
                }
            }

            requestList.add(requestHandle);

            Iterator<RequestHandle> iterator = requestList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().shouldBeGarbageCollected()) {
                    iterator.remove();
                }
            }
        }

        return requestHandle;
    }


    protected CacheAsyncHttpRequest newAsyncHttpCacheRequest(DefaultHttpClient client,
                                                             HttpContext httpContext, HttpUriRequest uriRequest,
                                                             String contentType, CacheTextResponseHandler responseHandler, Context context,
                                                             boolean onlyShowLocalCache, String cacheKey) {
        return new CacheAsyncHttpRequest(client, httpContext, uriRequest, responseHandler, onlyShowLocalCache,
                cacheKey);
    }
}
