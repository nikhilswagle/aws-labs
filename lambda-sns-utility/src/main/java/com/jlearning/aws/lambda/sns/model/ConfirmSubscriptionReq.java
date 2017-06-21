package com.jlearning.aws.lambda.sns.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfirmSubscriptionReq {

	@JsonProperty("topicName")
	private String topicName;
	
	@JsonProperty("token")
	private String token;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString(){
		String returnVal = null;
		try{
			ObjectMapper mapper = new ObjectMapper();
			returnVal = mapper.writeValueAsString(this);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return returnVal;
	}
}
