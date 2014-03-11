package sse.storage.db.dao;

import sse.storage.db.bean.Resource;

public class ResourceDao extends BaseDao {
  
  public static final ResourceDao INSTANCE = new ResourceDao();

  private ResourceDao() {
    super();
    this.tableName = this.prefix + "resources";
    this.beanClass = Resource.class;
    this.createTable();
  }

}
