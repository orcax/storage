/*
 * Chris X.
 * 
 * Copyright 2012-2014 Zhang Chenxi Project, SSE, Tongji University.
 * 
 * This software is the confidential and proprietary information of 
 * Zhang Chenxi project. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of 
 * the license agreement you participate in the project work. 
 */
package sse.storage.constant;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import static sse.storage.constant.Const.*;
import static sse.storage.constant.Toolkit.*;
import sse.storage.db.bean.Database;
import sse.storage.except.ConfigInitException;
import sse.storage.fs.bean.VDisk;

import com.xeiam.yank.DBConnectionManager;
import com.xeiam.yank.PropertiesUtils;

/**
 * Class Config
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Config {
    public static Config INSTANCE = new Config();

    private Map<String, VDisk> vdisks = null;
    private Map<String, Database> databases = null;
    private String currentVDiskId = "";
    private String currentDbId = "";

    private Config() {
	try {
	    initialize();
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}
    }

    /**
     * Load dynamic parameters from config xml file into memory.
     * 
     * @throws Exception
     */
    private void initialize() throws Exception {
	InputStream xml = Config.class.getClassLoader().getResourceAsStream(
		STORAGE_CONFIG);
	SAXReader saxReader = new SAXReader();
	Document doc = saxReader.read(xml);
	Element root = doc.getRootElement();

	/* (1) Import all vdisks */

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

	/* (2) Import all databases */

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

	/* (3) Set current vdisk and make root directory */

	String vdiskId = root.elementTextTrim("current-vdisk");
	if (!vdisks.containsKey(vdiskId)) {
	    throw new ConfigInitException("No vdisk named " + vdiskId
		    + " is found");
	}
	currentVDiskId = vdiskId;
	VDisk vdisk = vdisks.get(currentVDiskId);
	if (!VDisk.TYPE_MASTER.equals(vdisk.getType())) {
	    throw new ConfigInitException(
		    "Only master-type vdisk can be current");
	}
	Iterator<ResourceType> it = RESOURCE_DIR.keySet().iterator();
	while (it.hasNext()) {
	    vdisk.mkdirs(RESOURCE_DIR.get(it.next()));
	}

	/* (4) Set current database */

	String dbId = root.elementTextTrim("current-database");
	if (!databases.containsKey(dbId)) {
	    throw new ConfigInitException("No db named " + dbId + " is found");
	}
	currentDbId = dbId;
	Database db = databases.get(dbId);
	if (!Database.TYPE_MASTER.equals(db.getType())) {
	    throw new ConfigInitException(
		    "Only master-type databse can be current");
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
