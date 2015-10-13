package ch.liquidmind.inflection.compiler;

import org.antlr.v4.runtime.Token;

public class CompilationWarning extends CompilationFault
{
	public CompilationWarning(
			CompilationUnit compilationUnit,
			Token offendingTokenStart,
			Token offendingTokenEnd,
			String message )
	{
		super( compilationUnit, offendingTokenStart, offendingTokenEnd, message );
	}

	@Override
	protected String createFaultMessage()
	{
		return "WARNING: " + super.createFaultMessage();
	}
}
