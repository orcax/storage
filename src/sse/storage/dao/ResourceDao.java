package sse.storage.dao;

import sse.storage.bean.Resource;

public class ResourceDao extends BaseDao {
  
  public static final ResourceDao INSTANCE = new ResourceDao();

  private ResourceDao() {
    super();
    this.tableName = this.prefix + "resources";
    this.beanClass = Resource.class;
    this.createTable();
  }

}
