package ch.liquidmind.inflection.model.linked;

import java.lang.reflect.Field;

public class FieldLinked extends MemberLinked
{
	private Field field;

	public FieldLinked( String name )
	{
		super( name );
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
