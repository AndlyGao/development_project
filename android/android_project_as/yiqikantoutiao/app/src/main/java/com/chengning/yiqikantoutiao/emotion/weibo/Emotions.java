package com.chengning.yiqikantoutiao.emotion.weibo;

import java.io.Serializable;
import java.util.List;

public class Emotions implements Serializable {

	private List<Emotion> emotions;

	public List<Emotion> getEmotions() {
		return emotions;
	}

	public void setEmotions(List<Emotion> emotions) {
		this.emotions = emotions;
	}
	
}
