package sse.storage.core;

import static sse.storage.etc.Toolkit.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sse.storage.bean.Cluster;
import sse.storage.etc.Config;
import sse.storage.etc.ResourceType;

public class ClusterManager {

    private class ClusterCenter {
        private Map<ResourceType, BlockManager> centers = null;
        private String clusterId = null;

        public ClusterCenter(String clusterId) {
            this.clusterId = clusterId;
            this.centers = new HashMap<ResourceType, BlockManager>();
            for (ResourceType rt : ResourceType.values()) {
                try {
                    centers.put(rt, new BlockManager(clusterId, rt));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    continue;
                }
            }
        }

        public BlockManager getBlockManager(ResourceType rt) {
            return centers.get(rt);
        }

        @Override
        public String toString() {
            return centers.toString();
        }
    }

    private static ClusterManager instance = null;

    private Map<String, ClusterCenter> clusterCenters = null;

    private ClusterManager() {
        clusterCenters = new HashMap<String, ClusterCenter>();
        Map<String, Cluster> clusters = Config.getInstance().getClusters();
        Iterator<String> it = clusters.keySet().iterator();
        while (it.hasNext()) {
            Cluster cluster = clusters.get(it.next());
            clusterCenters.put(cluster.getId(),
                    new ClusterCenter(cluster.getId()));
        }
    }

    public synchronized static ClusterManager getInstance() {
        if (instance == null) {
            instance = new ClusterManager();
        }
        return instance;
    }

    public BlockManager getBlockManager(String clusterId, ResourceType rt) {
        ClusterCenter cc = clusterCenters.get(clusterId);
        if (cc == null) {
            return null;
        }
        return cc.getBlockManager(rt);
    }

    public BlockManager getBlockManager(String clusterId, String rt) {
        ClusterCenter cc = clusterCenters.get(clusterId);
        if (cc == null) {
            return null;
        }
        return cc.getBlockManager(ResourceType.toEnum(rt));
    }

    public BlockManager getMasterBlockManager(ResourceType rt) {
        Cluster master = Config.getInstance().getMasterCluster();
        ClusterCenter cc = clusterCenters.get(master.getId());
        if (cc == null) {
            return null;
        }
        return cc.getBlockManager(rt);
    }

}
