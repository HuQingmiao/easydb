
cd jockycs

jar -cf		  ..\easydb.jar walker res*.properties 
copy easydblog4j.properties   ..	/Y 
copy easydb.properties  ..	/Y 

cd ..

copy easydb.jar	        ..Prj_EDTest\lib	        /Y 
copy easydblog4j.properties   ..\Prj_EDTest\src		 /Y 
copy easydblog4j.properties   ..\Prj_EDTest\jockycs		 /Y 
copy easydb.properties  ..\Prj_EDTest\src		 /Y 
copy easydb.properties  ..\Prj_EDTest\jockycs		 /Y 

copy easydb.jar	           ..\Prj_J2EE\j2eeApp\WEB-INF\lib        /Y 
copy easydblog4j.properties   ..\Prj_J2EE\src				 /Y 
copy easydblog4j.properties   ..\Prj_J2EE\j2eeApp\WEB-INF\jockycs	    /Y 
copy easydb.properties 		..\Prj_J2EE\src				    /Y 
copy easydb.properties      ..\Prj_J2EE\j2eeApp\WEB-INF\jockycs	    /Y 





