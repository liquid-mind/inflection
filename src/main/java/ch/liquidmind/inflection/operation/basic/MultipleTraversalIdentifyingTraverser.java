package ch.liquidmind.inflection.operation.basic;

import java.util.HashSet;
import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.operation.LeftGraphTraverser;

public class MultipleTraversalIdentifyingTraverser extends LeftGraphTraverser
{
	public static final String VISITORS = MultipleTraversalIdentifyingTraverser.class.getName() + VISITORS_SUFFIX;

	private Set< IdentifiableObject< ?, ? > > multiplyTraversedObjects = new HashSet< IdentifiableObject< ?, ? > >();

	public MultipleTraversalIdentifyingTraverser( Taxonomy taxonomy )
	{
		super( taxonomy, getVisitors( VISITORS ) );
	}
	
	public Set< IdentifiableObject< ?, ? > > getMultiplyTraversedObjects()
	{
		return multiplyTraversedObjects;
	}
}
