package cn.ucai.live.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.data.model.IUserModel;
import cn.ucai.live.data.model.OnCompleteListener;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.UserModel;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.MFGT;
import cn.ucai.live.utils.PreferenceManager;
import cn.ucai.live.utils.ResultUtils;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.email)
    EditText mEtUserName;
    @BindView(R.id.password)
    EditText mEtPassword;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nick)
    EditText mEtNick;
    @BindView(R.id.Confirm_password)
    EditText mConfirmPassword;

    String mUserName;
    String mNick;
    String mPassword;
    ProgressDialog pd;
    IUserModel mModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        PreferenceManager.init(RegisterActivity.this);
        mModel = new UserModel();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @OnClick(R.id.register)
    public void onRegisterListener() {
        if (checkInput()) {
            showDialog();
            registerAppService();
        }
    }

    private void registerEMService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(mUserName, mPassword);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            showToast("注册成功");
                            PreferenceManager.getInstance().setCurrentUserName(mUserName);
                            PreferenceManager.getInstance().setCurrentUserNick(mNick);
                            MFGT.gotoLoginActivity(RegisterActivity.this);
                        }
                    });
                } catch (final HyphenateException e) {
                    unRegister();
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            showLongToast("注册失败：" + e.getMessage());
                        }
                    });
                }
            }
        }).start();

    }

    private void unRegister() {
        mModel.unregister(RegisterActivity.this, mUserName, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void showDialog() {
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("正在注册...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    private void registerAppService() {
        mModel.register(RegisterActivity.this, mUserName, mNick, mPassword, new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                boolean success = false;
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, String.class);
                    if (result != null) {
                        if (result.isRetMsg()) {
                            registerEMService();
                            success = true;
                        } else if (result.getRetCode() == I.MSG_REGISTER_USERNAME_EXISTS) {
                            CommonUtils.showShortToast(R.string.User_already_exists);
                        } else {
                            CommonUtils.showShortToast(R.string.Registration_failed);
                        }
                    }
                }
                if (!success) {
                    pd.dismiss();
                }
            }

            @Override
            public void onError(String error) {
                pd.dismiss();
                CommonUtils.showShortToast(R.string.Registration_failed);
            }
        });
    }

    private boolean checkInput() {
        mUserName = mEtUserName.getText().toString().trim();
        mNick = mEtNick.getText().toString().trim();
        mPassword = mEtPassword.getText().toString().trim();
        String password2 = mConfirmPassword.getText().toString().trim();
        if (TextUtils.isEmpty(mUserName)) {
            mEtUserName.requestFocus();
            mEtUserName.setError(getString(R.string.error_invalid_email));
            return false;
        }
        if (!mUserName.matches("[a-zA-Z]\\w{5,15}")) {
            mEtUserName.requestFocus();
            mEtUserName.setError(getString(R.string.illegal_user_name));
            return false;
        }
        if (TextUtils.isEmpty(mNick)) {
            mEtNick.requestFocus();
            mEtNick.setError(getString(R.string.error_invalid_nick));
            return false;
        }
        if (TextUtils.isEmpty(mPassword)) {
            mEtPassword.requestFocus();
            mEtPassword.setError(getString(R.string.error_invalid_password));
            return false;
        }
        if (TextUtils.isEmpty(password2)) {
            mConfirmPassword.requestFocus();
            mConfirmPassword.setError(getString(R.string.error_invalid_com_password));
            return false;
        }
        if (!mPassword.equals(password2)) {
            mConfirmPassword.requestFocus();
            mConfirmPassword.setError(getString(R.string.error_two_pwd_no_same));
            return false;
        }
        return true;
    }
}
