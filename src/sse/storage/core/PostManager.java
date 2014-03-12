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

import static sse.storage.constant.Const.*;
import sse.storage.constant.ResourceType;
import sse.storage.db.bean.Post;
import sse.storage.db.dao.PictureDao;
import sse.storage.db.dao.PostDao;
import sse.storage.fs.Coordinator;
import sse.storage.fs.bean.Block;
import sse.storage.fs.bean.ResourceFile;
import sse.storage.fs.io.Reader;
import sse.storage.fs.io.Writer;

/**
 * Class PostManager
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class PostManager {
    
    public static final PostManager INSTANCE = new PostManager();
    
    private PostManager(){
    }
    
    public ResourceFile read(int id) {
	Post post = (Post)PostDao.INSTANCE.read(id);
	ResourceFile rf = new ResourceFile();
	rf.fromPost(post);
	rf.setText(Reader.INSTNANCE.readFile(rf));
	return rf;
    }
    
    public ResourceFile save(String name, String text) {
	ResourceFile post = new ResourceFile();
	post.setName(name);
	post.setSize(text.getBytes().length);
	post.setType(ResourceType.POST.toString());
	post.setFormat(POST_FORMAT);
	post.setText(text);
	Block block = Coordinator.allocateBlock(ResourceType.POST);
	post.setBlock_id(block.getBlockId());
	post.setVdisk_id(block.getVdiskId());
	int id = PostDao.INSTANCE.insert(post.toPost());
	post.setId(id);
	Writer.INSTANCE.writeFile(post);
	return post;
    }

}
