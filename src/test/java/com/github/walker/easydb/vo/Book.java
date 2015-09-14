package com.github.walker.easydb.vo;

import com.github.walker.easydb.dao.BaseEntity;
import com.github.walker.easydb.datatype.EBinFile;
import com.github.walker.easydb.datatype.EFloat;
import com.github.walker.easydb.datatype.ELong;
import com.github.walker.easydb.datatype.EString;
import com.github.walker.easydb.datatype.ETimestamp;
import com.github.walker.easydb.datatype.ETxtFile;

/**
 * BOOK实体
 *
 * @author HuQingmiao
 */
@SuppressWarnings("serial")
public class Book extends BaseEntity {

    private ELong bookId;

    private EString title;

    private EFloat cost;

    private ETimestamp publishTime;

    private EBinFile blobContent;

    private ETxtFile textContent;

    // 不存在的列
    private EString aNotExistCol;

    public String[] pk() {
        return new String[]{"bookId"};
    }

    public EBinFile getBlobContent() {
        return blobContent;
    }

    public void setBlobContent(EBinFile blobContent) {
        this.blobContent = blobContent;
    }

    public ELong getBookId() {
        return bookId;
    }

    public void setBookId(ELong bookId) {
        this.bookId = bookId;
    }

    public EFloat getCost() {
        return cost;
    }

    public EString getTitle() {
        return title;
    }

    public void setTitle(EString title) {
        this.title = title;
    }

    public void setCost(EFloat cost) {
        this.cost = cost;
    }

    public ETimestamp getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(ETimestamp publishTime) {
        this.publishTime = publishTime;
    }

    public ETxtFile getTextContent() {
        return textContent;
    }

    public void setTextContent(ETxtFile textContent) {
        this.textContent = textContent;
    }

    public EString getaNotExistCol() {
        return aNotExistCol;
    }

    public void setaNotExistCol(EString aNotExistCol) {
        this.aNotExistCol = aNotExistCol;
    }
}