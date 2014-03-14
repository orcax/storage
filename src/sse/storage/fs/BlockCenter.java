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
package sse.storage.fs;

import static sse.storage.etc.Const.*;
import static sse.storage.etc.Toolkit.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sse.storage.bean.Block;
import sse.storage.bean.Resource;
import sse.storage.bean.VDisk;
import sse.storage.dao.BlockDao;
import sse.storage.etc.Config;
import sse.storage.etc.ResourceType;

/**
 * Class Coordinator
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class BlockCenter {

    public static final BlockCenter INSTANCE = new BlockCenter();

    private Map<Integer, Block> blocks = new HashMap<Integer, Block>();
    private Map<ResourceType, Integer> currBlocks = new HashMap<ResourceType, Integer>();

    private BlockCenter() {
        init();
    }

    private void init() {
        blocks.clear();
        currBlocks.clear();

        /* Load blocks from DB */

        List<?> tmp = BlockDao.INSTANCE.findAll();
        Iterator<?> it = tmp.iterator();
        while (it.hasNext()) {
            Block b = (Block) it.next();
            blocks.put(b.getId(), b);
            if (BLOCK_STATUS_USE.equals(b.getStatus())) {
                currBlocks.put(ResourceType.toEnum(b.getType()), b.getId());
            }
        }

        /* Check current blocks of different types */

        for (ResourceType rt : ResourceType.values()) {
            Integer blockId = currBlocks.get(rt);

            if (blockId == null) {

                /* Create a new block if no current block is specified */

                Block b = createBlock(rt, Config.INSTANCE.getCurrentVdisk()
                        .getId());
                if (b == null) {
                    error("Cannot create block on VDisk "
                            + Config.INSTANCE.getCurrentVdisk().getId());
                    return;
                }
                blocks.put(b.getId(), b);
                currBlocks.put(rt, b.getId());
            } else {
                checkCurrBlock(blocks.get(blockId));
            }
        }
    }

    /**
     * Check if the current block (that is in use) on FS accords with that in
     * DB. Returned null implies no difference, or changes is made in the
     * returned block.
     * 
     * @param block
     * @return Block
     * @throws IOException
     */
    private void checkCurrBlock(Block block) {
        if (block == null || !BLOCK_STATUS_USE.equals(block.getStatus())) {
            return;
        }
        VDisk vdisk = Config.INSTANCE.getVdisk(block.getVdisk_id());
        String subDir = Config.INSTANCE.getResDir(block.getType()) + "/b"
                + block.getId();

        try {
            /* Check if the block exists on FS */

            if (!vdisk.isdir(subDir)) {
                vdisk.mkdirs(subDir);
                block.setLeft_space(MAX_BLOCK_SIZE);
                block.setCreated(now());
                block.setModified(now());
                BlockDao.INSTANCE.update(block);
                blocks.put(block.getId(), block);
                return;
            }

            /* Check if block is full */

            String[] files = vdisk.ls(subDir);
            if (files.length >= MAX_BLOCK_SIZE) {
                chg2NewBlock(block);
                return;
            }

            /* Check if the left space are equal in FS and DB */

            if (MAX_BLOCK_SIZE - files.length != block.getLeft_space()) {
                block.setLeft_space(MAX_BLOCK_SIZE - files.length);
                block.setModified(now());
                BlockDao.INSTANCE.update(block);
                blocks.put(block.getId(), block);
            }

        } catch (IOException e) {
            e.printStackTrace();
            chg2NewBlock(block);
            return;
        }
    }

    /**
     * Update old block and create a new block via createBlock method.
     * 
     * @param block
     * @return Block
     */
    private Block chg2NewBlock(Block block) {
        /* Update old block in DB */

        block.setLeft_space(0);
        block.setModified(now());
        block.setStatus(BLOCK_STATUS_FULL);
        BlockDao.INSTANCE.update(block);
        blocks.put(block.getId(), block);

        /* Create new block in DB and FS */

        ResourceType rt = ResourceType.toEnum(block.getType());
        Block newBlock = createBlock(rt, Config.INSTANCE.getCurrentVdisk()
                .getId());
        if (newBlock == null) {
            // TODO WHAT IF FAIL TO CREATE UNDER CURRENT VDISK ???
            error("CANNOT CREATE BLOCK UNDER CURRENT VDISK");
        }
        blocks.put(newBlock.getId(), newBlock);
        currBlocks.put(rt, newBlock.getId());
        return newBlock;
    }

    /**
     * Create a new block in DB and FS. Return null if failed to create.
     * 
     * @param rt
     * @param vdiskId
     * @return Block
     */
    private Block createBlock(ResourceType rt, String vdiskId) {
        VDisk vdisk = Config.INSTANCE.getVdisk(vdiskId);
        if (vdisk == null) {
            return null;
        }

        /* Insert into DB */

        Block block = new Block();
        block.setVdisk_id(vdiskId);
        block.setLeft_space(MAX_BLOCK_SIZE);
        block.setType(rt.toString());
        block.setStatus(BLOCK_STATUS_USE);
        block.setCreated(now());
        block.setModified(now());
        int id = BlockDao.INSTANCE.insert(block);
        if (id == -1) {
            error("Cannot insert block into database");
            return null;
        }
        block.setId(id);

        /* Create block on FS */

        String subDir = Config.INSTANCE.getResDir(block.getType()) + "/b" + id;
        try {
            vdisk.rm(subDir);
            vdisk.mkdirs(subDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return block;
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
        VDisk vdisk = Config.INSTANCE.getVdisk(block.getVdisk_id());
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
        url.append(Config.INSTANCE.getResDir(ResourceType.toEnum(res.getType())));
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
     */
    public Block allocateBlock(ResourceType rt) {
        Block block = blocks.get(currBlocks.get(rt));
        if (block.getLeft_space() <= 0) {
            return chg2NewBlock(block);
        }
        block.setLeft_space(block.getLeft_space() - 1);
        block.setModified(now());
        BlockDao.INSTANCE.update(block);
        blocks.put(block.getId(), block);
        currBlocks.put(rt, block.getId());
        return block.clone();
    }

    /**
     * Get available blockk information of given resource.
     * 
     * @param rt
     * @return Block
     */
    public Block getAvailBlock(ResourceType rt) {
        Block block = blocks.get(currBlocks.get(rt));
        return block == null ? null : block.clone();
    }

    public Block getBlock(Integer id) {
        return blocks.get(id);
    }
    
    public void prtBlocks() {
        Iterator it = blocks.keySet().iterator();
        while(it.hasNext()) {
            info(blocks.get(it.next()));
        }
    }

}