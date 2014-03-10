package sse.storage.bean;

public class Post {
  private int id;
  private String name;
  private int size;
  private String block_id;
  private String vdisk_id;
  private String content;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getBlock_id() {
    return block_id;
  }

  public void setBlock_id(String block_id) {
    this.block_id = block_id;
  }

  public String getVdisk_id() {
    return vdisk_id;
  }

  public void setVdisk_id(String vdisk_id) {
    this.vdisk_id = vdisk_id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "Post [id=" + id + ", name=" + name + ", size=" + size
        + ", block_id=" + block_id + ", vdisk_id=" + vdisk_id + ", content="
        + content + "]";
  }

}
