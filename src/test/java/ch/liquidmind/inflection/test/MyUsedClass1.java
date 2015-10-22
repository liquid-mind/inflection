package ch.liquidmind.inflection.test;

public class MyUsedClass1
{
	public static String getE( MyClass1 myClass1 )
	{
		return myClass1.getA() + "*";
	}
	
	public static void setE( MyClass1 myClass1, String e )
	{
		myClass1.setA( e.substring( 0, e.length() - 1 ) );
	}
}
