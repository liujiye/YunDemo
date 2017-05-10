package com.demo.yun.avchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.demo.yun.R;
import com.demo.yun.avchat.receiver.PhoneCallStateObserver;
import com.netease.nim.uikit.common.util.sys.NetworkUtil;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.AVChatStateObserver;
import com.netease.nimlib.sdk.avchat.constant.AVChatEventType;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame;
import com.netease.nimlib.sdk.avchat.model.AVChatCalleeAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatCommonEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatControlEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatOnlineAckEvent;
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame;

import java.util.Map;

/**
 * Created by liujiye on 17/5/6.
 */

public class AVChatActivity extends Activity implements AVChatUI.AVChatListener, AVChatStateObserver
{
    // constant
    private static final String TAG = "AVChatActivity";
    private static final String KEY_IN_CALLING = "KEY_IN_CALLING";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static final String KEY_CALL_TYPE = "KEY_CALL_TYPE";
    private static final String KEY_SOURCE = "source";
    private static final String KEY_CALL_CONFIG = "KEY_CALL_CONFIG";
    public static final String INTENT_ACTION_AVCHAT = "INTENT_ACTION_AVCHAT";

    /**
     * 来自广播
     */
    public static final int FROM_BROADCASTRECEIVER = 0;
    /**
     * 来自发起方
     */
    public static final int FROM_INTERNAL = 1;
    /**
     * 来自通知栏
     */
    public static final int FROM_NOTIFICATION = 2;
    /**
     * 未知的入口
     */
    public static final int FROM_UNKNOWN = -1;

    // data
    private AVChatUI avChatUI; // 音视频总管理器
    private AVChatData avChatData; // config for connect video server
    private int state; // calltype 音频或视频
    private String receiverId; // 对方的account

    // state
    private boolean isUserFinish = false;
    private boolean mIsInComingCall = false;// is incoming call or outgoing call
    private boolean isCallEstablished = false; // 电话是否接通
    private static boolean needFinish = true; // 若来电或去电未接通时，点击home。另外一方挂断通话。从最近任务列表恢复，则finish
    private boolean hasOnPause = false; // 是否暂停音视频

    public static void launch(Context context, String account, int callType, int source)
    {
        needFinish = false;
        Intent intent = new Intent();
        intent.setClass(context, AVChatActivity.class);
        intent.putExtra(KEY_ACCOUNT, account);
        intent.putExtra(KEY_IN_CALLING, false);
        intent.putExtra(KEY_CALL_TYPE, callType);
        intent.putExtra(KEY_SOURCE, source);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View root = LayoutInflater.from(this).inflate(R.layout.activity_avchat, null);
        setContentView(root);
        mIsInComingCall = getIntent().getBooleanExtra(KEY_IN_CALLING, false);
        avChatUI = new AVChatUI(this, root, this);
        if (!avChatUI.initiation())
        {
            this.finish();
            return;
        }

        registerNetCallObserver(true);
        if (mIsInComingCall)
        {
            inComingCalling();
        }
        else
        {
            outgoingCalling();
        }
    }


    /**
     * 接听
     */
    private void inComingCalling()
    {
        avChatUI.inComingCalling(avChatData);
    }

    /**
     * 拨打
     */
    private void outgoingCalling()
    {
        if (!NetworkUtil.isNetAvailable(AVChatActivity.this))
        { // 网络不可用
            Toast.makeText(this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        avChatUI.outGoingCalling(receiverId, AVChatType.typeOfValue(state));
    }

    /**
     * 注册监听
     *
     * @param register
     */
    private void registerNetCallObserver(boolean register)
    {
        AVChatManager.getInstance().observeAVChatState(this, register);
        AVChatManager.getInstance().observeCalleeAckNotification(callAckObserver, register);
        AVChatManager.getInstance().observeControlNotification(callControlObserver, register);
        AVChatManager.getInstance().observeHangUpNotification(callHangupObserver, register);
        AVChatManager.getInstance().observeOnlineAckNotification(onlineAckObserver, register);
        AVChatManager.getInstance().observeTimeoutNotification(timeoutObserver, register);
        PhoneCallStateObserver.getInstance().observeAutoHangUpForLocalPhone(autoHangUpForLocalPhoneObserver, register);
    }

    /**
     * 注册/注销网络通话被叫方的响应（接听、拒绝、忙）
     */
    Observer<AVChatCalleeAckEvent> callAckObserver = new Observer<AVChatCalleeAckEvent>()
    {
        @Override
        public void onEvent(AVChatCalleeAckEvent ackInfo)
        {

//            AVChatSoundPlayer.instance().stop();
//
//            if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_BUSY) {
//
//                AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.PEER_BUSY);
//
//                avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
//            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_REJECT) {
//                avChatUI.closeSessions(AVChatExitCode.REJECT);
//            } else if (ackInfo.getEvent() == AVChatEventType.CALLEE_ACK_AGREE) {
//                avChatUI.isCallEstablish.set(true);
//                avChatUI.canSwitchCamera = true;
//            }
        }
    };


    Observer<Long> timeoutObserver = new Observer<Long>()
    {
        @Override
        public void onEvent(Long chatId)
        {

//            AVChatData info = avChatUI.getAvChatData();
//            if (info != null && info.getChatId() == chatId) {
//
//                avChatUI.closeSessions(AVChatExitCode.PEER_NO_RESPONSE);
//
//                // 来电超时，自己未接听
//                if (mIsInComingCall) {
//                    activeMissCallNotifier();
//                }
//
//                AVChatSoundPlayer.instance().stop();
//            }

        }
    };

    Observer<Integer> autoHangUpForLocalPhoneObserver = new Observer<Integer>()
    {
        @Override
        public void onEvent(Integer integer)
        {
//            AVChatSoundPlayer.instance().stop();
//            avChatUI.closeSessions(AVChatExitCode.PEER_BUSY);
        }
    };

    /**
     * 注册/注销网络通话控制消息（音视频模式切换通知）
     */
    Observer<AVChatControlEvent> callControlObserver = new Observer<AVChatControlEvent>()
    {
        @Override
        public void onEvent(AVChatControlEvent netCallControlNotification)
        {
            //handleCallControl(netCallControlNotification);
        }
    };

    /**
     * 注册/注销网络通话对方挂断的通知
     */
    Observer<AVChatCommonEvent> callHangupObserver = new Observer<AVChatCommonEvent>()
    {
        @Override
        public void onEvent(AVChatCommonEvent avChatHangUpInfo)
        {
//            AVChatSoundPlayer.instance().stop();
//
//            avChatUI.closeSessions(AVChatExitCode.HANGUP);
//            cancelCallingNotifier();
//            // 如果是incoming call主叫方挂断，那么通知栏有通知
//            if (mIsInComingCall && !isCallEstablished)
//            {
//                activeMissCallNotifier();
//            }
        }
    };

    /**
     * 注册/注销同时在线的其他端对主叫方的响应
     */
    Observer<AVChatOnlineAckEvent> onlineAckObserver = new Observer<AVChatOnlineAckEvent>()
    {
        @Override
        public void onEvent(AVChatOnlineAckEvent ackInfo)
        {
            //AVChatSoundPlayer.instance().stop();

            String client = null;
            switch (ackInfo.getClientType())
            {
            case ClientType.Web:
                client = "Web";
                break;
            case ClientType.Windows:
                client = "Windows";
                break;
            case ClientType.Android:
                client = "Android";
                break;
            case ClientType.iOS:
                client = "iOS";
                break;
            default:
                break;
            }
            if (client != null)
            {
                String option = ackInfo.getEvent() == AVChatEventType.CALLEE_ONLINE_CLIENT_ACK_AGREE ? "接听！" : "拒绝！";
                Toast.makeText(AVChatActivity.this, "通话已在" + client + "端被" + option, Toast.LENGTH_SHORT).show();
            }
            //avChatUI.closeSessions(-1);
        }
    };

    @Override
    public void uiExit()
    {

    }

    @Override
    public void onTakeSnapshotResult(String s, boolean b, String s1)
    {

    }

    @Override
    public void onConnectionTypeChanged(int i)
    {

    }

    @Override
    public void onAVRecordingCompletion(String s, String s1)
    {

    }

    @Override
    public void onAudioRecordingCompletion(String s)
    {

    }

    @Override
    public void onLowStorageSpaceWarning(long l)
    {

    }

    @Override
    public void onFirstVideoFrameAvailable(String s)
    {

    }

    @Override
    public void onVideoFpsReported(String s, int i)
    {

    }

    @Override
    public void onJoinedChannel(int i, String s, String s1)
    {

    }

    @Override
    public void onLeaveChannel()
    {

    }

    @Override
    public void onUserJoined(String s)
    {

    }

    @Override
    public void onUserLeave(String s, int i)
    {

    }

    @Override
    public void onProtocolIncompatible(int i)
    {

    }

    @Override
    public void onDisconnectServer()
    {

    }

    @Override
    public void onNetworkQuality(String s, int i)
    {

    }

    @Override
    public void onCallEstablished()
    {

    }

    @Override
    public void onDeviceEvent(int i, String s)
    {

    }

    @Override
    public void onFirstVideoFrameRendered(String s)
    {

    }

    @Override
    public void onVideoFrameResolutionChanged(String s, int i, int i1, int i2)
    {

    }

    @Override
    public boolean onVideoFrameFilter(AVChatVideoFrame avChatVideoFrame)
    {
        return false;
    }

    @Override
    public boolean onAudioFrameFilter(AVChatAudioFrame avChatAudioFrame)
    {
        return false;
    }

    @Override
    public void onAudioDeviceChanged(int i)
    {

    }

    @Override
    public void onReportSpeaker(Map<String, Integer> map, int i)
    {

    }

    @Override
    public void onAudioMixingEvent(int i)
    {

    }
}
