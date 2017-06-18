package com.jlearning.aws.lambda.sns.model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Payload {

	public Payload(){};
	
	public Payload(String message){
		this.message = message;
	}
	
	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString(){
		String response = null;
		try{
			ObjectMapper mapper = new ObjectMapper();
			response = mapper.writeValueAsString(this);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return response;
	}
}
