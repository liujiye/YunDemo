package com.demo.yun.avchat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.demo.yun.R;
import com.demo.yun.constant.CallStateEnum;
import com.demo.yun.theApp;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.permission.BaseMPermission;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.avchat.AVChatCallback;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.constant.AVChatAudioEffectMode;
import com.netease.nimlib.sdk.avchat.constant.AVChatMediaCodecMode;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.netease.nimlib.sdk.avchat.constant.AVChatUserRole;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.avchat.model.AVChatNotifyOption;
import com.netease.nimlib.sdk.avchat.model.AVChatParameters;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liujiye on 17/5/6.
 */

public class AVChatUI implements AVChatUIListener
{
    // constant
    private static final String TAG = "AVChatUI";

    // data
    private Context context;
    private AVChatData avChatData;
    private AVChatListener aVChatListener;
    private String receiverId;
    //private AVChatAudio avChatAudio;
    //private AVChatVideo avChatVideo;
    private AVChatSurface avChatSurface;
    private AVChatParameters avChatParameters;
    private String videoAccount; // 发送视频请求，onUserJoin回调的user account

    private CallStateEnum callingState = CallStateEnum.INVALID;

    private long timeBase = 0;

    // view
    private View root;

    // state
    public boolean canSwitchCamera = false;
    private boolean isClosedCamera = false;
    public AtomicBoolean isCallEstablish = new AtomicBoolean(false);

    private final String[] BASIC_PERMISSIONS = new String[]{Manifest.permission.CAMERA,};

    //是否在录制
    private boolean isRecording = false;

    // 检查存储
    private boolean recordWarning = false;

    List<Pair<String, Boolean>> recordList = new LinkedList<Pair<String, Boolean>>();

    public interface AVChatListener
    {
        void uiExit();
    }


    public AVChatUI(Context context, View root, AVChatListener listener)
    {
        this.context = context;
        this.root = root;
        this.aVChatListener = listener;
        this.avChatParameters = new AVChatParameters();
        configFromPreference(PreferenceManager.getDefaultSharedPreferences(context));
        updateAVChatOptionalConfig();
    }


    //Config from Preference
    private int videoCropRatio;
    private boolean videoAutoRotate;
    private int videoQuality;
    private boolean serverRecordAudio;
    private boolean serverRecordVideo;
    private boolean defaultFrontCamera;
    private boolean autoCallProximity;
    private int videoHwEncoderMode;
    private int videoHwDecoderMode;
    private boolean videoFpsReported;
    private int audioEffectAecMode;
    private int audioEffectNsMode;
    private int videoMaxBitrate;
    private int deviceDefaultRotation;
    private int deviceRotationOffset;
    private boolean audioHighQuality;
    private boolean audioDtx;

    private void configFromPreference(SharedPreferences preferences)
    {
        videoCropRatio = Integer.parseInt(preferences.getString(context.getString(
                R.string.nrtc_setting_vie_crop_ratio_key), "0"));
        videoAutoRotate = preferences.getBoolean(context.getString(R.string.nrtc_setting_vie_rotation_key), true);
        videoQuality = Integer.parseInt(preferences.getString(context.getString(R.string.nrtc_setting_vie_quality_key), 0 + ""));
        serverRecordAudio = preferences.getBoolean(context.getString(R.string.nrtc_setting_other_server_record_audio_key), false);
        serverRecordVideo = preferences.getBoolean(context.getString(R.string.nrtc_setting_other_server_record_video_key), false);
        defaultFrontCamera = preferences.getBoolean(context.getString(R.string.nrtc_setting_vie_default_front_camera_key), true);
        autoCallProximity = preferences.getBoolean(context.getString(R.string.nrtc_setting_voe_call_proximity_key), true);
        videoHwEncoderMode = Integer.parseInt(preferences.getString(context.getString(R.string.nrtc_setting_vie_hw_encoder_key), 0 + ""));
        videoHwDecoderMode = Integer.parseInt(preferences.getString(context.getString(R.string.nrtc_setting_vie_hw_decoder_key), 0 + ""));
        videoFpsReported = preferences.getBoolean(context.getString(R.string.nrtc_setting_vie_fps_reported_key), true);
        audioEffectAecMode = Integer.parseInt(preferences.getString(context.getString(R.string.nrtc_setting_voe_audio_aec_key), 2 + ""));
        audioEffectNsMode = Integer.parseInt(preferences.getString(context.getString(R.string.nrtc_setting_voe_audio_ns_key), 2 + ""));
        String value1 = preferences.getString(context.getString(R.string.nrtc_setting_vie_max_bitrate_key), 0 + "");
        videoMaxBitrate = Integer.parseInt(
                TextUtils.isDigitsOnly(value1) && !TextUtils.isEmpty(value1) ? value1 : 0 + "");
        String value2 = preferences.getString(context.getString(R.string.nrtc_setting_other_device_default_rotation_key), 0 + "");
        deviceDefaultRotation = Integer.parseInt(TextUtils.isDigitsOnly(value2) && !TextUtils.isEmpty(value2) ? value2 : 0 + "");
        String value3 = preferences.getString(context.getString(R.string.nrtc_setting_other_device_rotation_fixed_offset_key), 0 + "");
        deviceRotationOffset = Integer.parseInt(TextUtils.isDigitsOnly(value3) && !TextUtils.isEmpty(value3) ? value3 : 0 + "");
        audioHighQuality = preferences.getBoolean(context.getString(R.string.nrtc_setting_voe_high_quality_key), false);
        audioDtx = preferences.getBoolean(context.getString(R.string.nrtc_setting_voe_dtx_key), true);
    }


    /**
     * 1, autoCallProximity: 语音通话时使用, 距离感应自动黑屏
     * 2, videoCropRatio: 制定固定的画面裁剪比例，发送端有效
     * 3, videoAutoRotate: 结合自己设备角度和对方设备角度自动旋转画面
     * 4, serverRecordAudio: 需要服务器录制语音, 同时需要 APP KEY 下面开通了服务器录制功能
     * 5, serverRecordVideo: 需要服务器录制视频, 同时需要 APP KEY 下面开通了服务器录制功能
     * 6, defaultFrontCamera: 默认是否使用前置摄像头
     * 7, videoQuality: 视频质量调整, 最高建议使用480P
     * 8, videoFpsReported: 是否开启视频绘制帧率汇报
     * 9, deviceDefaultRotation: 99.99%情况下你不需要设置这个参数, 当设备固定在水平方向时,并且设备不会移动, 这时是无法确定设备角度的,可以设置一个默认角度
     * 10, deviceRotationOffset: 99.99%情况下你不需要设置这个参数, 当你的设备传感器获取的角度永远偏移固定值时设置,用于修正旋转角度
     * 11, videoMaxBitrate: 视频最大码率设置, 100K ~ 5M. 如果没有特殊需求不要去设置,会影响SDK内部的调节机制
     * 12, audioEffectAecMode: 语音处理选择, 默认使用平台内置,当你发现平台内置不好用时可以设置到SDK内置
     * 13, audioEffectNsMode: 语音处理选择, 默认使用平台内置,当你发现平台内置不好用时可以设置到SDK内置
     * 14, videoHwEncoderMode: 视频编码类型, 默认情况下不用设置.
     * 15, videoHwDecoderMode: 视频解码类型, 默认情况下不用设置.
     * 16, audioHighQuality: 高清语音，采用更高的采样率来传输语音
     * 17, audioDtx: 非连续发送，当监测到人声非活跃状态时减少数据包的发送
     */
    private void updateAVChatOptionalConfig()
    {
        avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_CALL_PROXIMITY, autoCallProximity);
        avChatParameters.setInteger(AVChatParameters.KEY_VIDEO_FIXED_CROP_RATIO, videoCropRatio);
        avChatParameters.setBoolean(AVChatParameters.KEY_VIDEO_ROTATE_IN_RENDING, videoAutoRotate);
        avChatParameters.setBoolean(AVChatParameters.KEY_SERVER_AUDIO_RECORD, serverRecordAudio);
        avChatParameters.setBoolean(AVChatParameters.KEY_SERVER_VIDEO_RECORD, serverRecordVideo);
        avChatParameters.setBoolean(AVChatParameters.KEY_VIDEO_DEFAULT_FRONT_CAMERA, defaultFrontCamera);
        avChatParameters.setInteger(AVChatParameters.KEY_VIDEO_QUALITY, videoQuality);
        avChatParameters.setBoolean(AVChatParameters.KEY_VIDEO_FPS_REPORTED, videoFpsReported);
        avChatParameters.setInteger(AVChatParameters.KEY_DEVICE_DEFAULT_ROTATION, deviceDefaultRotation);
        avChatParameters.setInteger(AVChatParameters.KEY_DEVICE_ROTATION_FIXED_OFFSET, deviceRotationOffset);

        if (videoMaxBitrate > 0)
        {
            avChatParameters.setInteger(AVChatParameters.KEY_VIDEO_MAX_BITRATE, videoMaxBitrate * 1024);
        }
        switch (audioEffectAecMode)
        {
        case 0:
            avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_ACOUSTIC_ECHO_CANCELER, AVChatAudioEffectMode.DISABLE);
            break;
        case 1:
            avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_ACOUSTIC_ECHO_CANCELER, AVChatAudioEffectMode.SDK_BUILTIN);
            break;
        case 2:
            avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_ACOUSTIC_ECHO_CANCELER, AVChatAudioEffectMode.PLATFORM_BUILTIN);
            break;
        }
        switch (audioEffectNsMode)
        {
        case 0:
            avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_NOISE_SUPPRESSOR, AVChatAudioEffectMode.DISABLE);
            break;
        case 1:
            avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_NOISE_SUPPRESSOR, AVChatAudioEffectMode.SDK_BUILTIN);
            break;
        case 2:
            avChatParameters.setString(AVChatParameters.KEY_AUDIO_EFFECT_NOISE_SUPPRESSOR, AVChatAudioEffectMode.PLATFORM_BUILTIN);
            break;
        }
        switch (videoHwEncoderMode)
        {
        case 0:
            avChatParameters.setString(AVChatParameters.KEY_VIDEO_ENCODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_AUTO);
            break;
        case 1:
            avChatParameters.setString(AVChatParameters.KEY_VIDEO_ENCODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_SOFTWARE);
            break;
        case 2:
            avChatParameters.setString(AVChatParameters.KEY_VIDEO_ENCODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_HARDWARE);
            break;
        }
        switch (videoHwDecoderMode)
        {
        case 0:
            avChatParameters.setString(AVChatParameters.KEY_VIDEO_DECODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_AUTO);
            break;
        case 1:
            avChatParameters.setString(AVChatParameters.KEY_VIDEO_DECODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_SOFTWARE);
            break;
        case 2:
            avChatParameters.setString(AVChatParameters.KEY_VIDEO_DECODER_MODE, AVChatMediaCodecMode.MEDIA_CODEC_HARDWARE);
            break;
        }
        avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_HIGH_QUALITY, audioHighQuality);
        avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_DTX_ENABLE, audioDtx);

        //观众角色,多人模式下使用. IM Demo没有多人通话, 全部设置为AVChatUserRole.NORMAL.
        avChatParameters.setInteger(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.NORMAL);
    }

    /**
     * 初始化，包含初始化音频管理器， 视频管理器和视频界面绘制管理器。
     *
     * @return boolean
     */
    public boolean initiation()
    {
        //AVChatProfile.getInstance().setAVChatting(true);
        //avChatAudio = new AVChatAudio(context,root.findViewById(R.id.avchat_audio_layout), this, this);
        //avChatVideo = new AVChatVideo(context, root.findViewById(R.id.avchat_video_layout), this, this);
        avChatSurface = new AVChatSurface(context, this, root.findViewById(R.id.avchat_surface_layout));

        return true;
    }


    /**
     * 来电
     */
    public void inComingCalling(AVChatData avChatData)
    {
        this.avChatData = avChatData;
        receiverId = avChatData.getAccount();

        //AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.RING);

        if (avChatData.getChatType() == AVChatType.AUDIO)
        {
            //onCallStateChange(CallStateEnum.INCOMING_AUDIO_CALLING);
        }
        else
        {
            //onCallStateChange(CallStateEnum.INCOMING_VIDEO_CALLING);
        }
    }

    /**
     * 拨打音视频
     */
    public void outGoingCalling(String account, final AVChatType callTypeEnum)
    {

        DialogMaker.showProgressDialog(context, null);

        AVChatSoundPlayer.instance().play(AVChatSoundPlayer.RingerTypeEnum.CONNECTING);

        this.receiverId = account;

        AVChatNotifyOption notifyOption = new AVChatNotifyOption();
        notifyOption.extendMessage = "extra_data";
//        默认forceKeepCalling为true，开发者如果不需要离线持续呼叫功能可以将forceKeepCalling设为false
//        notifyOption.forceKeepCalling = false;

        AVChatManager.getInstance().enableRtc();
        this.callingState = (callTypeEnum == AVChatType.VIDEO ? CallStateEnum.VIDEO : CallStateEnum.AUDIO);
        AVChatManager.getInstance().setParameters(avChatParameters);
        if (callTypeEnum == AVChatType.VIDEO)
        {
            AVChatManager.getInstance().enableVideo();
            AVChatManager.getInstance().startVideoPreview();
        }

        AVChatManager.getInstance().call2(account, callTypeEnum, notifyOption, new AVChatCallback<AVChatData>()
        {
            @Override
            public void onSuccess(AVChatData data)
            {
                avChatData = data;
                DialogMaker.dismissProgressDialog();
                //如果需要使用视频预览功能，在此进行设置，调用setupLocalVideoRender
                //如果不需要视频预览功能，那么删掉下面if语句代码即可
                if (callTypeEnum == AVChatType.VIDEO)
                {
                    List<String> deniedPermissions = BaseMPermission.getDeniedPermissions((Activity) context, BASIC_PERMISSIONS);
                    if (deniedPermissions != null && !deniedPermissions.isEmpty())
                    {
                        //avChatVideo.showNoneCameraPermissionView(true);
                        return;
                    }

//                    initLargeSurfaceView(DemoCache.getAccount());
                    initLargeSurfaceView(theApp.TEST3.getAccount());
                    canSwitchCamera = true;
                    onCallStateChange(CallStateEnum.OUTGOING_VIDEO_CALLING);
                }
            }

            @Override
            public void onFailed(int code)
            {
                LogUtil.d(TAG, "avChat call failed code->" + code);
                DialogMaker.dismissProgressDialog();

                AVChatSoundPlayer.instance().stop();

                if (code == ResponseCode.RES_FORBIDDEN)
                {
                    Toast.makeText(context, R.string.avchat_no_permission, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, R.string.avchat_call_failed, Toast.LENGTH_SHORT).show();
                }
                theApp.showToast("avChat call failed code->" + code);
                closeSessions(-1);
            }

            @Override
            public void onException(Throwable exception)
            {
                LogUtil.d(TAG, "avChat call onException->" + exception);
                DialogMaker.dismissProgressDialog();

                AVChatSoundPlayer.instance().stop();
            }
        });

        if (callTypeEnum == AVChatType.AUDIO)
        {
            onCallStateChange(CallStateEnum.OUTGOING_AUDIO_CALLING);
        }
        else
        {
            onCallStateChange(CallStateEnum.OUTGOING_VIDEO_CALLING);
        }
    }

    public void initLargeSurfaceView(String account)
    {
        avChatSurface.initLargeSurfaceView(account);
    }

    /**
     * 状态改变
     *
     * @param stateEnum
     */
    public void onCallStateChange(CallStateEnum stateEnum)
    {
        callingState = stateEnum;
        avChatSurface.onCallStateChange(stateEnum);
//        avChatAudio.onCallStateChange(stateEnum);
//        avChatVideo.onCallStateChange(stateEnum);
    }

    /**
     * 关闭本地音视频各项功能
     *
     * @param exitCode 音视频类型
     */
    public void closeSessions(int exitCode)
    {
        //not  user  hang up active  and warning tone is playing,so wait its end
        Log.i(TAG, "close session -> " + AVChatExitCode.getExitString(exitCode));
//        if (avChatAudio != null)
//            avChatAudio.closeSession(exitCode);
//        if (avChatVideo != null)
//            avChatVideo.closeSession(exitCode);
        showQuitToast(exitCode);
        isCallEstablish.set(false);
        canSwitchCamera = false;
        isClosedCamera = false;
        aVChatListener.uiExit();
    }

    /**
     * 给出结束的提醒
     *
     * @param code
     */
    public void showQuitToast(int code)
    {
        switch (code)
        {
        case AVChatExitCode.NET_CHANGE: // 网络切换
        case AVChatExitCode.NET_ERROR: // 网络异常
        case AVChatExitCode.CONFIG_ERROR: // 服务器返回数据错误
            Toast.makeText(context, R.string.avchat_net_error_then_quit, Toast.LENGTH_SHORT).show();
            break;
        case AVChatExitCode.PEER_HANGUP:
        case AVChatExitCode.HANGUP:
            if (isCallEstablish.get())
            {
                Toast.makeText(context, R.string.avchat_call_finish, Toast.LENGTH_SHORT).show();
            }
            break;
        case AVChatExitCode.PEER_BUSY:
            Toast.makeText(context, R.string.avchat_peer_busy, Toast.LENGTH_SHORT).show();
            break;
        case AVChatExitCode.PROTOCOL_INCOMPATIBLE_PEER_LOWER:
            Toast.makeText(context, R.string.avchat_peer_protocol_low_version, Toast.LENGTH_SHORT).show();
            break;
        case AVChatExitCode.PROTOCOL_INCOMPATIBLE_SELF_LOWER:
            Toast.makeText(context, R.string.avchat_local_protocol_low_version, Toast.LENGTH_SHORT).show();
            break;
        case AVChatExitCode.INVALIDE_CHANNELID:
            Toast.makeText(context, R.string.avchat_invalid_channel_id, Toast.LENGTH_SHORT).show();
            break;
        case AVChatExitCode.LOCAL_CALL_BUSY:
            Toast.makeText(context, R.string.avchat_local_call_busy, Toast.LENGTH_SHORT).show();
            break;
        default:
            break;
        }
    }

    public void peerVideoOff()
    {
        avChatSurface.peerVideoOff();
    }

    public void peerVideoOn()
    {
        avChatSurface.peerVideoOn();
    }


    private boolean needRestoreLocalVideo = false;
    private boolean needRestoreLocalAudio = false;

    //恢复视频和语音发送
    public void resumeVideo()
    {
        if (needRestoreLocalVideo)
        {
            AVChatManager.getInstance().muteLocalVideo(false);
            needRestoreLocalVideo = false;
        }

        if (needRestoreLocalAudio)
        {
            AVChatManager.getInstance().muteLocalAudio(false);
            needRestoreLocalAudio = false;
        }

    }

    //关闭视频和语音发送.
    public void pauseVideo()
    {

        if (!AVChatManager.getInstance().isLocalVideoMuted())
        {
            AVChatManager.getInstance().muteLocalVideo(true);
            needRestoreLocalVideo = true;
        }

        if (!AVChatManager.getInstance().isLocalAudioMuted())
        {
            AVChatManager.getInstance().muteLocalAudio(true);
            needRestoreLocalAudio = true;
        }
    }

    public boolean canSwitchCamera()
    {
        return canSwitchCamera;
    }

    public CallStateEnum getCallingState()
    {
        return callingState;
    }

    public String getVideoAccount()
    {
        return videoAccount;
    }

    public void setVideoAccount(String videoAccount)
    {
        this.videoAccount = videoAccount;
    }

    public String getAccount()
    {
        if (receiverId != null)
            return receiverId;
        return null;
    }

    public long getTimeBase()
    {
        return timeBase;
    }

    public void setTimeBase(long timeBase)
    {
        this.timeBase = timeBase;
    }

    public AVChatData getAvChatData()
    {
        return avChatData;
    }

    @Override
    public void onHangUp()
    {

    }

    @Override
    public void onRefuse()
    {

    }

    @Override
    public void onReceive()
    {

    }

    @Override
    public void toggleMute()
    {

    }

    @Override
    public void toggleSpeaker()
    {

    }

    @Override
    public void toggleRecord()
    {

    }

    @Override
    public void videoSwitchAudio()
    {

    }

    @Override
    public void audioSwitchVideo()
    {

    }

    @Override
    public void switchCamera()
    {

    }

    @Override
    public void closeCamera()
    {

    }
}
