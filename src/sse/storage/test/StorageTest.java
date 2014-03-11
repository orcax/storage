package sse.storage.test;

import static org.junit.Assert.*;

import org.junit.Test;

import sse.storage.bean.Picture;
import sse.storage.core.PictureManager;

public class StorageTest {
  
  @Test
  public void savePicture() {
    Picture p2 = PictureManager.INSTANCE.save("/var/tmp/storage/test/aaaaaa.jpg");
    assertNotNull(p2);
  }
  
}
