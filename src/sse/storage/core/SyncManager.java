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

import static sse.storage.etc.Toolkit.info;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import jcifs.smb.SmbException;
import sse.storage.bean.Block;
import sse.storage.bean.Cluster;
import sse.storage.bean.Resource;
import sse.storage.bean.ResourceEntity;
import sse.storage.bean.VDisk;
import sse.storage.dao.BlockDao;
import sse.storage.dao.ResourceDao;
import sse.storage.etc.Config;
import sse.storage.etc.ResourceType;
import sse.storage.except.SyncException;
import sse.storage.fs.Reader;
import sse.storage.fs.Writer;

/**
 * Class SyncManager
 * 
 * @version 2013.3.10
 * @author Chris X.
 */
public class SyncManager {

    private static SyncManager instance = null;

    private SyncManager() {
    }

    public static SyncManager getInstance() {
        if (instance == null) {
            instance = new SyncManager();
        }
        return instance;
    }

    /**
     * Low efficiency!
     * 
     * @param srcClusterId
     * @param dstClusterId
     * @throws SyncException
     */
    public void sync(String srcClusterId, String dstClusterId)
            throws SyncException {
        Cluster srcCluster = Config.getInstance().getCluster(srcClusterId);
        if (srcCluster == null) {
            throw new SyncException("Cluster " + srcClusterId
                    + " is not defined.");
        }
        Cluster dstCluster = Config.getInstance().getCluster(dstClusterId);
        if (dstCluster == null) {
            throw new SyncException("Cluster " + dstClusterId
                    + " is not defined");
        }

        /* Differ blocks in two databases of each cluster */

        DiffBlocks diffBlocks = diffBlocks(srcCluster.getDbId(),
                dstCluster.getDbId());
        BlockDao dstBlockDao = new BlockDao(dstCluster.getDbId());

        /* Create new blocks that exists in src cluster but not in dst */

        for (Iterator<Block> it = diffBlocks.newBlocks.iterator(); it.hasNext();) {
            Block b = it.next();
            b.setVdisk_id(dstCluster.getVdiskIds()[0]);
            dstBlockDao.insert(b);
        }

        /* Update old blocks that are new in src cluster but outdated in dst */

        for (Iterator<Block> it = diffBlocks.updateBlocks.iterator(); it
                .hasNext();) {
            Block b = it.next();
            b.setVdisk_id(dstCluster.getVdiskIds()[0]);
            dstBlockDao.update(b);
        }

        /* Delete redundant blocks that not exists in src cluster but in dst */

        for (Iterator<Block> it = diffBlocks.deleteBlocks.iterator(); it
                .hasNext();) {
            Block b = it.next();
            dstBlockDao.delete(b.getId());
        }

        /* Differ resources in two databases of each cluster */

        DiffRes diffRes = diffResources(srcCluster.getDbId(),
                dstCluster.getDbId());
        ResourceDao dstResDao = new ResourceDao(dstCluster.getDbId());

        /* Create new blocks that exists in src cluster but not in dst */

        for (Iterator<Resource> it = diffRes.newRes.iterator(); it.hasNext();) {
            Resource r = it.next();
            dstResDao.insert(r);
            copyResource(srcCluster, dstCluster, r);
        }

        /* Update old blocks that are new in src cluster but outdated in dst */

        for (Iterator<Resource> it = diffRes.updateRes.iterator(); it.hasNext();) {
            Resource r = it.next();
            dstResDao.update(r);
            copyResource(srcCluster, dstCluster, r);
        }

        /* Delete redundant blocks that not exists in src cluster but in dst */

        for (Iterator<Resource> it = diffRes.deleteRes.iterator(); it.hasNext();) {
            Resource r = it.next();
            dstResDao.delete(r.getId());
            // TODO Delete file on dst
        }
    }

    private class DiffBlocks {
        List<Block> newBlocks = new ArrayList<Block>();
        List<Block> updateBlocks = new ArrayList<Block>();
        List<Block> deleteBlocks = new ArrayList<Block>();
    }

    private DiffBlocks diffBlocks(String srcDbId, String dstDbId) {
        List<Block> srcBlocks = (List<Block>) new BlockDao(srcDbId).findAll();
        List<Block> dstBlocks = (List<Block>) new BlockDao(dstDbId).findAll();
        Comparator<Block> cmp = new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
                return o1.getId() - o2.getId();
            }
        };
        Collections.sort(srcBlocks, cmp);
        Collections.sort(dstBlocks, cmp);

        DiffBlocks diff = new DiffBlocks();
        int i = 0, j = 0;
        while (i < srcBlocks.size() && j < dstBlocks.size()) {
            Block src = srcBlocks.get(i);
            Block dst = dstBlocks.get(j);
            if (src.getId() == dst.getId()
                    && src.getModified().equals(dst.getModified())) {
                ++i;
                ++j;
                continue;
            }
            if (src.getId() == dst.getId()) {
                diff.updateBlocks.add(src);
                ++i;
                ++j;
                continue;
            }
            while (src.getId() < dst.getId()) {
                diff.newBlocks.add(src);
                src = srcBlocks.get(++i);
            }
            while (src.getId() > dst.getId()) {
                diff.deleteBlocks.add(dst);
                dst = dstBlocks.get(++j);
            }
        }
        while (i < srcBlocks.size()) {
            diff.newBlocks.add(srcBlocks.get(i++));
        }
        while (j < dstBlocks.size()) {
            diff.deleteBlocks.add(dstBlocks.get(j++));
        }
        return diff;
    }

    private class DiffRes {
        List<Resource> newRes = new ArrayList<Resource>();
        List<Resource> updateRes = new ArrayList<Resource>();
        List<Resource> deleteRes = new ArrayList<Resource>();
    }

    private DiffRes diffResources(String srcDbId, String dstDbId) {
        List<Resource> srcRes = (List<Resource>) new ResourceDao(srcDbId)
                .findAll();
        List<Resource> dstRes = (List<Resource>) new ResourceDao(dstDbId)
                .findAll();
        Comparator<Resource> cmp = new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                return o1.getId() - o2.getId();
            }
        };
        Collections.sort(srcRes, cmp);
        Collections.sort(dstRes, cmp);

        DiffRes diff = new DiffRes();
        int i = 0, j = 0;
        while (i < srcRes.size() && j < dstRes.size()) {
            Resource src = srcRes.get(i);
            Resource dst = dstRes.get(j);
            if (src.getId() == dst.getId()
                    && src.getModified().equals(dst.getModified())) {
                ++i;
                ++j;
                continue;
            }
            if (src.getId() == dst.getId()) {
                diff.updateRes.add(src);
                ++i;
                ++j;
                continue;
            }
            while (src.getId() < dst.getId()) {
                diff.newRes.add(src);
                src = srcRes.get(++i);
            }
            while (src.getId() > dst.getId()) {
                diff.deleteRes.add(dst);
                dst = dstRes.get(++j);
            }
        }
        while (i < srcRes.size()) {
            diff.newRes.add(srcRes.get(i++));
        }
        while (j < dstRes.size()) {
            diff.deleteRes.add(dstRes.get(j++));
        }
        return diff;
    }

    private void copyResource(Cluster srcCluster, Cluster dstCluster,
            Resource res) {
        ResourceEntity re = new ResourceEntity(res);
        byte[] content = Reader.getInstance().readFile(srcCluster.getId(), re);
        re.setContent(content);
        boolean result = Writer.getInstance().writeFile(dstCluster.getId(), re);
        info("Copying " + res.getId() + " From " + srcCluster.getId() + " To "
                + dstCluster.getId() + " -- " + result);
    }

    private void rmResource(Cluster cluster, Resource res) {
        VDisk vdisk = Config.getInstance().getVdisk(cluster.getVdiskIds()[0]);
        try {
            vdisk.rm(new BlockManager(cluster.getId(), ResourceType.toEnum(res
                    .getType())).mkurl(res));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
