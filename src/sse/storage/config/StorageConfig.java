package sse.storage.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import sse.storage.bean.Database;
import sse.storage.bean.VDisk;
import sse.storage.except.ConfigInitException;

import com.xeiam.yank.DBConnectionManager;
import com.xeiam.yank.PropertiesUtils;

public class StorageConfig {
  public static StorageConfig INSTANCE = new StorageConfig();

  public static final String STORAGE_CONFIG = "storage_config.xml";
  public static final String SQL_PROPERTIES = "mysql_sql.properties";
  public static final int BLOCK_SIZE = 200;
  public static final String PICTURE_DIR = "pictures";
  public static final String POST_DIR = "posts";
  public static final String RESOURCE_DIR = "resources";
  public static final String VIDEO_DIR = "videos";

  private Map<String, VDisk> vdisks = null;
  private Map<String, Database> databases = null;
  private String currentVDiskId = "";
  private String currentDbId = "";

  private StorageConfig() {
    try {
      initialize();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }

  private void initialize() throws Exception {
    InputStream xml = StorageConfig.class.getClassLoader().getResourceAsStream(
        STORAGE_CONFIG);
    SAXReader saxReader = new SAXReader();
    Document doc = saxReader.read(xml);
    Element root = doc.getRootElement();

    // Get vdisks

    vdisks = new HashMap<String, VDisk>();
    for (Iterator<?> it = root.elementIterator("vdisk"); it.hasNext();) {
      Element e = (Element) it.next();
      VDisk vdisk = new VDisk();
      vdisk.setId(e.elementTextTrim("id"));
      vdisk.setType(e.elementTextTrim("type"));
      vdisk.setIp(e.elementTextTrim("ip"));
      vdisk.setUser(e.elementTextTrim("user"));
      vdisk.setPassword(e.elementTextTrim("password"));
      vdisk.setRootPath(e.elementTextTrim("root-path"));
      if (!vdisk.isValid()) {
        throw new ConfigInitException("VDisk init failed\n" + vdisk);
      }
      vdisks.put(vdisk.getId(), vdisk);
    }
    if (vdisks.isEmpty()) {
      throw new ConfigInitException("No vdisk is found");
    }

    // Get databases

    databases = new HashMap<String, Database>();
    for (Iterator<?> it = root.elementIterator("database"); it.hasNext();) {
      Element e = (Element) it.next();
      Database db = new Database();
      db.setId(e.elementTextTrim("id"));
      db.setType(e.elementTextTrim("type"));
      db.setName(e.elementTextTrim("name"));
      db.setDriver(e.elementTextTrim("driver"));
      db.setUrl(e.elementTextTrim("url"));
      db.setUser(e.elementTextTrim("user"));
      db.setPassword(e.elementTextTrim("password"));
      db.setMaxconn(e.elementTextTrim("maxconn"));
      if (!db.isValid()) {
        throw new ConfigInitException("DB init failed\n" + db);
      }
      databases.put(db.getId(), db);
    }
    if (databases.isEmpty()) {
      throw new ConfigInitException("No database is found");
    }

    // Get current vdisk and make root directory

    String vdiskId = root.elementTextTrim("current-vdisk");
    if (!vdisks.containsKey(vdiskId)) {
      throw new ConfigInitException("No vdisk named " + vdiskId + " is found");
    }
    currentVDiskId = vdiskId;
    VDisk vdisk = vdisks.get(currentVDiskId);
    if (!VDisk.TYPE_MASTER.equals(vdisk.getType())) {
      throw new ConfigInitException("Only master-type vdisk can be current");
    }
    vdisk.makeRootDir();

    // Get current database

    String dbId = root.elementTextTrim("current-database");
    if (!databases.containsKey(dbId)) {
      throw new ConfigInitException("No db named " + dbId + " is found");
    }
    currentDbId = dbId;
    Database db = databases.get(dbId);
    if(!Database.TYPE_MASTER.equals(db.getType())) {
      throw new ConfigInitException("Only master-type databse can be current");
    }
    Properties sqlProps = PropertiesUtils
        .getPropertiesFromClasspath(SQL_PROPERTIES);
    DBConnectionManager.INSTANCE.init(db.getDbProps(), sqlProps);

  }
  
  public void init() {
    
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
