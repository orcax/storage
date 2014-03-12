package sse.storage.test;

import static sse.storage.constant.Const.*;
import static sse.storage.constant.Toolkit.*;
import sse.storage.constant.Config;
import sse.storage.constant.ResourceType;
import sse.storage.core.PictureManager;
import sse.storage.core.PostManager;
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

    //ResourceFile p = PictureManager.INSTANCE.save("/var/tmp/storage/test/aaaaaa.jpg");
//    ResourceFile p = PictureManager.INSTANCE.read(11);
//    info(p.getContent().length);
    
//      String text = "啦啦啦啦啦aaaaaa\r\n男男女女男男女女男男女女";
//      ResourceFile p = PostManager.INSTANCE.save("文章1", text);
//      info(p.getId());
      ResourceFile p = PostManager.INSTANCE.read(3);
      info(p.getText());
  }

}
