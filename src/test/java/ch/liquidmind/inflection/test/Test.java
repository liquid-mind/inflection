package ch.liquidmind.inflection.test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import __java.lang.__Class;
import __java.lang.reflect.__Method;
import ch.liquidmind.inflection.test.model.Person;

@SuppressWarnings( "unchecked" )
public class Test
{
	public static void main( String[] args )
	{
		Method method = __Class.getDeclaredMethod( Person.class, "getFirstName", new Class[] {} );
		
		for ( Annotation annotation : method.getAnnotations() )
			System.out.println( annotation );

		for ( Annotation annotation : method.getAnnotations() )
			System.out.println( getAnnotationLiteral( annotation ) );
	}
	
	private static final Set< Method > IGNOREABLE_METHODS = new HashSet< Method >();
	
	static
	{
		IGNOREABLE_METHODS.add( __Class.getDeclaredMethod( Annotation.class, "equals", new Class[] { Object.class } ) );
		IGNOREABLE_METHODS.add( __Class.getDeclaredMethod( Annotation.class, "hashCode", new Class[] {} ) );
		IGNOREABLE_METHODS.add( __Class.getDeclaredMethod( Annotation.class, "toString", new Class[] {} ) );
		IGNOREABLE_METHODS.add( __Class.getDeclaredMethod( Annotation.class, "annotationType", new Class[] {} ) );
	}
	
	private static String getAnnotationLiteral( Annotation annotation )
	{
		Class< ? > annotationInterface = annotation.getClass().getInterfaces()[ 0 ];
		
		String annotationString = "@" + annotationInterface.getName();
		annotationString += "( ";
		List< String > valueLiterals = new ArrayList< String >();
		
		for ( Method annotationMethod : annotationInterface.getMethods() )
		{
			if ( IGNOREABLE_METHODS.contains( annotationMethod ) )
				continue;
			
			Object value = __Method.invoke( annotationMethod, annotation, new Object[] {} );
			String valueLiteral = getValueLiteral( value );
			valueLiterals.add( annotationMethod.getName() + " = " + valueLiteral );
		}
		
		annotationString += String.join( ", ", valueLiterals );
		annotationString += " )";
		
		return annotationString;
	}
	
	private static String getValueLiteral( Object value )
	{
		String valueLiteral;
		
		if ( value instanceof Byte )
			valueLiteral = getByteLiteral( (Byte)value );
		else if ( value instanceof Short )
			valueLiteral = getShortLiteral( (Short)value );
		else if ( value instanceof Integer )
			valueLiteral = getIntegerLiteral( (Integer)value );
		else if ( value instanceof Long )
			valueLiteral = getLongLiteral( (Long)value );
		else if ( value instanceof Float )
			valueLiteral = getFloatLiteral( (Float)value );
		else if ( value instanceof Double )
			valueLiteral = getDoubleLiteral( (Double)value );
		else if ( value instanceof Character )
			valueLiteral = getCharacterLiteral( (Character)value );
		else if ( value instanceof Boolean )
			valueLiteral = getBooleanLiteral( (Boolean)value );
		else if ( value instanceof String )
			valueLiteral = getStringLiteral( (String)value );
		else if ( value instanceof Class )
			valueLiteral = getClassLiteral( (Class< ? >)value );
		else if ( value instanceof Enum )
			valueLiteral = getEnumLiteral( (Enum< ? >)value );
		else if ( value instanceof Annotation )
			valueLiteral = getAnnotationLiteral( (Annotation)value );
		else if ( value.getClass().isArray() )
			valueLiteral = getArrayLiteral( (Object[])value );
		else
			throw new IllegalStateException( "Unexpected type for value: " + value.getClass().getName() );
		
		return valueLiteral;
	}
	
	private static String getByteLiteral( Byte value )
	{
		return value.toString();
	}
	
	private static String getShortLiteral( Short value )
	{
		return value.toString();
	}
	
	private static String getIntegerLiteral( Integer value )
	{
		return value.toString();
	}
	
	private static String getLongLiteral( Long value )
	{
		return value.toString();
	}
	
	private static String getFloatLiteral( Float value )
	{
		return value.toString() + "F";
	}
	
	private static String getDoubleLiteral( Double value )
	{
		return value.toString();
	}
	
	private static String getCharacterLiteral( Character value )
	{
		return getByteLiteral( (byte)value.charValue() );
	}
	
	private static String getBooleanLiteral( Boolean value )
	{
		return value.toString();
	}
	
	private static String getStringLiteral( String value )
	{
		return "\"" + value + "\"";
	}
	
	private static String getClassLiteral( Class< ? > value )
	{
		return value.getName() + ".class";
	}
	
	private static String getEnumLiteral( Enum< ? > value )
	{
		return value.getDeclaringClass().getName() + "." + value.name();
	}
	
	private static String getArrayLiteral( Object[] values )
	{
		String arrayLiteral = "{ ";
		
		List< String > valueLiterals = new ArrayList< String >();
		
		for ( Object value : values )
			valueLiterals.add( getValueLiteral( value ) );
		
		arrayLiteral += String.join( ", ", valueLiterals );
		arrayLiteral += " }";
		
		return arrayLiteral;
	}
}
