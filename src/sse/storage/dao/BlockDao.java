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

import sse.storage.bean.Block;

/**
 * 
 * @author orcax
 * 
 */
public class BlockDao extends BaseDao {

    private static BlockDao instance = null;

    private BlockDao() {
        super();
        this.tableName = this.prefix + "blocks";
        this.beanClass = Block.class;
    }

    public static synchronized BlockDao getInstance() {
        if (instance == null) {
            instance = new BlockDao();
        }
        return instance;
    }

}
