package cn.ucai.live.data;

import android.content.Context;

import java.util.List;
import java.util.Map;

import cn.ucai.live.data.dao.GiftDao;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.utils.PreferenceManager;


public class LiveModel {
    protected Context context = null;
    GiftDao mGiftDao;

    public LiveModel(Context ctx){
        context = ctx;
        PreferenceManager.init(context);
        mGiftDao = new GiftDao();
    }

    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        PreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName(){
        return PreferenceManager.getInstance().getCurrentUsername();
    }

    public void saveGifts(List<Gift> list){
        mGiftDao.saveGifts(list);
    }

    public Map<Integer, Gift> getGifts() {
       return mGiftDao.getGifts();
    }

    public void deteleGift(int giftId){
        mGiftDao.deleteGift(giftId);
    }
}
