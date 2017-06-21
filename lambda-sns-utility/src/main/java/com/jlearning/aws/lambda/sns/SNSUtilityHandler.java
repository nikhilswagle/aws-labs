package com.jlearning.aws.lambda.sns;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.ConfirmSubscriptionResult;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.jlearning.aws.lambda.sns.model.ConfirmSubscriptionReq;
import com.jlearning.aws.lambda.sns.model.PublishToTopicRequest;
import com.jlearning.aws.lambda.sns.model.SubscribeEmailRequest;

public class SNSUtilityHandler {
	
	private static final AmazonSNS client = getClient();
	
	public static AmazonSNS getClient(){
		// Client Configuration
		ClientConfiguration config = new ClientConfiguration();
		config.withConnectionTimeout(110000);
		
		AmazonSNS client = AmazonSNSClientBuilder.standard()
													.withRegion(Regions.US_EAST_1)
													.withClientConfiguration(config)
													.build();
		return client;
	}

	public CreateTopicResult createTopic(String topicName, Context context){
		LambdaLogger logger = context.getLogger();
		logger.log("Start creating topic:"+topicName);
		//******************************* CREATE TOPIC ***********************************//		
		// Create Topic Request
		CreateTopicRequest request = new CreateTopicRequest(topicName);		
		// Send request to create the topic. 
		// Idempotent request - New topic will not be created if it already exists.
		CreateTopicResult response = client.createTopic(request);		
		logger.log("Topic Created : "+response.getTopicArn());
		return response;
		//******************************* END ***********************************//
	}
	
	public SubscribeResult subscribeEmailToTopic(SubscribeEmailRequest emailRequest, Context context){
		LambdaLogger logger = context.getLogger();
		logger.log("Start email subscription for: "+emailRequest.getEmailAddress());
		//******************************* SUBSCRIBE EMAIL TO TOPIC ***********************************//
		return subscribeToTopic(emailRequest, "email", context);
		//******************************* END ***********************************//
	}
	
	private SubscribeResult subscribeToTopic(SubscribeEmailRequest emailRequest, String protocol, Context context){
		LambdaLogger logger = context.getLogger();
		//******************************* SUBSCRIBE TOPIC ***********************************//
		SubscribeRequest request = new SubscribeRequest(createTopic(emailRequest.getTopicName(), context).getTopicArn(), "email", emailRequest.getEmailAddress());
		SubscribeResult response = client.subscribe(request);
		logger.log(response.toString());
		//******************************* END ***********************************//
		return response;
	}
	
	public ConfirmSubscriptionResult confirmSubscription(ConfirmSubscriptionReq req , Context context){
		LambdaLogger logger = context.getLogger();
		logger.log("Start confirming subscription: "+req.getToken());
		//******************************* CONFIRM SUBSCRIPTION ***********************************//
		ConfirmSubscriptionRequest request = new ConfirmSubscriptionRequest(createTopic(req.getTopicName(), context).getTopicArn(), req.getToken());
		ConfirmSubscriptionResult response = client.confirmSubscription(request);
		logger.log(response.toString());
		return response;
		//******************************* END ***********************************//
	}
	
	public PublishResult publish(PublishToTopicRequest req, Context context){
		LambdaLogger logger = context.getLogger();
		logger.log("Start publishing to topic: "+req.getMessage());
		//******************************* PUBLISH MESSAGE ***********************************//
		PublishRequest request = new PublishRequest(createTopic(req.getTopicName(), context).getTopicArn(), req.getMessage(), req.getSubject());
		PublishResult response = client.publish(request);
		logger.log(response.toString());
		return response;
		//******************************* END ***********************************//
	}
}
