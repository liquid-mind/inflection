package ch.liquidmind.inflection.bidir;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface Bidirectional
{
	public String value();
}
