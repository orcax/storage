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
package sse.storage.db.dao;

import sse.storage.db.bean.Picture;

/**
 * Class PictureDao
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class PictureDao extends BaseDao {

    public static final PictureDao INSTANCE = new PictureDao();

    private PictureDao() {
	super();
	this.tableName = this.prefix + "pictures";
	this.beanClass = Picture.class;
	this.createTable();
    }

}
