package ch.zhaw.inflection.operation.basic;

import ch.zhaw.inflection.operation.AbstractVisitor;
import ch.zhaw.inflection.operation.DimensionViewPair;
import ch.zhaw.inflection.operation.InflectionViewFrame;
import ch.zhaw.inflection.operation.InflectionViewPair;
import ch.zhaw.inflection.operation.basic.HashCodeTraverser.HashCodes;


public abstract class HashCodeAbstractVisitor extends AbstractVisitor< HashCodeTraverser >
{
	protected void setHashCode( int hashCode )
	{
		InflectionViewFrame parentFrame = getTraverser().getPreviousFrame();
		
		if ( parentFrame == null )
		{
			getTraverser().setCompositeHashCode( hashCode );
		}
		else
		{
			InflectionViewPair inflectionViewPair = getTraverser().getCurrentFrame().getInflectionViewPair();
			
			if ( inflectionViewPair instanceof DimensionViewPair )
			{
				DimensionViewPair dimensionViewPair = (DimensionViewPair)inflectionViewPair;
				
				// If this is a mapped value --> include key in hash code.
				if ( dimensionViewPair.getLeftDimensionView().isMapped() )
					hashCode = hashCode * HashCodeTraverser.HASHCODING_PRIME + dimensionViewPair.getLeftIndex().hashCode();
			}
				
			((HashCodes)parentFrame.getUserData()).addHashCode( hashCode );
		}
	}
}
