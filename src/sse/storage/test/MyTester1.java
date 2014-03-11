package sse.storage.test;

import sse.storage.bean.Picture;
import sse.storage.config.StorageConfig;
import sse.storage.core.PictureManager;
import sse.storage.dao.PictureDao;
import sse.storage.dao.PostDao;
import sse.storage.dao.ResourceDao;

public class MyTester1 {

  public static void main(String[] args) throws Exception {
    StorageConfig.INSTANCE.init();
    PictureDao.INSTANCE.init();
    PostDao.INSTANCE.init();
    ResourceDao.INSTANCE.init();
  }

}
