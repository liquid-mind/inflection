package ch.liquidmind.inflection.operation.extended;

import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VmapInstance;
import ch.liquidmind.inflection.operation.IdentifiableObjectPair;
import ch.liquidmind.inflection.operation.basic.BasicOperations;
import ch.liquidmind.inflection.operation.basic.IndentingPrintWriterTraverser;



public class JsonTraverser extends IndentingPrintWriterTraverser
{
	public static final String DEFAULT_CONFIGURATION = JsonTraverser.class.getName() + CONFIGURATION_SUFFIX;
	
	private Set< IdentifiableObject< ?, ? > > multiplyTraversedObjects;

	public JsonTraverser( Taxonomy taxonomy )
	{
		super( taxonomy, getConfiguration( DEFAULT_CONFIGURATION ) );
	}

	public JsonTraverser( Taxonomy taxonomy, VmapInstance vmapInstance )
	{
		super( taxonomy, vmapInstance );
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
