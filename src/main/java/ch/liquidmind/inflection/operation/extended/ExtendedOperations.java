package ch.liquidmind.inflection.operation.extended;

import java.io.OutputStream;
import java.util.List;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.Visitors;
import ch.liquidmind.inflection.operation.ClassViewPair;
import ch.liquidmind.inflection.operation.Operations;

public class ExtendedOperations extends Operations
{
	// TO JSON
	public static void toJson( Object object, String taxonomy, String visitors )
	{
		toJson( object, getTaxonomy( taxonomy ), getVisitors( visitors ) );
	}
	
	public static void toJson( Object object, Taxonomy taxonomy, Visitors visitors )
	{
		toJson( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, taxonomy, visitors );
	}

	public static void toJson( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, Taxonomy taxonomy, Visitors visitors )
	{
		JsonTraverser traverser = new JsonTraverser( taxonomy, visitors.newInstance() );
		traverser.setOutputStream( outputStream );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
	}
	
	// VALIDATE

	public static List< ValidationError > validate( Object object, String taxonomy, String visitors )
	{
		return validate( object, getTaxonomy( taxonomy ), getVisitors( visitors ) );
	}
	
	public static List< ValidationError > validate( Object object, Taxonomy taxonomy, Visitors visitors )
	{
		return validate( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, taxonomy, visitors );
	}

	public static List< ValidationError > validate( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, Taxonomy taxonomy, Visitors visitors )
	{
		ValidateTraverser traverser = new ValidateTraverser( taxonomy, visitors.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getValidationErrors();
	}
	
	// Synchronize
	
	public static < T > T synchronize( Object leftRootObject, Object rightRootObject, String taxonomy, String visitors )
	{
		return synchronize( leftRootObject, rightRootObject, getTaxonomy( taxonomy ), getVisitors( visitors ) );
	}

	public static < T > T synchronize( Object leftRootObject, Object rightRootObject, Taxonomy taxonomy, Visitors visitors )
	{
		return synchronize( leftRootObject, rightRootObject, DEFAULT_DEFAULT_ROOT_CLASS, taxonomy, visitors );
	}

	@SuppressWarnings( "unchecked" )
	public static < T > T synchronize( Object leftRootObject, Object rightRootObject, Class< ? > defaultRootClass, Taxonomy taxonomy, Visitors visitors )
	{
		SynchronizeTraverser traverser = new SynchronizeTraverser( taxonomy, visitors.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( leftRootObject, rightRootObject, defaultRootClass );
		traverser.traverse( pair );
		
		return (T)pair.getRightObject().getObject();
	}
}
