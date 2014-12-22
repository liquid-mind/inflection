package ch.zhaw.inflection.operation.basic;

import java.util.HashSet;
import java.util.Set;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.model.HGroup;
import ch.zhaw.inflection.operation.LeftGraphTraverser;

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
