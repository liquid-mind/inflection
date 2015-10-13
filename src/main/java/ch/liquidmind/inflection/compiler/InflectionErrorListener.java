package ch.liquidmind.inflection.compiler;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class InflectionErrorListener extends BaseErrorListener
{
	private CompilationUnit compilationUnit;
	
	public InflectionErrorListener( CompilationUnit compilationUnit )
	{
		super();
		this.compilationUnit = compilationUnit;
	}

	@Override
	public void syntaxError( Recognizer< ?, ? > recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e )
	{
		reportFault( new CompilationError( compilationUnit, (Token)offendingSymbol, (Token)offendingSymbol, msg ) );
	}
	
	public void reportFault( CompilationFault fault )
	{
		compilationUnit.getCompilationFaults().add( fault );
	}
}
