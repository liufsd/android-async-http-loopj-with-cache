package com.eusoft.loopj.core;

import android.os.Message;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fei-ke on 2015/1/20.
 */
public abstract class CacheTextResponseHandler extends TextHttpResponseHandler {
    protected static final int CACHE_MESSAGE = 7;

    public void sendCacheMessage(byte[] cache) {
        sendMessage(obtainMessage(CACHE_MESSAGE, new Object[]{cache}));
    }

    /**
     * 当缓存被找到时
     *
     * @param cache
     */
    public void onCache(byte[] cache) {
        onCache(getResponseString(cache, getCharset()));
    }

    public abstract void onCache(String cache);

    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        if (message.what == CACHE_MESSAGE) {
            Object[] response = (Object[]) message.obj;
            onCache((byte[]) response[0]);
        }
    }

    public byte[] sendResponseMessageWithCache(HttpResponse response) throws IOException {
        // do not process if request has been cancelled
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine status = response.getStatusLine();
            byte[] responseBody;
            responseBody = getResponseData(response.getEntity());
            // additional cancellation check as getResponseData() can take non-zero time to process
            if (!Thread.currentThread().isInterrupted()) {
                if (status.getStatusCode() >= 300) {
                    sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), responseBody, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
                } else {
                    sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), responseBody);
                    return responseBody;
                }
            }
        }
        return null;
    }

    /**
     * Returns byte array of response HttpEntity contents
     *
     * @param entity can be null
     * @return response entity body or null
     * @throws java.io.IOException if reading entity or creating byte array failed
     */
    byte[] getResponseData(HttpEntity entity) throws IOException {
        byte[] responseBody = null;
        if (entity != null) {
            InputStream instream = entity.getContent();
            if (instream != null) {
                long contentLength = entity.getContentLength();
                if (contentLength > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }
                int buffersize = (contentLength <= 0) ? BUFFER_SIZE : (int) contentLength;
                try {
                    ByteArrayBuffer buffer = new ByteArrayBuffer(buffersize);
                    try {
                        byte[] tmp = new byte[BUFFER_SIZE];
                        int l, count = 0;
                        // do not send messages if request has been cancelled
                        while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                            count += l;
                            buffer.append(tmp, 0, l);
                            sendProgressMessage(count, (int) (contentLength <= 0 ? 1 : contentLength));
                        }
                    } finally {
                        AsyncHttpClient.silentCloseInputStream(instream);
                        AsyncHttpClient.endEntityViaReflection(entity);
                    }
                    responseBody = buffer.toByteArray();
                } catch (OutOfMemoryError e) {
                    System.gc();
                    throw new IOException("File too large to fit into available memory");
                }
            }
        }
        return responseBody;
    }
}
