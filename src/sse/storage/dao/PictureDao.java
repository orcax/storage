package sse.storage.dao;

import sse.storage.bean.Picture;

public class PictureDao extends BaseDao {

  public static PictureDao INSTANCE = new PictureDao();

  private PictureDao() {
    super();
    this.tableName = "pictures";
    this.beanClass = Picture.class;
  }

}
