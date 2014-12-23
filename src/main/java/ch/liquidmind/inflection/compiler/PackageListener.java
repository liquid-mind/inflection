package ch.liquidmind.inflection.compiler;

import java.io.File;

import org.antlr.v4.runtime.CommonTokenStream;

import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.PackageDeclarationContext;

public class PackageListener extends AbstractInflectionListener
{
	private static final String DEFAULT_PACKAGE = "";
	
	public PackageListener( File compilationUnit, CommonTokenStream commonTokenStream, boolean bootstrap )
	{
		super( compilationUnit, commonTokenStream, DEFAULT_PACKAGE, null, bootstrap );
	}

	@Override
	public void enterPackageDeclaration( PackageDeclarationContext packageDeclarationContext )
	{
		APackageContext aPackageContext = (APackageContext)packageDeclarationContext.getChild( 1 );
		String packageName = getPackageName( aPackageContext );
		
		if ( !getBootstrap() && ( packageName.startsWith( JAVA_PACKAGE ) || packageName.startsWith( CH_LIQUIDMIND_INFLECTION_PACKAGE ) ) )
		{
			ClassViewErrorListener.displayError( getCompilationUnit(), getCommonTokenStream(), aPackageContext.start, aPackageContext.stop,
				"Package names must not start with " + JAVA_PACKAGE + " or " + CH_LIQUIDMIND_INFLECTION_PACKAGE + " as these are reserved names." );
			stopCompiling();
		}
		
		setPackageName( getPackageName( aPackageContext ) );
	}
}
