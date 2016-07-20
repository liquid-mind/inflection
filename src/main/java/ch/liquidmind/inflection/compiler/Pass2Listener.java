package ch.liquidmind.inflection.compiler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.reflect.ClassPath.ClassInfo;

import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport.PackageImportType;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.StaticClassImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.StaticMemberImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.TypeImport;
import ch.liquidmind.inflection.grammar.InflectionParser.APackageContext;
import ch.liquidmind.inflection.grammar.InflectionParser.AccessMethodModifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.AliasContext;
import ch.liquidmind.inflection.grammar.InflectionParser.AliasableClassSelectorContext;
import ch.liquidmind.inflection.grammar.InflectionParser.AliasableMemberSelectorContext;
import ch.liquidmind.inflection.grammar.InflectionParser.AnnotationClassContext;
import ch.liquidmind.inflection.grammar.InflectionParser.AnnotationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ClassSelectorContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ClassSelectorExpressionContext;
import ch.liquidmind.inflection.grammar.InflectionParser.CompilationUnitContext;
import ch.liquidmind.inflection.grammar.InflectionParser.DefaultAccessMethodModifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ExcludableClassSelectorContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ExcludableMemberSelectorContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ExcludeMemberModifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ExcludeViewModifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ExpressionContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ExtendedTaxonomyContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IdentifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IncludableClassSelectorContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IncludableMemberSelectorContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IncludeMemberModifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IncludeViewModifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.MemberAnnotationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.MemberDeclarationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.MemberSelectorContext;
import ch.liquidmind.inflection.grammar.InflectionParser.MemberSelectorExpressionContext;
import ch.liquidmind.inflection.grammar.InflectionParser.PackageImportContext;
import ch.liquidmind.inflection.grammar.InflectionParser.SimpleTypeContext;
import ch.liquidmind.inflection.grammar.InflectionParser.StaticClassImportContext;
import ch.liquidmind.inflection.grammar.InflectionParser.StaticMemberImportContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyAnnotationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyDeclarationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyExtensionsContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyNameContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TypeContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TypeImportContext;
import ch.liquidmind.inflection.grammar.InflectionParser.UsedClassSelectorContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ViewAnnotationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ViewDeclarationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.WildcardIdentifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.WildcardSimpleTypeContext;
import ch.liquidmind.inflection.loader.BootstrapTaxonomyLoader;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.model.compiled.AnnotationCompiled;
import ch.liquidmind.inflection.model.compiled.MemberCompiled;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;
import ch.liquidmind.inflection.model.compiled.ViewCompiled;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.selectors.FieldSelectorContext;
import ch.liquidmind.inflection.selectors.PropertySelectorContext;

// TODO There is a bug when the same view is defined more than once in the same taxonomy;
// instead of the second definition taking precedence over the first, the view incorrectly
// gets the union of both sets of members.
// TODO The compiler is not currently enforcing the constraint that each taxonomy has to 
// fully mirror the view object hierarchy, including java.lang.Object; the latter should be
// included in the base taxonomy ch.liquidmind.inflection.Taxonomy. Once this is working
// then the ProxyGenerator will generate XXX.java.lang.Object where XXX stands for some 
// fully qualified taxonomy name; these classes can be used in interfaces to indicate any
// object within a given taxonomy.
public class Pass2Listener extends AbstractInflectionListener
{
	private String currentAnnotationUnparsed;
	private TaxonomyCompiled currentTaxonomyCompiled;
	private List< AnnotationCompiled > currentTaxonomyAnnotationsCompiled;
	private List< ViewCompiled > currentViewsCompiled;
	private List< AnnotationCompiled > currentViewAnnotationsCompiled;
	private Map< ViewCompiled, List< MemberCompiled > > currentMembersCompiled;
	private List< AnnotationCompiled > currentMemberAnnotationsCompiled;
	private SelectionType currentViewSelectionType;
	private SelectionType currentMemberSelectionType;
	private AccessType currentAccessType;
	
	// TODO: I think there may be a bug with the currentAccessType similar to what we
	// had with currentAnnotation: since the variable is being using in various contexts,
	// it may yield incorrect values for higher productions if lower productions have
	// overwritten it.
	
	public Pass2Listener( CompilationUnit compilationUnit )
	{
		super( compilationUnit );
	}

	///////////
	// PREAMBLE
	///////////

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
	public void enterStaticClassImport( StaticClassImportContext staticClassImportContext )
	{
		String typeName = staticClassImportContext.getChild( 0 ).getText();
		validateNoDuplicateStaticClassImport( staticClassImportContext, typeName );
		validateNoOverlapWithStaticMemberImport( staticClassImportContext, typeName );
		getStaticClassImports().add( new StaticClassImport( typeName, staticClassImportContext ) );
	}
	
	private void validateNoDuplicateStaticClassImport( StaticClassImportContext staticClassImportContext, String className )
	{
		if ( getStaticClassImports().contains( new StaticClassImport( className ) ) )
			reportWarning( staticClassImportContext.start, staticClassImportContext.stop, "Duplicate import." );
	}
	
	private void validateNoOverlapWithStaticMemberImport( StaticClassImportContext staticClassImportContext, String className )
	{
		for ( StaticMemberImport staticMemberImport : getStaticMemberImports().values() )
		{
			String importClassName = staticMemberImport.getParserRuleContext().getChild( 0 ).getText();
			
			if ( importClassName.equals( className ) )
				reportWarning( staticClassImportContext.start, staticClassImportContext.stop, "Overlapping import: symbol already explicitly imported by 'import " + staticMemberImport.getName() + ";" );
		}
	}
	
	@Override
	public void enterStaticMemberImport( StaticMemberImportContext staticMemberImportContext )
	{
		String qualifiedMemberName = staticMemberImportContext.getText();
		String simpleMemberName = staticMemberImportContext.getChild( 2 ).getText();
		validateNoDuplicateStaticMemberImport( staticMemberImportContext, simpleMemberName );
		validateNoOverlapWithStaticClassImport( staticMemberImportContext, simpleMemberName );
		getStaticMemberImports().put( simpleMemberName, new StaticMemberImport( qualifiedMemberName, staticMemberImportContext ) );
	}
	
	private void validateNoDuplicateStaticMemberImport( StaticMemberImportContext staticMemberImportContext, String staticMemberName )
	{
		if ( getStaticMemberImports().keySet().contains( staticMemberName ) )
			reportWarning( staticMemberImportContext.start, staticMemberImportContext.stop, "Duplicate import." );
	}
	
	private void validateNoOverlapWithStaticClassImport( StaticMemberImportContext staticMemberImportContext, String staticMemberName )
	{
		String className = staticMemberImportContext.getChild( 0 ).getText();
		
		if ( getStaticClassImports().contains( new StaticClassImport( className ) ) )
			reportWarning( staticMemberImportContext.start, staticMemberImportContext.stop, "Overlapping import: symbol already implicitly imported by 'import " + className + ".*;" );
	}

	/////////////
	// TAXONOMIES
	/////////////

	@Override
	public void enterTaxonomyDeclaration( TaxonomyDeclarationContext taxonomyDeclarationContext )
	{
		currentTaxonomyAnnotationsCompiled = new ArrayList< AnnotationCompiled >();
	}
	
	@Override
	public void exitTaxonomyDeclaration( TaxonomyDeclarationContext taxonomyDeclarationContext )
	{
		currentTaxonomyCompiled.getAnnotationsCompiled().addAll( currentTaxonomyAnnotationsCompiled );
		currentTaxonomyCompiled.setDefaultAccessType( currentAccessType );
	}

	@Override
	public void exitTaxonomyAnnotation( TaxonomyAnnotationContext taxonomyAnnotationContext )
	{
		currentTaxonomyAnnotationsCompiled.add( new AnnotationCompiled( currentAnnotationUnparsed ) );
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
	public void enterTaxonomyExtensions( TaxonomyExtensionsContext taxonomyExtensionsContext )
	{
		// Taxonomies that don't extend anything else extend ch.liquidmind.inflection.Taxonomy
		// by default (analogous to java.lang.Object)
		if ( taxonomyExtensionsContext.getChildCount() == 0 )
			currentTaxonomyCompiled.getExtendedTaxonomies().add( BootstrapTaxonomyLoader.TAXONOMY );
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

	@Override
	public void exitDefaultAccessMethodModifier( DefaultAccessMethodModifierContext defaultAccessMethodModifierContext )
	{
		currentTaxonomyCompiled.setDefaultAccessType( currentAccessType );
	}
	
	////////
	// VIEWS
	////////

	@Override
	public void enterViewDeclaration( ViewDeclarationContext viewDeclarationContext )
	{
		currentViewAnnotationsCompiled = new ArrayList< AnnotationCompiled >();
		currentViewsCompiled = new ArrayList< ViewCompiled >();
	}
	
	@Override
	public void exitViewDeclaration( ViewDeclarationContext viewDeclarationContext )
	{
		for ( ViewCompiled currentViewCompiled : currentViewsCompiled )
		{
			currentViewCompiled.getAnnotationsCompiled().addAll( currentViewAnnotationsCompiled );
		}
	}

	@Override
	public void exitViewAnnotation( ViewAnnotationContext viewAnnotationContext )
	{
		currentViewAnnotationsCompiled.add( new AnnotationCompiled( currentAnnotationUnparsed ) );
	}
	
	@Override
	public void enterIncludableClassSelector( IncludableClassSelectorContext includableClassSelectorContext )
	{
		// We'll need to improved this logic at some point
		if ( includableClassSelectorContext.getChild( 0 ) instanceof ClassSelectorExpressionContext )
			return;
		
		enterClassSelector( includableClassSelectorContext );
	}

	@Override
	public void enterExcludableClassSelector( ExcludableClassSelectorContext excludableClassSelectorContext )
	{
		// We'll need to improved this logic at some point
		if ( excludableClassSelectorContext.getChild( 0 ) instanceof ExpressionContext )
			return;
		
		enterClassSelector( excludableClassSelectorContext );
	}

	private void enterClassSelector( ParserRuleContext classSelectorContext )
	{
		Set< String > matchingClasses = getMatchingClasses( classSelectorContext );
		setupViewsCompiled( matchingClasses );
	}
	
	private Set< String > getMatchingClasses( ParserRuleContext classSelectorContext )
	{
		SimpleTypeContext simpleTypeContext = getRuleContextRecursive( classSelectorContext, SimpleTypeContext.class );
		WildcardSimpleTypeContext wildcardSimpleTypeContext = getRuleContextRecursive( classSelectorContext, WildcardSimpleTypeContext.class );
		ParserRuleContext simpleClassSelectorContext = ( simpleTypeContext == null ? wildcardSimpleTypeContext : simpleTypeContext );
		APackageContext packageContext = getRuleContextRecursive( classSelectorContext, APackageContext.class );
		String packagePrefix = ( packageContext == null ? DEFAULT_PACKAGE_NAME : packageContext.getText() + "." );
		String packagePrefixRegEx = ( packagePrefix.equals( DEFAULT_PACKAGE_NAME ) ? "([a-zA-Z0-9_$.]*?\\.)?" : packagePrefix.replace( ".", "\\." ) );
		String classSelector = simpleClassSelectorContext.getText();
		String classSelectorRegEx = packagePrefixRegEx + classSelector.replace( ".", "\\." ).replace( "*", "[a-zA-Z0-9_$]*?" );
		
		return getMatchingClasses( packageContext, classSelectorRegEx );
	}
	
	// Note that the list of potentially matching classes could be cached
	// to optimize performance.
	private Set< String > getMatchingClasses( APackageContext packageContext, String classSelectorRegEx )
	{
		Set< String > matchingClasses = new HashSet< String >();

		if ( packageContext == null )
		{
			matchingClasses.addAll( getMatchingClassesFromPackageImports( getPackageImports(), classSelectorRegEx ) );
		}
		else
		{
			Set< PackageImport > packageImports = new HashSet< PackageImport >();
			packageImports.add( new PackageImport( packageContext.getText() ) );
			matchingClasses.addAll( getMatchingClassesFromPackageImports( packageImports, classSelectorRegEx ) );
		}

		matchingClasses.addAll( getMatchingClassesFromTypeImports( getTypeImports().values(), classSelectorRegEx ) );
		
		return matchingClasses;
	}
	
	private Set< String > getMatchingClassesFromTypeImports( Collection< TypeImport > typeImports, String classSelectorRegEx )
	{
		Set< String > matchingClasses = new HashSet< String >();

		for ( TypeImport typeImport : typeImports )
		{
			String potentialMatch = typeImport.getName();
			
			if ( potentialMatch.matches( classSelectorRegEx ) && getClass( potentialMatch ) != null )
			{
				matchingClasses.add( potentialMatch );
				typeImport.setWasReferenced( true );
			}
		}
		
		return matchingClasses;
	}
	
	private Set< String > getMatchingClassesFromPackageImports( Set< PackageImport > packageImports, String classSelectorRegEx )
	{
		Set< String > matchingClasses = new HashSet< String >();

		for ( PackageImport packageImport : packageImports )
		{
			String packageName = packageImport.getName();
			String packgeRegEx;
			
			if ( packageName.equals( DEFAULT_PACKAGE_NAME ) )
				packgeRegEx = "[a-zA-Z0-9_$]*";
			else
				packgeRegEx = packageName.replace( ".", "\\." ) + "\\.[a-zA-Z0-9_$]*";

			Set< String > typesInPackage = getMatchingClasses( packgeRegEx );
			
			for ( String potentialMatch : typesInPackage )
			{
				if ( potentialMatch.matches( classSelectorRegEx ) )
				{
					matchingClasses.add( potentialMatch );
					packageImport.setWasReferenced( true );
				}
			}
		}
		
		return matchingClasses;
	}
	
	private Map< String, Set< String > > matchingClassesCached = new HashMap< String, Set< String > >();
	
	private Set< String > getMatchingClasses( String packageRegEx )
	{
		Set< String > matchingClasses = matchingClassesCached.get( packageRegEx );
		
		if ( matchingClasses == null )
		{
			matchingClasses = calculateMatchingClasses( packageRegEx );
			matchingClassesCached.put( packageRegEx, matchingClasses );
		}
		
		return matchingClasses;
	}
	
	private Set< String > calculateMatchingClasses( String packageRegEx )
	{
		Set< String > matchingClasses = new HashSet< String >();
		
		Set< ClassInfo > allClasses = getCompilationUnit().getParentCompilationJob().getAllClassesInClassPath();
		// TODO: classes from rt.jar (bootstrap class loader) are not being included.
		
		for ( ClassInfo aClass : allClasses )
			if ( aClass.getName().matches( packageRegEx ) )
				matchingClasses.add( aClass.getName() );
		
		return matchingClasses;
	}

	private void setupViewsCompiled( Set< String > matchingClasses )
	{
		for ( String matchingClass : matchingClasses )
		{
			ViewCompiled matchingView = new ViewCompiled( matchingClass, currentTaxonomyCompiled );
			matchingView.setSelectionType( currentViewSelectionType );
			addOrOverrideView( currentTaxonomyCompiled.getViewsCompiled(), matchingView );
			addOrOverrideView( currentViewsCompiled, matchingView );
		}
	}
	
	private void addOrOverrideView( List< ViewCompiled > overridableViews, ViewCompiled newView )
	{
		boolean viewOverriden = false;
		
		for ( int i = 0 ; i < overridableViews.size() ; ++i )
		{
			ViewCompiled overridableView = overridableViews.get( i );
			
			if ( overridableView.getName().equals( newView.getName() ) && overridableView.getSelectionType().equals( newView.getSelectionType() ) )
			{
				overridableViews.set( i, newView );
				viewOverriden = true;
				break;
			}
		}
		
		if ( !viewOverriden )
			overridableViews.add( newView );
	}

	@Override
	public void enterAliasableClassSelector( AliasableClassSelectorContext aliasableClassSelectorContext )
	{
		// Note that I'm assuming that there is exactly one matching class and therefore
		// invoking iterator().next() is safe; the checks should have already been performed
		// in enterViewDeclaration().
		ClassSelectorContext classSelectorContext = (ClassSelectorContext)aliasableClassSelectorContext.getChild( 0 );
		AliasContext aliasContext = (AliasContext)aliasableClassSelectorContext.getChild( 2 );
		String alias = aliasContext.getText();
		String className = getMatchingClasses( classSelectorContext ).iterator().next();
		String fqAlias = ( getPackageName().equals( DEFAULT_PACKAGE_NAME ) ? alias : getPackageName() + "." + alias );
		validateAliasNameNotInConflict( aliasContext, fqAlias );
		
		for ( ViewCompiled viewCompiled : currentTaxonomyCompiled.getViewsCompiled() )
		{
			if ( viewCompiled.getName().equals( className ) )
			{
				viewCompiled.setAlias( alias );
				break;
			}
		}
	}
	
	// TODO: Currently, checks on conflicts with other alias are only performed within
	// a given taxonomy. Need to think about how to deal with taxonomy inheritence
	// (can aliases override other aliases? can they override views?). Also, should 
	// views be referencable via their alias?
	private void validateAliasNameNotInConflict( AliasContext aliasContext, String alias )
	{
		String fqAlias = ( getPackageName().equals( DEFAULT_PACKAGE_NAME ) ? alias : getPackageName() + "." + alias );
		String classSelectorRegEx = fqAlias.replace( ".", "\\." );
		Set< String > matchingTypes = new HashSet< String >();
		Set< PackageImport > packageImports = new HashSet< PackageImport >();
		packageImports.add( new PackageImport( getPackageName() ) );
		matchingTypes.addAll( getMatchingClassesFromPackageImports( packageImports, classSelectorRegEx ) );
		matchingTypes.addAll( getMatchingClassesFromTypeImports( getTypeImports().values(), classSelectorRegEx ) );
		
		if ( taxonomyExists( fqAlias ) )
			matchingTypes.add( fqAlias );
		
		if ( getKnownAliases().contains( alias ) )
			matchingTypes.add( fqAlias );
		
		if ( !matchingTypes.isEmpty() )
			reportError( aliasContext.start, aliasContext.stop, "Alias name is in conflict with existing types: " + String.join( ", ", matchingTypes ) + "." );
	}
	
	private Set< String > getKnownAliases()
	{
		Set< String > knownAliases = new HashSet< String >();
		
		for ( ViewCompiled viewCompiled : currentTaxonomyCompiled.getViewsCompiled() )
			if ( viewCompiled.getAlias() != null )
				knownAliases.add( viewCompiled.getAlias() );
		
		return knownAliases;
	}
	
	@Override
	public void enterUsedClassSelector( UsedClassSelectorContext usedClassSelectorContext )
	{
		Set< String > matchingClasses = getMatchingClasses( usedClassSelectorContext );
		validateUsedClasses( usedClassSelectorContext, matchingClasses );
		String usedClassName = matchingClasses.iterator().next();

		for ( ViewCompiled currentViewCompiled : currentViewsCompiled )
			currentViewCompiled.setUsedClass( usedClassName );
	}
	
	private void validateUsedClasses( UsedClassSelectorContext usedClassSelectorContext, Set< String > matchingClasses )
	{
		if ( matchingClasses.size() == 0 )
		{
			reportError( usedClassSelectorContext.start, usedClassSelectorContext.stop, "Could not find referenced class (Did you misspell? Or forget an import?)." );
		}
		else if ( matchingClasses.size() == 1 )
		{
			if ( getClass( matchingClasses.iterator().next() ) == null )
				reportError( usedClassSelectorContext.start, usedClassSelectorContext.stop, "Could not find referenced class (Did you misspell? Or forget an import? Or a jar?)." );
		}
		else if ( matchingClasses.size() > 1 )
		{
			reportError( usedClassSelectorContext.start, usedClassSelectorContext.stop, "Class reference is ambiguous; could refer to any of: " + String.join( ", ", matchingClasses ) );
		}
	}
	
	private Set< String > getMatchingClasses( UsedClassSelectorContext usedClassSelectorContext )
	{
		SimpleTypeContext simpleTypeContext = getRuleContextRecursive( usedClassSelectorContext, SimpleTypeContext.class );
		APackageContext packageContext = getRuleContextRecursive( usedClassSelectorContext, APackageContext.class );
		String packagePrefix = ( packageContext == null ? DEFAULT_PACKAGE_NAME : packageContext.getText() + "." );
		String packagePrefixRegEx = ( packagePrefix.equals( DEFAULT_PACKAGE_NAME ) ? "[a-zA-Z0-9_$.]*?" : packagePrefix.replace( ".", "\\." ) );
		String classSelector = simpleTypeContext.getText();
		String classSelectorRegEx = packagePrefixRegEx + classSelector.replace( ".", "\\." ).replace( "*", "[a-zA-Z0-9_$]*?" );
		
		return getMatchingClasses( packageContext, classSelectorRegEx );
	}

	@Override
	public void enterIncludeViewModifier( IncludeViewModifierContext includeViewModifierContext )
	{
		currentViewSelectionType = SelectionType.INCLUDE;
	}

	@Override
	public void enterExcludeViewModifier( ExcludeViewModifierContext excludeViewModifierContext )
	{
		currentViewSelectionType = SelectionType.EXCLUDE;
	}

	@Override
	public void enterClassSelectorExpression( ClassSelectorExpressionContext classSelectorExpressionContext )
	{
		Set< Class< ? > > selectableClasses = getCompilationUnit().getParentCompilationJob().getAllClassesInClassPath2();
		Set< String > matchingClasses = new HashSet< String >();
		
		for ( Class< ? > currentClass : selectableClasses )
		{
			ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
			ch.liquidmind.inflection.selectors.ClassSelectorContext context = new ch.liquidmind.inflection.selectors.ClassSelectorContext( getTaxonomyLoader(), selectableClasses, currentClass );
			Pass2SelectorListener listener = new Pass2SelectorListener( getCompilationUnit(), context, (ExpressionContext)classSelectorExpressionContext.getChild( 0 ) );
			parseTreeWalker.walk( listener, classSelectorExpressionContext );
			boolean classMatches = listener.getExpressionValue();
			
			if ( classMatches )
				matchingClasses.add( currentClass.getName() );
		}
		
		setupViewsCompiled( matchingClasses );
	}
	
	//////////
	// MEMBERS
	//////////

	@Override
	public void enterMemberDeclaration( MemberDeclarationContext memberDeclarationContext )
	{
		currentMembersCompiled = new HashMap< ViewCompiled, List< MemberCompiled > >();
		
		for ( ViewCompiled currentViewCompiled : currentViewsCompiled )
			currentMembersCompiled.put( currentViewCompiled, new ArrayList< MemberCompiled >() );
		
		currentMemberAnnotationsCompiled = new ArrayList< AnnotationCompiled >();
		currentAccessType = null;
	}
	
	@Override
	public void exitMemberDeclaration( MemberDeclarationContext memberDeclarationContext )
	{
		for ( List< MemberCompiled > currentMembersCompiledOfView : currentMembersCompiled.values() )
		{
			for ( MemberCompiled currentMemberCompiled : currentMembersCompiledOfView )
			{
				currentMemberCompiled.getAnnotationsCompiled().addAll( currentMemberAnnotationsCompiled );
				currentMemberCompiled.setSelectionType( currentMemberSelectionType );
				currentMemberCompiled.setAccessType( currentAccessType );
			}
		}
	}

	@Override
	public void exitMemberAnnotation( MemberAnnotationContext memberAnnotationContext )
	{
		currentMemberAnnotationsCompiled.add( new AnnotationCompiled( currentAnnotationUnparsed ) );
	}

	@Override
	public void enterIncludableMemberSelector( IncludableMemberSelectorContext includableMemberSelectorContext )
	{
		// We'll need to improved this logic at some point
		if ( includableMemberSelectorContext.getChild( 0 ) instanceof MemberSelectorExpressionContext )
			return;
		
		enterMemberSelector( includableMemberSelectorContext );
	}

	@Override
	public void enterExcludableMemberSelector( ExcludableMemberSelectorContext excludableMemberSelectorContext )
	{
		// We'll need to improved this logic at some point
		if ( excludableMemberSelectorContext.getChild( 0 ) instanceof MemberSelectorExpressionContext )
			return;
		
		enterMemberSelector( excludableMemberSelectorContext );
	}
	
	private void enterMemberSelector( ParserRuleContext memberSelectorContext )
	{
		AccessType effectiveAccessType = getEffectiveAccessType();
		
		for ( ViewCompiled currentViewCompiled : currentViewsCompiled )
		{
			List< String > matchingMembers = getMatchingMembers( currentViewCompiled, effectiveAccessType, memberSelectorContext );
			List< MemberCompiled > membersCompiled = getMembersCompiled( currentViewCompiled, matchingMembers );
			addMembersCompiled( currentViewCompiled.getMembersCompiled(), membersCompiled );
			addMembersCompiled( currentMembersCompiled.get( currentViewCompiled ), membersCompiled );
		}
	}
	
	private void addMembersCompiled( List< MemberCompiled > membersCompiledA, List< MemberCompiled > membersCompiledB )
	{
		for ( MemberCompiled memberCompiledB : membersCompiledB )
		{
			boolean foundMemberCompiled = false;
			
			for ( int i = 0 ; i < membersCompiledA.size() ; ++i )
			{
				if ( membersCompiledA.get( i ).getName().equals( memberCompiledB.getName() ) )
				{
					membersCompiledA.set( i, memberCompiledB );
					foundMemberCompiled = true;
					break;
				}
			}
			
			if ( !foundMemberCompiled )
				membersCompiledA.add( memberCompiledB );
		}
	}
	
	private AccessType getEffectiveAccessType()
	{
		AccessType effectiveAccessType;
		
		if ( currentAccessType != null )
			effectiveAccessType = currentAccessType;
		else
			effectiveAccessType = getDefaultAccessType( currentTaxonomyCompiled.getName() );
		
		return effectiveAccessType;
	}

	private AccessType getDefaultAccessType( String taxonomyName )
	{
		AccessType defaultAccessType = null;

		defaultAccessType = getDeclaredDefaultAccessType( taxonomyName );
		
		// Note that we follow the left-most taxonomy in the inheritence hierarchy
		// until we reach ch.liquidmind.inflection.Taxonomy.
		if ( defaultAccessType == null )
			defaultAccessType = getDefaultAccessType( getExtendedTaxonomies( taxonomyName ).get( 0 ) );
		
		return defaultAccessType;
	}
	
	private AccessType getDeclaredDefaultAccessType( String taxonomyName )
	{
		TaxonomyCompiled taxonomyCompiled = getKnownTaxonomiesCompiled().get( taxonomyName );
		Taxonomy taxonomy = getTaxonomyLoader().loadTaxonomy( taxonomyName );
		
		// Since we've already validated the taxonomy this condition should never occur,
		// but just in case...
		if ( taxonomyCompiled == null && taxonomy == null )
			throw new IllegalStateException( "Couldn't locate taxonomy " + taxonomyName + "." );
		
		AccessType declaredDefaultAccessType = ( taxonomyCompiled != null ? taxonomyCompiled.getDefaultAccessType() : taxonomy.getDefaultAccessType() );

		return declaredDefaultAccessType;
	}
	
	private List< String > getExtendedTaxonomies( String taxonomyName )
	{
		TaxonomyCompiled taxonomyCompiled = getKnownTaxonomiesCompiled().get( taxonomyName );
		Taxonomy taxonomy = getTaxonomyLoader().loadTaxonomy( taxonomyName );
		List< String > extendedTaxonomies;
		
		if ( taxonomyCompiled == null && taxonomy == null )
			throw new IllegalStateException( "Couldn't locate taxonomy " + taxonomyName + "." );

		if ( taxonomyCompiled != null )
		{
			extendedTaxonomies = taxonomyCompiled.getExtendedTaxonomies();
		}
		else if ( taxonomy != null )
		{
			extendedTaxonomies = new ArrayList< String >();
			
			for ( Taxonomy extendedTaxonomy : taxonomy.getExtendedTaxonomies() )
				extendedTaxonomies.add( extendedTaxonomy.getName() );
		}
		else
		{
			throw new IllegalStateException( "Couldn't locate taxonomy " + taxonomyName + "." );
		}
		
		return extendedTaxonomies;
	}
	
	// TODO: use a list instead of a set to preserve the order of members
	private List< String > getMatchingMembers( ViewCompiled viewCompiled, AccessType effectiveAccessType, ParserRuleContext memberSelectorContext )
	{
		IdentifierContext identifierContext = getRuleContextRecursive( memberSelectorContext, IdentifierContext.class );
		WildcardIdentifierContext wildcardIdentifierContext = getRuleContextRecursive( memberSelectorContext, WildcardIdentifierContext.class );
		String memberSelector = ( identifierContext != null ? identifierContext : wildcardIdentifierContext ).getText().toLowerCase();
		String memberSelectorRegEx = memberSelector.replace( "*", "[a-zA-Z0-9_$]*?" );
		Class< ? > viewClass = getClass( viewCompiled.getName() );
		Map< String, List< Member > > members = new HashMap< String, List< Member > >();
		List< String > memberNames = new ArrayList< String >();
		 
		if ( effectiveAccessType.equals( AccessType.FIELD ) )
			getFieldNames( viewClass, members, memberNames );
		else if ( effectiveAccessType.equals( AccessType.PROPERTY ) )
			getPropertyNames( viewClass, PropertyType.DYNAMIC, members, memberNames );
		else
			throw new IllegalStateException( "Unexpected value for effectiveAccessType: " + effectiveAccessType  );
		
		if ( viewCompiled.getUsedClass() != null )
		{
			Class< ? > usedClass = getClass( viewCompiled.getUsedClass() );
			getPropertyNames( usedClass, PropertyType.DYNAMIC, members, memberNames );
		}

		List< String > matchingMembers = new ArrayList< String >();

		for ( String memberName : memberNames )
		{
			if ( memberName.toLowerCase().matches( memberSelectorRegEx ) )
			{
				List< Member > specificMembers = members.get( memberName );
				validateSpecificMembersNotAmbiguous( specificMembers, memberSelectorContext );
				matchingMembers.add( memberName );
			}
		}
		
		validateMemberSelectorMatchesAtLeastOne( identifierContext, matchingMembers, viewCompiled );
		
		return matchingMembers;
	}

	private void validateMemberSelectorMatchesAtLeastOne( IdentifierContext identifierContext, List< String > matchingMembers, ViewCompiled viewCompiled )
	{
		if ( identifierContext != null && matchingMembers.isEmpty() )
			reportError( identifierContext.start, identifierContext.stop, "Member selector doesn't match any member in class " + viewCompiled.getName() );
	}
	
	private void validateSpecificMembersNotAmbiguous( List< Member > specificMembers, ParserRuleContext memberSelectorContext )
	{
		Set< String > specificMemberClasses = new HashSet< String >();
		
		for ( Member specificMember : specificMembers )
			specificMemberClasses.add( specificMember.getDeclaringClass().getName() );
		
		if ( specificMemberClasses.size() > 1 )
			reportError( memberSelectorContext.start, memberSelectorContext.stop, "Member selector is ambigous; could refer to classes: " + String.join( ", ", specificMemberClasses ) );
		else if ( specificMemberClasses.size() == 0 )
			throw new IllegalStateException( "Unexpected value for specificMemberClasses.size(): " + specificMemberClasses.size() );
	}

	private void getFieldNames( Class< ? > viewClass, Map< String, List< Member > > members, List< String > memberNames )
	{
		for ( Field declaredField : viewClass.getDeclaredFields() )
			addMember( members, declaredField, declaredField.getName(), memberNames );
	}
	
	private void addMember( Map< String, List< Member > > members, Member member, String memberName, List< String > memberNames )
	{
		List< Member > specificMembers = members.get( memberName );
		
		if ( specificMembers == null )
		{
			specificMembers = new ArrayList< Member >();
			members.put( memberName, specificMembers );
			memberNames.add( memberName );
		}
		
		specificMembers.add( member );
	}
	
	public enum PropertyType
	{
		STATIC,
		DYNAMIC
	}
	
	private void getPropertyNames( Class< ? > viewClass, PropertyType propertyType, Map< String, List< Member > > members, List< String > memberNames )
	{
		for ( Method declaredMethod : viewClass.getDeclaredMethods() )
		{
			if ( ( Modifier.isStatic( declaredMethod.getModifiers() ) && propertyType.equals( PropertyType.DYNAMIC ) ) ||
					( !Modifier.isStatic( declaredMethod.getModifiers() ) && propertyType.equals( PropertyType.STATIC ) ) )
				continue;
			
			String propertyName = getPropertyName( declaredMethod );
			
			if ( propertyName != null )
			{
				addMember( members, declaredMethod, propertyName, memberNames );
				validatePropertySignature( declaredMethod );
			}
		}
	}
	
	// TODO Put this and any other property helper methods in a 
	// separate class.
	public static String getPropertyName( Method declaredMethod )
	{
		String declaredMethodName = declaredMethod.getName();
		String propertyName = null;
		
		if ( declaredMethodName.startsWith( "get" ) )
			propertyName = declaredMethodName.substring( "get".length() );
		else if ( declaredMethodName.startsWith( "set" ) )
			propertyName = declaredMethodName.substring( "set".length() );
		else if ( declaredMethodName.startsWith( "is" ) )
			propertyName = declaredMethodName.substring( "is".length() );
		
		String propertyNameAdjusted = null;
		
		if ( propertyName == null || propertyName.length() == 0 )
			propertyNameAdjusted = propertyName;
		else if ( propertyName.length() == 1 )
			propertyNameAdjusted = propertyName.toLowerCase();
		else
			propertyNameAdjusted = propertyName.substring( 0, 1 ).toLowerCase() + propertyName.substring( 1 );
		
		return propertyNameAdjusted;
	}
	
	private void validatePropertySignature( Method declaredMethod )
	{
		// TODO: implement
	}
	
	private List< MemberCompiled > getMembersCompiled( ViewCompiled currentViewCompiled, List< String > matchingMembers )
	{
		List< MemberCompiled > membersCompiled = new ArrayList< MemberCompiled >();
		
		for ( String matchingMember : matchingMembers )
			membersCompiled.add( new MemberCompiled( matchingMember, currentViewCompiled ) );
		
		return membersCompiled;
	}

	@Override
	public void enterAliasableMemberSelector( AliasableMemberSelectorContext aliasableMemberSelectorContext )
	{
		MemberSelectorContext memberSelectorContext = (MemberSelectorContext)aliasableMemberSelectorContext.getChild( 0 );
		AliasContext aliasContext = (AliasContext)aliasableMemberSelectorContext.getChild( 2 );
		String alias = aliasContext.getText();
		String memberName = memberSelectorContext.getText();
		String fqAlias = ( getPackageName().equals( DEFAULT_PACKAGE_NAME ) ? alias : getPackageName() + "." + alias );
		validateAliasNameNotInConflict( aliasContext, fqAlias );
		
		for ( List< MemberCompiled > currentMembersCompiledOfView : currentMembersCompiled.values() )
		{
			for ( MemberCompiled currentMemberCompiled : currentMembersCompiledOfView )
			{
				if ( currentMemberCompiled.getName().toLowerCase().equals( memberName.toLowerCase() ) )
				{
					currentMemberCompiled.setAlias( alias );
					break;
				}
			}
		}
	}

	@Override
	public void enterIncludeMemberModifier( IncludeMemberModifierContext includeMemberModifierContext )
	{
		currentMemberSelectionType = SelectionType.INCLUDE;
	}

	@Override
	public void enterExcludeMemberModifier( ExcludeMemberModifierContext excludeMemberModifierContext )
	{
		currentMemberSelectionType = SelectionType.EXCLUDE;
	}
	
	@Override
	public void enterMemberSelectorExpression( MemberSelectorExpressionContext memberSelectorExpressionContext )
	{
		AccessType effectiveAccessType = getEffectiveAccessType();
		Set< Class< ? > > selectableClasses = getCompilationUnit().getParentCompilationJob().getAllClassesInClassPath2();
		TaxonomyLoader loader = getTaxonomyLoader();
		ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
		ExpressionContext expressionContext = (ExpressionContext)memberSelectorExpressionContext.getChild( 0 );

		for ( ViewCompiled currentViewCompiled : currentViewsCompiled )
		{
			Class< ? > currentClass = getClass( currentViewCompiled.getName() );
			Class< ? > currentAuxiliaryClass = ( currentViewCompiled.getUsedClass() == null ? null : getClass( currentViewCompiled.getUsedClass() ) );
			List< String > matchingMembers = new ArrayList< String >();
			
			if ( effectiveAccessType.equals( AccessType.FIELD ) )
			{
				Set< Field > selectableFields = getSelectableFields( currentClass );

				if ( currentAuxiliaryClass != null)
					selectableFields.addAll( getSelectableFields( currentAuxiliaryClass ) );
				
				for ( Field currentField : selectableFields )
				{
					FieldSelectorContext fieldSelectorContext = new FieldSelectorContext( loader, selectableClasses, currentClass, currentAuxiliaryClass, selectableFields, currentField );
					Pass2SelectorListener listener = new Pass2SelectorListener( getCompilationUnit(), fieldSelectorContext, expressionContext );
					parseTreeWalker.walk( listener, memberSelectorExpressionContext );
					boolean fieldMatches = listener.getExpressionValue();
					
					if ( fieldMatches )
						matchingMembers.add( currentField.getName() );
				}
			}
			else if ( effectiveAccessType.equals( AccessType.PROPERTY ) )
			{
				Set< PropertyDescriptor > selectableProperties = getSelectableProperties( currentClass );
				
				if ( currentAuxiliaryClass != null)
					selectableProperties.addAll( getSelectableProperties( currentAuxiliaryClass ) );
				
				for ( PropertyDescriptor currentProperty : selectableProperties )
				{
					PropertySelectorContext propertySelectorContext = new PropertySelectorContext( loader, selectableClasses, currentClass, currentAuxiliaryClass, selectableProperties, currentProperty );
					Pass2SelectorListener listener = new Pass2SelectorListener( getCompilationUnit(), propertySelectorContext, expressionContext );
					parseTreeWalker.walk( listener, memberSelectorExpressionContext );
					boolean propertyMatches = listener.getExpressionValue();
					
					if ( propertyMatches )
						matchingMembers.add( currentProperty.getName() );
				}
			}
			else
			{
				throw new IllegalStateException( "Unexpected value for effectiveAccessType." );
			}
			
			List< MemberCompiled > membersCompiled = getMembersCompiled( currentViewCompiled, matchingMembers );
			addMembersCompiled( currentViewCompiled.getMembersCompiled(), membersCompiled );
			addMembersCompiled( currentMembersCompiled.get( currentViewCompiled ), membersCompiled );
		}
	}
	
	private Set< Field > getSelectableFields( Class< ? > aClass )
	{
		Set< Field > selectableFields = new HashSet< Field >( Arrays.asList(  aClass.getDeclaredFields() ) );
		selectableFields.stream().forEach( x -> x.setAccessible( true ) );
		
		return selectableFields;
	}
	
	private static Set< PropertyDescriptor > getSelectableProperties( Class< ? > aClass )
	{
		Set< PropertyDescriptor > selectableProperties = new HashSet< PropertyDescriptor >( Arrays.asList( PropertyUtils.getPropertyDescriptors( aClass ) ) );
		selectableProperties = selectableProperties.stream()
			.filter( x -> ( x.getReadMethod() != null ) && x.getReadMethod().getDeclaringClass().equals( aClass ) )
			.collect( Collectors.toSet() );
		
		return selectableProperties;
	}
	
	///////
	// MISC
	///////

	@Override
	public void enterAnnotation( AnnotationContext annotationContext )
	{
		AnnotationClassContext annotationClassContext = (AnnotationClassContext)annotationContext.getChild( 1 );
		Set< String > matchingClasses = getMatchingClasses( annotationClassContext );
		validateAnnotationClass( annotationClassContext, matchingClasses );
		
		String atSymbol = annotationContext.getChild( 0 ).getText();
		String fqAnnotationName = matchingClasses.iterator().next();
		String annotationBody = ( annotationContext.getChildCount() == 3 ? annotationContext.getChild( 2 ).getText() : "" );
		
		currentAnnotationUnparsed = atSymbol + fqAnnotationName + annotationBody;
	}

	private void validateAnnotationClass( AnnotationClassContext annotationClassContext, Set< String > matchingClasses )
	{
		if ( matchingClasses.size() == 0 )
		{
			reportError( annotationClassContext.start, annotationClassContext.stop, "Could not find referenced annotation (Did you misspell? Or forget an import?)." );
		}
		else if ( matchingClasses.size() > 1 )
		{
			throw new IllegalStateException();
		}
	}
	
	@Override
	public void enterAccessMethodModifier( AccessMethodModifierContext accessMethodModifierContext )
	{
		if ( accessMethodModifierContext.getChildCount() == 0 )
			currentAccessType = null;
		else if ( accessMethodModifierContext.getChildCount() == 1 )
			currentAccessType = AccessType.valueOf( accessMethodModifierContext.getChild( 0 ).getText().toUpperCase() );
		else
			throw new IllegalStateException( "Unexpected value for accessMethodModifierContext.getChildCount()." );
	}
}
