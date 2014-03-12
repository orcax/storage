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
package sse.storage.fs.bean;

import sse.storage.db.bean.Picture;
import sse.storage.db.bean.Resource;

/**
 * Classs ResourceFile
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class ResourceFile extends Resource {
    private byte[] content;

    public byte[] getContent() {
	return content;
    }

    public void setContent(byte[] content) {
	this.content = content;
    }

    public Picture toPicture() {
	Picture pic = new Picture();
	pic.setId(this.getId());
	pic.setVdisk_id(this.getVdisk_id());
	pic.setBlock_id(this.getBlock_id());
	pic.setName(this.getName());
	pic.setFormat(this.getFormat());
	pic.setSize(this.getSize());
	pic.setDescription(this.getDescription());
	return pic;
    }
}
