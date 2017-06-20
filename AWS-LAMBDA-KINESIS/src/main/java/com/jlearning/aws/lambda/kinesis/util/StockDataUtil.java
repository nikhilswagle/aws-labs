package com.jlearning.aws.lambda.kinesis.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.jlearning.aws.lambda.kinesis.model.DataRetrievalRequest;
import com.jlearning.aws.lambda.rest.RestClient;

public class StockDataUtil {

	private static final String FILE_TICKER_LIST = "tickerSymbols.properties";
	private static final String FILE_CONFIG = "stockDataConfig.properties";
	public static final Properties CONFIG_PROPS = initializeConfigProperties();
	public static final Properties TICKER_PROPS = initializeTickerProperties();
	public static final RestClient REST_CLIENT = getRestClient();
	
	private static Properties initializeConfigProperties(){
		//InputStream configStream = ClassLoader.getSystemResourceAsStream(FILE_CONFIG);
		InputStream configStream = StockDataUtil.class.getClassLoader().getResourceAsStream(FILE_CONFIG);
		Properties configProps = new Properties();
		try {
			configProps.load(configStream);
			configStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return configProps;
	}
	
	private static Properties initializeTickerProperties(){
		//InputStream ticketListStream = ClassLoader.getSystemResourceAsStream(FILE_TICKER_LIST);
		InputStream ticketListStream = StockDataUtil.class.getClassLoader().getResourceAsStream(FILE_TICKER_LIST);
		Properties tickerProps = new Properties();
		try {
			tickerProps.load(ticketListStream);
			ticketListStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tickerProps;
	}
	
	private static RestClient getRestClient(){
		RestClient client = new RestClient();
		client.setConnTimeOut(Integer.valueOf(CONFIG_PROPS.getProperty("connection.timeout")).intValue());
		client.setReadTimeOut(Integer.valueOf(CONFIG_PROPS.getProperty("read.timeout")).intValue());
		client.setMaxRetryCount(Integer.valueOf(CONFIG_PROPS.getProperty("max.retry.count")).intValue());
		return client;
	}
	
	public static List<DataRetrievalRequest> generateStockDataRetrievalUrls(){
		List<DataRetrievalRequest> dataRetrievalUrls = new ArrayList<DataRetrievalRequest>();
		String baseUrl = CONFIG_PROPS.getProperty("base.url.function.globalQuote");
		String apiKey = CONFIG_PROPS.getProperty("api.key");
		String url = null;
		DataRetrievalRequest request;
		for(Entry<Object, Object> entry : TICKER_PROPS.entrySet()){
			url = baseUrl+"&symbol="+entry.getValue()+"&apikey="+apiKey;
			request = new DataRetrievalRequest();
			request.setSymbol(entry.getValue().toString());
			request.setUrl(url);
			dataRetrievalUrls.add(request);
		}		
		return dataRetrievalUrls;
	}	
}
