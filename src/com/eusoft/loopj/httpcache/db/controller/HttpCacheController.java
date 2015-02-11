package com.eusoft.loopj.httpcache.db.controller;

import com.eusoft.utils.Lists;
import com.eusoft.loopj.httpcache.db.DBController;
import com.eusoft.loopj.httpcache.db.DBNotInitializeException;
import com.eusoft.loopj.httpcache.entities.HttpCacheEntity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by liupeng on 9/9/14.
 */
public class HttpCacheController {
    private static final int EXPIRED_DAYS = 3;//Get three's date using Date default
    private static Dao<HttpCacheEntity, String> getDao() throws SQLException, DBNotInitializeException {
        return DBController.getDB().getDao(HttpCacheEntity.class);
    }

    public static byte[]  getCacheByUrl(String url) {
        try {
            List<HttpCacheEntity> result = getDao().queryForEq(HttpCacheEntity.TARGET_URL, url);
            if (Lists.isValidate(result)) {
                HttpCacheEntity entity = result.get(0);
                if (entity != null) {
                    entity.setUpdateTime(System.currentTimeMillis());
                    addOrUpdate(entity);
                    return entity.getJson();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addOrUpdate(HttpCacheEntity entity) {
        try {
            getDao().createOrUpdate(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
    }

    public static void delete(List<HttpCacheEntity> cacheEntities) {
        try {
            DatabaseConnection connection = getDao().startThreadConnection();
            getDao().delete(cacheEntities);
            getDao().endThreadConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
    }

    /**
     * clear local expired cache
     *
     * @param dur dur days
     */
    public static void clearExpired(int dur) {
        clearExpired(getExpiredTime(dur));
    }

    /**
     * get expired time
     *
     * @return date
     */
    private static Date getExpiredTime(int space) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -space);
        return cal.getTime();
    }

    /**
     * support delete expired row
     *
     * @param date expired date
     */
    public static void clearExpired(Date date) {
        try {
            List<HttpCacheEntity> resultList = Lists.newArrayList();
            QueryBuilder<HttpCacheEntity, String> queryBuilder = getDao().queryBuilder();
            Where<HttpCacheEntity, String> wheres = queryBuilder.where();
            wheres.le(HttpCacheEntity.UPDATETIME, date.getTime());
            resultList = getDao().query(queryBuilder.prepare());
            if (Lists.isValidate(resultList)) {
                delete(resultList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBNotInitializeException e) {
            e.printStackTrace();
        }
    }

    /**
     * add default clear cache method (3 days)
     */
    public static void clearLocalCache(){
        try {
            // Clear out expired cookies
            clearExpired(EXPIRED_DAYS);// clear before three days data
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
