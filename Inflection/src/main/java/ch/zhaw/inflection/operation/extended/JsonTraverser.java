package ch.zhaw.inflection.operation.extended;

import java.util.Set;

import ch.zhaw.inflection.IdentifiableObject;
import ch.zhaw.inflection.model.HGroup;
import ch.zhaw.inflection.model.VmapInstance;
import ch.zhaw.inflection.operation.IdentifiableObjectPair;
import ch.zhaw.inflection.operation.basic.BasicOperations;
import ch.zhaw.inflection.operation.basic.IndentingPrintWriterTraverser;



public class JsonTraverser extends IndentingPrintWriterTraverser
{
	public static final String DEFAULT_CONFIGURATION = JsonTraverser.class.getName() + CONFIGURATION_SUFFIX;
	
	private Set< IdentifiableObject< ?, ? > > multiplyTraversedObjects;

	public JsonTraverser( HGroup hGroup )
	{
		super( hGroup, getConfiguration( DEFAULT_CONFIGURATION ) );
	}

	public JsonTraverser( HGroup hGroup, VmapInstance vmapInstance )
	{
		super( hGroup, vmapInstance );
	}
	
	@Override
	public void traverse( IdentifiableObjectPair identifiableObjectPair )
	{
		multiplyTraversedObjects = BasicOperations.identifyMultiplyTraversedObjects( identifiableObjectPair.getLeftObject().getObject(), getHGroup() );
		super.traverse( identifiableObjectPair );
	}

	public Set< IdentifiableObject< ?, ? >> getMultiplyTraversedObjects()
	{
		return multiplyTraversedObjects;
	}
}
