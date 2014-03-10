package sse.storage.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import sse.storage.bean.Picture;
import sse.storage.bean.Post;

public class StorageManager {

  public static StorageManager INSTANCE = new StorageManager();

  private StorageManager() {

  }

  public Picture savePicture(String picPath) throws FileNotFoundException {
    File pic = new File(picPath);
    if (!pic.exists()){
      throw new FileNotFoundException(picPath);
    }
    
    String fullname = pic.getName();
    int index = fullname.lastIndexOf(".");
    String name = fullname.substring(0, index);
    String format = fullname.substring(index + 1);
    
    FileInputStream fis = new FileInputStream(pic);
    FileChannel channel = fis.getChannel();
    byte[] content = null;
    try {
      ByteBuffer byteBuffer = ByteBuffer.allocate((int)channel.size());
      while((channel.read(byteBuffer)) > 0);
      content = byteBuffer.array();
      
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        channel.close();
        fis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
 
    return (Picture) PictureManager.INSTANCE.save(name, format, content);
  }

  public Picture readPictureInfo(int id) {
    return null;
  }

  public byte[] readPictureFile(int id) {
    return null;
  }

  public Post savePost(Post post) {
    return null;
  }

  public Post readPost(int id) {
    return null;
  }

  public static void main(String[] args) {

  }

}
