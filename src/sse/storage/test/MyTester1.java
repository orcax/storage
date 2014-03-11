package sse.storage.test;

import sse.storage.constant.Config;
import sse.storage.core.PictureManager;
import sse.storage.db.bean.Picture;
import sse.storage.db.dao.PictureDao;
import sse.storage.db.dao.PostDao;
import sse.storage.db.dao.ResourceDao;

public class MyTester1 {

  public static void main(String[] args) throws Exception {
    Config.INSTANCE.init();
    PictureDao.INSTANCE.init();
    PostDao.INSTANCE.init();
    ResourceDao.INSTANCE.init();
  }

}
