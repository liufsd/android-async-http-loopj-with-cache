package com.eusoft.loopj.core;

import android.text.TextUtils;
import android.util.Log;


import com.google.gson.*;
import org.apache.http.Header;
import org.apache.http.HttpStatus;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by liupeng on 1/7/15.
 */
public abstract class CacheBaseGsonHttpResponseHandler<JSON_TYPE> extends CacheBaseJsonHttpResponseHandler<JSON_TYPE> {
    private final static boolean DEBUG = false;
    private final Class<JSON_TYPE> clazz;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
            String dateString = json.getAsString().replace("Z", "+0000");
            return DATE_FORMAT.parse(dateString, new ParsePosition(0));
        }
    }).create();

    public CacheBaseGsonHttpResponseHandler(Class<JSON_TYPE> parClazz) {
        this.clazz = parClazz;
    }

    @Override
    public void onSuccess(int i, Header[] headers, String s, JSON_TYPE json_type) {
        onGsonSuccess(json_type);
    }

    @Override
    public void onFailure(int i, Header[] headers, Throwable throwable, String s, JSON_TYPE json_type) {
        onGsonFail(i);
        try {
            if (i == HttpStatus.SC_UNAUTHORIZED) {
                //try login
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSON_TYPE parseResponse(String s, boolean b) throws Throwable {
        try {
            if (TextUtils.isEmpty(s)) {
                return null;
            }
            if (DEBUG) {
                Log.e("TAG", s);
            }
            return GSON.fromJson(s, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCacheFail() {
        if (DEBUG) {
            Log.e("TAG", "onCacheFail");
        }
    }

    abstract public void onGsonSuccess(JSON_TYPE result);

    abstract public void onGsonFail(int statusCode);
}
