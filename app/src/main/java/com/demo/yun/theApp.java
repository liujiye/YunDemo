package com.demo.yun;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.demo.yun.avchat.AVChatActivity;
import com.demo.yun.avchat.AVChatProfile;
import com.demo.yun.avchat.receiver.PhoneCallStateObserver;
import com.demo.yun.entity.Account;
import com.demo.yun.session.SessionHelper;
import com.demo.yun.util.UIUtil;
import com.demo.yun.util.YunXinUtil;
import com.demo.yun.util.sys.SystemUtil;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatControlCommand;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;

import java.util.Map;

/**
 * Created by liujiye on 17/5/6.
 */

public class theApp extends Application
{
    private static final String TAG = "YunDemo App";

    public static Context CONTEXT;
    // likechat账号
    //public static final Account TEST3 = new Account("test003", "1c641f3af395c4734afe3786ba818d63");
    //public static final Account TEST4 = new Account("test004", "e4c34b0e582ae5f59e1d417cb87a824f");
    // 云信账号, token使用123456的MD5值
    public static final Account TEST3 = new Account("liu1501134", "e10adc3949ba59abbe56e057f20f883e");
    public static final Account TEST4 = new Account("18178619319", "e10adc3949ba59abbe56e057f20f883e");

    private static Account mCurAccount = TEST3;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        CONTEXT = this;
        NIMClient.init(this, YunXinUtil.loginInfo(), YunXinUtil.options(this));
        if (inMainProcess())
        {
            //YunXinUtil.init();
            NimUIKit.init(this);
            
            // 会话窗口的定制初始化。
            SessionHelper.init();

            // 注册通知消息过滤器
            registerIMMessageFilter();

            // 初始化消息提醒
            NIMClient.toggleNotification(true);

            // 注册网络通话来电
            registerAVChatIncomingCallObserver(true);
        }
    }

    public static Account getCurAccount()
    {
        return mCurAccount;
    }

    public static void setCurAccount(Account account)
    {
        mCurAccount = account;
    }

    public boolean inMainProcess()
    {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }


    /**
     * 通知消息过滤器（如果过滤则该消息不存储不上报）
     */
    private void registerIMMessageFilter()
    {
        NIMClient.getService(MsgService.class).registerIMMessageFilter(new IMMessageFilter()
        {
            @Override
            public boolean shouldIgnore(IMMessage message)
            {
                if (message.getAttachment() != null)
                {
                    if (message.getAttachment() instanceof UpdateTeamAttachment)
                    {
                        UpdateTeamAttachment attachment = (UpdateTeamAttachment) message.getAttachment();
                        for (Map.Entry<TeamFieldEnum, Object> field : attachment.getUpdatedFields().entrySet())
                        {
                            if (field.getKey() == TeamFieldEnum.ICON)
                            {
                                return true;
                            }
                        }
                    }
                    else if (message.getAttachment() instanceof AVChatAttachment)
                    {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 注册网络通话来电
     *
     * @param register
     */
    private void registerAVChatIncomingCallObserver(boolean register)
    {
        AVChatManager.getInstance().observeIncomingCall(new Observer<AVChatData>()
        {
            @Override
            public void onEvent(AVChatData data)
            {
                String extra = data.getExtra();
                Log.e("Extra", "Extra Message->" + extra);
                if (PhoneCallStateObserver.getInstance().getPhoneCallState() != PhoneCallStateObserver.PhoneCallStateEnum.IDLE
                        || AVChatProfile.getInstance().isAVChatting()
                        || AVChatManager.getInstance().getCurrentChatId() != 0)
                {
                    LogUtil.i(TAG, "reject incoming call data =" + data.toString() + " as local phone is not idle");
                    AVChatManager.getInstance().sendControlCommand(data.getChatId(), AVChatControlCommand.BUSY, null);
                    return;
                }
                // 有网络来电打开AVChatActivity
                AVChatProfile.getInstance().setAVChatting(true);
                AVChatActivity.launch(theApp.CONTEXT, data, AVChatActivity.FROM_BROADCASTRECEIVER);
            }
        }, register);
    }


    public static Handler sm_handler = new Handler();

    public static void showToast(final String strToast)
    {
        sm_handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                UIUtil.showToastLong(CONTEXT, strToast);
            }
        });
    }
}
