package ch.liquidmind.inflection.operation.basic;

import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VisitorsInstance;
import ch.liquidmind.inflection.operation.LeftGraphTraverser;

public class HashCodeTraverser extends LeftGraphTraverser
{
	public static final String DEFAULT_VISITORS = HashCodeTraverser.class.getName() + VISITORS_SUFFIX;
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
	
	public HashCodeTraverser( Taxonomy taxonomy )
	{
		this( taxonomy, getVisitors( DEFAULT_VISITORS ) );
	}

	public HashCodeTraverser( Taxonomy taxonomy, VisitorsInstance visitorsInstance )
	{
		super( taxonomy, visitorsInstance );
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
