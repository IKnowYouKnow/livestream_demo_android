package cn.ucai.live.data;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.controller.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.live.LiveConstants;
import cn.ucai.live.data.dao.LiveDbManager;
import cn.ucai.live.data.model.Gift;
import cn.ucai.live.data.model.IUserModel;
import cn.ucai.live.data.model.UserModel;
import cn.ucai.live.data.restapi.ApiManager;
import cn.ucai.live.ui.activity.MainActivity;
import cn.ucai.live.utils.PreferenceManager;

public class LiveHelper {
    /**
     * data sync listener
     */
    public interface DataSyncListener {
        /**
         * sync complete
         *
         * @param success true：data sync successful，false: failed to sync data
         */
        void onSyncComplete(boolean success);
    }

    protected static final String TAG = "DemoHelper";

    private EaseUI easeUI;

    /**
     * EMEventListener
     */

    private UserProfileManager userProManager;

    private static LiveHelper instance = null;

    private LiveModel demoModel = null;

    private String username;

    private Context appContext;

    private Map<Integer, Gift> mGiftMap;

    private IUserModel mModel;

    private LocalBroadcastManager broadcastManager;


    private LiveHelper() {
    }

    public synchronized static LiveHelper getInstance() {
        if (instance == null) {
            instance = new LiveHelper();
        }
        return instance;
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(Context context) {
        demoModel = new LiveModel(context);
        mModel = new UserModel();
        appContext = context;
        EMOptions options = initChatOptions();
        if (EaseUI.getInstance().init(context, options)) {
            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
            //initialize preference manager
            PreferenceManager.init(context);
            //initialize profile manager
            getUserProfileManager().init(context);
            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
        }
    }

    private EMOptions initChatOptions() {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);

        //you need apply & set your own id if you want to use google cloud messaging.
        options.setGCMNumber("324169311137");
        //you need apply & set your own id if you want to use Mi push notification
        options.setMipushConfig("2882303761517426801", "5381742660801");
        //you need apply & set your own id if you want to use Huawei push notification
        options.setHuaweiPushAppId("10492024");
        return options;
    }


    protected void setEaseUIProviders() {
        // set profile provider if you want easeUI to handle avatar and nickname
        easeUI.setUserProfileProvider(new EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }

            @Override
            public User getAppUser(String username) {
                return getAppUserInfo(username);
            }
        });
    }
    private User getAppUserInfo(String username) {
        Log.i("main", "getAppUserInfo,username=" + username);
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        User user = null;
        if (username.equals(EMClient.getInstance().getCurrentUser()))
            return getUserProfileManager().getCurrentAppUser();
        Log.i("main", "getAppUserInfo,user=" + user);

        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new User(username);
            EaseCommonUtils.setAppUserInitialLetter(user);
        }
        Log.i("main", "getAppUserInfo,user=" + user);
        return user;
    }
    EMConnectionListener connectionListener;

    /**
     * set global listener
     */
    protected void setGlobalListeners() {
        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                EMLog.d("global listener", "onDisconnect" + error);
                if (error == EMError.USER_REMOVED) {
                    onUserException(LiveConstants.ACCOUNT_REMOVED);
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    onUserException(LiveConstants.ACCOUNT_CONFLICT);
                } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                    onUserException(LiveConstants.ACCOUNT_FORBIDDEN);
                }
            }

            @Override
            public void onConnected() {
            }
        };

        //register connection listener
        EMClient.getInstance().addConnectionListener(connectionListener);
        //register message event listener

    }

    /**
     * user met some exception: conflict, removed or forbidden
     */
    protected void onUserException(String exception) {
        EMLog.e(TAG, "onUserException: " + exception);
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(exception, true);
        appContext.startActivity(intent);
    }

    private EaseUser getUserInfo(String username) {
        // To get instance of EaseUser, here we get it from the user list in memory
        // You'd better cache it if you get it from your server
        EaseUser user = null;

        // if user is not in your contacts, set inital letter for him/her
        if (user == null) {
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }
        return user;
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * logout
     *
     * @param unbindDeviceToken whether you need unbind your device token
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    public LiveModel getModel() {
        return (LiveModel) demoModel;
    }

    /**
     * set current username
     *
     * @param username
     */
    public void setCurrentUserName(String username) {
        this.username = username;
        demoModel.setCurrentUserName(username);
    }

    /**
     * get current user's id
     */
    public String getCurrentUsernName() {
        if (username == null) {
            username = demoModel.getCurrentUsernName();
        }
        return username;
    }

    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }

    public void saveGifts(List<Gift> list) {
        demoModel.saveGifts(list);
        for (Gift gift : list) {
            mGiftMap.put(gift.getId(), gift);
        }
    }

    public Map<Integer, Gift> getGifts() {
        if (mGiftMap == null) {
            mGiftMap = demoModel.getGifts();
        }
        if (mGiftMap == null) {
            mGiftMap = new HashMap<>();
        }
        return mGiftMap;
    }

    public void syncLoadGiftList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGiftMap.clear();
                List<Gift> list = ApiManager.get().getAllGifts();
                if (list != null && list.size() > 0) {
                    for (Gift gift : list) {
                        // save gifts to cache
                        mGiftMap.put(gift.getId(), gift);
                    }
                    //save gifts to database
                    demoModel.saveGifts(list);
                }
            }
        }).start();
    }

    public void deleteGift(int giftId) {
        demoModel.deteleGift(giftId);
        mGiftMap.remove(giftId);
    }

    synchronized void reset() {
        getUserProfileManager().reset();
        LiveDbManager.getInstance().closeDB();
        mGiftMap.clear();
    }

}
