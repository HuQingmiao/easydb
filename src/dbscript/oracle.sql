DROP TABLE BOOK ;
CREATE TABLE BOOK
(
 BOOK_ID       	NUMBER(10)	     NOT NULL,  
 TITLE     	VARCHAR2(255)         , 
 PRICE      	NUMBER(10,2)          ,
 PUBLISH_TIME	DATE,			
 BLOB_CONTENT   BLOB,
 TEXT_CONTENT   CLOB,
 PRIMARY KEY(BOOK_ID)
) ;


DROP TABLE BOOK_EDITOR ;
CREATE TABLE BOOK_EDITOR( 
  BOOK_ID	NUMBER(10) NOT NULL,	
  EDITOR_ID	NUMBER(10) NOT NULL,	
  PRIMARY KEY(BOOK_ID, EDITOR_ID)
) ;


DROP TABLE EDITOR ;
CREATE TABLE EDITOR (	  
  EDITOR_ID     NUMBER(10)	 NOT NULL   ,
  NAME    	VARCHAR2(80)     NOT NULL   ,
  SEX		CHAR(1),		    	
  PRIMARY KEY(EDITOR_ID)
);



