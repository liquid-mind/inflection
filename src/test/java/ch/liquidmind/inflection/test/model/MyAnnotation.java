package ch.liquidmind.inflection.test.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.AccessMode;

@Retention( RetentionPolicy.RUNTIME )
public @interface MyAnnotation
{
	// Primitives
	public byte byteValue() default 0;
	public short shortValue() default 1;
	public int intValue() default 2;
	public long longValue() default 3;
	public float floatValue() default 4.0F;
	public double doubleValue() default 5.0;
	public char charValue() default 6;
	public boolean booleanValue() default true;
	
	// Strings
	public String stringValue() default "string";
	
	// Classes
	public Class< ? > classValue() default Object.class;
	
	// Enums
	public AccessMode enumValue() default AccessMode.EXECUTE;
	
	// Annotations
	public Deprecated annotationValue() default @Deprecated;
	
	// Arrays
	public String[] arrayValue() default { "string1", "string2" };
}
