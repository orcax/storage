package sse.storage.config;

import java.io.File;

import sse.storage.bean.Block;
import sse.storage.bean.VDisk;

public class StorageAdapter {

  private static Block pictureBlock;
  private static Block postBlock;

  static {
    reset();
  }

  private StorageAdapter() {
  }

  public static void reset() {
    pictureBlock = calAvailableBlock(ResourceType.PICTURE);
    postBlock = calAvailableBlock(ResourceType.POST);
  }

  /**
   * Calculate available block id of given resource. (COSTLY)
   * 
   * @param rt
   * @return Block
   */
  public static Block calAvailableBlock(ResourceType rt) {
    VDisk vdisk = StorageConfig.INSTANCE.getCurrentVdisk();
    int max = 1;
    Block block = new Block();
    block.setVdiskId(vdisk.getId());

    // Check if root directory exists
    String rootPath = vdisk.getRootPath() + "/";
    switch (rt) {
    case PICTURE:
      rootPath += StorageConfig.INSTANCE.PICTURE_DIR + "/";
      break;
    case POST:
      rootPath += StorageConfig.INSTANCE.POST_DIR + "/";
      break;
    }
    File rootDir = new File(rootPath);
    if (!rootDir.exists() || !rootDir.isDirectory()) {
      rootDir.mkdirs();
      new File(rootPath + "b" + max).mkdir();
      block.setBlockId("b" + max);
      block.setLeftSpace(StorageConfig.INSTANCE.BLOCK_SIZE);
      return block;
    }

    // Check maximum block under root directory
    for (File b : rootDir.listFiles()) {
      String name = b.getName();
      if (!name.startsWith("b") || !b.isDirectory()) {
        continue;
      }
      try {
        int num = Integer.parseInt(name.substring(1));
        max = max < num ? num : max;
      } catch (NumberFormatException e) {
        continue;
      }
    }

    // Check if the block has free space
    String blockPath = rootPath + "b" + max;
    File blockDir = new File(blockPath);
    if (!blockDir.exists() && !blockDir.isDirectory()) {
      blockDir.mkdir();
      block.setBlockId("b" + max);
      block.setLeftSpace(StorageConfig.INSTANCE.BLOCK_SIZE);
      return block;
    }
    int leftSpace = StorageConfig.INSTANCE.BLOCK_SIZE - blockDir.list().length;
    if (leftSpace > 0) {
      block.setBlockId("b" + max);
      block.setLeftSpace(leftSpace);
      return block;
    }
    blockDir = new File(rootPath + "b" + ++max);
    blockDir.mkdir();
    block.setBlockId("b" + max);
    block.setLeftSpace(StorageConfig.INSTANCE.BLOCK_SIZE);
    return block;
  }

  /**
   * Allocate one available block of given resource. Each time the method is
   * called, left space of the block will minus 1.
   * 
   * @param rt
   * @return Block
   */
  public static Block allocateBlock(ResourceType rt) {
    switch (rt) {
    case PICTURE:
      if (pictureBlock.getLeftSpace() <= 0) {
        pictureBlock = calAvailableBlock(rt);
      }
      pictureBlock.setLeftSpace(pictureBlock.getLeftSpace() - 1);
      return pictureBlock.clone();
    case POST:
      if (postBlock.getLeftSpace() <= 0) {
        postBlock = calAvailableBlock(rt);
      }
      postBlock.setLeftSpace(postBlock.getLeftSpace() - 1);
      return postBlock.clone();
    }
    return null;
  }

  /**
   * Get available blockk information of given resource.
   * 
   * @param rt
   * @return Block
   */
  public static Block getAvailableBlock(ResourceType rt) {
    switch (rt) {
    case PICTURE:
      return pictureBlock.clone();
    case POST:
      return postBlock.clone();
    }
    return null;
  }

}