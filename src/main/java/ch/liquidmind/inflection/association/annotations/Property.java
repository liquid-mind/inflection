package ch.liquidmind.inflection.association.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ch.liquidmind.inflection.association.Aggregation;

@Retention( RetentionPolicy.RUNTIME )
public @interface Property
{
	public static final class INFERED {}

	public Class< ? > type() default INFERED.class;
	public Aggregation aggregation() default Aggregation.NONE;
	public String redefines() default "";
	public String subsets() default "";
	public boolean isDerived() default false;
	public boolean isDerivedUnion() default false;
	public Dimension[] dimensions() default {};
}
