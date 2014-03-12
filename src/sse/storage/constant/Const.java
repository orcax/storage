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
package sse.storage.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Class Const
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public final class Const {

    public static final String STORAGE_CONFIG = "storage_config.xml";

    public static final String SQL_PROPERTIES = "mysql_sql.properties";

    public static final int BLOCK_SIZE = 200;

    public static final Map<ResourceType, String> RESOURCE_DIR = new HashMap<ResourceType, String>();
    static {
	RESOURCE_DIR.put(ResourceType.PICTURE, "/pictures");
	RESOURCE_DIR.put(ResourceType.POST, "/posts");
	RESOURCE_DIR.put(ResourceType.UNKNOWN, "/unknown");
    }

    private Const() {
    }
}
