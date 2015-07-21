package ch.liquidmind.inflection.operation.basic;

import java.io.OutputStream;
import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.Visitors;
import ch.liquidmind.inflection.operation.ClassViewPair;
import ch.liquidmind.inflection.operation.Operations;

// TODO Add constructors on traversers that allow the default visitors (DEFAULT_CONFIGURATION)
// to be overridden. Also, rename DEFAULT_CONFIGURATION to DEFAULT_VISITORS.
public class BasicOperations extends Operations
{
	// META-MODEL TO TEXT
	public static void metaModelToText( Object object, String taxonomy, String visitors )
	{
		metaModelToText( object, getTaxonomy( taxonomy ), getVisitors( visitors ) );
	}
	
	public static void metaModelToText( Object object, Taxonomy taxonomy, Visitors visitors )
	{
		metaModelToText( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, taxonomy, visitors );
	}
	
	public static void metaModelToText( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, Taxonomy taxonomy, Visitors visitors )
	{
		MetaModelToTextTraverser traverser = new MetaModelToTextTraverser( taxonomy, visitors.newInstance() );
		traverser.setOutputStream( outputStream );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
	}
	
	// IDENTIFY MULTIPLY TRAVERSED OBJECTS
	public static Set< IdentifiableObject< ?, ? > > identifyMultiplyTraversedObjects( Object object, String taxonomy )
	{
		return identifyMultiplyTraversedObjects( object, getTaxonomy( taxonomy ) );
	}
	
	public static Set< IdentifiableObject< ?, ? > > identifyMultiplyTraversedObjects( Object object, Taxonomy taxonomy )
	{
		return identifyMultiplyTraversedObjects( object, DEFAULT_DEFAULT_ROOT_CLASS, taxonomy );
	}
	
	public static Set< IdentifiableObject< ?, ? > > identifyMultiplyTraversedObjects( Object rootObject, Class< ? > defaultRootClass, Taxonomy taxonomy )
	{
		MultipleTraversalIdentifyingTraverser traverser = new MultipleTraversalIdentifyingTraverser( taxonomy );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getMultiplyTraversedObjects();
	}
	
	// TO TEXT
	public static void toText( Object object, String taxonomy, String visitors )
	{
		toText( object, getTaxonomy( taxonomy ), getVisitors( visitors ) );
	}
	
	public static void toText( Object object, Taxonomy taxonomy, Visitors visitors )
	{
		toText( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, taxonomy, visitors );
	}

	public static void toText( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, Taxonomy taxonomy, Visitors visitors )
	{
		ToTextTraverser traverser = new ToTextTraverser( taxonomy, visitors.newInstance() );
		traverser.setOutputStream( outputStream );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
	}
	
	// HASH CODE
	public static int hashcode( Object rootObject, String taxonomy, String visitors )
	{
		return hashcode( rootObject, getTaxonomy( taxonomy ), getVisitors( visitors ) );
	}

	public static int hashcode( Object rootObject, Taxonomy taxonomy, Visitors visitors )
	{
		return hashcode( rootObject, DEFAULT_DEFAULT_ROOT_CLASS, taxonomy, visitors );
	}

	public static int hashcode( Object rootObject, Class< ? > defaultRootClass, Taxonomy taxonomy, Visitors visitors )
	{
		HashCodeTraverser traverser = new HashCodeTraverser( taxonomy, visitors.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getCompositeHashCode();
	}

	// EQUALS
	public static boolean equals( Object leftRootObject, Object rightRootObject, String taxonomy, String visitors )
	{
		return equals( leftRootObject, rightRootObject, getTaxonomy( taxonomy ), getVisitors( visitors ) );
	}

	public static boolean equals( Object leftRootObject, Object rightRootObject, Taxonomy taxonomy, Visitors visitors )
	{
		return equals( leftRootObject, rightRootObject, DEFAULT_DEFAULT_ROOT_CLASS, taxonomy, visitors );
	}

	public static boolean equals( Object leftRootObject, Object rightRootObject, Class< ? > defaultRootClass, Taxonomy taxonomy, Visitors visitors )
	{
		EqualsTraverser traverser = new EqualsTraverser( taxonomy, visitors.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( leftRootObject, rightRootObject, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getResult();
	}
}
