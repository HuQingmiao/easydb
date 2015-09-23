DROP TABLE if EXISTS book ;
CREATE TABLE book
(
 book_id       	BIGINT   UNSIGNED  AUTO_INCREMENT  NOT NULL,
 title     	VARCHAR(255)         ,
 price      	FLOAT(10,2)          ,
 publish_time	DATE,
 blob_content   LONGBLOB,
 text_content   LONGTEXT,
 PRIMARY KEY(book_id)
) ;


DROP TABLE if EXISTS book_editor ;
CREATE TABLE book_editor(
  book_id BIGINT    UNSIGNED NOT NULL,
  editor_id BIGINT  UNSIGNED NOT NULL,
  PRIMARY KEY(book_id, editor_id)
) ;


DROP TABLE  if EXISTS editor ;
CREATE TABLE editor (
  editor_id     BIGINT  UNSIGNED  AUTO_INCREMENT  NOT NULL,
  name    	VARCHAR(80)     NOT NULL   ,
  sex		SET('m','f') ,
  PRIMARY KEY(editor_id)
);


