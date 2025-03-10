package com.chengning.fenghuo.data.bean;

import java.io.Serializable;

//At_new @我的
//comment_new 评论
//Dig_new 赞我的
//Dynamic_new 动态
//Fans_new 关注
//newpm 消息
//qun_notice_new 圈子通知
//notice_new 通知
//my_new 我的
public class PushUserDataBean implements Serializable {

	private static final long serialVersionUID = -3742428424249471896L;
	
	private String at_new;
	private String comment_new;
	private String dig_new;
	private String dynamic_new;
	private String fans_new;
	private String newpm;
	private String qun_notice_new;
	private String notice_new;
	private String my_new;

	public String getAt_new() {
		return at_new;
	}

	public void setAt_new(String at_new) {
		this.at_new = at_new;
	}

	public String getComment_new() {
		return comment_new;
	}

	public void setComment_new(String comment_new) {
		this.comment_new = comment_new;
	}

	public String getDig_new() {
		return dig_new;
	}

	public void setDig_new(String dig_new) {
		this.dig_new = dig_new;
	}

	public String getDynamic_new() {
		return dynamic_new;
	}

	public void setDynamic_new(String dynamic_new) {
		this.dynamic_new = dynamic_new;
	}

	public String getFans_new() {
		return fans_new;
	}

	public void setFans_new(String fans_new) {
		this.fans_new = fans_new;
	}

	public String getNewpm() {
		return newpm;
	}

	public void setNewpm(String newpm) {
		this.newpm = newpm;
	}

	public String getQun_notice_new() {
		return qun_notice_new;
	}

	public void setQun_notice_new(String qun_notice_new) {
		this.qun_notice_new = qun_notice_new;
	}

	public String getNotice_new() {
		return notice_new;
	}

	public void setNotice_new(String notice_new) {
		this.notice_new = notice_new;
	}

	public String getMy_new() {
		return my_new;
	}

	public void setMy_new(String my_new) {
		this.my_new = my_new;
	}

}
