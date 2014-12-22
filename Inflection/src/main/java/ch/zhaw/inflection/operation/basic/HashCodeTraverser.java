package ch.zhaw.inflection.operation.basic;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.inflection.model.HGroup;
import ch.zhaw.inflection.model.VmapInstance;
import ch.zhaw.inflection.operation.LeftGraphTraverser;

public class HashCodeTraverser extends LeftGraphTraverser
{
	public static final String DEFAULT_CONFIGURATION = HashCodeTraverser.class.getName() + CONFIGURATION_SUFFIX;
	public static final int HASHCODING_PRIME = 31;
	
	private int compositeHashCode;
	
	public static class HashCodes
	{
		private List< Integer > hashCodes = new ArrayList< Integer >();
		
		public void addHashCode( int hashCode )
		{
			hashCodes.add( hashCode );
		}
		
		public int getOrderedSum()
		{
			int orderedSum = 0;
			
			for ( int hashCode : hashCodes )
				orderedSum += hashCode * HASHCODING_PRIME;
			
			return orderedSum;
		}
		
		public int getUnorderedSum()
		{
			int unorderedSum = 0;
			
			for ( int hashCode : hashCodes )
				unorderedSum += hashCode;
			
			return unorderedSum;
		}
	}
	
	public HashCodeTraverser( HGroup hGroup )
	{
		this( hGroup, getConfiguration( DEFAULT_CONFIGURATION ) );
	}

	public HashCodeTraverser( HGroup hGroup, VmapInstance vmapInstance )
	{
		super( hGroup, vmapInstance );
	}

	@Override
	protected Object createUserData()
	{
		return new HashCodes();
	}

	public int getCompositeHashCode()
	{
		return compositeHashCode;
	}

	public void setCompositeHashCode( int compositeHashCode )
	{
		this.compositeHashCode = compositeHashCode;
	}
}
