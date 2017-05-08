package com.demo.yun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.demo.yun.R;
import com.demo.yun.entity.AppData;
import com.demo.yun.theApp;
import com.demo.yun.util.YunXinUtil;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

public class LoginActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                case R.id.btn_login:
                    onLogin();
                    break;
                case R.id.btn_main:
                    onMain();
                    break;
                case R.id.btn_contacts:
                    onContacts();
                    break;
                case R.id.btn_recent_contact:
                    onRecentContact();
                    break;
                case R.id.btn_chat:
                    onAudio();
                    break;
                case R.id.btn_video:
                    onVideo();
                    break;
                }
            }
        };

        findViewById(R.id.btn_login).setOnClickListener(clickListener);
        findViewById(R.id.btn_main).setOnClickListener(clickListener);
        findViewById(R.id.btn_recent_contact).setOnClickListener(clickListener);
        findViewById(R.id.btn_chat).setOnClickListener(clickListener);
        findViewById(R.id.btn_video).setOnClickListener(clickListener);
        findViewById(R.id.btn_contacts).setOnClickListener(clickListener);
    }

    private void onLogin()
    {
        onYunXinLogin();
    }

    private void onMain()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void onContacts()
    {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    private void onRecentContact()
    {
        Intent intent = new Intent(this, RecentContactActivity.class);
        startActivity(intent);
    }

    private void onAudio()
    {
        try
        {
            //启动单聊界面
            NimUIKit.setAccount(AppData.getYunXinAccount());
            NimUIKit.startP2PSession(this, "test003");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void onVideo()
    {

    }



    private void onYunXinLogin()
    {
        try
        {
            // String strToken = MD5.getStringMD5("123456");
            String strAccount = "test003";
            String strToken = "1c641f3af395c4734afe3786ba818d63";
            LoginInfo info = new LoginInfo(strAccount, strToken, YunXinUtil.APP_KEY); // config...
            RequestCallback<LoginInfo> callback =
                    new RequestCallback<LoginInfo>()
                    {
                        @Override
                        public void onSuccess(LoginInfo loginInfo)
                        {
                            theApp.showToast("onSuccess");

                            // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                            NimUIKit.setAccount(loginInfo.getAccount());
                            AppData.setYunXinAccount(loginInfo.getAccount());
                            AppData.setYunXinToken(loginInfo.getToken());
                        }

                        @Override
                        public void onFailed(int i)
                        {
                            theApp.showToast("onFailed " + i);
                        }

                        @Override
                        public void onException(Throwable throwable)
                        {
                            theApp.showToast("onException " + throwable.toString());
                        }
                    };
            NIMClient.getService(AuthService.class).login(info).setCallback(callback);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            theApp.showToast("Exception " + e.toString());
        }
    }
}
