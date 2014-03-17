package sse.storage.bean;

import java.util.Arrays;

public class Cluster {

    private static final String TYPE_MASTER = "master";
    private static final String TYPE_BACKUP = "backup";

    private String id;
    private String type;
    private String dbId;
    private String[] vdiskIds;

    public boolean isMaster() {
        return TYPE_MASTER.equalsIgnoreCase(type);
    }
    
    public boolean isBackup() {
        return TYPE_BACKUP.equalsIgnoreCase(type);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String[] getVdiskIds() {
        return vdiskIds;
    }

    public void setVdiskIds(String[] vdiskIds) {
        this.vdiskIds = vdiskIds;
    }

    @Override
    public String toString() {
        return "Cluster [id=" + id + ", type=" + type + ", dbId=" + dbId
                + ", vdiskIds=" + Arrays.toString(vdiskIds) + "]";
    }

}
