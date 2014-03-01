package sse.storage.config;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.xeiam.yank.DBConnectionManager;

import sse.storage.bean.Database;
import sse.storage.bean.VDisk;

public class StorageConfig {
  public static StorageConfig INSTANCE = new StorageConfig();
  
  public boolean isConfigSuccess = true;
  public String lastError = "";

  public int BLOCK_SIZE;
  public String PICTURE_DIR;
  public String POST_DIR;

  private Map<String, VDisk> vdisks = null;
  private Map<String, Database> databases = null;
  private String currentVDiskId = "";
  private String currentDbId = "";

  private StorageConfig() {
    init();
  }

  private void init() {
    try {
      InputStream xml = StorageConfig.class.getClassLoader()
          .getResourceAsStream("storage_config.xml");
      SAXReader saxReader = new SAXReader();
      Document doc = saxReader.read(xml);
      Element root = doc.getRootElement();

      // Get universal parameter
      BLOCK_SIZE = Integer.parseInt(root.elementTextTrim("block-size"));
      PICTURE_DIR = root.elementTextTrim("picture-dir");
      POST_DIR = root.elementTextTrim("post-dir");

      // Get vdisks
      vdisks = new HashMap<String, VDisk>();
      for (Iterator<?> it = root.elementIterator("vdisk"); it.hasNext();) {
        Element e = (Element) it.next();
        VDisk vdisk = new VDisk();
        vdisk.setId(e.elementTextTrim("id"));
        vdisk.setStatus(e.elementTextTrim("status"));
        vdisk.setIp(e.elementTextTrim("ip"));
        vdisk.setPort(e.elementTextTrim("port"));
        String rootPath = e.elementTextTrim("root-path");
        if (rootPath != null && rootPath.endsWith("/")) {
          vdisk.setRootPath(rootPath.substring(0, rootPath.length() - 1));
        } else {
          vdisk.setRootPath(rootPath);
        }
        vdisks.put(vdisk.getId(), vdisk);
      }
      if (vdisks.isEmpty()) {
        isConfigSuccess = false;
        lastError = "No vdisk is found. Make sure config file is correct.";
        return;
      }

      // Get databases
      databases = new HashMap<String, Database>();
      for (Iterator<?> it = root.elementIterator("database"); it.hasNext();) {
        Element e = (Element) it.next();
        Database db = new Database();
        db.setId(e.elementTextTrim("id"));
        db.setName(e.elementTextTrim("name"));
        db.setDriver(e.elementTextTrim("driver"));
        db.setUrl(e.elementTextTrim("url"));
        db.setUser(e.elementTextTrim("user"));
        db.setPassword(e.elementTextTrim("password"));
        db.setMaxconn(e.elementTextTrim("maxconn"));
        db.setSqlProps(e.elementTextTrim("sql-props"));
        databases.put(db.getId(), db);
      }
      if (databases.isEmpty()) {
        isConfigSuccess = false;
        lastError = "No database is found. Make sure config file is correct.";
        return;
      }

      // Get current vdisk
      String vdiskId = root.elementTextTrim("current-vdisk");
      if (!vdisks.containsKey(vdiskId)) {
        isConfigSuccess = false;
        lastError = "Failed to load current vdisk '" + vdiskId + "'.";
        return;
      }
      currentVDiskId = vdiskId;

      // Make root directories of current vdisk
      VDisk vdisk = vdisks.get(currentVDiskId);
      if (vdisk.getRootPath() == null || vdisk.getRootPath().isEmpty()) {
        isConfigSuccess = false;
        lastError = "Root path is missing of current vdisk '" + vdiskId + "'.";
        return;
      }
      File rootDir = new File(vdisk.getRootPath());
      if (!rootDir.exists()) {
        rootDir.mkdirs();
      }

      // Get current database
      String dbId = root.elementTextTrim("current-database");
      if (!databases.containsKey(dbId)) {
        isConfigSuccess = false;
        lastError = "Failed to load current database '" + dbId + "'.";
        return;
      }
      currentDbId = dbId;
      Database db = databases.get(dbId);
      DBConnectionManager.INSTANCE.init(db.getDbProps(), db.getSqlProps());

    } catch (Exception e) {
      isConfigSuccess = false;
      lastError = e.getMessage();
      return;
    }
    isConfigSuccess = true;
    lastError = "";
  }

  public VDisk getCurrentVdisk() {
    return vdisks.get(currentVDiskId);
  }

  public Database getCurrentDb() {
    return databases.get(currentDbId);
  }

  public VDisk getVdisk(String vdiskId) {
    return vdisks.get(vdiskId);
  }

  public Database getDatabase(String dbId) {
    return databases.get(dbId);
  }

}
