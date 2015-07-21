package ch.liquidmind.inflection.operation.basic;

import java.util.HashSet;
import java.util.Set;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VisitorsInstance;
import ch.liquidmind.inflection.operation.DefaultPairingTraverser;
import ch.liquidmind.inflection.operation.InflectionViewPair;

public class EqualsTraverser extends DefaultPairingTraverser
{
	public static final String DEFAULT_VISITORS = EqualsTraverser.class.getName() + VISITORS_SUFFIX;

	public EqualsTraverser( Taxonomy taxonomy )
	{
		this( taxonomy, getVisitors( DEFAULT_VISITORS ) );
	}

	public EqualsTraverser( Taxonomy taxonomy, VisitorsInstance visitorsInstance )
	{
		super( taxonomy, visitorsInstance );
	}

	public static class EqualsData
	{
		private Set< InflectionViewPair > equalPairs = new HashSet< InflectionViewPair >();
		private Set< InflectionViewPair > unequalPairs = new HashSet< InflectionViewPair >();

		public Set< InflectionViewPair > getEqualPairs()
		{
			return equalPairs;
		}

		public Set< InflectionViewPair > getUnequalPairs()
		{
			return unequalPairs;
		}
	}
	
	@Override
	protected Object createUserData()
	{
		return new EqualsData();
	}

	private boolean result;
	
	public boolean getResult()
	{
		return result;
	}
	
	void setResult( boolean result )
	{
		this.result = result;
	}
	
	public EqualsData getCurrentData()
	{
		return (EqualsData)getCurrentFrame().getUserData();
	}
	
	public EqualsData getPreviousData()
	{
		return ( getPreviousFrame() == null ? null : (EqualsData)getPreviousFrame().getUserData() );
	}
}
