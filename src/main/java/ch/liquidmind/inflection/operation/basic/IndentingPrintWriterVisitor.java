package ch.liquidmind.inflection.operation.basic;

import ch.liquidmind.inflection.operation.AbstractVisitor;

public class IndentingPrintWriterVisitor< TraverserType extends IndentingPrintWriterTraverser > extends AbstractVisitor< TraverserType >
{
	public IndentingPrintWriter getPrintWriter()
	{
		return getTraverser().getPrintWriter();
	}
	
	public void print( String msg )
	{
		getPrintWriter().print( msg );
	}
	
	public void println( String msg )
	{
		getPrintWriter().println( msg );
	}
	
	public void println()
	{
		getPrintWriter().println();
	}
	
	public void increaseIndent()
	{
		getPrintWriter().increaseIndent();
	}
	
	public void decreaseIndent()
	{
		getPrintWriter().decreaseIndent();
	}
}
