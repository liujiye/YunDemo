package com.demo.yun;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;

import com.demo.yun.entity.Account;
import com.demo.yun.util.UIUtil;
import com.demo.yun.util.YunXinUtil;
import com.demo.yun.util.sys.SystemUtil;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;

/**
 * Created by liujiye on 17/5/6.
 */

public class theApp extends Application
{
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
