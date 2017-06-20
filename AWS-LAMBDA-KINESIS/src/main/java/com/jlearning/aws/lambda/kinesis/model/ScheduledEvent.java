package com.jlearning.aws.lambda.kinesis.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScheduledEvent {

	@JsonProperty("account")
	private String account;
	
	@JsonProperty("region")
	private String region;
	
	@JsonProperty("detail")
	private Detail detail;
	
	@JsonProperty("detail-type")
	private String detailType;
	
	@JsonProperty("source")
	private String source;
	
	@JsonProperty("time")
	private String time;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("resources")
	private List<String> resources;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Detail getDetail() {
		return detail;
	}

	public void setDetail(Detail detail) {
		this.detail = detail;
	}

	public String getDetailType() {
		return detailType;
	}

	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}
}
