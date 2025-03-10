package com.shenyuan.militarynews.beans.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ArticlesPicBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5213390818519865280L;
	private String tid;
	String title;
	String link;
	String image;
	String description;
	ArrayList<String> content;
	ArrayList<ArticleItemPicBean> pics;
	String pubDate;
	String category;
	String author;
	int click;
	CommentListBean comments;

	private int goodpost;
	private int badpost;

	String video_play;
	String video_html;
	String video_photo;
	String is_favor;

	public int getGoodpost() {
		return goodpost;
	}

	public void setGoodpost(int goodpost) {
		this.goodpost = goodpost;
	}

	public int getBadpost() {
		return badpost;
	}

	public void setBadpost(int badpost) {
		this.badpost = badpost;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<String> getContent() {
		return content;
	}

	public void setContent(ArrayList<String> content) {
		this.content = content;
	}

	public ArrayList<ArticleItemPicBean> getPics() {
		return pics;
	}

	public void setPics(ArrayList<ArticleItemPicBean> pics) {
		this.pics = pics;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getClick() {
		return click;
	}

	public void setClick(int click) {
		this.click = click;
	}

	public CommentListBean getComments() {
		return comments;
	}

	public void setComments(CommentListBean comments) {
		this.comments = comments;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getVideo_play() {
		return video_play;
	}

	public void setVideo_play(String video_play) {
		this.video_play = video_play;
	}

	public String getVideo_html() {
		return video_html;
	}

	public void setVideo_html(String video_html) {
		this.video_html = video_html;
	}

	public String getVideo_photo() {
		return video_photo;
	}

	public void setVideo_photo(String video_photo) {
		this.video_photo = video_photo;
	}

	public String getIs_favor() {
		return is_favor;
	}

	public void setIs_favor(String is_favor) {
		this.is_favor = is_favor;
	}

}
