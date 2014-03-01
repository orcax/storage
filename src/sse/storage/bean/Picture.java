package sse.storage.bean;

import java.util.Arrays;

public class Picture {
  private Integer id;
  private String name;
  private String format;
  private Integer size;
  private String block_id;
  private String vdisk_id;
  private byte[] content;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
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

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "Picture [id=" + id + ", name=" + name + ", format=" + format
        + ", size=" + size + ", block_id=" + block_id + ", vdisk_id="
        + vdisk_id + ", content=" + Arrays.toString(content) + "]";
  }

}
