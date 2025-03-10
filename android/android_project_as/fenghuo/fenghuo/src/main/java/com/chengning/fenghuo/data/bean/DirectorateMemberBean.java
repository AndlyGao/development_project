package com.chengning.fenghuo.data.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @description 指挥部成员对象
 * @author wangyungang
 * @date 2015.7.20 9:59
 *
 */
public class DirectorateMemberBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -123153553647247038L;

	/**
	 * 头像
	 */
	private String face;
	
	/**
	 * 昵称
	 */
	private String nickname;
	
	/**
	 * 等级
	 */
	private String role_name;
	
	/**
	 * 勋章
	 */
	private ArrayList<String> medal_list;
	
	/**
	 * 积分
	 */
	private String extcredits1;
	
	/**
	 * 金币
	 */
	private String extcredits2;

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getRole_name() {
		return role_name;
	}

	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}

	public String getExtcredits1() {
		return extcredits1;
	}

	public void setExtcredits1(String extcredits1) {
		this.extcredits1 = extcredits1;
	}

	public String getExtcredits2() {
		return extcredits2;
	}

	public void setExtcredits2(String extcredits2) {
		this.extcredits2 = extcredits2;
	}

	public ArrayList<String> getMedal_list() {
		return medal_list;
	}

	public void setMedal_list(ArrayList<String> medal_list) {
		this.medal_list = medal_list;
	}

}
