package com.chengning.fenghuo.data.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageEntryBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1132063573119275557L;
	String msgfrom;
	int msgfromid;
	String msgnickname;
	String msgto;
	int msgtoid;
	String tonickname;
	String face;
	String dateline;
	ArrayList<String> message;
	ArrayList<Image> image_list;
	int is_new;
	String url;

	// local
	String local_state;
	String local_stick;
	String local_lasttime;
	int local_other_id;
	
	private int pos;

	public String getMsgfrom() {
		return msgfrom;
	}

	public void setMsgfrom(String msgfrom) {
		this.msgfrom = msgfrom;
	}

	public int getMsgfromid() {
		return msgfromid;
	}

	public void setMsgfromid(int msgfromid) {
		this.msgfromid = msgfromid;
	}

	public String getMsgnickname() {
		return msgnickname;
	}

	public void setMsgnickname(String msgnickname) {
		this.msgnickname = msgnickname;
	}

	public String getMsgto() {
		return msgto;
	}

	public void setMsgto(String msgto) {
		this.msgto = msgto;
	}

	public int getMsgtoid() {
		return msgtoid;
	}

	public void setMsgtoid(int msgtoid) {
		this.msgtoid = msgtoid;
	}

	public String getTonickname() {
		return tonickname;
	}

	public void setTonickname(String tonickname) {
		this.tonickname = tonickname;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public ArrayList<String> getMessage() {
		return message;
	}

	public void setMessage(ArrayList<String> message) {
		this.message = message;
	}

	public ArrayList<Image> getImage_list() {
		return image_list;
	}

	public void setImage_list(ArrayList<Image> image_list) {
		this.image_list = image_list;
	}

	public int getIs_new() {
		return is_new;
	}

	public void setIs_new(int is_new) {
		this.is_new = is_new;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLocal_state() {
		return local_state;
	}

	public void setLocal_state(String local_state) {
		this.local_state = local_state;
	}

	public String getLocal_stick() {
		return local_stick;
	}

	public void setLocal_stick(String local_stick) {
		this.local_stick = local_stick;
	}

	public String getLocal_lasttime() {
		return local_lasttime;
	}

	public void setLocal_lasttime(String local_lasttime) {
		this.local_lasttime = local_lasttime;
	}

	public int getLocal_other_id() {
		return local_other_id;
	}

	public void setLocal_other_id(int local_other_id) {
		this.local_other_id = local_other_id;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
	
}
