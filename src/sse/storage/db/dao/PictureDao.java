package sse.storage.db.dao;

import sse.storage.db.bean.Picture;

public class PictureDao extends BaseDao {

  public static final PictureDao INSTANCE = new PictureDao();
  
  private PictureDao() {
    super();
    this.tableName = this.prefix + "pictures";
    this.beanClass = Picture.class;
    this.createTable();
  }

}
