package sse.storage.db.dao;

import sse.storage.db.bean.Post;

public class PostDao extends BaseDao {

  public static final PostDao INSTANCE = new PostDao();

  private PostDao() {
    super();
    this.tableName = this.prefix + "posts";
    this.beanClass = Post.class;
    this.createTable();
  }
}
