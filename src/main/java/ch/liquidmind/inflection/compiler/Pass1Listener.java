package ch.liquidmind.inflection.compiler;

import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.DefaultPackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.SpecificPackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyNameContext;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class Pass1Listener extends AbstractInflectionListener
{
	public static final String JAVA_PACKAGE = "java";
	public static final String JAVA_LANG_PACKAGE = JAVA_PACKAGE + ".lang";
	public static final String CH_LIQUIDMIND_INFLECTION_PACKAGE = "ch.liquidmind.inflection";
	public static final String DEFAULT_PACKAGE_NAME = "";

	public Pass1Listener( CompilationUnit compilationUnit )
	{
		super( compilationUnit );
	}

	@Override
	public void enterSpecificPackage( SpecificPackageContext specificPackageContext )
	{
		APackageContext aPackageContext = (APackageContext)specificPackageContext.getChild( 1 );
		String packageName = getPackageName( aPackageContext );
		getCompilationUnit().getCompilationUnitCompiled().setPackageName( packageName );
		
		validateDoesntUseReservedNames( aPackageContext, packageName );
		validateCorrespondsWithFileName( aPackageContext, packageName );
	}
	
	@Override
	public void enterDefaultPackage( DefaultPackageContext defaultPackageContext )
	{
		getCompilationUnit().getCompilationUnitCompiled().setPackageName( DEFAULT_PACKAGE_NAME );
	}
	
	@Override
	public void enterTaxonomyName( TaxonomyNameContext taxonomyNameContext )
	{
		String simpleTaxonomyName = taxonomyNameContext.getChild( 0 ).getText();
		String taxonomyName = getCompilationUnit().getCompilationUnitCompiled().getPackageName() + "." + simpleTaxonomyName;
		
		validateTaxonomyNotRedundant( taxonomyNameContext, taxonomyName );
		
		TaxonomyCompiled taxonomyCompiled = new TaxonomyCompiled( taxonomyName );
		getCompilationUnit().getParentCompilationJob().getKnownTaxonomiesCompiled().put( taxonomyName, taxonomyCompiled );
		getCompilationUnit().getCompilationUnitCompiled().getTaxonomiesCompiled().add( taxonomyCompiled );
	}

	// VALIDATION
	
	private void validateTaxonomyNotRedundant( TaxonomyNameContext taxonomyNameContext, String taxonomyName )
	{
		CompilationJob job = getCompilationUnit().getParentCompilationJob();

		if ( job.getKnownTaxonomiesCompiled().keySet().contains( taxonomyName ) )
			reportError( taxonomyNameContext.start, taxonomyNameContext.stop, "Taxonomy '" + taxonomyName + "' already defined in compilation unit '" + getCompilationUnit().getCompilationUnitRaw().getSourceFile() + "'" );

		if ( job.getTaxonomyLoader().loadTaxonomy( taxonomyName ) != null )
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
		String fileName = getCompilationUnit().getCompilationUnitRaw().getSourceFile().getParentFile().getAbsolutePath();
		String expectedFileName = packageName.replace( ".", "/" ) ;
		
		if ( !fileName.endsWith( expectedFileName ) )
			reportError( aPackageContext.start, aPackageContext.stop, "Package name '" + packageName +
				"' does not correspond with file name '" + fileName + "'." );
	}
}
