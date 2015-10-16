package ch.liquidmind.inflection.compiler;

import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport.PackageImportType;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.TypeImport;
import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.CompilationUnitContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ExtendedTaxonomyContext;
import ch.liquidmind.inflection.grammar.InflectionParser.PackageImportContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyNameContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TypeContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TypeImportContext;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class Pass2Listener extends AbstractInflectionListener
{
	private TaxonomyCompiled currentTaxonomyCompiled;
	
	public Pass2Listener( CompilationUnit compilationUnit )
	{
		super( compilationUnit );
	}

	@Override
	public void exitCompilationUnit( CompilationUnitContext compilationUnitContext )
	{
		for ( TypeImport typeImport : getTypeImports().values() )
			if ( !typeImport.getWasReferenced() )
				reportWarning( typeImport.getParserRuleContext().start, typeImport.getParserRuleContext().stop, "Unused import." );
		
		for ( PackageImport packageImport : getPackageImports() )
			if ( !packageImport.getWasReferenced() && packageImport.getType().equals( PackageImportType.OTHER_PACKAGE ) && !packageImport.getName().equals( DEFAULT_PACKAGE_NAME ) )
				reportWarning( packageImport.getParserRuleContext().start, packageImport.getParserRuleContext().stop, "Unused import." );
	}

	@Override
	public void enterPackageImport( PackageImportContext packageImportContext )
	{
		APackageContext aPackageContext = (APackageContext)packageImportContext.getChild( 0 );
		String packageName = getPackageName( aPackageContext );
		validateNoDuplicatePackageImport( aPackageContext, packageName );
		validateNoOverlapWithTypeImport( aPackageContext, packageName );
		getPackageImports().add( new PackageImport( packageName, aPackageContext, PackageImportType.OTHER_PACKAGE ) );
	}
	
	private void validateNoDuplicatePackageImport( APackageContext aPackageContext, String packageName )
	{
		if ( getPackageImports().contains( new PackageImport( packageName ) ) )
			reportWarning( aPackageContext.start, aPackageContext.stop, "Duplicate import." );
	}	
	
	private void validateNoOverlapWithTypeImport( APackageContext aPackageContext, String packageName )
	{
		for ( TypeImport typeImport : getTypeImports().values() )
		{
			String typePackageName = getPackageName( typeImport.getName() );
			
			if ( packageName.equals( typePackageName ) )
				reportWarning( aPackageContext.start, aPackageContext.stop, "Overlapping import: symbol already implicitly imported by 'import " + typeImport.getName() + ";" );
		}
	}
	
	@Override
	public void enterTypeImport( TypeImportContext typeImportContext )
	{
		TypeContext typeContext = (TypeContext)typeImportContext.getChild( 0 );
		String typeName = getTypeName( typeContext );
		String simpleTypeName = getSimpleTypeName( typeContext );
		validateNoDuplicateTypeImport( typeContext, simpleTypeName );
		validateNoOverlapWithPackageImport( typeContext, simpleTypeName );
		getTypeImports().put( simpleTypeName, new TypeImport( typeName, typeContext ) );
	}
	
	private void validateNoDuplicateTypeImport( TypeContext typeContext, String simpleTypeName )
	{
		if ( getTypeImports().keySet().contains( simpleTypeName ) )
			reportWarning( typeContext.start, typeContext.stop, "Duplicate import." );
	}
	
	private void validateNoOverlapWithPackageImport( TypeContext typeContext, String simpleTypeName )
	{
		String packageNameOfType = getPackageName( typeContext );
		
		if ( getPackageImports().contains( new PackageImport( packageNameOfType ) ) )
			reportWarning( typeContext.start, typeContext.stop, "Overlapping import: symbol already implicitly imported by 'import " + packageNameOfType + ".*;" );
	}

	@Override
	public void enterTaxonomyName( TaxonomyNameContext taxonomyNameContext )
	{
		currentTaxonomyCompiled = getTaxonomyCompiled( getTaxonomyName( taxonomyNameContext ) );
	}
	
	// This method searches the taxonomies of the compilation unit rather than all known
	// taxonomies within the compilation job.
	private TaxonomyCompiled getTaxonomyCompiled( String taxonomyName )
	{
		TaxonomyCompiled foundTaxonomyCompiled = null;
		
		for ( TaxonomyCompiled taxonomyCompiled : getCompilationUnit().getCompilationUnitCompiled().getTaxonomiesCompiled() )
		{
			if ( taxonomyCompiled.getName().equals( taxonomyName ) )
			{
				foundTaxonomyCompiled = taxonomyCompiled;
				break;
			}
		}
		
		return foundTaxonomyCompiled;
	}
	
	@Override
	public void enterExtendedTaxonomy( ExtendedTaxonomyContext extendedTaxonomyContext )
	{
		TypeContext typeContext = (TypeContext)extendedTaxonomyContext.getChild( 0 );
		String resolvedTaxonomyReference = resolveTaxonomyReference( typeContext );
		
		if ( resolvedTaxonomyReference != null )
			currentTaxonomyCompiled.getExtendedTaxonomies().add( resolvedTaxonomyReference );
	}
	
	private String resolveTaxonomyReference( TypeContext typeContext )
	{
		// TODO Implement by looking first in the type imports and second in the 
		// package imports. Look for matching taxonomies as well as matching classes
		// in the latter and report an error if the resolution is ambiguous.
		
		String resolvedTaxonomyReference = null;
		List< String > matches = new ArrayList< String >();
		String packageName = getPackageName( typeContext );
		String simpleName = getSimpleTypeName( typeContext );
		
		if ( packageName.isEmpty() )
		{
			// First, try matching to a type import.
			TypeImport typeImport = getTypeImports().get( simpleName );
			
			if ( typeImport != null )
			{
				typeImport.setWasReferenced( true );
				String match = typeImport.getName();
				
				if ( taxonomyExists( match ) )
					matches.add( match );
			}

			// Then, try matching to one of the package imports.
			for ( PackageImport packageImport : getPackageImports() )
			{
				String potentialMatch = ( packageImport.getName().isEmpty() ? simpleName : packageImport.getName() + "." + simpleName );
				
				if ( taxonomyExists( potentialMatch ) )
				{
					packageImport.setWasReferenced( true );
					matches.add( potentialMatch );
				}
			}
		}
		else
		{
			String typeName = getTypeName( typeContext );
			
			if ( taxonomyExists( typeName ) )
				matches.add( typeName );
		}
		
		if ( matches.size() == 0 )
			reportError( typeContext.start, typeContext.stop, "Could not find referenced taxonomy (Did you misspell? Or forget an import? Or a jar?)." );
		else if ( matches.size() == 1 )
			resolvedTaxonomyReference = matches.get( 0 );
		else if ( matches.size() > 1 )
			reportError( typeContext.start, typeContext.stop, "Taxonomy reference is ambiguous; could refer to any of: " + String.join( ", ", matches ) + "." );
		else
			throw new IllegalStateException( "Unexpected value for matches.size()." );
		
		return resolvedTaxonomyReference;
	}
	
	private boolean taxonomyExists( String name )
	{
		boolean taxonomyExists = false;
		
		if ( getKnownTaxonomiesCompiled().get( name ) != null ||
				getTaxonomyLoader().loadTaxonomy( name ) != null )
			taxonomyExists = true;
	
		return taxonomyExists;
	}

//	private boolean validateExtendedTaxonomyExists( ExtendedTaxonomyContext extendedTaxonomyContext, String extendedTaxonomyName )
//	{
//		TaxonomyCompiled resolvedTaxonomyCompiled = getKnownTaxonomiesCompiled().get( extendedTaxonomyName );
//		Taxonomy resolvedTaxonomy = getTaxonomyLoader().loadTaxonomy( extendedTaxonomyName );
//		boolean extendedTaxonomyExists;
//		
//		if ( ( resolvedTaxonomyCompiled != null && resolvedTaxonomy == null ) ||
//				( resolvedTaxonomyCompiled == null && resolvedTaxonomy != null ) )
//		{
//			extendedTaxonomyExists = true;
//		}
//		else if ( resolvedTaxonomyCompiled == null && resolvedTaxonomy == null )
//		{
//			extendedTaxonomyExists = false;
//			reportError( extendedTaxonomyContext.start, extendedTaxonomyContext.stop, "Taxonomy not found (Did you misspell? Or forget import? Or forget to include jar?)." );
//		}
//		else
//		{
//			throw new IllegalStateException( "Unexpected values for resolvedTaxonomyCompiled and resolvedTaxonomy." );
//		}
//		
//		return extendedTaxonomyExists;
//	}
	
//	@Override
//	public void enterTypeImport( TypeImportContext typeImportContext )
//	{
//		TypeContext typeContext = (TypeContext)typeImportContext.getChild( 0 );
//		String typeName = getTypeName( typeContext );
//		String simpleTypeName = getSimpleTypeName( typeContext );
//		validateNoOverlapWithPackageImport( typeContext, simpleTypeName );
//		
//		// Register classs
//		if ( getClass( typeName ) != null )
//		{
//			addImportedClass( typeName, true );
//		}
//		// Register taxonomy
//		else
//		{
//			validateNoDuplicateTypeImport( typeContext, simpleTypeName );
//			getImportedTypes().put( simpleTypeName, new ImportedTaxonomy( typeName ) );
//		}
//	}
//	
//	@Override
//	public void enterPackageImport( PackageImportContext packageImportContext )
//	{
//		APackageContext aPackageContext = (APackageContext)packageImportContext.getChild( 0 );
//		String packageName = getPackageName( aPackageContext );
//		validateNoOverlapWithTypeImport( aPackageContext, packageName );
//		validateNoDuplicatePackageImport( aPackageContext, packageName );
//		
//		// Register classes
//		// TODO: exclude anonymous inner classes; possibly include exceptions
//		Reflections reflections = new Reflections( ConfigurationBuilder.build( packageName, getTaxonomyLoader().getClassLoader(), new SubTypesScanner( false ) ) );
//		Set< String > typesInPackage = reflections.getAllTypes();
//		
//		for ( String typeInPackage : typesInPackage )
//			addImportedClass( typeInPackage, false );
//		
//		// Register import
//		getImportedPackages().put( packageName, new ImportedPackage( packageName ) );
//	}
//	
//	private void addImportedClass( String name, boolean wasExplicit )
//	{
//		ImportedClasses importedClasses = (ImportedClasses)getImportedTypes().get( name );
//		
//		if ( importedClasses == null )
//		{
//			String simpleName = getClass( name ).getSimpleName();
//			importedClasses = new ImportedClasses();
//			getImportedTypes().put( simpleName, importedClasses );
//		}
//
//		importedClasses.getImportedClasses().add( new ImportedClass( name, wasExplicit ) );
//	}
//	
//	// VALIDATION
//	
//	private void validateNoOverlapWithPackageImport( TypeContext typeContext, String simpleTypeName )
//	{
//		String packageNameOfType = getPackageName( typeContext );
//		
//		if ( getImportedPackages().keySet().contains( packageNameOfType ) )
//			reportWarning( typeContext.start, typeContext.stop, "Overlapping import: symbol already implicitly imported by 'import " + packageNameOfType + ".*;" );
//	}
//	
//	private void validateNoDuplicateTypeImport( TypeContext typeContext, String simpleTypeName )
//	{
//		if ( getImportedTypes().keySet().contains( simpleTypeName ) )
//			reportWarning( typeContext.start, typeContext.stop, "Duplicate import." );
//	}
//
//	private void validateNoOverlapWithTypeImport( APackageContext aPackageContext, String packageName )
//	{
//		for ( ImportedType importedType : getImportedTypes().values() )
//		{
//			if ( importedType instanceof ImportedClasses )
//			{
//				ImportedClasses importedClasses = (ImportedClasses)importedType;
//				
//				for ( ImportedClass importedClass : importedClasses.getImportedClasses() )
//					if ( importedClass.getWasExplicit() )
//						validateNoOverlapWithTypeImport( aPackageContext, packageName, importedClass.getName() );
//			}
//			else if ( importedType instanceof ImportedTaxonomy )
//			{
//				ImportedTaxonomy importedTaxonomy = (ImportedTaxonomy)importedType;
//				validateNoOverlapWithTypeImport( aPackageContext, packageName, importedTaxonomy.getName() );
//			}
//			else
//			{
//				throw new IllegalStateException( "Unexpected type for importedType." );
//			}
//		}
//	}
//
//	private void validateNoOverlapWithTypeImport( APackageContext aPackageContext, String packageName, String typeName )
//	{
//		String foundPackageName = getPackageName( typeName );
//		
//		if ( packageName.equals( foundPackageName ) )
//			reportWarning( aPackageContext.start, aPackageContext.stop, "Overlapping import: symbol already implicitly imported by 'import " + typeName + ";" );
//	}
//	
//	private void validateNoDuplicatePackageImport( APackageContext aPackageContext, String packageName )
//	{
//		if ( getImportedPackages().keySet().contains( packageName ) )
//			reportWarning( aPackageContext.start, aPackageContext.stop, "Duplicate import." );
//	}
}
