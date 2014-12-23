package ch.liquidmind.inflection.operation.basic;

import java.io.OutputStream;
import java.io.PrintWriter;

public class IndentingPrintWriter extends PrintWriter
{
	public static final int DEFAULT_INDENT_SIZE = 4;

	private int indentLevel;
	private String singleIndentation;
	private String currentIndentation;
	private boolean indentationRequired = true;

	public IndentingPrintWriter()
	{
		this( DEFAULT_INDENT_SIZE );
	}

	public IndentingPrintWriter( OutputStream outputStream )
	{
		this( outputStream, DEFAULT_INDENT_SIZE );
	}

	public IndentingPrintWriter( int indentSize )
	{
		this( null, indentSize );
	}
	
	public IndentingPrintWriter( OutputStream outputStream, int indentSize )
	{
		super( outputStream );

		singleIndentation = "";
		
		for ( int j = 0 ; j < indentSize ; ++j )
			singleIndentation += " ";
		
		updateCurrentIndentation();
	}
	
	public void increaseIndent()
	{
		++indentLevel;
		updateCurrentIndentation();
	}
	
	public void decreaseIndent()
	{
		--indentLevel;
		updateCurrentIndentation();
	}
	
	private void updateCurrentIndentation()
	{
		currentIndentation = "";
		
		for ( int i = 0 ; i < indentLevel ; ++i )
			currentIndentation += singleIndentation;
	}

	@Override
	public void print( String msg )
	{
		if ( indentationRequired )
		{
			indentationRequired = false;
			super.print( currentIndentation + msg );
		}
		else
		{
			super.print( msg );
		}
	}
	
	@Override
	public void println( String msg )
	{
		if ( indentationRequired )
		{
			indentationRequired = false;
			super.println( currentIndentation + msg );
		}
		else
		{
			super.println( msg );
		}

		indentationRequired = true;
	}

	@Override
	public void println()
	{
		if ( indentationRequired )
		{
			indentationRequired = false;
			super.println();
		}
		else
		{
			super.println();
		}

		indentationRequired = true;
	}
}
