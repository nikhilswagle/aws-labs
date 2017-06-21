package com.jlearning.aws.lambda.sns.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SubscribeEmailRequest {

	@JsonProperty("topicName")
	private String topicName;
	
	@JsonProperty("emailAddress")
	private String emailAddress;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
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
