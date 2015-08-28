
cd classes

jar -cf		  ..\easydb.jar walker res*.properties 
copy log4j.properties   ..	/Y 
copy easydb.properties  ..	/Y 

cd ..

copy easydb.jar	        ..\Prj_EDTest\lib	        /Y 
copy log4j.properties   ..\Prj_EDTest\src		 /Y 
copy log4j.properties   ..\Prj_EDTest\classes		 /Y 
copy easydb.properties  ..\Prj_EDTest\src		 /Y 
copy easydb.properties  ..\Prj_EDTest\classes		 /Y 

copy easydb.jar	       ..\Prj_J2EE\j2eeApp\WEB-INF\lib        /Y 
copy log4j.properties  ..\Prj_J2EE\src				 /Y 
copy log4j.properties  ..\Prj_J2EE\j2eeApp\WEB-INF\classes	    /Y 
copy easydb.properties ..\Prj_J2EE\src				    /Y 
copy easydb.properties ..\Prj_J2EE\j2eeApp\WEB-INF\classes	    /Y 





