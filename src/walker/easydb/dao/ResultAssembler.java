package walker.easydb.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import walker.easydb.assistant.EasyConfig;
import walker.easydb.assistant.LogFactory;
import walker.easydb.assistant.MappingUtil;
import walker.easydb.exception.DataAccessException;
import walker.easydb.exception.FileAccessException;
import walker.easydb.exception.IllegalEntityException;

/**
 * 
 * This class builds the BaseEntity object. It transforms query result into
 * BaseEntity object, and put the BaseEntity instance into PageList object.
 * 
 * @author HuQingmiao
 * 
 */
public abstract class ResultAssembler {

	protected Logger log = LogFactory.getLogger(this.getClass());

	// 当读取大字段列时, 需要设置的存放EBinFile或ETxtFile的目录
	protected static String BASE_FILE_DIRC = EasyConfig.getProperty("baseFileDirc");

	// the constructor of BaseEntity
	@SuppressWarnings("rawtypes")
	protected Constructor cons;

	// the map(index, SettingMethodExp)
	protected HashMap<Integer, SettingMethodExp> indexMethodExpMap;

	protected int colCount;// the column count

	// the Class object of the BaseEntity
	protected Class<?> entityClass;

	// query result
	protected ResultSet rs;

	protected ArrayList<BaseEntity> rsList;

	/**
	 * 获取数据库类型
	 * 
	 * @return 数据库类型,如'mysql', 'oracle'
	 */
	public abstract String getDBType();

	/**
	 * Constructor of ResultAssembler
	 * 
	 * @param rs
	 *            ResultSet object.
	 * 
	 * @param entityClass
	 *            The Class object of BaseEntity which used to load the query
	 *            result.
	 * 
	 * @throws IllegalEntityException
	 * @throws DataAccessException
	 */
	public ResultAssembler(ResultSet rs, Class<?> entityClass) {
		this.rs = rs;
		this.entityClass = entityClass;
	}

	/**
	 * Analyzes the query result and put it into PageList.
	 * 
	 * @throws IllegalEntityException
	 * @throws DataAccessException
	 */
	public void buildEntityList() throws IllegalEntityException, DataAccessException, FileAccessException {

		this.analyzeRs();

		this.buildList();

	}

	/**
	 * Load data to the specified entity
	 * 
	 */
	protected boolean loadEntity(BaseEntity entity) throws IllegalEntityException, DataAccessException,
			FileAccessException {

		this.analyzeRs();

		return this.loadData(entity);
	}

	/**
	 * Analyzes the structure of Entity Class which instance used to loading
	 * query result.
	 * 
	 * @throws IllegalEntityException
	 * @throws DataAccessException
	 */
	protected void analyzeRs() throws IllegalEntityException, DataAccessException {
		this.indexMethodExpMap = new HashMap<Integer, SettingMethodExp>();

		try {
			// Get the constructor that without any parameter
			Class<?>[] types = new Class[] {};
			this.cons = entityClass.getConstructor(types);

			// Get the entity instance by the constructor and empty parameter
			Object[] args = new Object[] {};
			Object entity = cons.newInstance(args);

			Field[] fields = entity.getClass().getDeclaredFields();

			// 将fields缓存到map(fieldName,field)
			HashMap<String, Field> fieldMap = new HashMap<String, Field>();
			for (int i = 0; i < fields.length; i++) {
				fieldMap.put(fields[i].getName(), fields[i]);
			}

			ResultSetMetaData rsmd = rs.getMetaData();
			this.colCount = rsmd.getColumnCount();

			/**
			 * 列_PAGER_ROW 是分页查询的行号. 若给定的SQL需要查询所有列(如" SELECT * FROM ... "),
			 * 则经过分页构造后的SQL必定有行号（ROWNUM _PAGER_ROW）这一列, 而实体类中没有声明对应属性, 因此必须忽略此列。
			 */
			// 判断最后一列是否是分页查询的行号, 如果是则滤掉
			String colName = rsmd.getColumnName(colCount);
			if (colName.equals("PAGER_ROW")) {
				this.colCount--;
			}

			// the method name of class BaseEntity
			StringBuffer methodName = new StringBuffer();
			Method method = null;

			for (int col = 1; col <= colCount; col++) {
				colName = rsmd.getColumnName(col);

				String fieldName = MappingUtil.getFieldName(colName);

				Field field = (Field) fieldMap.get(fieldName);

				// no field matching the column name
				if (field == null) {
					// throw new IllegalEntityException(
					// IllegalEntityException.ILLEGAL_ENTITY, entityClass
					// .getName()
					// + "#" + fieldName);
					continue;
				}
				String fieldType = field.getType().getName();

				// builds the method name, such as: "setXX"
				methodName.append("set");
				methodName.append(Character.toUpperCase(fieldName.charAt(0)));
				methodName.append(fieldName.substring(1));

				method = entity.getClass().getMethod(methodName.toString(), new Class[] { Class.forName(fieldType) });

				// put the method object into MethodExp vector
				indexMethodExpMap.put(new Integer(col), new SettingMethodExp(fieldType, method));

				methodName.delete(0, methodName.length());
			}

			fieldMap.clear();

		} catch (NoSuchMethodException e) {
			log.error("",e);
			throw new IllegalEntityException(e.getMessage());
		} catch (InstantiationException e) {
			log.error("",e);
			throw new IllegalEntityException(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error("",e);
			throw new IllegalEntityException(e.getMessage());
		} catch (InvocationTargetException e) {
			log.error("",e);
			throw new IllegalEntityException(e.getMessage());
		} catch (SQLException e) {
			log.error("",e);
			throw new DataAccessException(e.getMessage());
		} catch (ClassNotFoundException e) {
			log.error("",e);
			throw new IllegalEntityException(e.getMessage());
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * Builds PageList object with query result.
	 * 
	 * @return
	 * 
	 * @throws IllegalEntityException
	 * @throws DataAccessException
	 */
	protected abstract void buildList() throws IllegalEntityException, DataAccessException, FileAccessException;

	protected abstract boolean loadData(BaseEntity entity) throws IllegalEntityException, DataAccessException,
			FileAccessException;

	/**
	 * 
	 * @return the ArrayList object that have already assembled.
	 * 
	 */
	public abstract ArrayList<BaseEntity> getRsList();
}
