package ch.liquidmind.inflection.compiler;

import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport.PackageImportType;
import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.DefaultPackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.SpecificPackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyNameContext;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class Pass1Listener extends AbstractInflectionListener
{
	public Pass1Listener( CompilationUnit compilationUnit )
	{
		super( compilationUnit );
	}

	@Override
	public void enterSpecificPackage( SpecificPackageContext specificPackageContext )
	{
		APackageContext aPackageContext = (APackageContext)specificPackageContext.getChild( 1 );
		String packageName = getPackageName( aPackageContext );
		validateDoesntUseReservedNames( aPackageContext, packageName );
		validateCorrespondsWithFileName( aPackageContext, packageName );
		setPackageName( packageName );
		getPackageImports().add( new PackageImport( packageName, null, PackageImportType.OWN_PACKAGE ) );
		
		if ( !packageName.equals( DEFAULT_PACKAGE_NAME ) )
			getPackageImports().add( new PackageImport( DEFAULT_PACKAGE_NAME, null, PackageImportType.OTHER_PACKAGE ) );
	}
	
	@Override
	public void enterDefaultPackage( DefaultPackageContext defaultPackageContext )
	{
		setPackageName( DEFAULT_PACKAGE_NAME );
		getPackageImports().add( new PackageImport( DEFAULT_PACKAGE_NAME, null, PackageImportType.OWN_PACKAGE ) );
	}
	
	@Override
	public void enterTaxonomyName( TaxonomyNameContext taxonomyNameContext )
	{
		String taxonomyName = getTaxonomyName( taxonomyNameContext );
		validateTaxonomyNotRedundant( taxonomyNameContext, taxonomyName );
		TaxonomyCompiled taxonomyCompiled = new TaxonomyCompiled( taxonomyName );
		getKnownTaxonomiesCompiled().put( taxonomyName, taxonomyCompiled );
		getTaxonomiesCompiled().add( taxonomyCompiled );
	}

	// VALIDATION
	
	private void validateTaxonomyNotRedundant( TaxonomyNameContext taxonomyNameContext, String taxonomyName )
	{
		if ( getKnownTaxonomiesCompiled().keySet().contains( taxonomyName ) )
			reportError( taxonomyNameContext.start, taxonomyNameContext.stop, "Taxonomy '" + taxonomyName + "' already defined in compilation unit '" + getCompilationUnit().getCompilationUnitRaw().getSourceFile() + "'" );

		if ( getTaxonomyLoader().loadTaxonomy( taxonomyName ) != null )
			reportError( taxonomyNameContext.start, taxonomyNameContext.stop, "Taxonomy '" + taxonomyName + "' already defined in previously compiled taxonomy." );
	}
	
	private void validateDoesntUseReservedNames( APackageContext aPackageContext, String packageName )
	{
		if ( getCompilationUnit().getParentCompilationJob().getCompilationMode().equals( CompilationMode.NORMAL ) &&
				( packageName.startsWith( JAVA_PACKAGE ) || packageName.startsWith( CH_LIQUIDMIND_INFLECTION_PACKAGE ) ) )
			reportError( aPackageContext.start, aPackageContext.stop,
				"Package names must not start with reserved names '" + JAVA_PACKAGE + "' or '" + CH_LIQUIDMIND_INFLECTION_PACKAGE + "'." );
	}

	private void validateCorrespondsWithFileName( APackageContext aPackageContext, String packageName )
	{
		String fileName = getCompilationUnit().getCompilationUnitRaw().getSourceFile().getParentFile().toURI().getPath().replaceAll("/+$", "");
		String expectedFileName = packageName.replace( ".", "/" ) ;
		
		if ( !fileName.endsWith( expectedFileName ) )
			reportError( aPackageContext.start, aPackageContext.stop, "Package name '" + packageName +
				"' does not correspond with file name '" + fileName + "'." );
	}
}
