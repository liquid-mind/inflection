package ch.liquidmind.inflection.operation.basic;

import java.util.HashSet;
import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.HGroup;
import ch.liquidmind.inflection.operation.LeftGraphTraverser;

public class MultipleTraversalIdentifyingTraverser extends LeftGraphTraverser
{
	public static final String CONFIGURATION = MultipleTraversalIdentifyingTraverser.class.getName() + CONFIGURATION_SUFFIX;

	private Set< IdentifiableObject< ?, ? > > multiplyTraversedObjects = new HashSet< IdentifiableObject< ?, ? > >();

	public MultipleTraversalIdentifyingTraverser( HGroup hGroup )
	{
		super( hGroup, getConfiguration( CONFIGURATION ) );
	}
	
	public Set< IdentifiableObject< ?, ? > > getMultiplyTraversedObjects()
	{
		return multiplyTraversedObjects;
	}
}
