package ch.liquidmind.inflection.model;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import __java.lang.__Class;
import __java.lang.reflect.__Field;

public class FieldView extends MemberView
{
	public FieldView()
	{
		super();
	}
	
	public FieldView( String name, ClassView< ? > owingClassView )
	{
		super( name, owingClassView );
	}
	
	public FieldView( String name, ClassView< ? > owingClassView, ClassView< ? > referencedClassView, Aggregation aggregation )
	{
		super( name, owingClassView, referencedClassView, aggregation );
	}

	public Field getField()
	{
		Field field = __Class.getDeclaredField( getOwningClassView().getJavaClass(), getName() );
		field.setAccessible( true );
		
		return field;
	}

	@Override
	public Type getRawMemberType()
	{
		return getField().getGenericType();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public < T > T getMemberInstance( Object containingObject )
	{
		return (T)__Field.get( getField(), containingObject );
	}
}
