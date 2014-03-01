package sse.storage.test;

import sse.storage.bean.Picture;
import sse.storage.core.PictureManager;

public class MyTester1 {

  public static void main(String[] args) throws Exception {
    // Properties dbProps =
    // PropertiesUtils.getPropertiesFromClasspath("mysql_db.properties");
    // Properties sqlProps =
    // PropertiesUtils.getPropertiesFromClasspath("MYSQL_SQL.properties");
    // DBConnectionManager.INSTANCE.init(dbProps, sqlProps);
    // List<Resource> rs = ResourceDao.select();
    // System.out.println(rs.get(0).getName());
    // DBConnectionManager.INSTANCE.release();
    //Picture p1 = StorageManager.INSTANCE.savePicture("/var/tmp/storage/test/aaaaaa.jpg");
    Picture p2 = PictureManager.INSTANCE.save("/var/tmp/storage/test/aaaaaa.jpg");
    //Picture p = PictureManager.INSTANCE.readInfo(1);
    //System.out.println(PictureManager.INSTANCE.readFile(p).length);
    
  }

}
