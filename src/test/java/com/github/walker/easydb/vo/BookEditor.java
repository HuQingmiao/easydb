package com.github.walker.easydb.vo;

import com.github.walker.easydb.dao.BaseEntity;
import com.github.walker.easydb.datatype.ELong;


/**
 * BOOK_EDITOR实体
 *
 * @author HuQingmiao
 */
@SuppressWarnings("serial")
public class BookEditor extends BaseEntity {

    private ELong bookId;

    private ELong editorId;

    public String[] pk() {
        return new String[]{"bookId", "editorId"};
    }

    public ELong getBookId() {
        return bookId;
    }

    public void setBookId(ELong bookId) {
        this.bookId = bookId;
    }

    public ELong getEditorId() {
        return editorId;
    }

    public void setEditorId(ELong editorId) {
        this.editorId = editorId;
    }
}
