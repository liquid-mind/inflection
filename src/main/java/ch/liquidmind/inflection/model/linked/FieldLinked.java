package ch.liquidmind.inflection.model.linked;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class FieldLinked extends MemberLinked
{
	private Field field;

	public FieldLinked( List< Annotation > annotations, String alias, ViewLinked parentViewLinked, Field field )
	{
		super( annotations, alias, parentViewLinked );
		this.field = field;
	}

	public Field getField()
	{
		return field;
	}

	public void setField( Field field )
	{
		this.field = field;
	}
}
