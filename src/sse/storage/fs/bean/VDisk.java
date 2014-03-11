package sse.storage.fs.bean;

import static sse.storage.constant.Toolkit.*;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;

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
  
  public boolean isLocal() {
    return IP_LOCAL.equals(ip);
  }

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
    }
    try {
      mkdirs(null);
      info("Made dir of " + rootPath);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public boolean isdir(String subDir) throws IOException {
    String path = rootPath;
    if (!isEmpty(subDir)) {
      path += subDir.startsWith("/") ? subDir : "/" + subDir;
    }
    if (isLocal()) {
      File dir = new File(path);
      return dir.exists() && dir.isDirectory();
    } else {
      SmbFile dir = new SmbFile(path);
      return dir.exists() && dir.isDirectory();
    }
  }

  public String[] ls(String subDir) throws IOException {
    String path = rootPath;
    if (!isEmpty(subDir)) {
      path += subDir.startsWith("/") ? subDir : "/" + subDir;
    }
    if (isLocal()) {
      return new File(path).list();
    } else {
      return new SmbFile(path).list();
    }
  }

  public String[] lsdirs(String subDir) throws IOException {
    String path = rootPath;
    if (!isEmpty(subDir)) {
      path += subDir.startsWith("/") ? subDir : "/" + subDir;
    }
    String[] names = null;
    if (isLocal()) {
      File[] dirs = new File(path).listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return dir.isDirectory() && name.startsWith("b");
        }
      });
      names = new String[dirs.length];
      for (int i = 0; i < dirs.length; i++) {
        names[i] = dirs[i].getName();
      }
    } else {
      SmbFile[] dirs = new SmbFile(path).listFiles(new SmbFilenameFilter() {
        @Override
        public boolean accept(SmbFile dir, String name) {
          try {
            return dir.isDirectory() && name.startsWith("b");
          } catch (SmbException e) {
            e.printStackTrace();
            return false;
          }
        }
      });
      names = new String[dirs.length];
      for (int i = 0; i < dirs.length; i++) {
        names[i] = dirs[i].getName();
      }
    }
    return names;
  }

  public void mkdirs(String subDir) throws IOException {
    String path = rootPath;
    if (!isEmpty(subDir)) {
      path += subDir.startsWith("/") ? subDir : "/" + subDir;
    }
    if (isLocal()) {
      File dir = new File(path);
      if (!dir.exists() || !dir.isDirectory()) {
        dir.mkdirs();
      }
    } else {
      SmbFile dir = new SmbFile(path);
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
