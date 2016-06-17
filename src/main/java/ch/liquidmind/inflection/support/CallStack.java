package ch.liquidmind.inflection.support;

import java.util.EmptyStackException;
import java.util.Stack;


public class CallStack
{
	private Stack< Frame > callStack = new Stack< Frame >();
	
	public void push()
	{
		Frame newFrame = new Frame();
		Frame currentFrame = peek();
		
		if ( currentFrame != null )
			newFrame.setParamsReceived( currentFrame.getParamsSent() );
		
		callStack.push( newFrame );
	}

	public void pop()
	{
		Frame oldFrame = callStack.pop();
		Frame currentFrame = peek();

		if ( currentFrame != null )
			currentFrame.setReturnsReceived( oldFrame.getReturnsSent() );
	}
	
	public Frame peek()
	{
		Frame frame = null;
		
		try
		{
			frame = callStack.peek();
		}
		catch ( EmptyStackException e )
		{
			// Ignore; return null.
		}
		
		return frame;
	}

	@Override
	public String toString()
	{
		String s = "callstack";
		s += "{\n";
		
		for ( int i = 0 ; i < callStack.size() ; ++i )
		{
			String s2 = "frame " + i + "\n";
			s2 += callStack.get( i ).toString();
			s += Frame.indentString( s2 );		
		}
		
		s += "}\n";
		
		return s;
	}
}
