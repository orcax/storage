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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import sse.storage.bean.Block;
import sse.storage.bean.ResourceEntity;
import sse.storage.bean.VDisk;
import sse.storage.core.BlockManager;
import sse.storage.core.ClusterManager;
import sse.storage.etc.Config;

/**
 * Class Reader
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Reader {

    private static Reader instance = null;

    private Reader() {
    }

    private byte[] readLocalFile(File file) {
        FileInputStream fis = null;
        FileChannel channel = null;
        byte[] content = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
            while ((channel.read(byteBuffer)) > 0) {
            }
            content = byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    private byte[] readLocalFile(String path) {
        return readLocalFile(new File(path));
    }

    private byte[] readSmbFile(SmbFile file) {
        System.setProperty("jcifs.smb.client.dfs.disabled", "true");
        SmbFileInputStream fis = null;
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        byte[] content = null;
        try {
            fis = new SmbFileInputStream(file);
            in = new BufferedInputStream(fis);
            out = new ByteArrayOutputStream();
            byte buffer[] = new byte[1024];
            for (int count = -1; (count = in.read(buffer, 0, 1024)) != -1;) {
                out.write(buffer, 0, count);
            }
            content = out.toByteArray();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    private byte[] readSmbFile(String url) {
        try {
            SmbFile file = new SmbFile(url);
            return readSmbFile(file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Public Interface */

    public static synchronized Reader getInstance() {
        if (instance == null) {
            instance = new Reader();
        }
        return instance;
    }

    public byte[] readFile(String clusterId, ResourceEntity re) {
        BlockManager bm = ClusterManager.getInstance().getBlockManager(
                clusterId, re.getType());
        Block block = bm.getBlock(re.getBlock_id());
        if (block == null) {
            return null;
        }
        VDisk vdisk = Config.getInstance().getVdisk(block.getVdisk_id());
        if (vdisk == null) {
            return null;
        }
        if (vdisk.isLocal()) {
            return readLocalFile(bm.mkurl(re));
        } else {
            return readSmbFile(bm.mkurl(re));
        }
    }

    public ResourceEntity readRawFile(String path, boolean local) {
        ResourceEntity res = new ResourceEntity();
        if (local) {
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            int index = file.getName().lastIndexOf(".");
            if (index < 0) {
                res.setName(file.getName());
                res.setFormat(null);
            } else {
                res.setName(file.getName().substring(0, index));
                res.setFormat(file.getName().substring(index + 1));
            }
            res.setContent(readLocalFile(file));
        } else {
            try {
                SmbFile file = new SmbFile(path);
                if (!file.exists()) {
                    return null;
                }
                int index = file.getName().lastIndexOf(".");
                if (index < 0) {
                    res.setName(file.getName());
                    res.setFormat(null);
                } else {
                    res.setName(file.getName().substring(0, index));
                    res.setFormat(file.getName().substring(index + 1));
                }
                res.setContent(readSmbFile(file));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (SmbException e) {
                e.printStackTrace();
                return null;
            }
        }
        return res;
    }
}
