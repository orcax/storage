package sse.storage.test;

import static sse.storage.constant.Const.*;
import static sse.storage.constant.Toolkit.*;
import sse.storage.constant.Config;
import sse.storage.constant.ResourceType;
import sse.storage.core.PictureManager;
import sse.storage.core.PictureManagerOld;
import sse.storage.db.bean.Picture;
import sse.storage.db.dao.PictureDao;
import sse.storage.db.dao.PostDao;
import sse.storage.db.dao.ResourceDao;
import sse.storage.fs.bean.ResourceFile;

public class MyTester1 {

  public static void main(String[] args) throws Exception {
//    Config.INSTANCE.init();
//    PictureDao.INSTANCE.init();
//    PostDao.INSTANCE.init();
//    ResourceDao.INSTANCE.init();

    ResourceFile p = PictureManager.INSTANCE.save("/var/tmp/storage/test/aaaaaa.jpg");
    System.out.println(p.getId());
  }

}
