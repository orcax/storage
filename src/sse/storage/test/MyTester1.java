package sse.storage.test;

import static sse.storage.etc.Toolkit.*;
import sse.storage.bean.ResourceEntity;
import sse.storage.core.BlockManager;
import sse.storage.core.ResourceManager;
import sse.storage.core.SyncManager;
import sse.storage.dao.BlockDao;
import sse.storage.dao.ResourceDao;
import sse.storage.etc.Config;
import sse.storage.except.DaoException;

public class MyTester1 {

    public static void main(String[] args) throws Exception {
        Config.getInstance();
        createTables();
        // ResourceManager rm = new ResourceManager();
        String picPath = "/var/tmp/test/aaaaaa.jpg";
        // ResourceEntity p1 = rm.savePicture(picPath);
        // ResourceEntity p1 = rm.readPicture(1);
        // info(p1.getContent().length);

        String text = "啦啦啦啦啦aaaaaa\r\n男男女女男男女女男男女女";
        // ResourceEntity p2 = rm.savePost("文章1", text);
        // ResourceEntity p2 = rm.readPost(4);
        // info(p2.getContentString());

        String pdfPath = "/var/tmp/test/eee.pdf";
        // ResourceEntity p3 = rm.saveOther(pdfPath);
        // ResourceEntity p3 = rm.readOther(6);
        // info(p3.getContent().length);

        new ResourceDao("db2").delete(1);
        new ResourceDao("db2").delete(3);
        new ResourceDao("db2").delete(4);
        SyncManager.getInstance().sync("sys1", "sys2");
    }

    public static void createTables() throws DaoException {
        new BlockDao("db1").createTable();
        new ResourceDao("db1").createTable();
        new BlockDao("db2").createTable();
        new ResourceDao("db2").createTable();
    }

}
