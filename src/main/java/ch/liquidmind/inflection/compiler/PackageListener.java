package ch.liquidmind.inflection.compiler;

import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.PackageDeclarationContext;

public class PackageListener extends AbstractInflectionListener
{
	public PackageListener(
			CompilationUnit compilationUnit,
			boolean bootstrap )
	{
		super( compilationUnit, null, bootstrap );
	}

	@Override
	public void enterPackageDeclaration( PackageDeclarationContext packageDeclarationContext )
	{
		APackageContext aPackageContext = (APackageContext)packageDeclarationContext.getChild( 1 );
		String packageName = getPackageName( aPackageContext );
		
		if ( !getBootstrap() && ( packageName.startsWith( JAVA_PACKAGE ) || packageName.startsWith( CH_LIQUIDMIND_INFLECTION_PACKAGE ) ) )
		{
			InflectionErrorListener.displayError( getCompilationUnit().getFile(), getCompilationUnit().getTokens(), aPackageContext.start, aPackageContext.stop,
				"Package names must not start with " + JAVA_PACKAGE + " or " + CH_LIQUIDMIND_INFLECTION_PACKAGE + " as these are reserved names." );
			stopCompiling();
		}
		
		getCompilationUnit().setPackageName( packageName );
	}
}
