package ch.liquidmind.inflection.compiler;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

import ch.liquidmind.inflection.grammar.InflectionBaseListener;
import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IdentifierContext;
import ch.liquidmind.inflection.model.compiled.AnnotatableElementCompiled;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public abstract class AbstractInflectionListener extends InflectionBaseListener
{
	public static final String JAVA_PACKAGE = "java";
	public static final String JAVA_LANG_PACKAGE = JAVA_PACKAGE + ".lang";
	public static final String CH_LIQUIDMIND_INFLECTION_PACKAGE = "ch.liquidmind.inflection";
	
	private boolean bootstrap;
	private CompilationUnit compilationUnit;
	
	public AbstractInflectionListener( CompilationUnit compilationUnit, boolean bootstrap )
	{
		super();
		this.compilationUnit = compilationUnit;
		this.bootstrap = bootstrap;
	}
	
	protected String getPackageName( APackageContext aPackageContext )
	{
		String packageName = "";
		
		for ( int i = 0 ; i < aPackageContext.getChildCount() ; ++i )
		{
			ParseTree child = aPackageContext.getChild( i );
			ParseTree token;
			
			// Identifier
			if ( child instanceof IdentifierContext )
			{
				IdentifierContext identifierContext = (IdentifierContext)child;
				token = identifierContext.getChild( 0 );
			}
			// DOT
			else
			{
				token = child;
			}
			
			packageName += token.toString();
		}
		
		return packageName;
	}

	protected String getIdentifierFQName( IdentifierContext identifierContext )
	{
		String classViewSimpleName = identifierContext.getChild( 0 ).toString();
		String classViewName = ( compilationUnit.getPackageName() == null ? classViewSimpleName : compilationUnit.getPackageName() + "." + classViewSimpleName );
		
		return classViewName;
	}
	
	protected void stopCompiling()
	{
		throw new RuntimeException( "Cannot compile." );
	}
	
	public CompilationUnit getCompilationUnit()
	{
		return compilationUnit;
	}

	public boolean getBootstrap()
	{
		return bootstrap;
	}
}
