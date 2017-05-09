package com.demo.yun.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.demo.yun.R;
import com.demo.yun.entity.AppData;
import com.demo.yun.session.action.AVChatAction;
import com.demo.yun.session.action.FileAction;
import com.demo.yun.session.action.GuessAction;
import com.demo.yun.session.action.SnapChatAction;
import com.demo.yun.theApp;
import com.demo.yun.util.YunXinUtil;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        View.OnClickListener clickListener = new View.OnClickListener()
        {
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
            SessionCustomization customization = customP2PChatOptions();
            //NimUIKit.startP2PSession(this, "test003");
            // 启动单聊
            NimUIKit.startChatting(this, "test003", SessionTypeEnum.P2P, customization, null);
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
            String strAccount = theApp.ACCOUNT;
            String strToken = theApp.TOKEN;
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

    private SessionCustomization customP2PChatOptions()
    {
        // 设置单聊界面定制
        SessionCustomization sessionCustomization = getP2pCustomization();
        NimUIKit.setCommonP2PSessionCustomization(sessionCustomization);
        return sessionCustomization;
    }

    private static SessionCustomization getP2pCustomization()
    {
        SessionCustomization sessionCustomization = null;
        if (sessionCustomization == null)
        {
            sessionCustomization = initBaseP2P();

            // 定制ActionBar右边的按钮，可以加多个
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
//            OptionsButton cloudMsgButton = new OptionsButton() {
//                @Override
//                public void onClick(Context context, View view, String sessionId)
//                {
//                    //MessageHistoryActivity.start(context, sessionId, SessionTypeEnum.P2P); // 漫游消息查询
//                }
//            };
//            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
//
//            buttons.add(cloudMsgButton);
            sessionCustomization.buttons = buttons;
        }
        return sessionCustomization;
    }

    private static SessionCustomization initBaseP2P()
    {
        SessionCustomization sessionCustomization = new SessionCustomization()
        {
            // 由于需要Activity Result， 所以重载该函数。
            @Override
            public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data)
            {
//                if (requestCode == NormalTeamInfoActivity.REQUEST_CODE && resultCode == Activity.RESULT_OK)
//                {
//                    String result = data.getStringExtra(NormalTeamInfoActivity.RESULT_EXTRA_REASON);
//                    if (result == null)
//                    {
//                        return;
//                    }
//                    if (result.equals(NormalTeamInfoActivity.RESULT_EXTRA_REASON_CREATE))
//                    {
//                        String tid = data.getStringExtra(NormalTeamInfoActivity.RESULT_EXTRA_DATA);
//                        if (TextUtils.isEmpty(tid))
//                        {
//                            return;
//                        }
//
//                        startTeamSession(activity, tid);
//                        activity.finish();
//                    }
//                }
            }

            @Override
            public MsgAttachment createStickerAttachment(String category, String item)
            {
                //return new StickerAttachment(category, item);
                return null;
            }
        };

        // 背景
        sessionCustomization.backgroundColor = Color.BLUE;
        sessionCustomization.backgroundUri = "file:///android_asset/xx/bk.jpg";
        sessionCustomization.backgroundUri = "file:///sdcard/Pictures/bk.png";
        sessionCustomization.backgroundUri = "android.resource://com.netease.nim.demo/drawable/bk";

        // 定制加号点开后可以包含的操作，默认已经有图片，视频等消息了，如果要去掉默认的操作，请修改MessageFragment的getActionList函数
        ArrayList<BaseAction> actions = new ArrayList<>();
        actions.add(new AVChatAction(AVChatType.AUDIO));
        actions.add(new AVChatAction(AVChatType.VIDEO));
        actions.add(new SnapChatAction());
        actions.add(new GuessAction());
        actions.add(new FileAction());
        sessionCustomization.actions = actions;
        sessionCustomization.withSticker = true;

        return sessionCustomization;
    }
}
