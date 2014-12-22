package ch.zhaw.inflection.test;

import java.util.HashSet;
import java.util.Set;

public class Class2 extends Class1
{
	private String field3;
	private Set< Class3 > compositeClass3s = new HashSet< Class3 >();
	private Set< Class3 > referencedClass3s = new HashSet< Class3 >();
	private Set< Class3 > bidirClass3s = new HashSet< Class3 >();
	private Set< Class3 > nestedClass2s = new HashSet< Class3 >();

	public Class2( String field1, String field2, String field3 )
	{
		super( field1, field2 );
	}

	@Override
	public String getField2()
	{
		return super.getField2();
	}

	@Override
	public void setField2( String field2 )
	{
		super.setField2( field2 );
	}

	public String getField3()
	{
		return field3;
	}

	public void setField3( String field3 )
	{
		this.field3 = field3;
	}

	public Set< Class3 > getCompositeClass3s()
	{
		return compositeClass3s;
	}

	public Set< Class3 > getReferencedClass3s()
	{
		return referencedClass3s;
	}

	public Set< Class3 > getBidirClass3s()
	{
		return bidirClass3s;
	}

	public Set< Class3 > getNestedClass2s()
	{
		return nestedClass2s;
	}
}
