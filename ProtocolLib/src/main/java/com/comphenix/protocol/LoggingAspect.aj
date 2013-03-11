package com.comphenix.protocol;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.google.common.base.Joiner;

public aspect LoggingAspect {
	pointcut publicMethodExecuted(): execution(* com.comphenix.protocol.injector..*(..))
     	&& !within(com.comphenix..*.Builder) 
		&& !execution(* *.toString(..))
		&& !execution(* *.getPluginName(..)) 
		&& !execution(* *.intercept(..));
	
	// The logger we will use
	private Logger protocolLogger;
	private Joiner defaultJoiner;
	
	public LoggingAspect() throws SecurityException, IOException {
		protocolLogger = Logger.getLogger("ProtocolLib");
		
		// Add a file handler
		Handler handler = new FileHandler("protocol-lib.log", true);
		handler.setFormatter(new SimpleFormatter() {
			public String format(LogRecord record) {
				return new java.util.Date() + " " + record.getLevel() + " " + record.getMessage() + "\r\n";
			}
		});
		protocolLogger.setUseParentHandlers(false);
		protocolLogger.addHandler(handler);
		
		// Argument joiner
		defaultJoiner = Joiner.on(", ").useForNull("null");
	}
	
	after(): publicMethodExecuted() {
		Object[] args = thisJoinPoint.getArgs();
		String methodInfo = "Called " + thisJoinPoint.getSignature();
		
		if (args.length > 0) {
		    protocolLogger.info(methodInfo + " with " + defaultJoiner.join(args));
		} else {
			protocolLogger.info(methodInfo);
		}
	}
}
