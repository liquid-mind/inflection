package ch.liquidmind.inflection.print;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class IndentingPrintWriter extends PrintWriter
{
	public static final int DEFAULT_INDENT_SIZE = 4;

	private int indentLevel;
	private String singleIndentation;
	private String currentIndentation;
	private boolean indentationRequired = true;

	public IndentingPrintWriter( OutputStream outputStream )
	{
		this( outputStream, DEFAULT_INDENT_SIZE );
	}

	public IndentingPrintWriter( Writer writer )
	{
		this( writer, DEFAULT_INDENT_SIZE );
	}
	
	public IndentingPrintWriter( Writer writer, int indentSize )
	{
		super( writer );
		setupIndentation( indentSize );
	}
	
	public IndentingPrintWriter( OutputStream outputStream, int indentSize )
	{
		super( outputStream );
		setupIndentation( indentSize );
	}
	
	private void setupIndentation( int indentSize )
	{
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
