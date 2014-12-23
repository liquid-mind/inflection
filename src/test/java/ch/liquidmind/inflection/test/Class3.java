package ch.liquidmind.inflection.test;

import java.util.HashSet;
import java.util.Set;

public class Class3 extends Class1
{
	private String field4;
	private String field5;
	private Set< Class2 > bidirClass2s = new HashSet< Class2 >();

	public Class3( String field1, String field2, String field4, String field5 )
	{
		super( field1, field2 );
		this.field4 = field4;
		this.field5 = field5;
	}

	public String getField4()
	{
		return field4;
	}

	public void setField4( String field4 )
	{
		this.field4 = field4;
	}

	public String getField5()
	{
		return field5;
	}

	public void setField5( String field5 )
	{
		this.field5 = field5;
	}

	public Set< Class2 > getBidirClass2s()
	{
		return bidirClass2s;
	}
}
