package sse.storage.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import sse.storage.bean.Cluster;
import sse.storage.bean.ResourceEntity;
import sse.storage.core.ResourceManager;
import sse.storage.etc.Config;

public class StorageTest {
  
  @Test
  public void savePicture() {
    ResourceEntity re = null;
    try {
        re = new ResourceManager().savePicture("/var/tmp/storage/test/aaaaaa.jpg");
        assertNotNull(re);
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }
  
}
