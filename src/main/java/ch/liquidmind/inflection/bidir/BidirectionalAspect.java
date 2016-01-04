package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;

import __java.lang.__Class;
import __java.lang.reflect.__Field;
import __java.lang.reflect.__Method;
import ch.liquidmind.inflection.bidir.CollectionRegistry.RegisteredCollection;


// TODO
// -should make BidirectionalCollection a part of RegisteredCollection; avoids recreating this object on each invocation.
// -introduce BidirectinoalIterator and implement the remove() operation.
// -should be possible for a list to contain the same element more than once.
// -should be possible to determine order of elements in list (hooks?).
// -should be possible to use maps.
// -should be possible to use multi-dimensional collections.
// -what about arrays?
@Aspect
public class BidirectionalAspect
{
	@Around( value="set( @ch.liquidmind.inflection.bidir.Bidirectional * * ) && !within( ch.liquidmind.inflection.bidir.* )", argNames="joinPoint" )
	public void aroundBidirectionalSet( JoinPoint joinPoint )
	{
		Object object = joinPoint.getTarget();
		Object value = joinPoint.getArgs()[ 0 ];
		Field field = ((FieldSignature)joinPoint.getSignature()).getField();
		field.setAccessible( true );
		handleValueSet( object, field, value );
	}
	
	// Note that the last condition, "!call( * ch.liquidmind.inflection.bidir.*(..) )", is important to ensure
	// that the classes in this package do not trigger infinite loops.
	@Pointcut( "call( * java.lang.reflect.Field.set*(..) ) && !call( * java.lang.reflect.Field.setAccessible(..) ) && !within( ch.liquidmind.inflection.bidir.* ) && if()" )
	public static boolean aroundFieldSetPredicate( JoinPoint joinPoint )
	{
		Field field = (Field)joinPoint.getTarget();
		Bidirectional bidirectional = field.getAnnotation( Bidirectional.class );
		
		return bidirectional != null;
	}
	
	@Around( "aroundFieldSetPredicate( joinPoint )" )
	public void aroundFieldSet( JoinPoint joinPoint )
	{
		Field field = (Field)joinPoint.getTarget();
		field.setAccessible( true );
		Object object = joinPoint.getArgs()[ 0 ];
		Object value = joinPoint.getArgs()[ 1 ];
		handleValueSet( object, field, value );
	}
	
	// TODO: catch reflective invocations of collection operations.
	@Pointcut( "call( * java.util.Collection.*(..) ) && !within( ch.liquidmind.inflection.bidir.* ) && if()" )
	public static boolean aroundCollectionOperationPredicate( JoinPoint joinPoint )
	{
		Collection< ? > collection = (Collection< ? >)joinPoint.getTarget();
		RegisteredCollection registeredCollection = CollectionRegistry.getRegistry().lookup( collection );

		return registeredCollection != null;
	}
	
	@Around( "aroundCollectionOperationPredicate( joinPoint )" )
	public Object aroundCollectionOperation( JoinPoint joinPoint )
	{
		Collection< ? > collection = (Collection< ? >)joinPoint.getTarget();
		RegisteredCollection registeredCollection = CollectionRegistry.getRegistry().lookup( collection );
		Collection< ? > invokableCollection = getBidirectionalCollection( registeredCollection );
		Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
		Class< ? > collectionClass = invokableCollection.getClass();
		Method invokableMethod = __Class.getMethod( collectionClass, method.getName(), method.getParameterTypes() );
		invokableMethod.setAccessible( true );
		Object retVal = __Method.invoke( invokableMethod, invokableCollection, joinPoint.getArgs() );
		
		return retVal;
	}
	
	private BidirectionalCollection< ? > getBidirectionalCollection( RegisteredCollection registeredCollection )
	{
		BidirectionalCollection< ? > bidirectionalCollection;
		Collection< ? > collection = registeredCollection.getCollection();
		Object owner = registeredCollection.getOwner();
		Field field = registeredCollection.getField();
		
		if ( collection instanceof List )
			bidirectionalCollection = new BidirectionalList<>( owner, field, (List< ? >)collection );
		else if ( collection instanceof Set )
			bidirectionalCollection = new BidirectionalSet<>( owner, field, (Set< ? >)collection );
		else
			throw new IllegalStateException( "Bidirectional collections must be either lists or sets." );
		
		return bidirectionalCollection;
	}
	
	private void handleValueSet( Object objectA, Field fieldA, Object newValueB )
	{
		Object oldValueB = __Field.get( fieldA, objectA );
		
		if ( Collection.class.isAssignableFrom( fieldA.getType() ) )
		{
			handleCollectionRegistration( objectA, fieldA, (Collection< ? >)oldValueB, (Collection< ? >)newValueB );
			setField( fieldA, objectA, newValueB );
		}
		
		Set< Link > removableLinks = getRemovableLinks( objectA, fieldA, oldValueB, newValueB );
		Set< Link > addableLinks = ( newValueB == null ? new HashSet< Link >() : Link.getLinks( objectA, fieldA, newValueB ) );
		
		// It is possible for there to be an intersection between the set of a removable and the set of 
		// addable links. For example, if oldValueB and newValueB are collections and if they have an
		// intersection, then that intersection will be marked for both adding and removing. Rather than
		// actually perform these redundant operations, we simply remove the intersection from both sets.
		List< Link > intersection = new ArrayList< Link >( removableLinks );
		intersection.retainAll( addableLinks );
		removableLinks.removeAll( intersection );
		addableLinks.removeAll( intersection );
		
		Link.removeLinks( removableLinks );
		Link.addLinks( addableLinks );
	}
	
	private static Set< Link > getRemovableLinks( Object objectA, Field fieldA, Object oldValueB, Object newValueB )
	{
		Class< ? > classB = getOpposingClass( fieldA );
		Field fieldB = getOpposingField( fieldA, classB );
		
		// 1. All links from objectA's current state are removable.
		// TODO: If oldValueB is a collection then make a shallow copy and pass that to getLinks() instead.
		// This avoids modifying oldValueB itself which might surprise a user.
		Set< Link > removableLinks = ( oldValueB == null ? new HashSet< Link >() : Link.getLinks( objectA, fieldA, oldValueB ) );	
		
		// 2. All links from all new objectB's current state are removable IF the multiplicity of
		//    ClassA from ClassB is one (1).
		if ( !Collection.class.isAssignableFrom( fieldB.getType() ) && newValueB != null )
		{
			if ( Collection.class.isAssignableFrom( fieldA.getType() ) )
			{
				for ( Object newObjectB : (Collection< ? >)newValueB )
					removableLinks.addAll( Link.getLinks( newObjectB, fieldB ) );
			}
			else
			{
				removableLinks.addAll( Link.getLinks( newValueB, fieldB ) );
			}
		}

		return removableLinks;
	}
	
	public static Class< ? > getOpposingClass( Field field )
	{
		Class< ? > opposingClass;
		
		if ( Collection.class.isAssignableFrom( field.getType() ) )
			opposingClass = getGenericTypeParameter( field );
		else
			opposingClass = field.getType();
		
		return opposingClass;
	}
	
	private static Class< ? > getGenericTypeParameter( Field field )
	{
		Type type = field.getGenericType();
		Class< ? > genericTypeParameter;
		
		if  ( !(type instanceof ParameterizedType ) )
			throw new IllegalStateException( "Collection must define a type parameter." );
		
		Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
		
		if ( typeArguments.length != 1 )
			throw new IllegalStateException( "Collection must define exactly one type parameter." );

		Type typeArgument = typeArguments[ 0 ];
		
		if ( !(typeArgument instanceof Class) )
			throw new IllegalStateException( "Type parameter must refer to a non-generic class." );
		
		genericTypeParameter = (Class< ? >)typeArgument;
		
		return genericTypeParameter;
	}
	
	public static Field getOpposingField( Field fieldA, Class< ? > classB )
	{
		Bidirectional bidirectional = fieldA.getAnnotation( Bidirectional.class );
		String opposingFieldName = bidirectional.value();
		Field opposingField = getFieldRecursive( classB, opposingFieldName );
		opposingField.setAccessible( true );
		
		return opposingField;
	}
	
	private static Field getFieldRecursive( Class< ? > theClass, String fieldName )
	{
		Field field = null;
		
		try
		{
			field = theClass.getDeclaredField( fieldName );
		}
		catch ( NoSuchFieldException e )
		{
		}
		
		if ( field == null && theClass.getSuperclass() != null )
			field = getFieldRecursive( theClass.getSuperclass(), fieldName );
		
		return field;
	}	
	
	private void handleCollectionRegistration( Object object, Field field, Collection< ? > oldCollection, Collection< ? > newCollection )
	{
		CollectionRegistry registery = CollectionRegistry.getRegistry();

		if ( oldCollection != null )
			registery.unregister( oldCollection );
		
		if ( newCollection != null )
			registery.register( new RegisteredCollection( object, newCollection, field ) );
	}

	// Note that this cannot be done via deflector libs since those joinpoints are not excluded from selection.
	public static void setField( Field field, Object object, Object value )
	{
		try
		{
			field.set( object, value );
		}
		catch ( IllegalArgumentException e )
		{
			throw new RuntimeException( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new RuntimeException( e );
		}
	}
}
