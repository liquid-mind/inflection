package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.model.HGroup;
import ch.liquidmind.inflection.model.VmapInstance;
import ch.liquidmind.inflection.operation.DefaultPairingTraverser;



public class SynchronizeTraverser extends DefaultPairingTraverser
{
	public static final String DEFAULT_CONFIGURATION = SynchronizeTraverser.class.getName() + CONFIGURATION_SUFFIX;

	public SynchronizeTraverser( HGroup hGroup )
	{
		super( hGroup, getConfiguration( DEFAULT_CONFIGURATION ) );
	}

	public SynchronizeTraverser( HGroup hGroup, VmapInstance vmapInstance )
	{
		super( hGroup, vmapInstance );
	}
	
//	@Override
//	protected List< IdentifiableObjectPair > createIdentifiableObjectPairsFromUnorderedDimension( DimensionViewPair dimensionViewPair )
//	{
//		// TODO implement this to use Object.equals() to determine which elements belong together.
//		throw new UnsupportedOperationException();
//	}
}
