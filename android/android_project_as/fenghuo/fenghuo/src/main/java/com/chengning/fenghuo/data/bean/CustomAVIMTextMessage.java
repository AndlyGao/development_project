package com.chengning.fenghuo.data.bean;

import com.avos.avoscloud.im.v2.AVIMMessageField;
import com.avos.avoscloud.im.v2.AVIMMessageType;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

@AVIMMessageType(type = 10)
public class CustomAVIMTextMessage extends AVIMTextMessage {
	
	@AVIMMessageField(name = "_lcstate")
	private SendState state;
	
	public enum SendState{
		SUCCESS,
		FAILTURE,
		SENDING
		;
	}

	public SendState getState() {
		return state;
	}

	public void setState(SendState state) {
		this.state = state;
	}
}
