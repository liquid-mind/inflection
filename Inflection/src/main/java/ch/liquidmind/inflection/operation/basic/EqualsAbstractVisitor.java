package ch.liquidmind.inflection.operation.basic;

import ch.liquidmind.inflection.operation.AbstractVisitor;
import ch.liquidmind.inflection.operation.InflectionViewPair;
import ch.liquidmind.inflection.operation.basic.EqualsTraverser.EqualsData;


public class EqualsAbstractVisitor extends AbstractVisitor< EqualsTraverser >
{
	protected void returnEquality( InflectionViewPair pair, boolean isEqual )
	{
		EqualsData data = getTraverser().getPreviousData();
		
		if ( data == null )
			getTraverser().setResult( isEqual );
		else if ( isEqual == true )
			data.getEqualPairs().add( pair );
		else
			data.getUnequalPairs().add( pair );
	}

	protected boolean areBothNullOrNotNull( Object objLeft, Object objRight )
	{
		boolean areBothNullOrNotNull = false;
		
		if ( objLeft == null && objRight == null )
			areBothNullOrNotNull = true;
		else if ( objLeft != null && objRight != null )
			areBothNullOrNotNull = true;
		
		return areBothNullOrNotNull;
	}
}
