package com.eusoft.loopj.httpcache.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eusoft.loopj.httpcache.entities.HttpCacheEntity;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.android.DatabaseTableConfigUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


/**
 * Created by liupeng on 9/5/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    protected AndroidConnectionSource connectionSource = new AndroidConnectionSource(this);

    public DatabaseHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
    }

    /**
     * Robert added :This is called when the database is first created. Usually
     * you should call createTable statements here to create the tables that
     * will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        DatabaseConnection conn = connectionSource.getSpecialConnection();
        boolean clearSpecial = false;
        if (conn == null) {
            conn = new AndroidDatabaseConnection(db, true);
            try {
                connectionSource.saveSpecialConnection(conn);
                clearSpecial = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Could not save special connection", e);
            }
        }
        try {
            onCreate();
        } catch (DBNotInitializeException e) {
            Log.e(DatabaseHelper.class.getName(), "DBNotInitializeException", e);
        } finally {
            if (clearSpecial) {
                connectionSource.clearSpecialConnection(conn);
            }
        }
    }

    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DatabaseConnection conn = connectionSource.getSpecialConnection();
        boolean clearSpecial = false;
        if (conn == null) {
            conn = new AndroidDatabaseConnection(db, true);
            try {
                connectionSource.saveSpecialConnection(conn);
                clearSpecial = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Could not save special connection", e);
            }
        }
        try {
            onUpgrade(oldVersion, newVersion);
        } catch (DBNotInitializeException e) {
            Log.e(DatabaseHelper.class.getName(), "DBNotInitializeException", e);
        } finally {
            if (clearSpecial) {
                connectionSource.clearSpecialConnection(conn);
            }
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
    }

    /**
     * This is called when your application is upgraded and it has a higher
     * version number. This allows you to adjust the various data to match the
     * new version number.
     *
     * @throws DBNotInitializeException
     */
    private void onUpgrade(int oldVersion, int newVersion) throws DBNotInitializeException {
        Log.i(DatabaseHelper.class.getName(), "onUpgrade");
        clearAllData(HttpCacheEntity.class);
    }



    private void onCreate() throws DBNotInitializeException {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, HttpCacheEntity.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new DBNotInitializeException("Can't create database");
        }
    }

    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
        // lookup the dao, possibly invoking the cached database config
        Dao<T, ?> dao = DaoManager.lookupDao(connectionSource, clazz);
        if (dao == null) {
            // try to use our new reflection magic
            DatabaseTableConfig<T> tableConfig = DatabaseTableConfigUtil.fromClass(connectionSource, clazz);
            if (tableConfig == null) {
                /**
                 * TODO: we have to do this to get to see if they are using the
                 * deprecated annotations like {@link DatabaseFieldSimple}.
                 */
                dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, clazz);
            } else {
                dao = (Dao<T, ?>) DaoManager.createDao(connectionSource, tableConfig);
            }
        }
        @SuppressWarnings("unchecked")
        D castDao = (D) dao;
        return castDao;
    }

    public void clearAllData(Class<?> entity) {
        try {
            TableUtils.dropTable(connectionSource, entity, true);
            onCreate();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}