package sse.storage.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import sse.storage.bean.Post;
import sse.storage.bean.Post;
import sse.storage.config.StorageConfig;
import sse.storage.dao.PostDao;

public class PostManager implements ResourceManager {

  public static PostManager INSTANCE = new PostManager();

  private PostManager() {
  }

  @Override
  public Object read(int id) {
    Post post = (Post) PostDao.INSTANCE.read(id);
    if (post == null) {
      return null;
    }
    FileInputStream fis = null;
    try{
      fis = new FileInputStream(path(post));
    } catch(FileNotFoundException e) {
      
    } finally {
      fis.close();
    }
    return null;
  }

  @Override
  public Object save(String filePath) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object save(String name, String format, byte[] content) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String path(Object resource) {
    Post post = (Post) resource;
    return post == null ? "" : String.format("%s/%s/%s/%s",
        StorageConfig.INSTANCE.getVdisk(post.getVdisk_id()).getRootPath(),
        StorageConfig.INSTANCE.POST_DIR, post.getBlock_id(), post.getId());
  }

}
