package ch.zhaw.inflection.compiler;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ch.zhaw.inflection.BasicTypes;
import ch.zhaw.inflection.grammar.InflectionBaseListener;
import ch.zhaw.inflection.grammar.InflectionParser.APackageContext;
import ch.zhaw.inflection.grammar.InflectionParser.IdentifierContext;

public class AbstractInflectionListener extends InflectionBaseListener
{
	public static final String JAVA_PACKAGE = "java";
	public static final String JAVA_LANG_PACKAGE = JAVA_PACKAGE + ".lang";
	public static final String CH_ZHAW_INFLECTION_PACKAGE = "ch.zhaw.inflection";
	public static final Set< String > BASIC_TYPE_VIEWS = new HashSet< String >();
	
	static
	{
		BASIC_TYPE_VIEWS.add( BasicTypes.byteView.getName() );
		BASIC_TYPE_VIEWS.add( BasicTypes.shortView.getName() );
		BASIC_TYPE_VIEWS.add( BasicTypes.intView.getName() );
		BASIC_TYPE_VIEWS.add( BasicTypes.longView.getName() );
		BASIC_TYPE_VIEWS.add( BasicTypes.floatView.getName() );
		BASIC_TYPE_VIEWS.add( BasicTypes.doubleView.getName() );
		BASIC_TYPE_VIEWS.add( BasicTypes.charView.getName() );
		BASIC_TYPE_VIEWS.add( BasicTypes.booleanView.getName() );
	}
	
	private boolean bootstrap;
	private File compilationUnit;
	private CommonTokenStream commonTokenStream;
	private String packageName;
	private Map< String, InflectionResourceCompiled > inflectionResourcesCompiled;
	
	public AbstractInflectionListener( File compilationUnit, CommonTokenStream commonTokenStream, String packageName, Map< String, InflectionResourceCompiled > inflectionResourcesCompiled, boolean bootstrap )
	{
		super();
		this.compilationUnit = compilationUnit;
		this.commonTokenStream = commonTokenStream;
		this.packageName = packageName;
		this.inflectionResourcesCompiled = inflectionResourcesCompiled;
		this.bootstrap = bootstrap;
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

	protected String getIdentifierFQName( IdentifierContext identifierContext )
	{
		String classViewSimpleName = identifierContext.getChild( 0 ).toString();
		String classViewName;
		
		if ( BASIC_TYPE_VIEWS.contains( classViewSimpleName ) )
			classViewName = classViewSimpleName;
		else
			classViewName = ( packageName == null ? classViewSimpleName : packageName + "." + classViewSimpleName );
		
		return classViewName;
	}
	
	protected void stopCompiling()
	{
		throw new RuntimeException( "Cannot compile view file." );
	}
	
	public File getCompilationUnit()
	{
		return compilationUnit;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName( String packageName )
	{
		this.packageName = packageName;
	}

	public CommonTokenStream getCommonTokenStream()
	{
		return commonTokenStream;
	}

	public void setCommonTokenStream( CommonTokenStream commonTokenStream )
	{
		this.commonTokenStream = commonTokenStream;
	}

	public Map< String, InflectionResourceCompiled > getInflectionResourcesCompiled()
	{
		return inflectionResourcesCompiled;
	}

	public boolean getBootstrap()
	{
		return bootstrap;
	}
}
