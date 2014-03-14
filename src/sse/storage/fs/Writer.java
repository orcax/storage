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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import sse.storage.bean.Block;
import sse.storage.bean.ResourceEntity;
import sse.storage.bean.VDisk;
import sse.storage.etc.Config;

/**
 * Class Writer
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Writer {

    public static final Writer INSTANCE = new Writer();

    private Writer() {

    }

    public boolean writeFile(ResourceEntity re) {
        if (re == null || re.getBlock_id() == null) {
            return false;
        }
        Block block = BlockCenter.INSTANCE.getBlock(re.getBlock_id());
        if (block == null) {
            return false;
        }
        VDisk vdisk = Config.INSTANCE.getVdisk(block.getVdisk_id());
        if (vdisk == null) {
            return false;
        }
        if (vdisk.isLocal()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(BlockCenter.INSTANCE.mkurl(re));
                fos.write(re.getContent());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            SmbFileOutputStream fos = null;
            try {
                SmbFile file = new SmbFile(BlockCenter.INSTANCE.mkurl(re));
                fos = new SmbFileOutputStream(file);
                fos.write(re.getContent());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    public void transferFile(File src, File dest) {

    }

    public void transferFile(SmbFile src, SmbFile dest) {

    }

    public void transferFile(File src, SmbFile dest) {

    }

    public void transferFile(SmbFile src, File dest) {

    }

}
