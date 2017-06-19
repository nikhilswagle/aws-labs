package com.jlearning.aws.lambda.kinesis;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class DataProducer {
	
	private static final AmazonKinesis KINESIS_CLIENT = getKinesisClient();
	
	private static final String ARN_KINESIS_STREAM = "arn:aws:kinesis:us-east-1:664912755804:stream/stock-data-stream";
	
	private static LambdaLogger logger;
	
	private static AmazonKinesis getKinesisClient(){
		// Client Configuration
		ClientConfiguration config = new ClientConfiguration();
		config.withConnectionTimeout(110000);
		
		AmazonKinesis client = AmazonKinesisClientBuilder.standard()
													.withRegion(Regions.US_EAST_1)
													.withClientConfiguration(config)
													.build();
		return client;
		
	}

	/**
	 * Lambda function that is scheduled retrieve stock data for certain tickers and
	 * send the data stream to kinesis
	 * Data is further analyzed using Kinesis Analytics.  
	 * @param event
	 * @param context
	 * @return
	 */
	public List<PutRecordResult> dataProductionHandler(String data, Context context){
		List<PutRecordResult> response = new ArrayList<PutRecordResult>();
		String partitionKey = null;
		partitionKey = "";
		logger.log("Partition Key: "+partitionKey);printLineBreak();
		PutRecordRequest request = new PutRecordRequest();
		request.withStreamName(ARN_KINESIS_STREAM)
		   	   .withPartitionKey(partitionKey)
		   	   .withData(ByteBuffer.wrap(data.getBytes()));
		response.add(KINESIS_CLIENT.putRecord(request));
		return response;
	}
	
	private void printLineBreak(){
		logger.log("\n");
	}
}
