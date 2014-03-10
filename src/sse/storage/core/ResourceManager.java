package sse.storage.core;

public interface ResourceManager {
  
  public Object read(int id);

  public Object save(String filePath);

  public Object save(String name, String format, byte[] content);

  public String path(Object resource);
  
}
