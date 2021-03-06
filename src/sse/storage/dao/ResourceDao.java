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
package sse.storage.dao;

import sse.storage.bean.Resource;

/**
 * Class ResourceDao
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class ResourceDao extends BaseDao {

    public ResourceDao(String dbId) {
        this.tableName = this.prefix + "resources";
        this.beanClass = Resource.class;
        this.dbId = dbId;
    }  
}
