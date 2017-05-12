package com.demo.yun.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;

import com.demo.yun.R;
import com.demo.yun.activity.MainActivity;
import com.demo.yun.entity.AppData;
import com.demo.yun.theApp;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;


/**
 * 网易云信相关操作
 */
public class YunXinUtil
{
    // 这是likechat的appkey
    //public static final String APP_KEY = "fa0f2219206b8a2e1be41fb9382cd0f4";
    // 这是云信Demo的appkay
    public static final String APP_KEY = "45c6af3c98409b18a84451215d0bdd6e";

    /**
     * 只能在主线程运行
     */
    public static void init()
    {
        try
        {
            // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
            NIMClient.init(theApp.CONTEXT, loginInfo(), options(theApp.CONTEXT));

            // 初始化UIKit模块
            NimUIKit.init(theApp.CONTEXT);

            // 会话窗口的定制: 示例代码可详见demo源码中的SessionHelper类。
            // 1.注册自定义消息附件解析器（可选）
            // 2.注册各种扩展消息类型的显示ViewHolder（可选）
            // 3.设置会话中点击事件响应处理（一般需要）
            //SessionHelper.init();

            // 通讯录列表定制：示例代码可详见demo源码中的ContactHelper类。
            // 1.定制通讯录列表中点击事响应处理（一般需要，UIKit 提供默认实现为点击进入聊天界面)
            //ContactHelper.init();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // 如果返回值为 null，则全部使用默认参数。
    public static SDKOptions options(Context context)
    {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = MainActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config;

        options.appKey = APP_KEY;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        String sdkPath = Environment.getExternalStorageDirectory() + "/" +
                context.getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        // options.thumbnailSize = ${Screen.width} /2;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider()
        {
            @Override
            public UserInfo getUserInfo(String account)
            {
                return null;
            }

            @Override
            public int getDefaultIconResId()
            {
                return R.drawable.avatar_def;
            }

            @Override
            public Bitmap getTeamIcon(String tid)
            {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(String account)
            {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                           SessionTypeEnum sessionType)
            {
                return null;
            }
        };
        return options;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    public static LoginInfo loginInfo()
    {
        // 从本地读取上次登录成功时保存的用户登录信息
        String account = AppData.getYunXinAccount();
        String token = AppData.getYunXinToken();

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token))
        {
            AppData.setYunXinAccount(account.toLowerCase());
            return new LoginInfo(account, token);
        }
        else
        {
            return null;
        }
    }
}
