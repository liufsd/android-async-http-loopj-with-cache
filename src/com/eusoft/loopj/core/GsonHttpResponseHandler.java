package com.eusoft.loopj.core;

/**
 * Created by liupeng on 2/11/15.
 */
public abstract class GsonHttpResponseHandler <JSON_TYPE> extends CacheBaseGsonHttpResponseHandler<JSON_TYPE>{
    public GsonHttpResponseHandler(Class<JSON_TYPE> parClazz) {
        super(parClazz);
    }

    @Override
    public void onCacheSuccess(JSON_TYPE response) {

    }

    @Override
    public void onCacheFail() {
        super.onCacheFail();
    }
}
