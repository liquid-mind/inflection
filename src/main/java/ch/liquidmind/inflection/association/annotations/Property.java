package ch.liquidmind.inflection.association.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface Property
{
	public enum Aggregation
	{
		SHARED, COMPOSITE, NONE;
	}
	
	public static final class INFERED {}

	public Class< ? > type() default INFERED.class;
	public Aggregation aggregation() default Aggregation.NONE;
	public String redefines() default "";
	public String subsets() default "";
	public String[] multiplicity() default {};
}
