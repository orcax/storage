package sse.storage.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import sse.storage.core.PictureManager;
import sse.storage.fs.bean.ResourceFile;

public class StorageTest {
  
  @Test
  public void savePicture() {
    ResourceFile p = PictureManager.INSTANCE.save("/var/tmp/storage/test/aaaaaa.jpg");
    assertNotNull(p);
  }
  
}
