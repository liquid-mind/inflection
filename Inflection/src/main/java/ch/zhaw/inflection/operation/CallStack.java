package ch.zhaw.inflection.operation;

import java.util.Stack;

public class CallStack extends Stack< InflectionViewFrame >
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings( "unchecked" )
	public synchronized < T extends InflectionViewFrame > T peek( int offsetFromCurrent )
	{
		InflectionViewFrame peekableFrame = null;
		int peekableIndex = size() - 1 - offsetFromCurrent;

		if ( peekableIndex >= 0 )
			peekableFrame = get( peekableIndex );
		
		return (T)peekableFrame;
	}
}
