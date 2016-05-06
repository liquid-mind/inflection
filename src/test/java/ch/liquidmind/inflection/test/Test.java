package ch.liquidmind.inflection.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class Test< T1 >
{
	public static class Test2< T2 >
	{}
	
	// super class == java.lang.Enum
	public enum Test3
	{
		Test31, Test32
	}
	
	public interface Test4
	{
		public interface Test5
		{}
	}
	
	public @interface Test6
	{
		public @interface Test7
		{}
		
		public class Test8 {}
	}
	
	// Field.getType() --> (java.lang.Class<T>) int
	// Field.getGenericType() --> (java.lang.Class<T>) int
	private int field1;
	
	// Field.getType() --> (java.lang.Class<T>) interface java.util.List
	// Field.getGenericType() --> (java.lang.Class<T>) java.util.List<java.lang.Integer>
	@Deprecated
	@Test6.Test7
	private Test2< Test2< Integer > > field2;
	
	public static void main( String[] args ) throws NoSuchFieldException, SecurityException
	{
		ParameterizedType p = (ParameterizedType)Test.class.getDeclaredField( "field2" ).getGenericType();
		ParameterizedType p2 = (ParameterizedType)p.getActualTypeArguments()[ 0 ];
		Type ot = p2.getOwnerType();
		Type rt = p2.getRawType();
		
		Class< ? > ec = Test2.class.getEnclosingClass();
		Class< ? > dc = Test2.class.getDeclaringClass();
		boolean b = Test6.class.isInterface();
	}
}
