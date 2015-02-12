package com.eusoft.loopj.httpcache.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by liupeng on 9/9/14.
 */
@DatabaseTable(tableName = "cache")
public class HttpCacheEntity {
    public static final String TARGET_URL = "url";
    public static final String UPDATETIME = "updateTime";
    HttpCacheEntity(){

    }
   public HttpCacheEntity(String _url,byte[]  _json) {
        this.url = _url;
        this.json = _json;
        this.updateTime = System.currentTimeMillis();
    }
    @DatabaseField(id = true, columnName = TARGET_URL)
    private String url;
    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] json;
    @DatabaseField(columnName = UPDATETIME)
    private long updateTime;
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[]  getJson() {
        return json;
    }

    public void setJson(byte[]  json) {
        this.json = json;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    @Override
    public String toString() {
        return " url: " + url + " json: " + json;
    }
}
