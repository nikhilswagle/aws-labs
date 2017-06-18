package com.jlearning.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class HelloWorld {

	public String myHandlerFunction(String name, Context context){
		LambdaLogger logger = context.getLogger();
		String echoName = "Hello.... "+name; 
		logger.log(echoName);
		return echoName;
	}
}

