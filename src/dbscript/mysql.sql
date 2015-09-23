DROP TABLE if EXISTS BOOK ;
CREATE TABLE BOOK
(
 book_id       	BIGINT     UNSIGNED NOT NULL,
 title     	VARCHAR(255)         ,
 price      	FLOAT(10,2)          ,
 publish_time	DATE,
 blob_content   LONGBLOB,
 text_content   LONGTEXT,
 PRIMARY KEY(BOOK_ID)
) ;


DROP TABLE if EXISTS BOOK_EDITOR ;
CREATE TABLE BOOK_EDITOR( 
  book_id BIGINT    UNSIGNED NOT NULL,
  editor_id BIGINT  UNSIGNED NOT NULL,
  PRIMARY KEY(BOOK_ID, EDITOR_ID)
) ;


DROP TABLE  if EXISTS EDITOR ;
CREATE TABLE EDITOR (	  
  editor_id     BIGINT UNSIGNED NOT NULL   ,
  name    	VARCHAR(80)     NOT NULL   ,
  sex		SET('m','f') ,
  PRIMARY KEY(EDITOR_ID)
);



