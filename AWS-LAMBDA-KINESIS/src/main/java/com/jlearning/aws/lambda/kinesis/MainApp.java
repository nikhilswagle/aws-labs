package com.jlearning.aws.lambda.kinesis;

import com.amazonaws.regions.Regions;
import com.jlearning.aws.lambda.kinesis.model.ScheduledEvent;


public class MainApp {
	public static void main(String[] args) {
		ScheduledEvent event = new ScheduledEvent();
		event.setRegion(Regions.US_EAST_1.name());
		DataProducer producer = new DataProducer();
		try{
			producer.dataProductionHandler(event, null);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
