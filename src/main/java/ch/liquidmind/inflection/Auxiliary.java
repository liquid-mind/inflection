package ch.liquidmind.inflection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface Auxiliary
{
	public String value();
}
