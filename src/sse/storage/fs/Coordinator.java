package sse.storage.fs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import sse.storage.constant.Config;
import sse.storage.constant.ResourceType;
import sse.storage.fs.bean.Block;
import sse.storage.fs.bean.ResourceFile;
import sse.storage.fs.bean.VDisk;

public class Coordinator {

  private static Map<ResourceType, Block> availBlocks = new HashMap<ResourceType, Block>();

  static {
    reset();
  }

  private Coordinator() {
  }

  public static void reset() {
    availBlocks.clear();
    try {
      Block pictureBlock = renewBlock(ResourceType.PICTURE);
      availBlocks.put(ResourceType.PICTURE, pictureBlock);
      Block postBlock = renewBlock(ResourceType.POST);
      availBlocks.put(ResourceType.POST, postBlock);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String mkurl(ResourceFile res) {
    if (res == null || res.getVdisk_id() == null || res.getBlock_id() == null) {
      return null;
    }
    VDisk vdisk = Config.INSTANCE.getVdisk(res.getVdisk_id());
    return String.format("%s%s/%s/%d.%s", vdisk.getRootPath(),
        Config.RESOURCE_DIR.get(ResourceType.valueOf(res.getType())),
        res.getBlock_id(), res.getId(), res.getFormat());
  }

  /**
   * Calculate available block id of given resource. (COSTLY)
   * 
   * @param rt
   * @return Block
   * @throws IOException
   */
  private static Block renewBlock(ResourceType rt) throws IOException {
    VDisk vdisk = Config.INSTANCE.getCurrentVdisk();
    String resDir = Config.RESOURCE_DIR.get(rt);
    Block block = new Block();
    block.setVdiskId(vdisk.getId());

    // Check maximum block number under resource directory

    int max = -1;
    String[] dirNames = vdisk.lsdirs(resDir);
    for (String name : dirNames) {
      try {
        int num = Integer.parseInt(name.substring(1));
        max = max < num ? num : max;
      } catch (NumberFormatException e) {
        continue;
      }
    }

    // Check if the block exists

    if (max == -1) {
      max = 1;
      vdisk.mkdirs(resDir + "/b" + max);
      block.setBlockId("b" + max);
      block.setLeftSpace(Config.BLOCK_SIZE);
      return block;
    }

    // Check if the block has free space

    String blockDir = resDir + "/b" + max;
    String[] files = vdisk.ls(blockDir);
    if (files.length < Config.BLOCK_SIZE) {
      block.setBlockId("b" + max);
      block.setLeftSpace(Config.BLOCK_SIZE - files.length);
      return block;
    }

    // Make new block
    ++max;
    vdisk.mkdirs(resDir + "/b" + max);
    block.setBlockId("b" + max);
    block.setLeftSpace(Config.BLOCK_SIZE);
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
    Block block = availBlocks.get(rt);
    if (block == null) {
      return null;
    }
    if (block.getLeftSpace() <= 0) {
      try {
        block = renewBlock(rt);
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    }
    block.setLeftSpace(block.getLeftSpace() - 1);
    availBlocks.put(rt, block);
    return block.clone();
  }

  /**
   * Get available blockk information of given resource.
   * 
   * @param rt
   * @return Block
   */
  public static Block getAvailBlock(ResourceType rt) {
    Block block = availBlocks.get(rt);
    return block == null ? null : block.clone();
  }

}