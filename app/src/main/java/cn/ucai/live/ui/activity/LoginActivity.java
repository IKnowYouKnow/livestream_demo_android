package cn.ucai.live.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import cn.ucai.live.R;
import cn.ucai.live.data.model.IUserModel;
import cn.ucai.live.data.model.UserModel;
import cn.ucai.live.utils.MFGT;
import cn.ucai.live.utils.PreferenceManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    Button mEmailSignInButton;
    IUserModel mModel;
    String mUsername, mPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (EMClient.getInstance().isLoggedInBefore()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        initView();
        initData();
        setOnClickListener();
    }

    private void initData() {
        mModel = new UserModel();

        String username = PreferenceManager.getInstance().getCurrentUsername();
        if (username != null) {
            mEmailView.setText(username);
        }
    }

    private void initView() {
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void setOnClickListener() {
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.register).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                MFGT.gotoRegisterActivity(LoginActivity.this);
            }
        });

    }


    private boolean checkInput() {
        mUsername = mEmailView.getText().toString();
        mPwd = mPasswordView.getText().toString();
        // Check for a valid email address.
        if (TextUtils.isEmpty(mUsername)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            mEmailView.requestFocus();
            return false;
        }
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mPwd)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
            return false;
        }
        return true;
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//    // Reset errors.
//    mEmailView.setError(null);
//    mPasswordView.setError(null);
//
//    // Store values at the time of the login attempt.
//    Editable email = mEmailView.getText();
//    Editable password = mPasswordView.getText();

//    boolean cancel = false;
//    View focusView = null;


//    if (cancel) {
//      // There was an error; don't attempt login and focus the first
//      // form field with an error.
//      focusView.requestFocus();
//    } else {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        if (checkInput()) {
            showProgress(true);
            EMClient.getInstance().login(mUsername, mPwd, new EMCallBack() {
                @Override
                public void onSuccess() {
                    Log.i("main", "LoginActivity,环信服务器登录成功");
                    showProgress(false);
//                  startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    MFGT.gotoMain(LoginActivity.this);
                    finish();
                }

                @Override
                public void onError(int i, final String s) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            Log.i("main", "LoginActivity,环信服务器登录失败");
                            mPasswordView.setError(s);
                            mPasswordView.requestFocus();
                        }
                    });
                }

                @Override
                public void onProgress(int i, String s) {
                }
            });
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

