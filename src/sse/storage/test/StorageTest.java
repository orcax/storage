package sse.storage.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import sse.storage.bean.ResourceEntity;
import sse.storage.core.ResourceManager;

public class StorageTest {
  
  @Test
  public void savePicture() {
    ResourceEntity re = ResourceManager.INSTANCE.savePicture("/var/tmp/storage/test/aaaaaa.jpg");
    assertNotNull(re);
  }
  
}
