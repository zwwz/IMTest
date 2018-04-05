package com.example.njxzc.edu.cn.imtest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.mobileim.FeedbackAPI;
import com.alibaba.mobileim.YWChannel;
import com.alibaba.mobileim.YWConstants;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.WxLog;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.login.YWLoginCode;
import com.alibaba.mobileim.login.YWLoginState;
import com.alibaba.mobileim.utility.IMNotificationUtils;
import com.alibaba.mobileim.utility.IMPrefsTools;
import com.alibaba.tcms.env.YWEnvType;

import java.util.Random;

/**
 * Created by wangyan on 2018/3/29.
 */

public class LoginActivity extends Activity {
//    private static final int GUEST = 1;
    private static final String USER_ID = "userId";
    private static final String PASSWORD = "password";
    private static final String APPKEY = "appkey";
    private static final String TAG = LoginActivity.class.getSimpleName();
    private LoginSampleHelper loginHelper;
    private EditText userIdView;
    private EditText passwordView;
    private EditText appKeyView;
//    private ProgressBar progressBar;
    private Button loginButton;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ImageView lg;
    private Button annoyloginButton;
    private int mClickCount=0;

    public static final String AUTO_LOGIN_STATE_ACTION = "com.openim.autoLoginStateActionn";

    private BroadcastReceiver mAutoLoginStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int state = intent.getIntExtra("state", -1);
            YWLog.i(TAG, "mAutoLoginStateReceiver, loginState = " + state);
            if (state == -1){
                return;
            }
            handleAutoLoginState(state);
        }
    };

    private void startChattingActivity(String targetId){
        startActivity(loginHelper.getIMKit().getChattingActivityIntent(targetId));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_login);
        loginHelper = LoginSampleHelper.getInstance();
        userIdView = (EditText) findViewById(R.id.account);
        passwordView = (EditText) findViewById(R.id.password);
        appKeyView = (EditText) findViewById(R.id.appkey);
        appKeyView.setVisibility(View.VISIBLE);
//        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        initLoginInfo();

        init(userIdView.getText().toString(), appKeyView.getText().toString());

        myRegisterReceiver();

        loginButton = (Button) findViewById(R.id.login);

        userIdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    passwordView.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前网络状态，若当前无网络则提示用户无网络
                if (YWChannel.getInstance().getNetWorkState().isNetWorkNull()) {
                    Toast.makeText(LoginActivity.this, "网络已断开，请稍后再试哦~", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginButton.setClickable(false);
                final Editable userId = userIdView.getText();
                final Editable password = passwordView.getText();
                final Editable appKey = appKeyView.getText();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userIdView.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(passwordView.getWindowToken(), 0);
                if (TextUtils.isEmpty(appKey)){
                    LoginSampleHelper.APP_KEY= YWConstants.YWSDKAppKey;
                }
                init(userId.toString(), appKeyView.getText().toString());
//                progressBar.setVisibility(View.VISIBLE);
                loginHelper.login_Sample(userId.toString(), password.toString(), appKey.toString(), new IWxCallback() {

                    @Override
                    public void onSuccess(Object... arg0) {
                        saveLoginInfoToLocal(userId.toString(), password.toString(), appKey.toString());

                        loginButton.setClickable(true);
//                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "登录成功",
                                Toast.LENGTH_SHORT).show();
                        YWLog.i(TAG, "login success!");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(intent);
                        LoginActivity.this.finish();
                    }

                    @Override
                    public void onProgress(int arg0) {

                    }

                    @Override
                    public void onError(int errorCode, String errorMessage) {
//                        progressBar.setVisibility(View.GONE);
                        loginButton.setClickable(true);
                        YWLog.w(TAG, "登录失败，错误码：" + errorCode + "  错误信息：" + errorMessage);
                        IMNotificationUtils.getInstance().showToast(LoginActivity.this, errorMessage);
                    }
                });
            }
        });
    }

    private void init(String userId, String appKey){
        //初始化imkit
        LoginSampleHelper.getInstance().initIMKit(userId, appKey);
        //自定义头像和昵称回调初始化(如果不需要自定义头像和昵称，则可以省去)
//        UserProfileSampleHelper.initProfileCallback();
        //通知栏相关的初始化
        NotificationInitSampleHelper.init();
    }

    /**
     * 保存登录的用户名密码到本地
     *
     * @param userId
     * @param password
     */
    private void saveLoginInfoToLocal(String userId, String password, String appkey) {
        IMPrefsTools.setStringPrefs(LoginActivity.this, USER_ID, userId);
        IMPrefsTools.setStringPrefs(LoginActivity.this, PASSWORD, password);
        IMPrefsTools.setStringPrefs(LoginActivity.this, APPKEY, appkey);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int loginState = LoginSampleHelper.getInstance().getAutoLoginState().getValue();
        handleAutoLoginState(loginState);
//        YWLog.i(TAG, "onResume, loginState = " + LoginSampleHelper.getInstance().getAutoLoginState().getValue());
        YWLog.i(TAG, "onResume, loginState = " + loginState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myUnregisterReceiver();
    }

    private void myRegisterReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(AUTO_LOGIN_STATE_ACTION);
        LocalBroadcastManager.getInstance(YWChannel.getApplication()).registerReceiver(mAutoLoginStateReceiver, filter);
    }

    private void myUnregisterReceiver(){
        LocalBroadcastManager.getInstance(YWChannel.getApplication()).unregisterReceiver(mAutoLoginStateReceiver);
    }

    private void handleAutoLoginState(int loginState){
        if (loginState == YWLoginState.logining.getValue()){
//            if (progressBar.getVisibility() != View.VISIBLE){
//                progressBar.setVisibility(View.VISIBLE);
//            }
            loginButton.setClickable(false);
        }else if (loginState == YWLoginState.success.getValue()){
            loginButton.setClickable(true);
//            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        } else {
            //当作失败处理
//            progressBar.setVisibility(View.GONE);
            loginButton.setClickable(true);
        }
    }

    /**
     * 生成随机帐号
     * @return
     */
    private String getRandAccount(){
        final int max=90;
        final int min=10;
        Random random = new Random();
        return "testpro" + (random.nextInt(max)%(max-min+1) + min);
    }

    private void initLoginInfo(){
        //读取登录成功后保存的用户名、密码和appkey
        String localId = IMPrefsTools.getStringPrefs(LoginActivity.this, USER_ID, "");
        if (!TextUtils.isEmpty(localId)) {
            userIdView.setText(localId);
            String localPassword = IMPrefsTools.getStringPrefs(LoginActivity.this, PASSWORD, "");
            if (!TextUtils.isEmpty(localPassword)) {
                passwordView.setText(localPassword);
            }
            String localAppKey = IMPrefsTools.getStringPrefs(LoginActivity.this, APPKEY, "");
            if (!TextUtils.isEmpty(localAppKey)){
                appKeyView.setText(localAppKey);
            }
        }

        if (LoginSampleHelper.sEnvType == YWEnvType.ONLINE){
            if (TextUtils.isEmpty(userIdView.getText())){
                userIdView.setText(getRandAccount());
            }
            if (TextUtils.isEmpty(passwordView.getText())){
                passwordView.setText("taobao1234");
            }
            if (TextUtils.isEmpty(appKeyView.getText())){
                appKeyView.setText(LoginSampleHelper.APP_KEY);
            }
        }else if(LoginSampleHelper.sEnvType == YWEnvType.PRE){
            if (TextUtils.isEmpty(userIdView.getText())){
                userIdView.setText("testpro74");
            }
            if (TextUtils.isEmpty(passwordView.getText())){
                passwordView.setText("taobao1234");
            }
            if (TextUtils.isEmpty(appKeyView.getText())){
                appKeyView.setText(LoginSampleHelper.APP_KEY);
            }
        }
    }

    private IWxCallback mloginCallback = new IWxCallback() {
        @Override
        public void onSuccess(Object... result) {
            WxLog.d("test", "Feedback login success");
            Intent intent = FeedbackAPI.getFeedbackActivityIntent();
            if(intent!=null) {
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }
        @Override
        public void onError(int code, String info) {
            WxLog.d("test", "Feedback  login fail");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT);
                    finish();
                }
            });
        }
        @Override
        public void onProgress(int progress) {

        }
    };
}
