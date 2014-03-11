package sse.storage.fs.bean;

public class Block {
  private String vdiskId;
  private String blockId;
  private int leftSpace;

  public String getVdiskId() {
    return vdiskId;
  }

  public void setVdiskId(String vdiskId) {
    this.vdiskId = vdiskId;
  }

  public String getBlockId() {
    return blockId;
  }

  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  public int getLeftSpace() {
    return leftSpace;
  }

  public void setLeftSpace(int leftSpace) {
    this.leftSpace = leftSpace;
  }

  @Override
  public Block clone() {
    Block block = new Block();
    block.vdiskId = this.vdiskId;
    block.blockId = this.blockId;
    block.leftSpace = this.leftSpace;
    return block;
  }

  @Override
  public String toString() {
    return "Block [vdiskId=" + vdiskId + ", block=Id" + blockId + ", leftSpace="
        + leftSpace + "]";
  }

}
