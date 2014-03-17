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
package sse.storage.core;

import static sse.storage.etc.Const.DEFAULT_FORMAT;
import static sse.storage.etc.Const.RES_STATUS_DELETED;
import static sse.storage.etc.Const.RES_STATUS_NORMAL;
import static sse.storage.etc.Toolkit.error;

import java.sql.Timestamp;

import sse.storage.bean.Block;
import sse.storage.bean.Cluster;
import sse.storage.bean.Database;
import sse.storage.bean.Resource;
import sse.storage.bean.ResourceEntity;
import sse.storage.dao.ResourceDao;
import sse.storage.etc.Config;
import sse.storage.etc.ResourceType;
import sse.storage.except.ResourceException;
import sse.storage.fs.Reader;
import sse.storage.fs.Writer;

/**
 * Class ResourceManager has to be binded with a cluster ID so that resoruces
 * can be read/written under that cluster. DEFAULT binded with master cluster.
 * 
 * @version 2013.3.10
 * @author Chris X.
 */
public class ResourceManager {

    private String clusterId = null;
    private Database db = null;
    private ResourceDao resourceDao = null;

    public ResourceManager() throws Exception {
        Cluster master = Config.getInstance().getMasterCluster();
        if (master == null) {
            throw new ResourceException("No master cluster is defined");
        }
        this.clusterId = master.getId();
        this.db = Config.getInstance().getDatabaseByCluster(clusterId);
        if (this.db == null) {
            throw new ResourceException("No DB is defined in cluster "
                    + clusterId);
        }
        this.resourceDao = new ResourceDao(db.getId());
    }

    public ResourceManager(String clusterId) throws Exception {
        this.clusterId = clusterId;
        this.db = Config.getInstance().getDatabaseByCluster(clusterId);
        if (this.db == null) {
            throw new ResourceException("No DB is defined in cluster "
                    + clusterId);
        }
        this.resourceDao = new ResourceDao(db.getId());
    }

    /**
     * Read resource from db and file from fs.
     * 
     * @param id
     * @return ResourceEntity
     */
    private ResourceEntity read(int id) {
        Resource res = (Resource) resourceDao.read(id);
        if (res.getStatus() == RES_STATUS_DELETED) {
            error("Resource has been deleted");
            return null;
        }
        ResourceEntity re = new ResourceEntity(res);
        re.setContent(Reader.getInstance().readFile(clusterId, re));
        return re;
    }

    public ResourceEntity readPicture(int id) {
        return read(id);
    }

    public ResourceEntity readPost(int id) {
        return read(id);
    }

    public ResourceEntity readOther(int id) {
        return read(id);
    }

    /**
     * Save or update resource.
     * 
     * @param re
     * @return id
     */
    private int save(ResourceEntity re) {
        int id = -1;
        if (re.getId() == null) {
            id = resourceDao.insert(re.toResource());
            if (id < 0) {
                error("Save resource failed");
                return -1;
            }
            re.setId(id);
        } else {
            id = re.getId();
            int row = resourceDao.update(re.toResource());
            if (row <= 0) {
                error("Update resource failed");
                return -1;
            }
        }
        if (re.getContent() != null) {
            Writer.getInstance().writeFile(clusterId, re);
        }
        return id;
    }

    /**
     * Save picture in specified cluster.
     * 
     * @param name
     * @param format
     * @param content
     * @return
     * @throws Exception
     */
    public ResourceEntity savePicture(String name, String format, byte[] content)
            throws Exception {
        ResourceEntity re = new ResourceEntity();
        re.setName(name);
        re.setFormat(format);
        re.setType(ResourceType.PICTURE.toString());
        re.setStatus(RES_STATUS_NORMAL);
        re.setSize(content.length);
        re.setCreated(new Timestamp(System.currentTimeMillis()));
        re.setModified(new Timestamp(System.currentTimeMillis()));
        re.setContent(content);

        BlockManager bm = ClusterManager.getInstance().getBlockManager(
                clusterId, ResourceType.PICTURE);
        Block block = bm.allocateBlock(ResourceType.PICTURE);
        re.setBlock_id(block.getId());
        int id = save(re);
        if (id == -1) {
            return null;
        }
        re.setId(id);
        return re;
    }

    public ResourceEntity savePicture(String localPath) throws Exception {
        ResourceEntity re = Reader.getInstance().readRawFile(localPath, true);
        if (re == null) {
            error("File not exists");
            return null;
        }
        return savePicture(re.getName(), re.getFormat(), re.getContent());
    }

    /**
     * Save post in specified cluster.
     * 
     * @param name
     * @param content
     * @return
     * @throws Exception
     */
    public ResourceEntity savePost(String name, String content)
            throws Exception {
        ResourceEntity re = new ResourceEntity();
        re.setName(name);
        re.setFormat(DEFAULT_FORMAT);
        re.setType(ResourceType.POST.toString());
        re.setStatus(RES_STATUS_NORMAL);
        re.setSize(content.getBytes().length);
        re.setCreated(new Timestamp(System.currentTimeMillis()));
        re.setModified(new Timestamp(System.currentTimeMillis()));
        re.setContent(content);

        BlockManager bm = ClusterManager.getInstance().getBlockManager(
                clusterId, ResourceType.POST);
        Block block = bm.allocateBlock(ResourceType.POST);
        re.setBlock_id(block.getId());
        int id = save(re);
        if (id == -1) {
            return null;
        }
        re.setId(id);
        return re;
    }

    /**
     * Save other resource in specified cluster.
     * 
     * @param name
     * @param format
     * @param content
     * @return
     * @throws Exception
     */
    public ResourceEntity saveOther(String name, String format, byte[] content)
            throws Exception {
        ResourceEntity re = new ResourceEntity();
        re.setName(name);
        if (format == null) {
            format = DEFAULT_FORMAT;
        }
        re.setFormat(format);
        re.setType(ResourceType.OTHER.toString());
        re.setStatus(RES_STATUS_NORMAL);
        re.setSize(content.length);
        re.setCreated(new Timestamp(System.currentTimeMillis()));
        re.setModified(new Timestamp(System.currentTimeMillis()));
        re.setContent(content);

        BlockManager bm = ClusterManager.getInstance().getBlockManager(
                clusterId, ResourceType.OTHER);
        Block block = bm.allocateBlock(ResourceType.OTHER);
        re.setBlock_id(block.getId());
        int id = save(re);
        if (id == -1) {
            return null;
        }
        re.setId(id);
        return re;
    }

    public ResourceEntity saveOther(String localPath) throws Exception {
        ResourceEntity re = Reader.getInstance().readRawFile(localPath, true);
        if (re == null) {
            error("File not exists");
            return null;
        }
        return saveOther(re.getName(), re.getFormat(), re.getContent());
    }

}
