package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VisitorsInstance;
import ch.liquidmind.inflection.operation.DefaultPairingTraverser;



public class SynchronizeTraverser extends DefaultPairingTraverser
{
	public static final String DEFAULT_VISITORS = SynchronizeTraverser.class.getName() + VISITORS_SUFFIX;

	public SynchronizeTraverser( Taxonomy taxonomy )
	{
		super( taxonomy, getVisitors( DEFAULT_VISITORS ) );
	}

	public SynchronizeTraverser( Taxonomy taxonomy, VisitorsInstance visitorsInstance )
	{
		super( taxonomy, visitorsInstance );
	}
	
//	@Override
//	protected List< IdentifiableObjectPair > createIdentifiableObjectPairsFromUnorderedDimension( DimensionViewPair dimensionViewPair )
//	{
//		// TODO implement this to use Object.equals() to determine which elements belong together.
//		throw new UnsupportedOperationException();
//	}
}
