package com.example.njxzc.edu.cn.imtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;

public class MainActivity extends AppCompatActivity {

    final String userid = "testpro1";
    final String APP_KEY = "23015524";
    //此对象获取到后，保存为全局对象，供APP使用
//此对象跟用户相关，如果切换了用户，需要重新获取
    YWIMKit mIMKit = YWAPI.getIMKitInstance(userid, APP_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mQuery = (Button)findViewById(R.id.btn_query);
        //调到咨询医生界面
        mQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FragmentTab.class);
                startActivity(intent);
            }
        });
//        loginAndStartActivity();
    }

    private void loginAndStartActivity(){
        //开始登录
        String userid = "testpro1";
        String password = "taobao1234";
        IYWLoginService loginService = mIMKit.getLoginService();
        YWLoginParam loginParam = YWLoginParam.createLoginParam(userid, password);
        loginService.login(loginParam, new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {

                //最近会话窗口
                Intent intent = mIMKit.getConversationActivityIntent();


                //打开单聊窗口
                final String target = "testpro2"; //消息接收者ID
                final String appkey = "23015524"; //消息接收者appKey
//                Intent intent = mIMKit.getChattingActivityIntent(target, appkey);
                startActivity(intent);
            }

            @Override
            public void onProgress(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onError(int errCode, String description) {
                //如果登录失败，errCode为错误码,description是错误的具体描述信息
            }
        });

    }
}
