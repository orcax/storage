package sse.storage.test;

import static org.junit.Assert.*;

import org.junit.Test;

import sse.storage.core.PictureManager;
import sse.storage.db.bean.Picture;

public class StorageTest {
  
  @Test
  public void savePicture() {
    Picture p2 = PictureManager.INSTANCE.save("/var/tmp/storage/test/aaaaaa.jpg");
    assertNotNull(p2);
  }
  
}
