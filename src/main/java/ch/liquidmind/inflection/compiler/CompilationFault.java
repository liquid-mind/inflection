package ch.liquidmind.inflection.compiler;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.antlr.v4.runtime.Token;

import __java.io.__OutputStream;
import __java.io.__Writer;

public class CompilationFault
{
	private static final String CR = System.lineSeparator();
	
	private CompilationUnit compilationUnit;
	private Token offendingTokenStart;
	private Token offendingTokenEnd;
	private String message;
	
	public CompilationFault(
			CompilationUnit compilationUnit,
			Token offendingTokenStart,
			Token offendingTokenEnd,
			String message )
	{
		super();
		this.compilationUnit = compilationUnit;
		this.offendingTokenStart = offendingTokenStart;
		this.offendingTokenEnd = offendingTokenEnd;
		this.message = message;
	}

	public CompilationUnit getCompilationUnit()
	{
		return compilationUnit;
	}

	public Token getOffendingTokenStart()
	{
		return offendingTokenStart;
	}

	public Token getOffendingTokenEnd()
	{
		return offendingTokenEnd;
	}

	public String getMessage()
	{
		return message;
	}

	public void print()
	{
		print( System.err );
	}
	
	public void print( OutputStream os )
	{
		OutputStreamWriter writer = new OutputStreamWriter( os );
		__Writer.write( writer, createFaultMessage() );
		__Writer.flush( writer );
	}
	
	protected String createFaultMessage()
	{
		String sourceFileName = compilationUnit.getCompilationUnitRaw().getSourceFile().getAbsolutePath();
		int startLine = offendingTokenStart.getLine();
		int startChar = offendingTokenStart.getCharPositionInLine();
		String[] sourceFileContent = compilationUnit.getCompilationUnitParsed().getSourceFileContent();
		int emphasisStartIndex = offendingTokenStart.getStartIndex();
		int emphasisStopIndex = offendingTokenEnd.getStopIndex();

		String faultLine = sourceFileName + ", line " + startLine + ":" + startChar + "   " + message;
		String errorLine = sourceFileContent[ offendingTokenStart.getLine() - 1 ];
		String emphasisLine = "";
		
		for ( int i = 0; i < offendingTokenStart.getCharPositionInLine(); i++ )
			if ( errorLine.substring( i, i + 1 ).equals( "\t" ) )
				emphasisLine += "\t";
			else
				emphasisLine += " ";
		
		if ( emphasisStartIndex >= 0 && emphasisStopIndex >= 0 )
			for ( int i = emphasisStartIndex; i <= emphasisStopIndex; i++ )
				emphasisLine += "^";

		return faultLine + CR + errorLine + CR + emphasisLine + CR;
	}
}
