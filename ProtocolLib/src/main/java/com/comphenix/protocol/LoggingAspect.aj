package com.comphenix.protocol;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.google.common.base.Joiner;

public aspect LoggingAspect {
	pointcut publicMethodExecuted(): execution(* com.comphenix.protocol..*(..))
     	&& !within(com.comphenix..*.Builder) 
		&& !within(com.comphenix.protocol.LoggingAspect)
		&& !execution(* *.toString(..))
		&& !execution(* *.getPluginName(..));
	
	// The logger we will use
	private Logger protocolLogger;
	private Joiner defaultJoiner;
	
	public LoggingAspect() throws SecurityException, IOException {
		protocolLogger = Logger.getLogger("ProtocolLib");
		
		// Add a file handler
		Handler handler = new FileHandler("protocol-lib.log", true);
		handler.setFormatter(new SimpleFormatter());
		protocolLogger.setUseParentHandlers(false);
		protocolLogger.addHandler(handler);
		
		// Argument joiner
		defaultJoiner = Joiner.on(", ").useForNull("null");
	}
	
	after(): publicMethodExecuted() {
	    protocolLogger.info("Called " + 
	    	thisJoinPoint.getSignature() + " with " + defaultJoiner.join(thisJoinPoint.getArgs())
	    );
	}
}
