package sse.storage.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import sse.storage.bean.Block;
import sse.storage.bean.Picture;
import sse.storage.config.ResourceType;
import sse.storage.config.StorageAdapter;
import sse.storage.config.StorageConfig;
import sse.storage.dao.PictureDao;

public class PictureManager implements ResourceManager {

  public static PictureManager INSTANCE = new PictureManager();

  private PictureManager() {
  }

  @Override
  public Picture read(int id) {
    Picture pic = (Picture) PictureDao.INSTANCE.read(id);
    if (pic == null) {
      return null;
    }
    FileInputStream fis = null;
    FileChannel channel = null;
    try {
      fis = new FileInputStream(path(pic));
      channel = fis.getChannel();
      ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
      while ((channel.read(byteBuffer)) > 0) {
      }
      pic.setContent(byteBuffer.array());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fis != null)
          fis.close();
        if (channel != null)
          channel.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return pic;
  }

  @Override
  public Object save(String name, String format, byte[] content) {
    Picture picture = new Picture();
    picture.setName(name);
    picture.setFormat(format);
    picture.setSize(content.length);
    Block block = StorageAdapter.allocateBlock(ResourceType.PICTURE);
    picture.setBlock_id(block.getBlockId());
    picture.setVdisk_id(block.getVdiskId());
    int id = PictureDao.INSTANCE.insert(picture);
    picture.setId(id);
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(path(picture));
      fos.write(content);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return picture;
  }

  /**
   * High efficiency file transfer via channel to channel.
   * 
   * @param path
   * @return
   */
  @Override
  public Picture save(String inPath) {
    FileInputStream fis = null;
    FileOutputStream fos = null;
    FileChannel inC = null, outC = null;

    try {
      File inFile = new File(inPath);
      fis = new FileInputStream(inFile);
      inC = fis.getChannel();

      // Insert into database
      int index = inFile.getName().lastIndexOf(".");
      String name = inFile.getName().substring(0, index);
      String format = inFile.getName().substring(index + 1);
      Picture picture = new Picture();
      picture.setName(name);
      picture.setFormat(format);
      picture.setSize(fis.available());
      Block block = StorageAdapter.allocateBlock(ResourceType.PICTURE);
      picture.setBlock_id(block.getBlockId());
      picture.setVdisk_id(block.getVdiskId());
      int id = PictureDao.INSTANCE.insert(picture);
      picture.setId(id);

      // Save to file system
      fos = new FileOutputStream(path(picture));
      outC = fos.getChannel();
      int length = picture.getSize();
      while (true) {
        if (inC.position() == inC.size()) {
          break;
        }
        if (inC.size() - inC.position() < picture.getSize()) {
          length = (int) (inC.size() - inC.position());
        } else {
          length = picture.getSize();
        }
        inC.transferTo(inC.position(), length, outC);
        inC.position(inC.position() + length);
      }

      return picture;

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fis != null) {
          fis.close();
        }
        if (inC != null) {
          inC.close();
        }
        if (fos != null) {
          fos.close();
        }
        if (outC != null) {
          outC.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  @Override
  public String path(Object resource) {
    Picture pic = (Picture) resource;
    return pic == null ? "" : String.format("%s/%s/%s/%s.%s",
        StorageConfig.INSTANCE.getVdisk(pic.getVdisk_id()).getRootPath(),
        StorageConfig.INSTANCE.PICTURE_DIR, pic.getBlock_id(), pic.getId(),
        pic.getFormat());
  }
}
