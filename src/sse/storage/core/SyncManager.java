package sse.storage.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import sse.storage.bean.Block;
import sse.storage.bean.Cluster;
import sse.storage.bean.Resource;
import sse.storage.bean.ResourceEntity;
import sse.storage.dao.BlockDao;
import sse.storage.dao.ResourceDao;
import sse.storage.etc.Config;
import sse.storage.except.SyncException;
import sse.storage.fs.Reader;
import sse.storage.fs.Writer;

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

    public void sync(String srcClusterId, String dstClusterId)
            throws SyncException {
        Cluster srcCluster = Config.getInstance().getCluster(srcClusterId);
        Cluster dstCluster = Config.getInstance().getCluster(dstClusterId);
        if (srcCluster == null) {
            throw new SyncException("Cluster " + srcClusterId
                    + " is not defined.");
        }
        if (dstCluster == null) {
            throw new SyncException("Cluster " + dstClusterId
                    + " is not defined");
        }

        List<Block> diffBlocks = diffBlocks(srcCluster.getDbId(),
                dstCluster.getDbId());

        Iterator<Block> it = diffBlocks.iterator();
        while (it.hasNext()) {
            Block srcB = it.next();
            Block dstB = srcB.clone();
            dstB.setVdisk_id(dstCluster.getVdiskIds()[0]);
            Config.getInstance().setCurrDbId(dstCluster.getDbId());
            int row = BlockDao.getInstance().update(dstB);
            if (row == 0) {
                BlockDao.getInstance().insert(dstB);
            }
            
            List<Resource> diffRes = diffResources(srcCluster.getDbId(),
                    srcB.getId(), dstCluster.getDbId(), srcB.getId());
            Iterator<Resource> it2 = diffRes.iterator();
            while (it2.hasNext()) {
                copyResource(srcCluster, dstCluster, it2.next());
            }
        }
    }

    private List<Block> diffBlocks(String srcDbId, String dstDbId) {
        Config.getInstance().setCurrDbId(srcDbId);
        List<Block> srcBlocks = (List<Block>) BlockDao.getInstance().findAll();
        Config.getInstance().setCurrDbId(dstDbId);
        List<Block> dstBlocks = (List<Block>) BlockDao.getInstance().findAll();
        Comparator<Block> cmp = new Comparator<Block>() {
            @Override
            public int compare(Block o1, Block o2) {
                return o1.getId() - o2.getId();
            }
        };
        Collections.sort(srcBlocks, cmp);
        Collections.sort(dstBlocks, cmp);

        List<Block> diff = new ArrayList<Block>();
        int i = 0, j = 0;
        while (i < srcBlocks.size() && j < dstBlocks.size()) {
            Block src = srcBlocks.get(i);
            Block dst = dstBlocks.get(j);
            if (src.getId() == dst.getId() && src.getModified().equals(dst)) {
                ++i;
                ++j;
                continue;
            }
            if (src.getId() == dst.getId()) {
                diff.add(src);
                ++i;
                ++j;
                continue;
            }
            while (src.getId() < dst.getId()) {
                diff.add(src);
                src = srcBlocks.get(++i);
            }
            if (src.getId() > dst.getId()) {
                // TODO Ignore those blocks that exist only in dst but not in
                // src.
                j = i;
            }
        }
        while (i < srcBlocks.size()) {
            diff.add(srcBlocks.get(i++));
        }
        return diff;
    }

    private List<Resource> diffResources(String srcDbId, Integer srcBlockId,
            String dstDbId, Integer dstBlockId) {
        Config.getInstance().setCurrDbId(srcDbId);
        List<Resource> srcRes = (List<Resource>) ResourceDao.getInstance()
                .find("block_id=" + srcBlockId);
        Config.getInstance().setCurrDbId(dstDbId);
        List<Resource> dstRes = (List<Resource>) ResourceDao.getInstance()
                .find("block_id=" + dstBlockId);

        Comparator<Resource> cmp = new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                return o1.getId() - o2.getId();
            }
        };
        Collections.sort(srcRes, cmp);
        Collections.sort(dstRes, cmp);

        List<Resource> diff = new ArrayList<Resource>();
        int i = 0, j = 0;
        while (i < srcRes.size() && j < dstRes.size()) {
            Resource src = srcRes.get(i);
            Resource dst = dstRes.get(j);
            if (src.getId() == dst.getId() && src.getModified().equals(dst)) {
                ++i;
                ++j;
                continue;
            }
            if (src.getId() == dst.getId()) {
                diff.add(src);
                ++i;
                ++j;
                continue;
            }
            while (src.getId() < dst.getId()) {
                diff.add(src);
                src = srcRes.get(++i);
            }
            if (src.getId() > dst.getId()) {
                // TODO Ignore those blocks that exist only in dst but not in
                // src.
                j = i;
            }
        }
        while (i < srcRes.size()) {
            diff.add(srcRes.get(i++));
        }
        return diff;
    }

    private void copyResource(Cluster srcCluster, Cluster dstCluster,
            Resource res) {
        Config.getInstance().setCurrDbId(dstCluster.getDbId());
        ResourceDao.getInstance().insert(res);
        ResourceEntity re = new ResourceEntity(res);
        byte[] content = Reader.getInstance().readFile(srcCluster.getId(), re);
        re.setContent(content);
        Writer.getInstance().writeFile(dstCluster.getId(), re);
    }
}
