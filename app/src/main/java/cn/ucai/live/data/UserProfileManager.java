package cn.ucai.live.data;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;

import java.io.File;

import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.data.model.IUserModel;
import cn.ucai.live.data.model.OnCompleteListener;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.UserModel;
import cn.ucai.live.data.restapi.ApiManager;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.PreferenceManager;
import cn.ucai.live.utils.ResultUtils;


public class UserProfileManager {
    private static final String TAG = UserProfileManager.class.getSimpleName();

    /**
     * application context
     */
    protected Context appContext = null;

    /**
     * init flag: test if the sdk has been inited before, we don't need to init
     * again
     */
    private boolean sdkInited = false;

    private User currentAppUser;
    IUserModel mModel;

    public UserProfileManager() {
    }

    public synchronized boolean init(Context context) {
        if (sdkInited) {
            return true;
        }
        sdkInited = true;
        appContext = context;
        currentAppUser = new User();
        mModel = new UserModel();
        return true;
    }

    public synchronized void reset() {
        currentAppUser = null;
        PreferenceManager.getInstance().removeCurrentUserInfo();
    }


    public synchronized User getCurrentAppUser() {
        Log.e(TAG, "getCurrentAppUser: currentAppUser++++++++" + currentAppUser);
        if (currentAppUser == null || currentAppUser.getMUserName() == null) {
            String username = EMClient.getInstance().getCurrentUser();
            Log.e(TAG, "getCurrentAppUser: ++++++++" + username);
            currentAppUser = new User(username);
            String nick = getCurrentUserNick();
            currentAppUser.setMUserNick((nick != null) ? nick : username);
            currentAppUser.setAvatar(getCurrentUserAvatar());
        }
        return currentAppUser;
    }

    public boolean updateCurrentUserNickName(final String nickname) {
        Log.i("main", "updateCurrentUserNickName,nickname=" + nickname);
        mModel.updateNick(appContext, EMClient.getInstance().getCurrentUser(), nickname, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                boolean success = false;
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null && result.getRetData() != null) {
                        User user = (User) result.getRetData();
                        if (user != null) {
                            success = true;
                            setCurrentAppUserNick(user.getMUserNick());
                            Log.i("main", "updateCurrentUserNickName,user=" + user);

                        }

                    } else {
                        CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                    }
                }
                appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_USER_NICK)
                        .putExtra(I.User.USER_NAME, success));
            }

            @Override
            public void onError(String error) {
                CommonUtils.showShortToast(R.string.toast_updatenick_fail);
                appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_USER_NICK)
                        .putExtra(I.User.USER_NAME, false));
            }
        });
        return false;
    }

    public void uploadUserAvatar(File file) {
        mModel.updateAvatar(appContext, EMClient.getInstance().getCurrentUser(), file,
                new OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        boolean success = false;

                        if (s != null) {
                            Result result = ResultUtils.getResultFromJson(s, User.class);
                            if (result != null && result.isRetMsg()) {
                                User user = (User) result.getRetData();
                                if (user != null) {
                                    success = true;
                                    setCurrentAppUserAvatar(user.getAvatar());
                                }
                            }
                        }
                        appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
                                .putExtra(I.Avatar.UPDATE_TIME, success));
                    }

                    @Override
                    public void onError(String error) {
                        appContext.sendBroadcast(new Intent(I.REQUEST_UPDATE_AVATAR)
                                .putExtra(I.Avatar.UPDATE_TIME, false));
                    }
                });
//        String avatarUrl = ParseManager.getInstance().uploadParseAvatar(data);
//        if (avatarUrl != null) {
//            setCurrentUserAvatar(avatarUrl);
//        }
//        return avatarUrl;
    }

    public void updateUserInfo(User user) {
        setCurrentAppUserNick(user.getMUserNick());
        setCurrentAppUserAvatar(user.getAvatar());
    }
    public void asyncGetCurrentAppUserInfo() {
        Log.i("main","UserProfileManager,asyncGetCurrentAppUserInfo,username="+ EMClient.getInstance().getCurrentUser());
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = ApiManager.get().loadUserInfo(EMClient.getInstance().getCurrentUser());
                if (user != null) {
                    updateUserInfo(user);
                    currentAppUser.cloneByOther(user);
                    Log.i(TAG,"asyncGetCurrentAppUserInfo,user = "+user);
                }
            }
        }).start();

//        mModel.loadUserInfo(appContext, EMClient.getInstance().getCurrentUser(), new OnCompleteListener<String>() {
//            @Override
//            public void onSuccess(String s) {
//                if (s != null) {
//                    Result result = ResultUtils.getResultFromJson(s, User.class);
//                    if (result != null && result.isRetMsg()) {
//                        Log.e(TAG, "onSuccess: result=" + result);
//                        User user = (User) result.getRetData();
//                        if (user != null) {
//                            updateUserInfo(user);
//                            currentAppUser.cloneByOther(user);
//                            Log.i("main", "UserProfileManager,asyncGetCurrentAppUserInfo,user=" + user);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        });
    }

    private void setCurrentAppUserNick(String nick) {
        PreferenceManager.getInstance().setCurrentUserNick(nick);
        getCurrentAppUser().setMUserNick(nick);
    }

    private void setCurrentAppUserAvatar(String avatar) {
        PreferenceManager.getInstance().setCurrentUserAvatar(avatar);
        getCurrentAppUser().setAvatar(avatar);
    }

    private String getCurrentUserNick() {
        Log.e(TAG, "getCurrentUserNick: ++++PreferenceManager.getInstance().getCurrentUserNick()++++" + PreferenceManager.getInstance().getCurrentUserNick());
        return PreferenceManager.getInstance().getCurrentUserNick();
    }

    private String getCurrentUserAvatar() {
        return PreferenceManager.getInstance().getCurrentUserAvatar();
    }

}
