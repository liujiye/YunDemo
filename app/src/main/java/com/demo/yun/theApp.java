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
    public static final Account TEST3 = new Account("test003", "1c641f3af395c4734afe3786ba818d63");
    public static final Account TEST4 = new Account("test004", "e4c34b0e582ae5f59e1d417cb87a824f");
//    public static final String ACCOUNT = "test003";
//    public static final String TOKEN = "1c641f3af395c4734afe3786ba818d63";

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
