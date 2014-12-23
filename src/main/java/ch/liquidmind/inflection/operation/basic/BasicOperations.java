package ch.liquidmind.inflection.operation.basic;

import java.io.OutputStream;
import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.HGroup;
import ch.liquidmind.inflection.model.VMap;
import ch.liquidmind.inflection.operation.ClassViewPair;
import ch.liquidmind.inflection.operation.Operations;

// TODO Add constructors on traversers that allow the default vmap (DEFAULT_CONFIGURATION)
// to be overridden. Also, rename DEFAULT_CONFIGURATION to DEFAULT_VMAP.
public class BasicOperations extends Operations
{
	// META-MODEL TO TEXT
	public static void metaModelToText( Object object, String hGroup, String vmap )
	{
		metaModelToText( object, getHGroup( hGroup ), getVMap( vmap ) );
	}
	
	public static void metaModelToText( Object object, HGroup hGroup, VMap vmap )
	{
		metaModelToText( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, hGroup, vmap );
	}
	
	public static void metaModelToText( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, HGroup hGroup, VMap vmap )
	{
		MetaModelToTextTraverser traverser = new MetaModelToTextTraverser( hGroup, vmap.newInstance() );
		traverser.setOutputStream( outputStream );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
	}
	
	// IDENTIFY MULTIPLY TRAVERSED OBJECTS
	public static Set< IdentifiableObject< ?, ? > > identifyMultiplyTraversedObjects( Object object, String hGroup )
	{
		return identifyMultiplyTraversedObjects( object, getHGroup( hGroup ) );
	}
	
	public static Set< IdentifiableObject< ?, ? > > identifyMultiplyTraversedObjects( Object object, HGroup hGroup )
	{
		return identifyMultiplyTraversedObjects( object, DEFAULT_DEFAULT_ROOT_CLASS, hGroup );
	}
	
	public static Set< IdentifiableObject< ?, ? > > identifyMultiplyTraversedObjects( Object rootObject, Class< ? > defaultRootClass, HGroup hGroup )
	{
		MultipleTraversalIdentifyingTraverser traverser = new MultipleTraversalIdentifyingTraverser( hGroup );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getMultiplyTraversedObjects();
	}
	
	// TO TEXT
	public static void toText( Object object, String hGroup, String vmap )
	{
		toText( object, getHGroup( hGroup ), getVMap( vmap ) );
	}
	
	public static void toText( Object object, HGroup hGroup, VMap vmap )
	{
		toText( object, DEFAULT_DEFAULT_ROOT_CLASS, DEFAULT_OUTPUT_STREAM, hGroup, vmap );
	}

	public static void toText( Object rootObject, Class< ? > defaultRootClass, OutputStream outputStream, HGroup hGroup, VMap vmap )
	{
		ToTextTraverser traverser = new ToTextTraverser( hGroup, vmap.newInstance() );
		traverser.setOutputStream( outputStream );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
	}
	
	// HASH CODE
	public static int hashcode( Object rootObject, String hGroup, String vmap )
	{
		return hashcode( rootObject, getHGroup( hGroup ), getVMap( vmap ) );
	}

	public static int hashcode( Object rootObject, HGroup hGroup, VMap vmap )
	{
		return hashcode( rootObject, DEFAULT_DEFAULT_ROOT_CLASS, hGroup, vmap );
	}

	public static int hashcode( Object rootObject, Class< ? > defaultRootClass, HGroup hGroup, VMap vmap )
	{
		HashCodeTraverser traverser = new HashCodeTraverser( hGroup, vmap.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( rootObject, null, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getCompositeHashCode();
	}

	// EQUALS
	public static boolean equals( Object leftRootObject, Object rightRootObject, String hGroup, String vmap )
	{
		return equals( leftRootObject, rightRootObject, getHGroup( hGroup ), getVMap( vmap ) );
	}

	public static boolean equals( Object leftRootObject, Object rightRootObject, HGroup hGroup, VMap vmap )
	{
		return equals( leftRootObject, rightRootObject, DEFAULT_DEFAULT_ROOT_CLASS, hGroup, vmap );
	}

	public static boolean equals( Object leftRootObject, Object rightRootObject, Class< ? > defaultRootClass, HGroup hGroup, VMap vmap )
	{
		EqualsTraverser traverser = new EqualsTraverser( hGroup, vmap.newInstance() );
		ClassViewPair pair = traverser.createRootClassViewPair( leftRootObject, rightRootObject, defaultRootClass );
		traverser.traverse( pair );
		
		return traverser.getResult();
	}
}
