package com.jlearning.aws.lambda.kinesis.model;

public class DataRetrievalRequest {

	private String url;
	
	private String symbol;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
