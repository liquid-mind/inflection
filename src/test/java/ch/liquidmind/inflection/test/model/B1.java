package ch.liquidmind.inflection.test.model;

import java.util.Date;
import java.util.List;

public class B1 extends A {

	private long longMember;
	private int intMember;
	private double doubleMember;
	private float floatMember;
	private byte byteMember;
	private boolean booleanMember;
	private char charMember;

	private String stringMember;
	private Object objectMember;
	private Date dateMember;
	
	private TestEnum enumMember;
	private List<B1> listMember;

	public long getLongMember() {
		return longMember;
	}

	public void setLongMember(long longMember) {
		this.longMember = longMember;
	}

	public int getIntMember() {
		return intMember;
	}

	public void setIntMember(int intMember) {
		this.intMember = intMember;
	}

	public double getDoubleMember() {
		return doubleMember;
	}

	public void setDoubleMember(double doubleMember) {
		this.doubleMember = doubleMember;
	}

	public float getFloatMember() {
		return floatMember;
	}

	public void setFloatMember(float floatMember) {
		this.floatMember = floatMember;
	}

	public byte getByteMember() {
		return byteMember;
	}

	public void setByteMember(byte byteMember) {
		this.byteMember = byteMember;
	}

	public boolean isBooleanMember() {
		return booleanMember;
	}

	public void setBooleanMember(boolean booleanMember) {
		this.booleanMember = booleanMember;
	}

	public char getCharMember() {
		return charMember;
	}

	public void setCharMember(char charMember) {
		this.charMember = charMember;
	}

	public String getStringMember() {
		return stringMember;
	}

	public void setStringMember(String stringMember) {
		this.stringMember = stringMember;
	}

	public Object getObjectMember() {
		return objectMember;
	}

	public void setObjectMember(Object objectMember) {
		this.objectMember = objectMember;
	}
	
	public Date getDateMember() {
		return dateMember;
	}
	
	public void setDateMember(Date dateMember) {
		this.dateMember = dateMember;
	}
	
	public TestEnum getEnumMember() {
		return enumMember;
	}
	
	public void setEnumMember(TestEnum enumMember) {
		this.enumMember = enumMember;
	}
	
	public List<B1> getListMember() {
		return listMember;
	}
	
	public void setListMember(List<B1> listMember) {
		this.listMember = listMember;
	}

}
