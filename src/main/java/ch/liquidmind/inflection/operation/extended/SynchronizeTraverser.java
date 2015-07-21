package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VmapInstance;
import ch.liquidmind.inflection.operation.DefaultPairingTraverser;



public class SynchronizeTraverser extends DefaultPairingTraverser
{
	public static final String DEFAULT_CONFIGURATION = SynchronizeTraverser.class.getName() + CONFIGURATION_SUFFIX;

	public SynchronizeTraverser( Taxonomy taxonomy )
	{
		super( taxonomy, getConfiguration( DEFAULT_CONFIGURATION ) );
	}

	public SynchronizeTraverser( Taxonomy taxonomy, VmapInstance vmapInstance )
	{
		super( taxonomy, vmapInstance );
	}
	
//	@Override
//	protected List< IdentifiableObjectPair > createIdentifiableObjectPairsFromUnorderedDimension( DimensionViewPair dimensionViewPair )
//	{
//		// TODO implement this to use Object.equals() to determine which elements belong together.
//		throw new UnsupportedOperationException();
//	}
}
