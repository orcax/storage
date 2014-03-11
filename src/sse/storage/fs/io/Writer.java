package sse.storage.fs.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import sse.storage.constant.Config;
import sse.storage.db.bean.Resource;
import sse.storage.fs.Coordinator;
import sse.storage.fs.bean.ResourceFile;
import sse.storage.fs.bean.VDisk;

public class Writer {

  public static final Writer INSTANCE = new Writer();

  private Writer() {

  }

  public void writeFile(ResourceFile res) {
    if (res == null || res.getBlock_id() == null || res.getVdisk_id() == null) {
      return;
    }
    VDisk vdisk = Config.INSTANCE.getVdisk(res.getBlock_id());
    if (vdisk.isLocal()) {
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(Coordinator.mkurl(res));
        fos.write(res.getContent());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
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
        SmbFile file = new SmbFile(Coordinator.mkurl(res));
        fos = new SmbFileOutputStream(file);
        fos.write(res.getContent());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
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
  }

  public void transferFile(VDisk src, VDisk dest) {

  }

}
