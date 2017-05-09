package com.demo.yun.session.action;

import com.demo.yun.R;
import com.netease.nim.uikit.session.actions.BaseAction;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class GuessAction extends BaseAction
{

    public GuessAction()
    {
        super(R.drawable.message_plus_guess_selector, R.string.input_panel_guess);
    }

    @Override
    public void onClick()
    {
       //GuessAttachment attachment = new GuessAttachment();
        //IMMessage message;
        //if (getContainer() != null && getContainer().sessionType == SessionTypeEnum.ChatRoom)
        //{
        //    message = ChatRoomMessageBuilder.createChatRoomCustomMessage(getAccount(), attachment);
        //}
        //else
        //{
        //    message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), attachment.getValue().getDesc(), attachment);
        //}

       //sendMessage(message);
    }
}
