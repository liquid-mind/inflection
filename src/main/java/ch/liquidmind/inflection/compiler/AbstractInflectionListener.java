package ch.liquidmind.inflection.compiler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.TypeImport;
import ch.liquidmind.inflection.grammar.InflectionBaseListener;
import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IdentifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyNameContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TypeContext;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class AbstractInflectionListener extends InflectionBaseListener
{
	public static final String JAVA_PACKAGE = "java";
	public static final String JAVA_LANG_PACKAGE = JAVA_PACKAGE + ".lang";
	public static final String CH_LIQUIDMIND_INFLECTION_PACKAGE = "ch.liquidmind.inflection";
	public static final String DEFAULT_PACKAGE_NAME = "";

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
	
	protected String getSimpleTypeName( TypeContext typeContext )
	{
		String simpleTypeName;
		
		if ( typeContext.getChildCount() == 1 )
			simpleTypeName = typeContext.getChild( 0 ).getText();
		else if ( typeContext.getChildCount() == 3 )
			simpleTypeName = typeContext.getChild( 2 ).getText();
		else
			throw new IllegalStateException( "Unexpected number of tokens in typeContext." );
		
		return simpleTypeName;
	}
	
	protected String getPackageName( TypeContext typeContext )
	{
		String packageName;
		
		if ( typeContext.getChildCount() == 1 )
			packageName = "";
		else if ( typeContext.getChildCount() == 3 )
			packageName = getPackageName( (APackageContext)typeContext.getChild( 0 ) );
		else
			throw new IllegalStateException( "Unexpected number of tokens in typeContext." );
		
		return packageName;
	}
	
	protected String getTypeName( TypeContext typeContext )
	{
		String simpleTypeName = getSimpleTypeName( typeContext );
		String packageName = getPackageName( typeContext );
		String typeName = ( packageName.isEmpty() ? simpleTypeName : packageName + "." + simpleTypeName );
		
		return typeName;
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
	
	protected String getPackageName( String fqTypeName )
	{
		String packageName;
		int lastIndex = fqTypeName.lastIndexOf( "." );
		
		if ( lastIndex == -1 )
			packageName = "";
		else
			packageName = fqTypeName.substring( 0, lastIndex );
		
		return packageName;
	}
	
	protected Class< ? > getClass( String name )
	{
		Class< ? > aClass = null;
		
		try
		{
			aClass = getTaxonomyLoader().getClassLoader().loadClass( name );
		}
		catch ( ClassNotFoundException e )
		{
		}
		
		return aClass;
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
	
	protected Map< String, TypeImport > getTypeImports()
	{
		return getCompilationUnit().getCompilationUnitCompiled().getTypeImports();
	}
	
	protected Set< PackageImport > getPackageImports()
	{
		return getCompilationUnit().getCompilationUnitCompiled().getPackageImports();
	}
	
	protected Map< String, TaxonomyCompiled > getKnownTaxonomiesCompiled()
	{
		return getCompilationUnit().getParentCompilationJob().getKnownTaxonomiesCompiled();
	}
	
	protected TaxonomyLoader getTaxonomyLoader()
	{
		return getCompilationUnit().getParentCompilationJob().getTaxonomyLoader();
	}
	
	protected String getPackageName()
	{
		return getCompilationUnit().getCompilationUnitCompiled().getPackageName();
	}
	
	protected void setPackageName( String name )
	{
		getCompilationUnit().getCompilationUnitCompiled().setPackageName( name );
	}
	
	protected List< TaxonomyCompiled > getTaxonomiesCompiled()
	{
		return getCompilationUnit().getCompilationUnitCompiled().getTaxonomiesCompiled();
	}
	
	protected String getTaxonomyName( TaxonomyNameContext taxonomyNameContext )
	{
		String simpleTaxonomyName = taxonomyNameContext.getChild( 0 ).getText();
		String taxonomyName = getPackageName() + "." + simpleTaxonomyName;
		
		return taxonomyName;
	}
	
	@SuppressWarnings( "unchecked" )
	protected < T extends ParserRuleContext > T getFirstMatchingParserRuleContext( ParserRuleContext parentContext, Class< ? > ... parserRuleContextTypes )
	{
		T firstMatchingParserRuleContext = null;
		Set< Class< ? > > parserRuleContextTypesAsSet = new HashSet< Class< ? > >( Arrays.asList( parserRuleContextTypes ) );
		
		for ( int i = 0 ; i < parentContext.getChildCount() ; ++i )
		{
			if ( parserRuleContextTypesAsSet.contains( parentContext.getChild( i ).getClass() ) )
			{
				firstMatchingParserRuleContext = (T)parentContext.getChild( i );
				break;
			}
		}
		
		return firstMatchingParserRuleContext;
	}
	
	protected < T extends ParserRuleContext > T getRuleContextRecursive( ParserRuleContext parentContext, Class<? extends T> parserRuleContextType )
	{
		T ruleContextRecursive = parentContext.getRuleContext( parserRuleContextType, 0 );

		if ( ruleContextRecursive == null )
		{
			for ( int i = 0 ; i < parentContext.getChildCount() ; ++i )
			{
				if ( parentContext.getChild( i ) instanceof TerminalNode )
					continue;
				
				ruleContextRecursive = getRuleContextRecursive( (ParserRuleContext)parentContext.getChild( i ), parserRuleContextType );
				
				if ( ruleContextRecursive != null )
					break;
			}
		}
		
		return ruleContextRecursive;
	}
}
