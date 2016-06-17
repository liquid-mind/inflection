package ch.liquidmind.inflection.association.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface Dimension
{
	public String multiplicity() default "";
	public boolean isOrdered() default false;
	public boolean isUnique() default false;
}
