package sse.storage.fs.bean;

import sse.storage.db.bean.Resource;

public class ResourceFile extends Resource {
  private byte[] content;

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }
}
