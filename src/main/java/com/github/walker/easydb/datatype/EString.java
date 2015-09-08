package com.github.walker.easydb.datatype;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * EString��, �ṩ��String��Ĺ��ܲ�ʵ����UpdateIdentifier�ӿ�.
 * 

 * @see UpdateIdentifier
 * 
 * @author Huqingmiao
 *  
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class EString implements UpdateIdentifier, Serializable,Comparable {

    private String aString = null;
 
    //��ʶ�Ƿ�Ѷ�Ӧ�и���
    private boolean needUpdate = false;

    /**
     * �����ӣ��䴴���Ķ�������Ӧ���н�������ΪNULL
     *  
     */
    public EString() {
        this.aString = null;
        this.needUpdate = true;
    }

    /**
     * Constructs a new String by decoding the specified array of bytes using
     * the platform's default charset.
     * 
     * @see java.lang.String#String(byte[])
     */
    public EString(byte[] bytes) {
        this.aString = new String(bytes);
        this.needUpdate = true;
    }

    /**
     * Constructs a new String by decoding the specified subarray of bytes using
     * the platform's default charset.
     * 
     * @see java.lang.String#String(byte[], int, int)
     */
    public EString(byte[] bytes, int offset, int length) {
        this.aString = new String(bytes, offset, length);
        this.needUpdate = true;
    }

    /**
     * Constructs a new String by decoding the specified subarray of bytes using
     * the specified charset.
     * 
     * @see java.lang.String#String(byte[], int, int,String)
     */
    public EString(byte[] bytes, int offset, int length, String charsetName)
            throws UnsupportedEncodingException {

        this.aString = new String(bytes, offset, length, charsetName);
        this.needUpdate = true;
    }

    /**
     * Constructs a new String by decoding the specified array of bytes using
     * the specified charset.
     * 
     * @see java.lang.String#String(byte[],String)
     */
    public EString(byte[] bytes, String charsetName)
            throws UnsupportedEncodingException {

        this.aString = new String(bytes, charsetName);
        this.needUpdate = true;
    }

    /**
     * Allocates a new String so that it represents the sequence of characters
     * currently contained in the character array argument.
     * 
     * @see java.lang.String#String(char[])
     */
    public EString(char[] value) {
        this.aString = new String(value);
        this.needUpdate = true;
    }

    /**
     * Allocates a new String that contains characters from a subarray of the
     * character array argument.
     * 
     * @see java.lang.String#String(char[],int, int)
     */
    public EString(char[] value, int offset, int count) {
        this.aString = new String(value, offset, count);
        this.needUpdate = true;
    }

    /**
     * 
     * Initializes a newly created String object so that it represents the same
     * sequence of characters as the argument; in other words, the newly created
     * string is a copy of the argument string.
     * 
     * @see java.lang.String#String(String)
     */
    public EString(String s) {
        this.aString = new String(s);
        this.needUpdate = true;
    }

    /**
     * 
     * Allocates a new string that contains the sequence of characters currently
     * contained in the string buffer argument.
     * 
     * @see java.lang.String#String(StringBuffer)
     */
    public EString(StringBuffer buffer) {
        this.aString = new String(buffer);
        this.needUpdate = true;
    }



    /**
     * 
     * Compares two strings lexicographically.
     * 
     @see java.lang.String#compareTo(String)
     */
    public int compareTo(String anotherString) {
        
        if (this.aString != null) {
            return this.aString.compareTo(anotherString);
        } else {
            return "".compareTo(anotherString);
        }
    }
    
    public int compareTo(Object o) {
        if (this.aString != null) {
            if (o instanceof String) {
                return this.aString.compareTo((String) o);
            } else if (o instanceof EString) {
                return this.aString.compareTo(((EString) o).toString());
            }
        } else {
            if (o instanceof String) {
                return "".compareTo((String) o);
            } else if (o instanceof EString) {
                return "".compareTo(((EString) o).toString());
            }
        }
        return 0;
    }

    /**
     * Compares two strings lexicographically.
     *  
     */
    public int compareTo(EString anotherString) {
        
        if (this.aString != null) {
            return this.aString.compareTo(anotherString.toString());
        } else {
            return "".compareTo(anotherString.toString());
        }
    }

    /**
     * 
     * Compares two strings lexicographically, ignoring case differences.
     * 
     @see java.lang.String#compareToIgnoreCase(String)
     */
    public int compareToIgnoreCase(String anotherString) {
        
        if (this.aString != null) {
            return this.aString.compareToIgnoreCase(anotherString);
        } else {
            return "".compareToIgnoreCase(anotherString);
        }
    }

    /**
     * Compares two strings lexicographically, ignoring case differences.
     *  
     */
    public int compareToIgnoreCase(EString anotherString) {
        if (this.aString != null) {
            return this.aString.compareToIgnoreCase(anotherString.toString());
        } else {
            return "".compareToIgnoreCase(anotherString.toString());
        }
    }

    /**
     * Tests if this string ends with the specified suffix.
     * 
     *@see java.lang.String#endsWith(String)
     *  
     */
    public boolean endsWith(String suffix) {
        if (this.aString != null) {
            return this.aString.endsWith(suffix);
        }
        return false;
    }

    /**
     * Compares this string to the specified object.
     * 
     *@see java.lang.String#equals(Object)
     */
    public boolean equals(Object anObject) {        
        if (this.aString != null) {
            return this.aString.equals(anObject.toString());
        }else{
            if(anObject==null){
                return true;
            } 
            return false;
        }
    }

    /**
     * Compares this String to another String, ignoring case considerations.
     * 
     *@see java.lang.String#equalsIgnoreCase(String)
     */
    public boolean equalsIgnoreCase(String anotherString) {
        if (this.aString != null) {
            return this.aString.equals(anotherString);
        }else{
            if(anotherString==null){
                return true;
            } 
            return false;
        }
    }



    /**
     * Returns the index within this string of the first occurrence of the
     * specified character.
     * 
     *@see java.lang.String#indexOf(int)
     */
    public int indexOf(int ch) {
        
        if (this.aString != null) {
            return this.aString.indexOf(ch);
        }
        
        return -1;
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified character, starting the search at the specified index.
     * 
     *@see java.lang.String#indexOf(int,int)
     */
    public int indexOf(int ch, int fromIndex) {    
        
        if (this.aString != null) {
            return this.aString.indexOf(ch, fromIndex);
        }
        
        return -1;
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring.
     * 
     * @see java.lang.String#indexOf(String)
     */
    public int indexOf(String str) {

        if (this.aString != null) {
            return this.aString.indexOf(str);
        }
        return -1;
    }

    /**
     * Returns the index within this string of the first occurrence of the
     * specified substring, starting at the specified index.
     * 
     * @see java.lang.String#indexOf(String,int)
     *  
     */
    public int indexOf(String str, int fromIndex) {
               
        if (this.aString != null) {
            return this.aString.indexOf(str, fromIndex);
        }
        return -1;
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified character.
     * 
     * @see java.lang.String#lastIndexOf(int)
     */
    public int lastIndexOf(int ch) {

        if (this.aString != null) {
            return this.aString.lastIndexOf(ch);
        }
        return -1;
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified character, searching backward starting at the specified index.
     * 
     * @see java.lang.String#lastIndexOf(int,int)
     */
    public int lastIndexOf(int ch, int fromIndex) {
        if (this.aString != null) {
            return this.aString.lastIndexOf(ch, fromIndex);
        }
        return -1;
    }

    /**
     * Returns the index within this string of the rightmost occurrence of the
     * specified substring.
     * 
     * @see java.lang.String#lastIndexOf(String)
     */
    public int lastIndexOf(String str) {
        if (this.aString != null) {
            return this.aString.lastIndexOf(str);
        }
        return -1;
    }

    /**
     * Returns the index within this string of the last occurrence of the
     * specified substring, searching backward starting at the specified index.
     * 
     * @see java.lang.String#lastIndexOf(String,int)
     */
    public int lastIndexOf(String str, int fromIndex) {
        if (this.aString != null) {
            return this.aString.lastIndexOf(str, fromIndex);
        }
        return -1;
    }

    /**
     * Tests if this string starts with the specified prefix.
     * 
     * @see java.lang.String#startsWith(String)
     */
    public boolean startsWith(String prefix) {
        if (this.aString != null) {
            return this.aString.startsWith(prefix);
        }
        return false;
    }

    /**
     * Tests if this string starts with the specified prefix beginning a
     * specified index.
     * 
     * @see java.lang.String#startsWith(String, int)
     */
    public boolean startsWith(String prefix, int toffset) {
        if (this.aString != null) {
            return this.aString.startsWith(prefix, toffset);
        }
        return false;
    }

    /**
     * Converts all of the characters in this String to lower case using the
     * rules of the default locale.
     * 
     * @see java.lang.String#toLowerCase()
     */
    public EString toLowerCase() {
        if (this.aString != null) {
            return new EString(this.aString.toLowerCase());
        }
        return this;
    }

    /**
     * Converts all of the characters in this String to upper case using the
     * rules of the default locale.
     * 
     * @see java.lang.String#toUpperCase()
     */
    public EString toUpperCase() {
        if (this.aString != null) {
            return new EString(this.aString.toUpperCase());
        }
        return this;

    }

    /**
     * Returns the length of this string.
     * 
     * @see java.lang.String#length()
     */
    public int length() {
        if (this.aString != null) {
            return this.aString.length();
        }

        return 0;
    }

    /**
     * Returns a copy of the string, with leading and trailing whitespace
     * omitted.
     * 
     * @see java.lang.String#trim()
     */
    public EString trim() {

        if (this.aString != null) {
            return new EString(this.aString.trim());
        }

        return this;
    }

    /**
     * 
     * ����String���͵�����
     * 
     */
    public String toString() {
        if (this.aString != null) {
            return this.aString;
        }
        return "";
    }

    /**
     * �����Ƿ�Ѷ�Ӧ�и���;
     * 
     * @param flag
     */
    public void setUpdate(boolean flag) {
        this.needUpdate = flag;

    }

    /**
     * ֻ�е��˷�������trueʱ, �־û������Ż���¶�Ӧ��.
     *  
     */
    public boolean needUpdate() {
        return this.needUpdate;
    }

    /**
     *�ж�����ֵ�Ƿ�Ϊnull, �Ծ����Ƿ�Ӧ�ý���Ӧ����null
     * 
     */
    public boolean isEmpty() {
        return this.aString == null;
    }
    
    
}
