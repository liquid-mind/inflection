package ch.zhaw.inflection.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ch.zhaw.inflection.ClassViewNotFoundException;
import ch.zhaw.inflection.DelegatingInflectionResourceLoader;
import ch.zhaw.inflection.InflectionResourceLoader;
import ch.zhaw.inflection.compiler.ClassViewCompiled.MemberViewCompiled;
import ch.zhaw.inflection.compiler.VmapCompiled.MappingCompiled;
import ch.zhaw.inflection.grammar.InflectionParser.APackageContext;
import ch.zhaw.inflection.grammar.InflectionParser.AggregationModifierContext;
import ch.zhaw.inflection.grammar.InflectionParser.ClassViewDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.DefaultAggregationModifierContext;
import ch.zhaw.inflection.grammar.InflectionParser.DefaultMappingDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.DefaultMemberTypeModifierContext;
import ch.zhaw.inflection.grammar.InflectionParser.DefaultSuperDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.GroupedClassViewContext;
import ch.zhaw.inflection.grammar.InflectionParser.HgroupDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.IdentifierContext;
import ch.zhaw.inflection.grammar.InflectionParser.ImportPackageSymbolContext;
import ch.zhaw.inflection.grammar.InflectionParser.ImportTypeSymbolContext;
import ch.zhaw.inflection.grammar.InflectionParser.MappedVisitorContext;
import ch.zhaw.inflection.grammar.InflectionParser.MappingClassViewContext;
import ch.zhaw.inflection.grammar.InflectionParser.MappingDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.MappingMemberViewContext;
import ch.zhaw.inflection.grammar.InflectionParser.MemberTypeModifierContext;
import ch.zhaw.inflection.grammar.InflectionParser.MemberViewContext;
import ch.zhaw.inflection.grammar.InflectionParser.MemberViewDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.MemberViewNameContext;
import ch.zhaw.inflection.grammar.InflectionParser.NoSuperHgroupDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.SimpleTypeContext;
import ch.zhaw.inflection.grammar.InflectionParser.SuperDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.SuperHgroupDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.SuperVmapDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.TypeContext;
import ch.zhaw.inflection.grammar.InflectionParser.ViewofDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.VmapDeclarationContext;
import ch.zhaw.inflection.model.Aggregation;
import ch.zhaw.inflection.model.InflectionResource;
import ch.zhaw.inflection.model.VMap;


/*
 * TODO Add checks for the following rules:
 * -View inheritance hierarchy must correspond to the class inheritance hierarchy.
 * -Cycles in inheritance hierarchy are not allowed.
 * -Add support for inner classes (relevant for visitors, etc. ...)
 */
public class CompilePass2Listener extends AbstractInflectionListener
{
	public static final String OBJECT_VIEW = CH_ZHAW_INFLECTION_PACKAGE + ".ObjectView";
	public static final String HGROUP = CH_ZHAW_INFLECTION_PACKAGE + ".HGroup";
	public static final String PROPERTY = "property";
	public static final String FIELD = "field";
	public static final String DISCRETE = "discrete";
	public static final String COMPOSITE = "composite";
	public static final String DEFAULT_VISITOR = "__default_visitor";

	public static final String MEMBER_TYPE_DEFAULT = PROPERTY;
	public static final String AGGREGATION_DEFAULT = COMPOSITE;

	private static final Map< String, MemberViewCompiled.Type > MEMBER_TYPE_MAP = new HashMap< String, MemberViewCompiled.Type >();
	private static final Map< String, Aggregation > AGGREGATION_MAP = new HashMap< String, Aggregation >();
	
	static
	{
		MEMBER_TYPE_MAP.put( PROPERTY, MemberViewCompiled.Type.Property );
		MEMBER_TYPE_MAP.put( FIELD, MemberViewCompiled.Type.Field );
		
		AGGREGATION_MAP.put( DISCRETE, Aggregation.None );
		AGGREGATION_MAP.put( COMPOSITE, Aggregation.Composite );
	}
	
	private InflectionResourceLoader inflectionResourceLoader;
	private List< String > importedPackages;
	private Map< String, String > importedTypes;
	private ClassViewCompiled currentClassViewCompiled;
	private MemberViewCompiled currentMemberViewCompiled;
	private VmapCompiled currentVmapCompiled;
	private List< String > currentMappingInflectionViews;
	private List< String > currentMappedVisitors;
	private HgroupCompiled currentHgroupCompiled;
	
	public CompilePass2Listener( File compilationUnit, CommonTokenStream commonTokenStream, String packageName, Map< String, InflectionResourceCompiled > inflectionResourcesCompiled, InflectionResourceLoader inflectionResourceLoader, boolean bootstrap )
	{
		super( compilationUnit, commonTokenStream, packageName, inflectionResourcesCompiled, bootstrap );
		this.inflectionResourceLoader = inflectionResourceLoader;
		importedPackages = new ArrayList< String >();
		importedTypes = new HashMap< String, String >();
		currentMappingInflectionViews = new ArrayList< String >();
		currentMappedVisitors = new ArrayList< String >();
		
		// Note that the order of precedence from highest to lowest is:
		// -the declared package
		// -JAVA_LANG_PACKAGE
		// -CH_ZHAW_INFLECTION_PACKAGE
		// The precedence is guaranteed by the ordered List.
		addImportedPackage( null, packageName );
		addImportedPackage( null, JAVA_LANG_PACKAGE );

		if ( !bootstrap || !packageName.equals( CH_ZHAW_INFLECTION_PACKAGE ) )
			addImportedPackage( null, CH_ZHAW_INFLECTION_PACKAGE );
	}
	
	protected void addImportedPackage( APackageContext aPackageContext, String packageName )
	{
		if ( importedPackages.contains( packageName ) )
			ClassViewErrorListener.displayWarning( getCompilationUnit(), getCommonTokenStream(), aPackageContext.start, aPackageContext.stop, "Package already imported: ignoring duplicate." );
		else
			importedPackages.add( packageName );
	}
	
	@Override
	public void enterImportPackageSymbol( ImportPackageSymbolContext importPackageSymbolContext )
	{
		APackageContext aPackageContext = (APackageContext)importPackageSymbolContext.getChild( 0 );
		String packageName = getPackageName( aPackageContext );
		addImportedPackage( aPackageContext, packageName );
	}

	@Override
	public void enterImportTypeSymbol( ImportTypeSymbolContext importTypeSymbolContext )
	{
		TypeContext typeContext = (TypeContext)importTypeSymbolContext.getChild( 0 );
		String typeName = getFQTypeName( typeContext );
		
		if ( !typeExists( typeName ) )
			ClassViewErrorListener.displayWarning( getCompilationUnit(), getCommonTokenStream(), typeContext.start, typeContext.stop, "Unknown symbol: ignoring import." );
		else
			addImportedType( typeContext, getSimpleTypeName( typeName ), typeName );
	}
	
	private String getSimpleTypeName( String typeName )
	{
		int lastIndex = typeName.lastIndexOf( "." );
		String simpleTypeName = ( lastIndex == -1 ? typeName : typeName.substring( lastIndex + 1 ) );
		
		return simpleTypeName;
	}
	
	private boolean typeExists( String typeName )
	{
		boolean typeExists = false;
		
		if ( getInflectionResourcesCompiled().get( typeName ) != null ||
			inflectionResourceLoader.loadClassView( typeName ) != null ||
			inflectionResourceLoader.loadVmap( typeName ) != null ||
			loadClass( typeName ) != null )
			typeExists = true;

		return typeExists;
	}
	
	private Class< ? > loadClass( String className )
	{
		Class< ? > theClass = null;
		
		try
		{
			theClass = inflectionResourceLoader.findClass( className );
		}
		catch ( ClassNotFoundException e )
		{
		}
		
		return theClass;
	}

	private String getFQTypeName( TypeContext typeContext )
	{
		String typeName;
		ParseTree firstChild = typeContext.getChild( 0 );
		
		if ( firstChild instanceof APackageContext )
		{
			typeName = getPackageName( (APackageContext)firstChild );
			typeName += typeContext.getChild( 1 ).toString() + typeContext.getChild( 2 ).getChild( 0 ).getChild( 0 ).toString();
		}
		else if ( firstChild instanceof SimpleTypeContext )
		{
			typeName = typeContext.getChild( 0 ).getChild( 0 ).getChild( 0 ).toString();
		}
		else
		{
			throw new IllegalStateException( "Unexpected type for firstChild: " + firstChild.getClass().getName() );
		}
		
		return typeName;
	}
	
	private void addImportedType( TypeContext typeContext, String simpleTypeName, String fqTypeName )
	{
		if ( importedTypes.keySet().contains( simpleTypeName ) )
			ClassViewErrorListener.displayWarning( getCompilationUnit(), getCommonTokenStream(), typeContext.start, typeContext.stop, "Type already imported: ignoring duplicate." );
		else
			importedTypes.put( simpleTypeName, fqTypeName );
	}	
	
	// CLASS VIEW
	
	@Override
	public void enterClassViewDeclaration( ClassViewDeclarationContext classViewDeclarationContext )
	{
		IdentifierContext identifierContext = (IdentifierContext)classViewDeclarationContext.getChild( 1 );
		String classViewName = getIdentifierFQName( identifierContext );
		currentClassViewCompiled = (ClassViewCompiled)getInflectionResourcesCompiled().get( classViewName );
	}
	
	@Override
	public void enterViewofDeclaration( ViewofDeclarationContext viewofDeclarationContext )
	{
		TypeContext typeContext = (TypeContext)viewofDeclarationContext.getChild( 1 ).getChild( 0 );
		Class< ? > javaClass = getClass( typeContext );
		currentClassViewCompiled.setJavaClassName( javaClass.getName() );
	}

	@Override
	public void enterDefaultSuperDeclaration( DefaultSuperDeclarationContext defaultSuperDeclarationContext )
	{
		if ( !currentClassViewCompiled.getName().equals( OBJECT_VIEW ) )
			currentClassViewCompiled.setExtendedClassViewName( OBJECT_VIEW );
	}

	@Override
	public void enterSuperDeclaration( SuperDeclarationContext superDeclarationContext )
	{
		TypeContext typeContext = (TypeContext)superDeclarationContext.getChild( 1 ).getChild( 0 );
		String extendedClassViewName = getInflectionResourceName( typeContext );
		currentClassViewCompiled.setExtendedClassViewName( extendedClassViewName );
	}
	
	@Override
	public void enterMemberViewDeclaration( MemberViewDeclarationContext memberViewDeclarationContext )
	{
		currentMemberViewCompiled = new MemberViewCompiled( currentClassViewCompiled );
	}

	@Override
	public void exitMemberViewDeclaration( MemberViewDeclarationContext memberViewDeclarationContext )
	{
		currentClassViewCompiled.getMemberViews().add( currentMemberViewCompiled );
	}
	
	@Override
	public void enterDefaultMemberTypeModifier( DefaultMemberTypeModifierContext defaultMemberTypeModifierContext )
	{
		currentMemberViewCompiled.setType( MEMBER_TYPE_MAP.get( MEMBER_TYPE_DEFAULT ) );
	}

	@Override
	public void enterMemberTypeModifier( MemberTypeModifierContext memberTypeModifierContext )
	{
		currentMemberViewCompiled.setType( MEMBER_TYPE_MAP.get( memberTypeModifierContext.getChild( 0 ).getText() ) );
	}
	
	@Override
	public void enterDefaultAggregationModifier( DefaultAggregationModifierContext defaultAggregationModifierContext )
	{
		currentMemberViewCompiled.setAggregation( AGGREGATION_MAP.get( AGGREGATION_DEFAULT ) );
	}
	
	@Override
	public void enterAggregationModifier( AggregationModifierContext aggregationModifierContext )
	{
		currentMemberViewCompiled.setAggregation( AGGREGATION_MAP.get( aggregationModifierContext.getChild( 0 ).getText() ) );
	}

	@Override
	public void enterMemberView( MemberViewContext memberViewContext )
	{
		TypeContext typeContext = (TypeContext)memberViewContext.getChild( 0 ).getChild( 0 );
		String classViewName = getInflectionResourceName( typeContext );
		currentMemberViewCompiled.setClassViewName( classViewName );
	}

	@Override
	public void enterMemberViewName( MemberViewNameContext memberViewNameContext )
	{
		IdentifierContext identifierContext = (IdentifierContext)memberViewNameContext.getChild( 0 );
		String memberViewName = identifierContext.getChild( 0 ).getText();
		currentMemberViewCompiled.setName( memberViewName );
	}

	// VMAP
	
	@Override
	public void enterVmapDeclaration( VmapDeclarationContext vmapDeclarationContext )
	{
		IdentifierContext identifierContext = (IdentifierContext)vmapDeclarationContext.getChild( 1 );
		String vmapName = getIdentifierFQName( identifierContext );
		currentVmapCompiled = (VmapCompiled)getInflectionResourcesCompiled().get( vmapName );
	}

	@Override
	public void enterSuperVmapDeclaration( SuperVmapDeclarationContext superVmapDeclarationContext )
	{
		TypeContext typeContext = (TypeContext)superVmapDeclarationContext.getChild( 1 ).getChild( 0 );
		String extendedVMapName = getInflectionResourceName( typeContext );
		currentVmapCompiled.setExtendedVmapName( extendedVMapName );
	}

	@Override
	public void exitMappingDeclaration( MappingDeclarationContext mappingDeclarationContext )
	{
		if ( currentMappingInflectionViews.contains( DEFAULT_VISITOR ) )
		{
			currentVmapCompiled.setDefaultVisitorClassName( currentMappedVisitors.get( 0 ) );
		}
		else
		{
			for ( String mappingInflectionView : currentMappingInflectionViews )
			{
				for ( String currentMappedVisitor : currentMappedVisitors )
				{
					MappingCompiled mappingCompiled = new MappingCompiled( currentVmapCompiled );
					mappingCompiled.setInflectionViewName( mappingInflectionView );
					mappingCompiled.setVisitorClassName( currentMappedVisitor );
					currentVmapCompiled.getClassViewToVisitorMappings().add( mappingCompiled );
				}
			}
		}
		
		currentMappingInflectionViews.clear();
		currentMappedVisitors.clear();
	}
	
	@Override
	public void enterDefaultMappingDeclaration( DefaultMappingDeclarationContext defaultMappingDeclarationContext )
	{
		currentMappingInflectionViews.add( DEFAULT_VISITOR );
	}

	@Override
	public void enterMappingClassView( MappingClassViewContext mappingClassViewContext )
	{
		TypeContext typeContext = (TypeContext)mappingClassViewContext.getChild( 0 ).getChild( 0 );
		String mappingClassView = getInflectionResourceName( typeContext );
		currentMappingInflectionViews.add( mappingClassView );
	}

	@Override
	public void enterMappingMemberView( MappingMemberViewContext mappingMemberViewContext )
	{
		TypeContext typeContext = (TypeContext)mappingMemberViewContext.getChild( 0 ).getChild( 0 );
		String owningClassView = getInflectionResourceName( typeContext );
		IdentifierContext identifierContext = (IdentifierContext)mappingMemberViewContext.getChild( 2 );
		String memberView = identifierContext.getText();
		currentMappingInflectionViews.add( owningClassView + "->" + memberView );
	}

	@Override
	public void enterMappedVisitor( MappedVisitorContext mappedVisitorContext )
	{
		TypeContext typeContext = (TypeContext)mappedVisitorContext.getChild( 0 ).getChild( 0 );
		Class< ? > visitorClass = getClass( typeContext );
		currentMappedVisitors.add( visitorClass.getName() );
	}
	
	// HGROUP
	
	@Override
	public void enterHgroupDeclaration( HgroupDeclarationContext hgroupDeclarationContext )
	{
		IdentifierContext identifierContext = (IdentifierContext)hgroupDeclarationContext.getChild( 1 );
		String hgroupName = getIdentifierFQName( identifierContext );
		currentHgroupCompiled = (HgroupCompiled)getInflectionResourcesCompiled().get( hgroupName );
	}

	@Override
	public void enterSuperHgroupDeclaration( SuperHgroupDeclarationContext superHgroupDeclarationContext )
	{
		TypeContext typeContext = (TypeContext)superHgroupDeclarationContext.getChild( 1 ).getChild( 0 );
		String extendedHgroupName = getInflectionResourceName( typeContext );
		currentHgroupCompiled.setExtendedHgroupName( extendedHgroupName );
	}
	
	@Override
	public void enterNoSuperHgroupDeclaration( NoSuperHgroupDeclarationContext ctx )
	{
		if ( !currentHgroupCompiled.getName().equals( HGROUP ) )
			currentHgroupCompiled.setExtendedHgroupName( HGROUP );
	}

	@Override
	public void enterGroupedClassView( GroupedClassViewContext groupedClassViewContext )
	{
		TypeContext typeContext = (TypeContext)groupedClassViewContext.getChild( 0 ).getChild( 0 );
		String groupedClassViewName = getInflectionResourceName( typeContext );
		currentHgroupCompiled.getClassViewNames().add( groupedClassViewName );
	}

	// SUPPORT
	private String getInflectionResourceName( TypeContext typeContext )
	{
		String iresName;
		Object iresCompiledOrLinked = getInflectionResourceCompiledOrLinked( typeContext );
		
		if ( iresCompiledOrLinked instanceof InflectionResourceCompiled )
			iresName = ((InflectionResourceCompiled)iresCompiledOrLinked).getName();
		else if ( iresCompiledOrLinked instanceof InflectionResource )
			iresName = ((InflectionResource)iresCompiledOrLinked).getName();
		else
			throw new IllegalStateException( "Unexpected type for iresCompiledOrLinked: " + iresCompiledOrLinked.getClass().getName() );
		
		return iresName;
	}
	
	protected Class< ? > getClass( TypeContext typeContext )
	{
		ResolverCallback< Class< ? > > callback = new ResolverCallback< Class< ? > >() {
			@Override
			public Class< ? > getType( String typeName )
			{
				Class< ? > type = DelegatingInflectionResourceLoader.BASIC_TYPE_MAP.get( typeName );
				
				if ( type == null )
					type = loadClass( typeName );
				
				return type;
			}
		};
		
		return resolveType( typeContext, callback );
	}
	
	protected Object getInflectionResourceCompiledOrLinked( TypeContext typeContext )
	{
		ResolverCallback< Object > callback = new ResolverCallback< Object >() {
			@Override
			public Object getType( String typeName )
			{
				Object type = getInflectionResourcesCompiled().get( typeName );
				
				if ( type == null )
					type = loadInflectionResource( typeName );
				
				if ( type == null )
					type = loadVmap( typeName );
				
				return type;
			}
		};
		
		return resolveType( typeContext, callback );
	}
	
	private InflectionResource loadInflectionResource( String inflectionResourceName )
	{
		InflectionResource inflectionResource = null;
		
		try
		{
			inflectionResource = inflectionResourceLoader.loadInflectionResource( inflectionResourceName );
		}
		catch ( ClassViewNotFoundException e )
		{
		}
		
		return inflectionResource;
	}
	
	private VMap loadVmap( String vmapName )
	{
		VMap vmap = null;
		
		try
		{
			vmap = inflectionResourceLoader.loadVmap( vmapName );
		}
		catch ( ClassViewNotFoundException e )
		{
		}
		
		return vmap;
	}
	
	private static interface ResolverCallback< T >
	{
		public T getType( String typeName );
	}
	
	@SuppressWarnings( "unchecked" )
	private < T > T resolveType( TypeContext typeContext, ResolverCallback< T > callback )
	{
		T resolvedType;
		
		// If the ClassViewContext defines a fully-qualified name --> use that.
		if ( typeContext.getChild( 0 ) instanceof APackageContext )
		{
			String fqTypeName = getFQTypeName( typeContext );
			resolvedType = callback.getType( fqTypeName );
		}
		else
		{
			// Otherwise, try looking in the default package
			String simpleTypeName = getFQTypeName( typeContext );
			resolvedType = callback.getType( simpleTypeName );

			// Otherwise, try looking in the set of importedClassViews.
			if ( resolvedType == null )
				resolvedType = (T)importedTypes.get( simpleTypeName );
			
			// Otherwise, try looking in the set of importedPackages.
			if ( resolvedType == null )
			{
				for ( String importedPackage : importedPackages )
				{
					resolvedType = callback.getType( importedPackage + "." + simpleTypeName );
					
					if ( resolvedType != null )
						break;
				}	
			}
			
			// Otherwise, fail.
			if ( resolvedType == null )
			{
				ClassViewErrorListener.displayError( getCompilationUnit(), getCommonTokenStream(), typeContext.start, typeContext.stop, "Cannot resolve symbol (did you forget an import?)." );
				stopCompiling();
			}
		}

		return resolvedType;
	}	
}
