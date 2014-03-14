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

import static sse.storage.etc.Const.*;
import static sse.storage.etc.Toolkit.*;

import java.sql.Timestamp;

import sse.storage.bean.Block;
import sse.storage.bean.Resource;
import sse.storage.bean.ResourceEntity;
import sse.storage.dao.ResourceDao;
import sse.storage.etc.ResourceType;
import sse.storage.fs.BlockCenter;
import sse.storage.fs.Reader;
import sse.storage.fs.Writer;

/**
 * Class ResourceManager
 * 
 * @version 2013.3.10
 * @author Chris X.
 */
public class ResourceManager {

    public static final ResourceManager INSTANCE = new ResourceManager();

    private ResourceManager() {
    }

    /**
     * Read resource from db and file from fs.
     * 
     * @param id
     * @return ResourceEntity
     */
    private ResourceEntity read(int id) {
        Resource res = (Resource) ResourceDao.INSTANCE.read(id);
        if (res.getStatus() == RES_STATUS_DELETED) {
            error("Resource has been deleted");
            return null;
        }
        ResourceEntity re = new ResourceEntity();
        re.fromResource(res);
        re.setContent(Reader.INSTNANCE.readFile(re));
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
            id = ResourceDao.INSTANCE.insert(re.toResource());
            if (id < 0) {
                error("Save resource failed");
                return -1;
            }
            re.setId(id);
        } else {
            id = re.getId();
            int row = ResourceDao.INSTANCE.update(re.toResource());
            if (row <= 0) {
                error("Update resource failed");
                return -1;
            }
        }
        if (re.getContent() != null) {
            Writer.INSTANCE.writeFile(re);
        }
        return id;
    }

    public ResourceEntity savePicture(String name, String format, byte[] content) {
        ResourceEntity re = new ResourceEntity();
        re.setName(name);
        re.setFormat(format);
        re.setType(ResourceType.PICTURE.toString());
        re.setStatus(RES_STATUS_NORMAL);
        re.setSize(content.length);
        re.setCreated(new Timestamp(System.currentTimeMillis()));
        re.setModified(new Timestamp(System.currentTimeMillis()));
        re.setContent(content);
        Block block = BlockCenter.INSTANCE.allocateBlock(ResourceType.PICTURE);
        re.setBlock_id(block.getId());
        int id = save(re);
        if (id == -1) {
            return null;
        }
        re.setId(id);
        return re;
    }

    public ResourceEntity savePicture(String localPath) {
        ResourceEntity re = Reader.INSTNANCE.readRawFile(localPath, true);
        if (re == null) {
            error("File not exists");
            return null;
        }
        return savePicture(re.getName(), re.getFormat(), re.getContent());
    }

    public ResourceEntity savePost(String name, String content) {
        ResourceEntity re = new ResourceEntity();
        re.setName(name);
        re.setFormat(DEFAULT_FORMAT);
        re.setType(ResourceType.POST.toString());
        re.setStatus(RES_STATUS_NORMAL);
        re.setSize(content.getBytes().length);
        re.setCreated(new Timestamp(System.currentTimeMillis()));
        re.setModified(new Timestamp(System.currentTimeMillis()));
        re.setContent(content);
        Block block = BlockCenter.INSTANCE.allocateBlock(ResourceType.POST);
        re.setBlock_id(block.getId());
        int id = save(re);
        if (id == -1) {
            return null;
        }
        re.setId(id);
        return re;
    }

    public ResourceEntity saveOther(String name, String format, byte[] content) {
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
        Block block = BlockCenter.INSTANCE.allocateBlock(ResourceType.OTHER);
        re.setBlock_id(block.getId());
        int id = save(re);
        if (id == -1) {
            return null;
        }
        re.setId(id);
        return re;
    }

    public ResourceEntity saveOther(String localPath) {
        ResourceEntity re = Reader.INSTNANCE.readRawFile(localPath, true);
        if (re == null) {
            error("File not exists");
            return null;
        }
        return saveOther(re.getName(), re.getFormat(), re.getContent());
    }

}
