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

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jcifs.smb.SmbException;
import sse.storage.bean.Block;
import sse.storage.bean.Cluster;
import sse.storage.bean.Database;
import sse.storage.bean.Resource;
import sse.storage.bean.VDisk;
import sse.storage.dao.BlockDao;
import sse.storage.etc.Config;
import sse.storage.etc.ResourceType;
import sse.storage.except.BlockException;
import sse.storage.except.DaoException;

/**
 * Class BlockCenter is binded with unique <Cluster, ResourceType> pairs so that
 * it can manage blocks in the specific place.
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class BlockManager {

    private String clusterId = null;
    private ResourceType rt = null;
    private Map<Integer, Block> blocks = null;
    private Integer currBlockId = null;

    public BlockManager(String clusterId, ResourceType rt) throws Exception {
        this.clusterId = clusterId;
        this.rt = rt;
        this.blocks = new HashMap<Integer, Block>();
        init();
    }

    /**
     * Update old block in DB and create a new block in both DB and FS.
     * 
     * @param block
     * @return Block
     * @throws BlockException
     * @throws SmbException
     * @throws MalformedURLException
     * @throws DaoException
     */
    private Block switchNewBlock() throws BlockException,
            MalformedURLException, SmbException, DaoException {
        VDisk vdisk = Config.getInstance().getVdiskByCluster(clusterId);
        if (vdisk == null) {
            throw new BlockException("Could not get vdisk in cluster "
                    + clusterId);
        }
        Database db = Config.getInstance().getDatabaseByCluster(clusterId);
        if (db == null) {
            throw new BlockException("Could not get database in cluster "
                    + clusterId);
        }
        Config.getInstance().setCurrDbId(db.getId());
        Block currBlock = blocks.get(currBlockId);

        /* Update old block in DB */

        if (currBlock != null) {
            currBlock.setLeft_space(0);
            currBlock.setModified(now());
            currBlock.setStatus(BLOCK_STATUS_FULL);
            BlockDao.getInstance().update(currBlock);
            blocks.put(currBlock.getId(), currBlock);
        }

        /* Insert new block in DB */

        Block newBlock = new Block();
        newBlock.setVdisk_id(vdisk.getId());
        newBlock.setLeft_space(MAX_BLOCK_SIZE);
        newBlock.setType(rt.toString());
        newBlock.setStatus(BLOCK_STATUS_USE);
        newBlock.setCreated(now());
        newBlock.setModified(now());
        int id = BlockDao.getInstance().insert(newBlock);
        if (id == -1) {
            throw new BlockException("Cannot insert newBlock into database");
        }
        newBlock.setId(id);

        /* Create block on FS */

        String subDir = Config.getInstance().getResDir(rt) + "/b" + id;
        vdisk.rm(subDir);
        vdisk.mkdirs(subDir);

        /* Update block info of this class */

        blocks.put(newBlock.getId(), newBlock);
        currBlockId = newBlock.getId();

        return newBlock;
    }

    /**
     * Check if the current block (that is in use) on FS accords with that in
     * DB. Returned null implies no difference, or changes is made in the
     * returned block.
     * 
     * @param block
     * @return Block
     * @throws BlockException
     * @throws SmbException
     * @throws MalformedURLException
     * @throws DaoException
     * @throws IOException
     */
    private void checkCurrBlock() throws BlockException, MalformedURLException,
            SmbException, DaoException {
        VDisk vdisk = Config.getInstance().getVdiskByCluster(clusterId);
        if (vdisk == null) {
            throw new BlockException("Could not get current vdisk");
        }
        Database db = Config.getInstance().getDatabaseByCluster(clusterId);
        if (db == null) {
            throw new BlockException("Could not get current database");
        }
        Config.getInstance().setCurrDbId(db.getId());
        Block currBlock = blocks.get(currBlockId);
        String subDir = Config.getInstance().getResDir(rt) + "/b" + currBlockId;

        /* Check if the block exists on FS */

        if (!vdisk.isdir(subDir)) {
            vdisk.mkdirs(subDir);
            currBlock.setLeft_space(MAX_BLOCK_SIZE);
            currBlock.setCreated(now());
            currBlock.setModified(now());
            BlockDao.getInstance().update(currBlock);
            blocks.put(currBlock.getId(), currBlock);
            return;
        }

        /* Check if block is full */

        String[] files = vdisk.ls(subDir);
        if (files.length >= MAX_BLOCK_SIZE) {
            switchNewBlock();
            return;
        }

        /* Check if the left space are equal in FS and DB */

        if (MAX_BLOCK_SIZE - files.length != currBlock.getLeft_space()) {
            currBlock.setLeft_space(MAX_BLOCK_SIZE - files.length);
            currBlock.setModified(now());
            BlockDao.getInstance().update(currBlock);
            blocks.put(currBlock.getId(), currBlock);
        }
    }

    private void init() throws Exception {
        blocks.clear();
        Database db = Config.getInstance().getDatabaseByCluster(clusterId);
        if (db == null) {
            throw new BlockException("Could not get current database");
        }
        Config.getInstance().setCurrDbId(db.getId());

        /* Load blocks from DB */

        List<?> tmp = BlockDao.getInstance().find(
                "type='" + rt.toString() + "'");
        Iterator<?> it = tmp.iterator();
        while (it.hasNext()) {
            Block b = (Block) it.next();
            blocks.put(b.getId(), b);
            if (BLOCK_STATUS_USE.equals(b.getStatus())) {
                currBlockId = b.getId();
            }
        }

        /* Check current blocks of different types */

        if (currBlockId != null) {
            checkCurrBlock();
        }
    }

    public String mkurl(Resource res) {
        if (res == null) {
            error("mkurl: resource is null");
            return null;
        }
        Block block = blocks.get(res.getBlock_id());
        if (block == null) {
            error("mkurl: block is NOT found");
            return null;
        }
        VDisk vdisk = Config.getInstance().getVdisk(block.getVdisk_id());
        if (vdisk == null) {
            error("mkurl: vdisk is NOT found");
            return null;
        }
        StringBuffer url = new StringBuffer();
        if (vdisk.getRootPath() == null) {
            error("mkurl: root path is null");
            return null;
        }
        url.append(vdisk.getRootPath() + "/");
        if (res.getType() == null) {
            error("mkurl: resource type is not specified");
            return url.toString() + "/";
        }
        url.append(Config.getInstance().getResDir(
                ResourceType.toEnum(res.getType())));
        url.append("/b" + block.getId() + "/");
        if (res.getId() == null) {
            error("mkurl: resource id is not specified");
            return url.toString() + "/";
        }
        url.append(res.getId());
        if (isEmpty(res.getFormat())) {
            return url.toString();
        }
        url.append("." + res.getFormat());
        return url.toString();
    }

    /**
     * Allocate one available block of given resource. Each time the method is
     * called, left space of the block will minus 1.
     * 
     * @param rt
     * @return Block
     * @throws Exception
     */
    public Block allocateBlock(ResourceType rt) throws Exception {
        Database db = Config.getInstance().getDatabaseByCluster(clusterId);
        if (db == null) {
            throw new BlockException("Could not get current database");
        }
        Config.getInstance().setCurrDbId(db.getId());

        Block block = blocks.get(currBlockId);
        if (block == null || block.getLeft_space() <= 0) {
            return switchNewBlock();
        }

        block.setLeft_space(block.getLeft_space() - 1);
        block.setModified(now());
        BlockDao.getInstance().update(block);
        blocks.put(block.getId(), block);
        currBlockId = block.getId();
        return block.clone();
    }

    /**
     * Get available blockk information of given resource.
     * 
     * @param rt
     * @return Block
     */
    public Block getAvailBlock(ResourceType rt) {
        Block block = blocks.get(currBlockId);
        return block == null ? null : block.clone();
    }

    public Block getBlock(Integer id) {
        return blocks.get(id);
    }

    public void prtBlocks() {
        Iterator<?> it = blocks.keySet().iterator();
        while (it.hasNext()) {
            info(blocks.get(it.next()));
        }
    }

}