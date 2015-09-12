package com.github.walker.easydb.dao;

/**
 *
 * This class express the field of BaseEntity object.
 *
 * @author HuQingmiao
 */
public class FieldExp {

	private String fieldName;// the field name, or the attribute name

	private String fieldType;// the field type, such as "java.lang.String"

	private Object fieldValue;// the value of field

	/**
	 *
	 * @param fieldName
	 *            the field name, or the attribute name
	 * @param fieldType
	 *            the field type, such as "java.lang.String"
	 * @param fieldValue
	 *            the value of field
	 */
	public FieldExp(String fieldName, String fieldType, Object fieldValue) {
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.fieldValue = fieldValue;
	}

	/**
	 *
	 * @param fieldName
	 *            the field name, or the attribute name
	 * @param fieldType
	 *            the field type, such as "java.lang.String"
	 */
	public FieldExp(String fieldName, String fieldType) {
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.fieldValue = null;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}
}
