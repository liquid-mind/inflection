package ch.liquidmind.inflection.operation.extended;

import java.io.OutputStream;
import java.util.List;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VMap;
import ch.liquidmind.inflection.operation.ClassViewPair;
import ch.liquidmind.inflection.operation.Operations;

public class ExtendedOperations extends Operations
{
	// TO JSON
	public static void toJson( Object object, String taxonomy, String vmap )
	{
		toJson( object, getTaxonomy( taxonomy ), getVMap( vmap ) );
	}
	
	public static void toJson( Object object, Taxonomy taxonomy, VMap vmap )
	{
		toJson( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, taxonomy, vmap );
	}

	public static void toJson( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, Taxonomy taxonomy, VMap vmap )
	{
		JsonTraverser traverser = new JsonTraverser( taxonomy, vmap.newInstance() );
		traverser.setOutputStream( outputStream );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
	}
	
	// VALIDATE

	public static List< ValidationError > validate( Object object, String taxonomy, String vmap )
	{
		return validate( object, getTaxonomy( taxonomy ), getVMap( vmap ) );
	}
	
	public static List< ValidationError > validate( Object object, Taxonomy taxonomy, VMap vmap )
	{
		return validate( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, taxonomy, vmap );
	}

	public static List< ValidationError > validate( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, Taxonomy taxonomy, VMap vmap )
	{
		ValidateTraverser traverser = new ValidateTraverser( taxonomy, vmap.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getValidationErrors();
	}
	
	// Synchronize
	
	public static < T > T synchronize( Object leftRootObject, Object rightRootObject, String taxonomy, String vmap )
	{
		return synchronize( leftRootObject, rightRootObject, getTaxonomy( taxonomy ), getVMap( vmap ) );
	}

	public static < T > T synchronize( Object leftRootObject, Object rightRootObject, Taxonomy taxonomy, VMap vmap )
	{
		return synchronize( leftRootObject, rightRootObject, DEFAULT_DEFAULT_ROOT_CLASS, taxonomy, vmap );
	}

	@SuppressWarnings( "unchecked" )
	public static < T > T synchronize( Object leftRootObject, Object rightRootObject, Class< ? > defaultRootClass, Taxonomy taxonomy, VMap vmap )
	{
		SynchronizeTraverser traverser = new SynchronizeTraverser( taxonomy, vmap.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( leftRootObject, rightRootObject, defaultRootClass );
		traverser.traverse( pair );
		
		return (T)pair.getRightObject().getObject();
	}
}
