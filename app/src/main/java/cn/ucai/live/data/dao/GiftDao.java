package cn.ucai.live.data.dao;

import java.util.List;
import java.util.Map;

import cn.ucai.live.data.model.Gift;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class GiftDao {
    public static final String TAB_NAME = "tb_gift";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GNAME = "gname";
    public static final String COLUMN_GURL = "gurl";
    public static final String COLUMN_GPRICE = "gprice";

    public GiftDao(){

    }

    public void saveGifts(List<Gift> list) {
        LiveDbManager.getInstance().saveGifts(list);
    }

    public Map<Integer,Gift> getGifts() {
        return LiveDbManager.getInstance().getGifts();
    }

    public void deleteGift(int giftId) {
        LiveDbManager.getInstance().deleteGift(giftId);
    }
}
