package ch.liquidmind.inflection.model;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import __java.lang.reflect.__Method;

public class PropertyView extends MemberView
{
	public PropertyView()
	{
		super();
	}
	
	public PropertyView( String name, ClassView< ? > owingClassView )
	{
		super( name, owingClassView );
	}
	
	public PropertyView( String name, ClassView< ? > owingClassView, ClassView< ? > referencedClassView, Aggregation aggregation )
	{
		super( name, owingClassView, referencedClassView, aggregation );
	}
	
	@Override
	public Type getRawMemberType()
	{
		Method method = getPropertyReadMethod( getOwningClassView().getJavaClass() );
		return method.getGenericReturnType();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public < T > T getMemberInstance( Object containingObject )
	{
		Method method = getPropertyReadMethod( containingObject.getClass() );
		T memberInstance = (T)__Method.invoke( method, containingObject );
		
		return memberInstance;
	}
	
	private Method getPropertyReadMethod( Class< ? > theClass )
	{
		Method foundMethod = null;
		String getterName1 = "get" + getName().toLowerCase();
		String getterName2 = "is" + getName().toLowerCase();
		
		for ( Method method : theClass.getMethods() )
		{
			String methodName = method.getName().toLowerCase();
			
			if ( methodName.equals( getterName1 ) || methodName.equals( getterName2 ) )
			{
				foundMethod = method;
				break;
			}
		}
		
		if ( foundMethod == null)
			throw new NoSuchPropertyException( theClass, getName() );
		
		return foundMethod;
	}
}
