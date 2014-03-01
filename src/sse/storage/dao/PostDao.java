package sse.storage.dao;

import sse.storage.bean.Post;

public class PostDao extends BaseDao {

  public static PostDao INSTANCE = new PostDao();

  private PostDao() {
    super();
    this.tableName = "posts";
    this.beanClass = Post.class;
  }
}
