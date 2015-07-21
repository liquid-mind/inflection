package ch.liquidmind.inflection.operation.basic;

import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VisitorsInstance;
import ch.liquidmind.inflection.operation.IdentifiableObjectPair;


public class ToTextTraverser extends IndentingPrintWriterTraverser
{
	public static final String DEFAULT_VISITORS = ToTextTraverser.class.getName() + VISITORS_SUFFIX;
	
	private Set< IdentifiableObject< ?, ? > > multiplyTraversedObjects;

	public ToTextTraverser( Taxonomy taxonomy )
	{
		this( taxonomy, getVisitors( DEFAULT_VISITORS ) );
	}
	
	public ToTextTraverser( Taxonomy taxonomy, VisitorsInstance visitorsInstance )
	{
		super( taxonomy, visitorsInstance );
	}
	
	@Override
	public void traverse( IdentifiableObjectPair identifiableObjectPair )
	{
		multiplyTraversedObjects = BasicOperations.identifyMultiplyTraversedObjects( identifiableObjectPair.getLeftObject().getObject(), getTaxonomy() );
		super.traverse( identifiableObjectPair );
	}

	public Set< IdentifiableObject< ?, ? >> getMultiplyTraversedObjects()
	{
		return multiplyTraversedObjects;
	}
}
