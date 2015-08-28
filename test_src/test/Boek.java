package test;

import walker.easydb.dao.BaseEntity;
import walker.easydb.datatype.EBinFile;
import walker.easydb.datatype.EFloat;
import walker.easydb.datatype.ELong;
import walker.easydb.datatype.EString;
import walker.easydb.datatype.ETimestamp;
import walker.easydb.datatype.ETxtFile;

/**
 * BOOKÊµÌå
 * 
 * @author HuQingmiao
 *  
 */
@SuppressWarnings("serial")
public class Boek extends BaseEntity {

    private ELong bookId;

    private EString title;

    private EFloat cost;

    private ETimestamp publishTime;

    private EBinFile blobContent;

    private ETxtFile textContent;
    
    public String[] pk() {
        return new String[] { "bookId" };
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

    public EString getTitle() {
        return title;
    }

    public void setTitle(EString title) {
        this.title = title;
    }
}
