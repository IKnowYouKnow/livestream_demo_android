package cn.ucai.live.data;

import cn.ucai.live.I;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/4/13 0013.
 */

public interface LiveService {
    @GET("live/getAllGifts")
    Call<String> getAllGifts();

    @GET("findUserByUserName")
    Call<String> loadUserInfo(@Query(I.User.USER_NAME) String username);

    @GET("live/createChatRoom")
    Call<String> createChatRoom(@Query("auth") String auth,
                                @Query("name")String name,
                                @Query("description")String des,
                                @Query("owner")String owner,
                                @Query("maxusers")int maxusers,
                                @Query("members")String members
    );

}
