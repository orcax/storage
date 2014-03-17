/*
 * Chris X.
 * 
 * Copyright 2012-2014 Zhang Chenxi Project, SSE, Tongji University.
 * 
 * This software is the confidential and proprietary information of 
 * Zhang Chenxi project. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of 
 * the license agreement you participate in the project work. 
 */
package sse.storage.except;

/**
 * Class ConfigInitException
 * 
 * @version 2014.3.10
 * @author Chris X. 
 */
@SuppressWarnings("serial")
public class ConfigException extends Exception {

    private String errorMsg;

    public ConfigException() {

    }

    public ConfigException(String errorMsg) {
	this.errorMsg = errorMsg;
    }

    @Override
    public void printStackTrace() {
	System.out.println("\n[ERROR] " + errorMsg);
	super.printStackTrace();
    }
}
