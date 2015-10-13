package ch.liquidmind.inflection.compiler;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import ch.liquidmind.inflection.grammar.InflectionBaseListener;
import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IdentifierContext;

public class AbstractInflectionListener extends InflectionBaseListener
{
	private CompilationUnit compilationUnit;

	public AbstractInflectionListener( CompilationUnit compilationUnit )
	{
		super();
		this.compilationUnit = compilationUnit;
	}

	protected CompilationUnit getCompilationUnit()
	{
		return compilationUnit;
	}

	protected String getPackageName( APackageContext aPackageContext )
	{
		String packageName = "";
		
		for ( int i = 0 ; i < aPackageContext.getChildCount() ; ++i )
		{
			ParseTree child = aPackageContext.getChild( i );
			ParseTree token;
			
			// DOT
			if ( child instanceof IdentifierContext )
			{
				IdentifierContext identifierContext = (IdentifierContext)child;
				token = identifierContext.getChild( 0 );
			}
			// Identifier
			else
			{
				token = child;
			}
			
			packageName += token.toString();
		}
		
		return packageName;
	}
	
	protected void reportError( Token offendingTokenStart, Token offendingTokenEnd, String message )
	{
		getCompilationUnit().getErrorListener().reportFault(
			new CompilationError( getCompilationUnit(), offendingTokenStart, offendingTokenEnd, message ) );	
	}
	
	protected void reportWarning( Token offendingTokenStart, Token offendingTokenEnd, String message )
	{
		getCompilationUnit().getErrorListener().reportFault(
			new CompilationWarning( getCompilationUnit(), offendingTokenStart, offendingTokenEnd, message ) );	
	}
}
