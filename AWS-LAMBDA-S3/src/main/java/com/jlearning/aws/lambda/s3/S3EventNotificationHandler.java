package com.jlearning.aws.lambda.s3;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.LogType;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.jlearning.aws.lambda.sns.model.PublishToTopicRequest;

public class S3EventNotificationHandler {
	
	private static final AmazonS3 S3_CLIENT = getS3Client();
	
	private static final AWSLambda LAMBDA_CLIENT = getLambdaClient();
	
	private static final String ARN_LAMBDA_FUNCTION_PUBLISH_MESSAGE = "arn:aws:lambda:us-east-1:664912755804:function:publish-message-handler";
	
	private static final String S3_LOAD_EVENT_TOPIC = "s3-event-topic";
	
	private static final String S3_LOAD_EVENT_EMAIL_SUBJECT = "NEW OBJECT ADDED";
		
	private static LambdaLogger logger;
	
	/**
	 * Return client for Amazon S3 service.
	 * @return
	 */
	private static AmazonS3 getS3Client(){
		// Client Configuration
		ClientConfiguration config = new ClientConfiguration();
		config.withConnectionTimeout(110000);
		
		AmazonS3 client = AmazonS3ClientBuilder.standard()
													.withRegion(Regions.US_EAST_1)
													.withClientConfiguration(config)
													.build();
		return client;
	}
	
	/**
	 * Return client for Amazon Lambda service.
	 * @return
	 */
	private static AWSLambda getLambdaClient(){
		// Client Configuration
		ClientConfiguration config = new ClientConfiguration();
		config.withConnectionTimeout(110000);
		
		AWSLambda client = AWSLambdaAsyncClientBuilder.standard()
													.withRegion(Regions.US_EAST_1)
													.withClientConfiguration(config)
													.build();
		return client;
	}	

	/**
	 * Lambda function that gets triggered on S3 Event.
	 * Additional configuration is required to 
	 * 1. Set up trigger on lambda function for S3 putObject event for a specific bucket/object
	 * 2. Set up event on the specific S3 bucket for an object with certain prefix and suffix
	 * 
	 * The function reads the file data and publishes the bucket/filenames to the SNS topic so that email
	 * is sent to subscribers of the topic.
	 *  
	 * @param event
	 * @param context
	 * @return
	 */
	public Map<String, List<String>> notifyS3Event(S3Event event, Context context){
		logger = context.getLogger();
		logger.log("Start reading object");
		printLineBreak();
		//******************************* READ S3 OBJECT ***********************************//
		Map<String, List<String>> response = new HashMap<String, List<String>>();
		String s3Obj;
		String data = null;
		InvokeResult result;
		List<S3EventNotificationRecord> recordList = event.getRecords();
		for(S3EventNotificationRecord record : recordList){
			logger.log("Region : "+record.getAwsRegion()); printLineBreak();
			logger.log("Event Name : "+record.getEventName()); printLineBreak();
			logger.log("Event Source : "+record.getEventSource()); printLineBreak();
			S3Entity entity = record.getS3();
			S3Object object = S3_CLIENT.getObject(new GetObjectRequest(entity.getBucket().getName(), entity.getObject().getKey()));
			InputStream inStream = object.getObjectContent();			
			s3Obj = entity.getBucket().getName()+"::"+entity.getObject().getKey();
			
			// Add bucket wise list of files
			addToResponse(response, entity);
			
			//Process input stream
			try{
				logger.log(s3Obj); printLineBreak();
				data = IOUtils.toString(inStream);
				logger.log(data); printLineBreak();
				inStream.close();
			}
			catch(Exception ex){
				logger.log("Exception occured"); printLineBreak();
			}		
		}
		// Publish to the topic
		PublishToTopicRequest publishReq = new PublishToTopicRequest();
		publishReq.setTopicName(S3_LOAD_EVENT_TOPIC);
		publishReq.setSubject(S3_LOAD_EVENT_EMAIL_SUBJECT);
		publishReq.setMessage(buildMessageToPublish(response));
		result = publishToTopic(publishReq);
		logger.log(result.toString()); printLineBreak();
		
		return response;
		//******************************* END ***********************************//
	}
	
	private void printLineBreak(){
		logger.log("\n");
	}
	
	/**
	 * Invoke lambda function to send out email. 
	 * @param message
	 */
	private InvokeResult publishToTopic(PublishToTopicRequest publishReq){
		InvokeRequest request = new InvokeRequest();
		logger.log("Invoking lambda function with payload: "+publishReq.toString());
		request.withFunctionName(ARN_LAMBDA_FUNCTION_PUBLISH_MESSAGE)
			   .withPayload(publishReq.toString())
			   .withInvocationType(InvocationType.RequestResponse)
			   .withLogType(LogType.None);		
		return LAMBDA_CLIENT.invoke(request);
	}
	
	private String buildMessageToPublish(Map<String, List<String>> response){
		String baseContent = "Given below is the list of objects added.\n\n";
		
		StringBuffer bucket = null;
		for(Entry<String, List<String>> entry : response.entrySet()){
			bucket = new StringBuffer("Bucket: ");
			bucket.append(entry.getKey());
			bucket.append("\n");
			
			for(String obj : entry.getValue()){
				bucket.append("\t");
				bucket.append(obj);
				bucket.append("\n");
			}
			
			bucket.append("\n");
		}		
		return baseContent+bucket;
	}
	
	private void addToResponse(Map<String, List<String>> response, S3Entity entity){
		String key = entity.getBucket().getName();
		String objName = entity.getObject().getKey();
		if(!response.containsKey(key)){
			response.put(key, new ArrayList<String>());
		}
		response.get(key).add(objName);
	}
}
