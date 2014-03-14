package sse.storage.dao;

import sse.storage.bean.Block;

public class BlockDao extends BaseDao {

    public static final BlockDao INSTANCE = new BlockDao();

    private BlockDao() {
	super();
	this.tableName = this.prefix + "blocks";
	this.beanClass = Block.class;
	this.createTable();
    }
}
