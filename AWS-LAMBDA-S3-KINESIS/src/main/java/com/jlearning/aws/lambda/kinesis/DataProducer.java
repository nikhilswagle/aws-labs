package com.jlearning.aws.lambda.kinesis;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import com.jlearning.aws.lambda.kinesis.model.Payload;

public class DataProducer {
	
	private static final AmazonS3 S3_CLIENT = getS3Client();
	
	private static final AWSLambda LAMBDA_CLIENT = getLambdaClient();
	
	private static final String LAMBDA_FUNCTION_PUBLISH_MESSAGE = "arn:aws:lambda:us-east-1:664912755804:function:publish-message-handler";
	
	public static AmazonS3 getS3Client(){
		// Client Configuration
		ClientConfiguration config = new ClientConfiguration();
		config.withConnectionTimeout(110000);
		
		AmazonS3 client = AmazonS3ClientBuilder.standard()
													.withRegion(Regions.US_EAST_1)
													.withClientConfiguration(config)
													.build();
		return client;
	}
	
	public static AWSLambda getLambdaClient(){
		// Client Configuration
		ClientConfiguration config = new ClientConfiguration();
		config.withConnectionTimeout(110000);
		
		AWSLambda client = AWSLambdaAsyncClientBuilder.standard()
													.withRegion(Regions.US_EAST_1)
													.withClientConfiguration(config)
													.build();
		return client;
	}

	public List<String> fileReadHandler(S3Event event, Context context){
		LambdaLogger logger = context.getLogger();
		logger.log("Start reading object");
		printLineBreak(logger);
		//******************************* READ S3 OBJECT ***********************************//
		List<String> responseList = new ArrayList<String>();
		String response;
		InvokeResult result;
		List<S3EventNotificationRecord> recordList = event.getRecords();
		for(S3EventNotificationRecord record : recordList){
			logger.log("Region : "+record.getAwsRegion()); printLineBreak(logger);
			logger.log("Event Name : "+record.getEventName()); printLineBreak(logger);
			logger.log("Event Source : "+record.getEventSource()); printLineBreak(logger);
			S3Entity entity = record.getS3();
			S3Object object = S3_CLIENT.getObject(new GetObjectRequest(entity.getBucket().getName(), entity.getObject().getKey()));
			InputStream inStream = object.getObjectContent();
			
			response = entity.getBucket().getName()+"::"+entity.getObject().getKey();
			
			//Process input stream
			try{
				logger.log(response); printLineBreak(logger);
				logger.log(IOUtils.toString(inStream)); printLineBreak(logger);
				inStream.close();
				responseList.add(response);
			}
			catch(Exception ex){
				logger.log("Exception occured"); printLineBreak(logger);
			}
			
			// Publish to the topic 
			result = publishToTopic(response, logger);
			logger.log(result.toString()); printLineBreak(logger);
		}
		return responseList;
		//******************************* END ***********************************//
	}
	
	private void printLineBreak(LambdaLogger logger){
		logger.log("\n");
	}
	
	/**
	 * Invoke lambda function to send out email. 
	 * @param message
	 */
	private InvokeResult publishToTopic(String message, LambdaLogger logger){
		InvokeRequest request = new InvokeRequest();
		Payload payload = new Payload(message);
		logger.log("Invoking lambda function with payload: "+payload.toString());
		request.withFunctionName(LAMBDA_FUNCTION_PUBLISH_MESSAGE)
			   .withPayload(payload.toString())
			   .withInvocationType(InvocationType.RequestResponse)
			   .withLogType(LogType.None);
		
		return LAMBDA_CLIENT.invoke(request);
	}
}
