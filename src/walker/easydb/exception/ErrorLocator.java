package walker.easydb.exception;

import java.util.HashMap;

import org.apache.log4j.Logger;

import walker.easydb.assistant.LogFactory;

/**
 * <p>
 * Locator of database error, it translates the error code of different database
 * into unified error type.
 * </p>
 * 
 * @author HuQingmiao
 */
public abstract class ErrorLocator {
	
	protected static Logger log = LogFactory.getLogger(ErrorLocator.class);

	private static HashMap<String, ErrorLocator> insMap = new HashMap<String, ErrorLocator>(3); 


	public synchronized static ErrorLocator getInstance(String dbType) {
		
		if (!insMap.containsKey(dbType)) {
			
			if ("oracle".equalsIgnoreCase(dbType)) {
				insMap.put(dbType, new OracleErrorLocator());
				
			} else if ("mysql".equalsIgnoreCase(dbType)) {
				insMap.put(dbType, new MysqlErrorLocator());
				
			} else if ("db2".equalsIgnoreCase(dbType)) {
				insMap.put(dbType, new DB2ErrorLocator());
				
			} else if ("sqlserver".equalsIgnoreCase(dbType)) {
				insMap.put(dbType, new SqlserverErrorLocator());
			}
			
			
		}
		return insMap.get(dbType);
	}
	

    abstract public String getErrorMsg(int errorCode);

}