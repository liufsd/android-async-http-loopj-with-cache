package com.eusoft.loopj.httpcache.db;


import com.eusoft.utils.JniApi;

/**
 * Created by liupeng on 9/5/14.
 */
public class DBController {

    private static final int DB_VERSION =1;
    private static DatabaseHelper db = null;
    private static final String DB_NAME="cache.db";

    public static void createDB(){
        initialDB();
    }

    private static synchronized void initialDB(){
        if(db==null){
            db=new DatabaseHelper(JniApi.appcontext,DB_NAME, null, DB_VERSION);
        }
    }

    public static DatabaseHelper getDB() throws DBNotInitializeException {
        initialDB();
        if (db == null) {
            throw new DBNotInitializeException("DB not created.");
        }
        return db;
    }


    public static synchronized void destoryDB() {
        if (db != null) {
            db.close();
//			OpenHelperManager.releaseHelper();
            db = null;
        }
    }

    public static synchronized void clearAllData(Class<?> entity){
        if (db != null) {
            db.clearAllData(entity);
        }
    }
}
