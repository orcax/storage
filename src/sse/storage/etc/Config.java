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
package sse.storage.etc;

import static sse.storage.etc.Const.*;
import static sse.storage.etc.Toolkit.*;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jcifs.smb.SmbException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import sse.storage.bean.Cluster;
import sse.storage.bean.Database;
import sse.storage.bean.VDisk;
import sse.storage.except.ConfigException;

import com.xeiam.yank.DBConnectionManager;
import com.xeiam.yank.PropertiesUtils;

/**
 * Class Config
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Config {

    private final static Map<ResourceType, String> RES_DIRS = new HashMap<ResourceType, String>();
    static {
        for (ResourceType rt : ResourceType.values()) {
            RES_DIRS.put(rt, rt.toString());
        }
    }

    private Map<String, Cluster> clusters = null;
    private Map<String, VDisk> vdisks = null;
    private Map<String, Database> databases = null;

    private String currentDbId = null;

    private static Config instance = null;

    private Config() {
        InputStream xml = Config.class.getClassLoader().getResourceAsStream(
                STORAGE_CONFIG);
        SAXReader saxReader = new SAXReader();
        try {
            Document doc = saxReader.read(xml);
            Element root = doc.getRootElement();
            initDatabases(root);
            initVDisks(root);
            initClusters(root);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConfigException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SmbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initDatabases(Element root) throws ConfigException {
        databases = new HashMap<String, Database>();
        Properties sqlProps = PropertiesUtils
                .getPropertiesFromClasspath(SQL_PROPERTIES);

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
            if (!db.isValid()) {
                throw new ConfigException("DB " + db.getId() + " is invalid");
            }
            databases.put(db.getId(), db);

            DBConnectionManager.INSTANCE.init(db.getDbProps(), sqlProps);

            info("Load database: " + db);
        }
        if (databases.isEmpty()) {
            throw new ConfigException("No database is defined in "
                    + STORAGE_CONFIG);
        }
    }

    private void initVDisks(Element root) throws ConfigException,
            MalformedURLException, SmbException {
        vdisks = new HashMap<String, VDisk>();
        for (Iterator<?> it = root.elementIterator("vdisk"); it.hasNext();) {
            Element e = (Element) it.next();
            VDisk vdisk = new VDisk();
            vdisk.setId(e.elementTextTrim("id"));
            vdisk.setIp(e.elementTextTrim("ip"));
            vdisk.setUser(e.elementTextTrim("user"));
            vdisk.setPassword(e.elementTextTrim("password"));
            vdisk.setRootPath(e.elementTextTrim("root-path"));
            if (!vdisk.isValid()) {
                throw new ConfigException("VDisk " + vdisk.getId()
                        + " is invalid");
            }
            vdisks.put(vdisk.getId(), vdisk);

            Iterator<?> it2 = RES_DIRS.keySet().iterator();
            while (it2.hasNext()) {
                vdisk.mkdirs(RES_DIRS.get(it2.next()));
            }

            info("Load vdisk: " + vdisk);
        }
        if (vdisks.isEmpty()) {
            throw new ConfigException("No vdisk is defined in "
                    + STORAGE_CONFIG);
        }
    }

    private void initClusters(Element root) throws ConfigException {
        int masterN = 0, backupN = 0;
        clusters = new HashMap<String, Cluster>();
        for (Iterator<?> it = root.elementIterator("cluster"); it.hasNext();) {
            Element e = (Element) it.next();
            Cluster cluster = new Cluster();
            cluster.setId(e.elementTextTrim("id"));
            cluster.setType(e.elementTextTrim("type"));
            if (cluster.isMaster()) {
                masterN++;
            }
            if (cluster.isBackup()) {
                backupN++;
            }

            cluster.setDbId(e.elementTextTrim("database-id"));
            if (!databases.containsKey(cluster.getDbId())) {
                throw new ConfigException("Database " + cluster.getDbId()
                        + " in the cluster " + cluster.getId()
                        + " should be first defined");
            }
            this.currentDbId = cluster.getDbId();

            List<String> vdiskIds = new ArrayList<String>();
            for (Iterator<?> it2 = e.elementIterator("vdisk-id"); it2.hasNext();) {
                Element e2 = (Element) it2.next();
                String vdiskId = e2.getTextTrim();
                if (!vdisks.containsKey(vdiskId)) {
                    throw new ConfigException("VDisk " + vdiskId
                            + " in the cluster " + cluster.getId()
                            + " should be first defined");
                }
                vdiskIds.add(vdiskId);
            }
            if (vdiskIds.isEmpty()) {
                throw new ConfigException("No vdisk is specified in cluster "
                        + cluster.getId());
            }
            cluster.setVdiskIds(vdiskIds.toArray(new String[0]));
            clusters.put(cluster.getId(), cluster);

            info("Load cluster: " + cluster);
        }
        if (masterN != 1) {
            throw new ConfigException(
                    "There should be one master cluster defined.");
        }
        if (backupN <= 0) {
            throw new ConfigException(
                    "There should be at least one Backup cluster defined");
        }
    }

    /* Public Interface */

    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public Map<String, Cluster> getClusters() {
        return this.clusters;
    }

    public Cluster getCluster(String clusterId) {
        return clusters.get(clusterId);
    }

    public Cluster getMasterCluster() {
        Cluster master = null;
        Iterator<?> it = clusters.keySet().iterator();
        while (it.hasNext()) {
            Cluster cluster = clusters.get(it.next());
            if (cluster.isMaster()) {
                master = cluster;
                break;
            }
        }
        return master;
    }

    public Cluster getBackupCluster() {
        Cluster backup = null;
        Iterator<?> it = clusters.keySet().iterator();
        while (it.hasNext()) {
            Cluster cluster = clusters.get(it.next());
            if (cluster.isBackup()) {
                backup = cluster;
                break;
            }
        }
        return backup;
    }

    public Map<String, VDisk> getVdisks() {
        return this.vdisks;
    }

    public VDisk getVdisk(String vdiskId) {
        return vdisks.get(vdiskId);
    }

    /**
     * Return FIRST vdisk in the cluster.
     * 
     * @param clusterId
     * @return
     */
    public VDisk getVdiskByCluster(String clusterId) {
        Cluster cluster = clusters.get(clusterId);
        if (cluster == null) {
            error("Cluster " + clusterId + " is not defined");
            return null;
        }
        return vdisks.get(cluster.getVdiskIds()[0]);
    }

    public Map<String, Database> getDatabases() {
        return this.databases;
    }

    public Database getDatabase(String dbId) {
        return databases.get(dbId);
    }

    public Database getDatabaseByCluster(String clusterId) {
        Cluster cluster = clusters.get(clusterId);
        if (cluster == null) {
            error("Cluster " + clusterId + " is not defined");
            return null;
        }
        return databases.get(cluster.getDbId());
    }

    public String getResDir(ResourceType rt) {
        return RES_DIRS.get(rt);
    }

    public String getResDir(String type) {
        return RES_DIRS.get(ResourceType.toEnum(type));
    }

    public String getCurrDbId() {
        return this.currentDbId;
    }

    public void setCurrDbId(String dbId) {
        if (databases.containsKey(dbId)) {
            this.currentDbId = dbId;
        }
    }

}
