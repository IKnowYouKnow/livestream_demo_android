package cn.ucai.live.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.live.LiveApplication;
import cn.ucai.live.data.model.Gift;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class LiveDbManager {
    private static LiveDbManager dbManager = new LiveDbManager();
    private DBOpenHelper mOpenHelper;

    private LiveDbManager(){
        mOpenHelper = DBOpenHelper.getInstance(LiveApplication.getInstance().getApplicationContext());
    }

    public static synchronized LiveDbManager getInstance(){
        if (dbManager == null) {
            dbManager = new LiveDbManager();
        }
        return dbManager;
    }

    public synchronized void saveGifts(List<Gift> gifts) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(GiftDao.TAB_NAME, null, null);
            for (Gift gift : gifts) {
                ContentValues values = new ContentValues();
                values.put(GiftDao.COLUMN_ID,gift.getId());
                values.put(GiftDao.COLUMN_GNAME,gift.getGname());
                values.put(GiftDao.COLUMN_GURL,gift.getGurl());
                values.put(GiftDao.COLUMN_GPRICE,gift.getGprice());
                db.replace(GiftDao.TAB_NAME, null, values);
            }
        }
    }

    public synchronized Map<Integer,Gift> getGifts(){
        Map<Integer, Gift> giftMap = new HashMap<>();
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + GiftDao.TAB_NAME, null);
            while (cursor.moveToNext()) {
                Gift gift = new Gift();
                gift.setId(cursor.getInt(cursor.getColumnIndex(GiftDao.COLUMN_ID)));
                gift.setGname(cursor.getString(cursor.getColumnIndex(GiftDao.COLUMN_GNAME)));
                gift.setGurl(cursor.getString(cursor.getColumnIndex(GiftDao.COLUMN_GURL)));
                gift.setGprice(cursor.getInt(cursor.getColumnIndex(GiftDao.COLUMN_GPRICE)));
                giftMap.put(gift.getId(), gift);
            }
        }
        return giftMap;
    }

    public synchronized void deleteGift(int giftId){
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        if (db.isOpen())
        db.rawQuery("delete " + GiftDao.TAB_NAME + " where " + GiftDao.COLUMN_ID + " = " + giftId, null);
    }

}
