package ch.liquidmind.inflection.compiler;

import org.antlr.v4.runtime.Token;

public class CompilationError extends CompilationFault
{
	public CompilationError(
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
		return "ERROR" + super.createFaultMessage();
	}
}
