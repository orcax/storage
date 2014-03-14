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
 * Class Const
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public final class Const {

    public static final String STORAGE_CONFIG = "storage_config.xml";

    public static final String SQL_PROPERTIES = "mysql_sql.properties";

    public static final int MAX_BLOCK_SIZE = 200;
    public static final String ENCODING = "utf-8";
    public static final String DEFAULT_FORMAT = "data";

    public static final String BLOCK_STATUS_USE = "use";
    public static final String BLOCK_STATUS_FULL = "full";

    public static final String RES_STATUS_NORMAL = "normal";
    public static final String RES_STATUS_DELETED = "deleted";

    private Const() {
    }
}
