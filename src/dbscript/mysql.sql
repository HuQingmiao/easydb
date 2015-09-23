DROP TABLE if EXISTS book ;
CREATE TABLE book
(
 book_id       	BIGINT     UNSIGNED NOT NULL,
 title     	VARCHAR(255)         ,
 price      	FLOAT(10,2)          ,
 publish_time	DATE,
 blob_content   LONGBLOB,
 text_content   LONGTEXT,
 PRIMARY KEY(book_id)
) ;


DROP TABLE if EXISTS book_editor ;
CREATE TABLE BOOK_EDITOR( 
  book_id BIGINT    UNSIGNED NOT NULL,
  editor_id BIGINT  UNSIGNED NOT NULL,
  PRIMARY KEY(book_id, editor_id)
) ;


DROP TABLE  if EXISTS editor ;
CREATE TABLE EDITOR (
  editor_id     BIGINT UNSIGNED NOT NULL   ,
  name    	VARCHAR(80)     NOT NULL   ,
  sex		SET('m','f') ,
  PRIMARY KEY(editor_id)
);



DROP TABLE  if EXISTS editor ;
CREATE TABLE EDITOR (
  editor_id     BIGINT UNSIGNED NOT NULL   ,
  name    	VARCHAR(80)     NOT NULL   ,
  sex		SET('m','f') ,
  PRIMARY KEY(editor_id)
);
