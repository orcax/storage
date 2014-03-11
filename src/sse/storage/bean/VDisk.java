package sse.storage.bean;

import static sse.storage.config.Toolkit.isEmpty;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class VDisk {

  public static final String TYPE_MASTER = "master";
  public static final String TYPE_BACKUP = "backup";
  public static final String IP_LOCAL = "localhost";

  private String id;
  private String type;
  private String ip;
  private String user;
  private String password;
  private String rootPath; // Only support SMB protocal for remote access

  public boolean isValid() {
    if (isEmpty(id, type, ip)) {
      return false;
    }
    if (!TYPE_MASTER.equals(type) && !TYPE_BACKUP.equals(type)) {
      return false;
    }
    if (isEmpty(rootPath)) {
      rootPath = "/";
    } else {
      int i = rootPath.length() - 1;
      while (i >= 0 && rootPath.charAt(i) == '/') {
        --i;
      }
      rootPath = rootPath.substring(0, i + 1);
      if (!rootPath.startsWith("/")) {
        rootPath = "/" + rootPath;
      }
    }
    if (!IP_LOCAL.equals(ip)) {
      rootPath = "smb://" + user + ":" + password + "@" + ip + rootPath;
      // TODO Remote access test can be done here:
    }
    return true;
  }

  public void makeRootDir() throws IOException {
    System.out.println("[INFO] Make root directory: " + rootPath);
    if (IP_LOCAL.equals(ip)) {
      File dir = new File(rootPath);
      if (!dir.exists() || !dir.isDirectory()) {
        dir.mkdirs();
      }
    } else {
      SmbFile dir = new SmbFile(rootPath);
      if (!dir.exists() || !dir.isDirectory()) {
        dir.mkdirs();
      }
    }
  }

  /* Getters, setters and toString */

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRootPath() {
    return rootPath;
  }

  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String toString() {
    return "VDisk [id=" + id + ", type=" + type + ", ip=" + ip + ", user="
        + user + ", password=" + password + ", rootPath=" + rootPath + "]";
  }

}
