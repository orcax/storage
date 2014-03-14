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

import java.sql.Timestamp;

/**
 * Class Toolkit
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public final class Toolkit {

    public static final boolean isEmpty(String str) {
	return str == null || str.isEmpty();
    }

    public static final boolean isEmpty(String... strs) {
	for (String str : strs) {
	    if (isEmpty(str)) {
		return true;
	    }
	}
	return false;
    }

    public static final void info(Object obj) {
	System.out.println("[INFO] " + obj);
    }

    public static final void error(Object obj) {
	System.out.println("[ERROR] " + obj);
    }

    public static final Timestamp now() {
	return new Timestamp(System.currentTimeMillis());
    }

    private Toolkit() {
    }
}
