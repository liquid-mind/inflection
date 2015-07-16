package ch.liquidmind.inflection.operation.extended;

import java.io.OutputStream;
import java.util.List;

import ch.liquidmind.inflection.model.HGroup;
import ch.liquidmind.inflection.model.VMap;
import ch.liquidmind.inflection.operation.ClassViewPair;
import ch.liquidmind.inflection.operation.Operations;

public class ExtendedOperations extends Operations
{
	// TO JSON
	public static void toJson( Object object, String hGroup, String vmap )
	{
		toJson( object, getHGroup( hGroup ), getVMap( vmap ) );
	}
	
	public static void toJson( Object object, HGroup hGroup, VMap vmap )
	{
		toJson( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, hGroup, vmap );
	}

	public static void toJson( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, HGroup hGroup, VMap vmap )
	{
		JsonTraverser traverser = new JsonTraverser( hGroup, vmap.newInstance() );
		traverser.setOutputStream( outputStream );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
	}
	
	// VALIDATE

	public static List< ValidationError > validate( Object object, String hGroup, String vmap )
	{
		return validate( object, getHGroup( hGroup ), getVMap( vmap ) );
	}
	
	public static List< ValidationError > validate( Object object, HGroup hGroup, VMap vmap )
	{
		return validate( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, hGroup, vmap );
	}

	public static List< ValidationError > validate( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, HGroup hGroup, VMap vmap )
	{
		ValidateTraverser traverser = new ValidateTraverser( hGroup, vmap.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getValidationErrors();
	}
	
	// Synchronize
	
	public static < T > T synchronize( Object leftRootObject, Object rightRootObject, String hGroup, String vmap )
	{
		return synchronize( leftRootObject, rightRootObject, getHGroup( hGroup ), getVMap( vmap ) );
	}

	public static < T > T synchronize( Object leftRootObject, Object rightRootObject, HGroup hGroup, VMap vmap )
	{
		return synchronize( leftRootObject, rightRootObject, DEFAULT_DEFAULT_ROOT_CLASS, hGroup, vmap );
	}

	@SuppressWarnings( "unchecked" )
	public static < T > T synchronize( Object leftRootObject, Object rightRootObject, Class< ? > defaultRootClass, HGroup hGroup, VMap vmap )
	{
		SynchronizeTraverser traverser = new SynchronizeTraverser( hGroup, vmap.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( leftRootObject, rightRootObject, defaultRootClass );
		traverser.traverse( pair );
		
		return (T)pair.getRightObject().getObject();
	}
}
