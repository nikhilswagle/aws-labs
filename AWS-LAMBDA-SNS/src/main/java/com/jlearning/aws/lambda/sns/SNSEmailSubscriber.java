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
import com.jlearning.aws.lambda.sns.model.Payload;

public class SNSEmailSubscriber {
	
	private static final AmazonSNS client = getClient();
	private static final String TOPIC_NAME = "test-sns-topic";
	private static final String SUBJECT = "S3 File Upload Event !!!";
	
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

	public CreateTopicResult createTopicHandler(String topicName, Context context){
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
	
	public SubscribeResult subscribeEmailHandler(String email, Context context){
		LambdaLogger logger = context.getLogger();
		logger.log("Start email subscription:"+email);
		//******************************* SUBSCRIBE TOPIC ***********************************//
		// Subscribe to the topic.
		SubscribeRequest request = new SubscribeRequest(createTopicHandler(TOPIC_NAME, context).getTopicArn(), "email", email);
		SubscribeResult response = client.subscribe(request);
		logger.log(response.toString());
		return response;
		//******************************* END ***********************************//
	}
	
	public ConfirmSubscriptionResult confirmSubscriptionHandler(String token, Context context){
		LambdaLogger logger = context.getLogger();
		logger.log("Start confirming subscription:"+token);
		//******************************* CONFIRM SUBSCRIPTION ***********************************//
		ConfirmSubscriptionRequest request = new ConfirmSubscriptionRequest(createTopicHandler(TOPIC_NAME, context).getTopicArn(), token);
		ConfirmSubscriptionResult response = client.confirmSubscription(request);
		logger.log(response.toString());
		return response;
		//******************************* END ***********************************//
	}
	
	public PublishResult publishHandler(Payload message, Context context){
		LambdaLogger logger = context.getLogger();
		logger.log("Start publishing to topic: "+message.getMessage());
		//******************************* PUBLISH MESSAGE ***********************************//
		PublishRequest request = new PublishRequest(createTopicHandler(TOPIC_NAME, context).getTopicArn(), message.getMessage(), SUBJECT);
		PublishResult response = client.publish(request);
		logger.log(response.toString());
		return response;
		//******************************* END ***********************************//
	}
}
