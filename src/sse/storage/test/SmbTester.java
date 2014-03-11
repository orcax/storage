package sse.storage.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class SmbTester {
  public static void main(String[] args) {
    String url = "smb://orcax:1234@172.16.103.129/Public/0.jpg";
    String local = "/var/tmp/storage/pictures/b1/0.jpg";
    InputStream in = null;
    OutputStream out = null;
    try {
      SmbFile remoteFile = new SmbFile(url);
      in = new BufferedInputStream(new FileInputStream(local));
      out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
      byte[] buffer = new byte[1024]; 
      while(in.read(buffer)!=-1){ 
       out.write(buffer); 
       buffer = new byte[1024]; 
      } 
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }   
    }
  }
}
