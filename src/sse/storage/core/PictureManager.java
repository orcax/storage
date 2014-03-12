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

import sse.storage.constant.ResourceType;
import sse.storage.db.bean.Picture;
import sse.storage.db.dao.PictureDao;
import sse.storage.fs.Coordinator;
import sse.storage.fs.bean.Block;
import sse.storage.fs.bean.ResourceFile;
import sse.storage.fs.io.Reader;
import sse.storage.fs.io.Writer;

/**
 * Class PictureManager
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class PictureManager {
    public static final PictureManager INSTANCE = new PictureManager();

    private PictureManager() {
    }

    public ResourceFile read(int id) {
	Picture pic = (Picture) PictureDao.INSTANCE.read(id);
	ResourceFile rf = new ResourceFile();
	rf.fromPicture(pic);
	rf.setContent(Reader.INSTNANCE.readFile(rf));
	return rf;
    }

    public ResourceFile save(String name, String format, byte[] content) {
	ResourceFile pic = new ResourceFile();
	pic.setName(name);
	pic.setFormat(format);
	pic.setSize(content.length);
	pic.setType(ResourceType.PICTURE.toString());
	pic.setContent(content);
	Block block = Coordinator.allocateBlock(ResourceType.PICTURE);
	pic.setBlock_id(block.getBlockId());
	pic.setVdisk_id(block.getVdiskId());
	int id = PictureDao.INSTANCE.insert(pic.toPicture());
	pic.setId(id);
	Writer.INSTANCE.writeFile(pic);
	return pic;
    }

    public ResourceFile save(String localPath) {
	ResourceFile pic = Reader.INSTNANCE.readRawFile(localPath, true);
	if (pic == null) {
	    return null;
	}
	return save(pic.getName(), pic.getFormat(), pic.getContent());
    }

}
