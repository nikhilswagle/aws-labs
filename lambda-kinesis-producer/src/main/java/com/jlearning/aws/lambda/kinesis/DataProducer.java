package com.jlearning.aws.lambda.kinesis;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.jlearning.aws.lambda.kinesis.model.DataRetrievalRequest;
import com.jlearning.aws.lambda.kinesis.model.ScheduledEvent;
import com.jlearning.aws.lambda.kinesis.util.StockDataUtil;
import com.jlearning.aws.lambda.rest.RestClient;

public class DataProducer {
	
	private static final AmazonKinesis KINESIS_CLIENT = getKinesisClient();
	
	//private static final String ARN_KINESIS_STREAM = "arn:aws:kinesis:us-east-1:664912755804:stream/stock-data-stream";
	
	private static final String KINESIS_STREAM_NAME = "stock-data-stream";
	
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
	public List<PutRecordResult> dataProductionHandler(ScheduledEvent event, Context context){
		logger = context.getLogger();
		List<PutRecordResult> response = new ArrayList<PutRecordResult>();
		String partitionKey = null;
		InputStream inStream = null;
		try{
			RestClient client = StockDataUtil.REST_CLIENT;
			List<DataRetrievalRequest> reqList = StockDataUtil.generateStockDataRetrievalUrls();
			for(DataRetrievalRequest req : reqList){
				inStream = client.sendGetRequest(req.getUrl());
				String stockData = IOUtils.toString(inStream);
				/*System.out.println(req.getSymbol() + "->" + req.getUrl());
				System.out.println(stockData);*/
				logger.log("Stock Data: "+stockData);printLineBreak();				
				partitionKey = req.getSymbol();
				logger.log("Partition Key: "+partitionKey);printLineBreak();
				PutRecordRequest request = new PutRecordRequest();
				request.withStreamName(KINESIS_STREAM_NAME)
				   	   .withPartitionKey(partitionKey)
				   	   .withData(ByteBuffer.wrap(stockData.getBytes()));
				response.add(KINESIS_CLIENT.putRecord(request));
				inStream.close();
			}
		}
		catch(Exception ex){
			logger.log(ex.getMessage());
		}
		return response;
	}
	
	private void printLineBreak(){
		logger.log("\n");
	}
}
