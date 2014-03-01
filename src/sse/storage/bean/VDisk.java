package sse.storage.bean;

public class VDisk {

  public final static String STATUS_USE = "use";

  private String id;
  private String status;
  private String ip;
  private String port;
  private String rootPath;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getRootPath() {
    return rootPath;
  }

  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String toString() {
    return "VDisk [id=" + id + ", status=" + status + ", ip=" + ip + ", port="
        + port + ", rootPath=" + rootPath + "]";
  }

}
