package com.jlearning.aws.lambda.sns.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PublishToTopicRequest {

	@JsonProperty("topicName")
	private String topicName;
	
	@JsonProperty("subject")
	private String subject;
	
	@JsonProperty("message")
	private String message;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
