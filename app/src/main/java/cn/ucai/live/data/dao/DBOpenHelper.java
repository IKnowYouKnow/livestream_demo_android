package cn.ucai.live.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    private static DBOpenHelper instance;
    private static String DB_NAME = "db_gift";

    private static String CREATE_GIFT_TABLE = "create table "+ GiftDao.TAB_NAME
            +" ("+ GiftDao.COLUMN_ID +" integer primary key,"
            + GiftDao.COLUMN_GNAME+" TEXT ,"
            + GiftDao.COLUMN_GURL + " TEXT,"
            + GiftDao.COLUMN_GPRICE+ "integer"
            +");";
    public static DBOpenHelper getInstance(Context context){
        if (instance == null) {
            instance = new DBOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GIFT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void closeDb(){
        if (instance != null) {
            SQLiteDatabase db = instance.getWritableDatabase();
            db.close();
        }
        instance = null;
    }
}
