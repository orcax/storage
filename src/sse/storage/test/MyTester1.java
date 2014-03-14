package sse.storage.test;

import static sse.storage.etc.Toolkit.*;
import sse.storage.bean.ResourceEntity;
import sse.storage.core.ResourceManager;
import sse.storage.fs.BlockCenter;

public class MyTester1 {

    public static void main(String[] args) throws Exception {
        // ResourceDao.INSTANCE.init();
        // List<?> blocks = BlockDao.INSTANCE.findAll();
        // Block b = (Block)blocks.get(0);
        // b.setModified(new Timestamp(System.currentTimeMillis()));
        // int rows = BlockDao.INSTANCE.update(b);
        // info(rows);

        String picPath = "/var/tmp/test/aaaaaa.jpg";
        // ResourceEntity p = ResourceManager.INSTANCE.savePicture(picPath);
        ResourceEntity p1 = ResourceManager.INSTANCE.readPicture(1);
        info(p1.getContent().length);

        String text = "啦啦啦啦啦aaaaaa\r\n男男女女男男女女男男女女";
        // ResourceEntity p = ResourceManager.INSTANCE.savePost("文章1", text);
        ResourceEntity p2 = ResourceManager.INSTANCE.readPost(4);
        info(p2.getContentString());

        String pdfPath = "/var/tmp/test/eee.pdf";
        // ResourceEntity p = ResourceManager.INSTANCE.saveOther(pdfPath);
        ResourceEntity p3 = ResourceManager.INSTANCE.readOther(6);
        info(p3.getContent().length);
    }

}
