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
package sse.storage.etc;

/**
 * Class ResourceType
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public enum ResourceType {
    PICTURE("picture"), POST("post"), OTHER("other");

    private final String value;

    private ResourceType(String type) {
	this.value = type;
    }

    @Override
    public String toString() {
	return this.value;
    }

    public static ResourceType toEnum(String value) {
	for (ResourceType rt : ResourceType.values()) {
	    if (rt.value.equalsIgnoreCase(value)) {
		return rt;
	    }
	}
	return null;
    }
}
