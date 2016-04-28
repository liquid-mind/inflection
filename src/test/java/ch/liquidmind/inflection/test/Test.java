package ch.liquidmind.inflection.test;

public class Test
{
	public void test( String s )
	{}
	
	public void test( Object o )
	{}
	
	public void test()
	{
		test( "test" );
	}
	
	public static class Super
	{}
	
	public static class Sub extends Super
	{}
	
	public void test( Super s )
	{}
	
	public void test2()
	{
		test( new Sub() );
	}
	
	
}
