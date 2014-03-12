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

/**
 * Class Block
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Block {
    private String vdiskId;
    private String blockId;
    private int leftSpace;

    public String getVdiskId() {
	return vdiskId;
    }

    public void setVdiskId(String vdiskId) {
	this.vdiskId = vdiskId;
    }

    public String getBlockId() {
	return blockId;
    }

    public void setBlockId(String blockId) {
	this.blockId = blockId;
    }

    public int getLeftSpace() {
	return leftSpace;
    }

    public void setLeftSpace(int leftSpace) {
	this.leftSpace = leftSpace;
    }

    @Override
    public Block clone() {
	Block block = new Block();
	block.vdiskId = this.vdiskId;
	block.blockId = this.blockId;
	block.leftSpace = this.leftSpace;
	return block;
    }

    @Override
    public String toString() {
	return "Block [vdiskId=" + vdiskId + ", block=Id" + blockId
		+ ", leftSpace=" + leftSpace + "]";
    }

}
