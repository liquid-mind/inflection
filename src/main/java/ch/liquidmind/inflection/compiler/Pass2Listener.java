package ch.liquidmind.inflection.compiler;

import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport.PackageImportType;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.TypeImport;
import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.PackageImportContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TypeContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TypeImportContext;

public class Pass2Listener extends AbstractInflectionListener
{
	public Pass2Listener( CompilationUnit compilationUnit )
	{
		super( compilationUnit );
	}
	
	@Override
	public void enterPackageImport( PackageImportContext packageImportContext )
	{
		APackageContext aPackageContext = (APackageContext)packageImportContext.getChild( 0 );
		String packageName = getPackageName( aPackageContext );
		validateNoDuplicatePackageImport( aPackageContext, packageName );
		validateNoOverlapWithTypeImport( aPackageContext, packageName );
		getPackageImports().add( new PackageImport( packageName, PackageImportType.OTHER_PACKAGE ) );
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
		getTypeImports().put( simpleTypeName, new TypeImport( typeName ) );
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
