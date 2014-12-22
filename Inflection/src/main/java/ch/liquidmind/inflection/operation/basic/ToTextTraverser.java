package ch.liquidmind.inflection.operation.basic;

import java.util.Set;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.HGroup;
import ch.liquidmind.inflection.model.VmapInstance;
import ch.liquidmind.inflection.operation.IdentifiableObjectPair;


public class ToTextTraverser extends IndentingPrintWriterTraverser
{
	public static final String DEFAULT_CONFIGURATION = ToTextTraverser.class.getName() + CONFIGURATION_SUFFIX;
	
	private Set< IdentifiableObject< ?, ? > > multiplyTraversedObjects;

	public ToTextTraverser( HGroup hGroup )
	{
		this( hGroup, getConfiguration( DEFAULT_CONFIGURATION ) );
	}
	
	public ToTextTraverser( HGroup hGroup, VmapInstance vmapInstance )
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
