package ch.liquidmind.inflection.operation.basic;

import java.io.OutputStream;
import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VMap;
import ch.liquidmind.inflection.operation.ClassViewPair;
import ch.liquidmind.inflection.operation.Operations;

// TODO Add constructors on traversers that allow the default vmap (DEFAULT_CONFIGURATION)
// to be overridden. Also, rename DEFAULT_CONFIGURATION to DEFAULT_VMAP.
public class BasicOperations extends Operations
{
	// META-MODEL TO TEXT
	public static void metaModelToText( Object object, String taxonomy, String vmap )
	{
		metaModelToText( object, getTaxonomy( taxonomy ), getVMap( vmap ) );
	}
	
	public static void metaModelToText( Object object, Taxonomy taxonomy, VMap vmap )
	{
		metaModelToText( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, taxonomy, vmap );
	}
	
	public static void metaModelToText( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, Taxonomy taxonomy, VMap vmap )
	{
		MetaModelToTextTraverser traverser = new MetaModelToTextTraverser( taxonomy, vmap.newInstance() );
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
	public static void toText( Object object, String taxonomy, String vmap )
	{
		toText( object, getTaxonomy( taxonomy ), getVMap( vmap ) );
	}
	
	public static void toText( Object object, Taxonomy taxonomy, VMap vmap )
	{
		toText( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, taxonomy, vmap );
	}

	public static void toText( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, Taxonomy taxonomy, VMap vmap )
	{
		ToTextTraverser traverser = new ToTextTraverser( taxonomy, vmap.newInstance() );
		traverser.setOutputStream( outputStream );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
	}
	
	// HASH CODE
	public static int hashcode( Object rootObject, String taxonomy, String vmap )
	{
		return hashcode( rootObject, getTaxonomy( taxonomy ), getVMap( vmap ) );
	}

	public static int hashcode( Object rootObject, Taxonomy taxonomy, VMap vmap )
	{
		return hashcode( rootObject, DEFAULT_DEFAULT_ROOT_CLASS, taxonomy, vmap );
	}

	public static int hashcode( Object rootObject, Class< ? > defaultRootClass, Taxonomy taxonomy, VMap vmap )
	{
		HashCodeTraverser traverser = new HashCodeTraverser( taxonomy, vmap.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getCompositeHashCode();
	}

	// EQUALS
	public static boolean equals( Object leftRootObject, Object rightRootObject, String taxonomy, String vmap )
	{
		return equals( leftRootObject, rightRootObject, getTaxonomy( taxonomy ), getVMap( vmap ) );
	}

	public static boolean equals( Object leftRootObject, Object rightRootObject, Taxonomy taxonomy, VMap vmap )
	{
		return equals( leftRootObject, rightRootObject, DEFAULT_DEFAULT_ROOT_CLASS, taxonomy, vmap );
	}

	public static boolean equals( Object leftRootObject, Object rightRootObject, Class< ? > defaultRootClass, Taxonomy taxonomy, VMap vmap )
	{
		EqualsTraverser traverser = new EqualsTraverser( taxonomy, vmap.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( leftRootObject, rightRootObject, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getResult();
	}
}
