package ch.liquidmind.inflection.model.linked;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface UnparsedAnnotation
{
	public String value() default "";
}