package ch.liquidmind.inflection.operation.basic;

import java.util.HashSet;
import java.util.Set;

import ch.liquidmind.inflection.model.HGroup;
import ch.liquidmind.inflection.model.VmapInstance;
import ch.liquidmind.inflection.operation.DefaultPairingTraverser;
import ch.liquidmind.inflection.operation.InflectionViewPair;

public class EqualsTraverser extends DefaultPairingTraverser
{
	public static final String DEFAULT_CONFIGURATION = EqualsTraverser.class.getName() + CONFIGURATION_SUFFIX;

	public EqualsTraverser( HGroup hGroup )
	{
		this( hGroup, getConfiguration( DEFAULT_CONFIGURATION ) );
	}

	public EqualsTraverser( HGroup hGroup, VmapInstance vmapInstance )
	{
		super( hGroup, vmapInstance );
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
