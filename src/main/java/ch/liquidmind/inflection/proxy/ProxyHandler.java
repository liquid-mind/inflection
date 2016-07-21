package ch.liquidmind.inflection.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.Inflection;
import ch.liquidmind.inflection.compiler.Pass2Listener;
import ch.liquidmind.inflection.model.external.Field;
import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Property;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.proxy.memory.ManualMemoryManager;
import ch.liquidmind.inflection.proxy.memory.TaxonomySpecificMemoryManager.ObjectType;

public class ProxyHandler implements InvocationHandler
{
	private static ThreadLocal< ProxyHandler > contextProxyHandler = new ThreadLocal< ProxyHandler >();
	
	public static ProxyHandler getContextProxyHandler()
	{
		if ( contextProxyHandler.get() == null )
			contextProxyHandler.set( new ProxyHandler() );
		
		return contextProxyHandler.get();
	}

	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable
	{
		return invoke( (Proxy)proxy, method, args );
	}

	private Object invoke( Proxy proxy, Method method, Object[] args ) throws Throwable
	{
		String memberName = Pass2Listener.getPropertyName( method );
		Taxonomy taxonomy = Inflection.getTaxonomy( proxy );
		Member member = taxonomy.getMember( Inflection.getView( proxy ), memberName );
		Object viewableObject = ManualMemoryManager.getContextProxyRegistry().getObject( taxonomy, ObjectType.Object, proxy );
		Object auxiliaryObject = ManualMemoryManager.getContextProxyRegistry().getObject( taxonomy, ObjectType.Auxiliary, proxy );
		Object targetObject = ( method.getDeclaringClass().isAssignableFrom( viewableObject.getClass() ) ? viewableObject : auxiliaryObject );
		List< Object > viewableArgs = getViewableObjects( taxonomy, args );
		MemberOperation memberOperation = getMemberOperation( method );
		Object viewableRetVal;
		
		if ( member instanceof Field )
			viewableRetVal = delegateByField( (Field)member, memberOperation, targetObject, viewableArgs );
		else if ( member instanceof Property )
			viewableRetVal = delegateByProperty( (Property)member, memberOperation, targetObject, viewableArgs );
		else
			throw new IllegalStateException();
		
		Object proxyRetVal = getProxyObject( Inflection.getTaxonomy( proxy ), viewableRetVal );

		return proxyRetVal;
	}
	
	private MemberOperation getMemberOperation( Method method )
	{
		MemberOperation memberOperation;
		String name = method.getName();
		
		if ( name.startsWith( "get" ) || name.startsWith( "is" ) )
			memberOperation = MemberOperation.READ;
		else if ( name.startsWith( "set" ) )
			memberOperation = MemberOperation.WRITE;
		else
			throw new IllegalStateException();
		
		return memberOperation;
	}
	
	private Object delegateByProperty( Property property, MemberOperation memberOperation, Object targetObject, List< Object > viewableArgs ) throws Throwable
	{
		Method method;
		
		if ( memberOperation.equals( MemberOperation.READ ) )
			method = property.getReadMethod();
		else if ( memberOperation.equals( MemberOperation.WRITE ) )
			method = property.getWriteMethod();
		else
			throw new IllegalStateException();

		return method.invoke( targetObject, viewableArgs.toArray( new Object[ viewableArgs.size() ] ) );
	}
	
	private Object delegateByField( Field field, MemberOperation memberOperation, Object targetObject, List< Object > viewableArgs ) throws Throwable
	{
		Object retVal = null;
		
		if ( memberOperation.equals( MemberOperation.READ ) )
			retVal = field.getField().get( targetObject );
		else if ( memberOperation.equals( MemberOperation.WRITE ) )
			field.getField().set( targetObject, viewableArgs.get( 0 ) );
		
		return retVal;
	}
	
	private List< Object > getViewableObjects( Taxonomy taxonomy, Object[] rawObjects )
	{
		List< Object > viewableObjects = new ArrayList< Object >();
		
		for ( Object rawObject : rawObjects )
		{
			if ( rawObject instanceof Proxy )
				viewableObjects.add( ManualMemoryManager.getContextProxyRegistry().getObject( taxonomy, ObjectType.Object, (Proxy)rawObject ) );
			else
				viewableObjects.add( rawObject );
		}
		
		return viewableObjects;
	}
	
	private Object getProxyObject( Taxonomy taxonomy, Object viewableObject )
	{
		Object proxyObject = ManualMemoryManager.getContextProxyRegistry().getObject( taxonomy, ObjectType.Proxy, viewableObject );
		
		if ( proxyObject == null )
			proxyObject = viewableObject;
		
		return proxyObject;
	}
}
