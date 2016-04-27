package ch.liquidmind.inflection.proxy;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import __java.io.__FileOutputStream;
import __java.io.__OutputStream;
import __java.lang.__Class;
import __java.lang.reflect.__Method;
import __org.apache.commons.io.__FileUtils;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Field;
import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Property;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.model.linked.NamedElementLinked;
import ch.liquidmind.inflection.model.linked.UnparsedAnnotation;
import ch.liquidmind.inflection.util.InflectionPrinter;

// TODO Proxies of abstract classes should be abstract themselves (since it doesn't
// make sense to be able to instantiate a view of an un-instantiable class).
// TODO Make ProxyGenerator work like InflectionCompiler, i.e., introduce a ProxyGeneratorJob, etc.
// TODO Make the taxonomy a class and proxies public static inner classes thereof. This has
// the advantage of being syntactically more intuitive (dot notation) and also references to the
// taxonomy can be refactored more easily.
@SuppressWarnings( "unchecked" )
public class ProxyGenerator
{
	private File baseDir;
	private Taxonomy taxonomy;
	private List< String > annotationNames;
	private PrintWriter printWriter;
	
	// TODO Introduce apache commons cli, analogous to deflector.
	public static void main( String[] args )
	{
		Map< String, List< String > > options = InflectionPrinter.parseOptions( args );
		
		String output = options.get( "-output" ).get( 0 );
		List< String > taxonomyNames = options.get( "-taxonomies" );
		List< String > annotationNames = options.get( "-annotations" );
		
		for ( String taxonomyName : taxonomyNames )
		{
			Taxonomy taxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( taxonomyName );
			new ProxyGenerator( new File( output ), taxonomy, annotationNames ).generateTaxonomy();
		}
	}
	
	public ProxyGenerator( File baseDir, Taxonomy taxonomy, List< String > annotationNames )
	{
		super();
		this.baseDir = baseDir;
		this.taxonomy = taxonomy;
		this.annotationNames = annotationNames;
	}

	public void generateTaxonomy()
	{
		if ( !baseDir.exists() )
			__FileUtils.forceMkdir( null, baseDir );
		
		generateProxyCollections();
		
		for ( View view : taxonomy.getViews() )
			generateView( view );
	}
	
	private void generateProxyCollections()
	{
		generateProxyCollection( ListProxy.class );
		generateProxyCollection( SetProxy.class );
		generateProxyCollection( MapProxy.class );
		generateProxyCollection( IteratorProxy.class );
	}
	
	private void generateProxyCollection( Class< ? > proxyCollection )
	{
		String fqCollectionName = getFullyQualifiedCollectionName( taxonomy, proxyCollection );
		String collectionFileName = fqCollectionName.replace( ".", "/" ) + ".java";
		File collectionFile = new File( baseDir, collectionFileName );
		
		if ( !collectionFile.getParentFile().exists() )
			__FileUtils.forceMkdir( null, collectionFile.getParentFile() );
		
		OutputStream outputStream = __FileOutputStream.__new( collectionFile );
		printWriter = new PrintWriter( outputStream );
		
		try
		{
			generateProxyCollection( proxyCollection, fqCollectionName );
		}
		finally
		{
			printWriter.close();
			__OutputStream.close( outputStream );
		}
	}
	
	private void generateProxyCollection( Class< ? > proxyCollection, String fqCollectionName )
	{
		int lastIndexOfDot = fqCollectionName.lastIndexOf( "." );
		String packageName = fqCollectionName.substring( 0, lastIndexOfDot );
		String simpleName = fqCollectionName.substring( lastIndexOfDot + 1 );
		String genericType = ( Map.class.isAssignableFrom( proxyCollection ) ? "< K, V >" : "< E >" );
		
		printWriter.println( "package " + packageName + ";" );
		printWriter.println();
		
		printWriter.println( "public class " + simpleName + genericType + " extends " + proxyCollection.getName() + genericType );
		printWriter.println( "{" );
		printWriter.println( "    public " + simpleName + "()");
		printWriter.println( "    {" );
		printWriter.println( "        super( \"" + taxonomy.getName() + "\" );" );
		printWriter.println( "    }" );
		printWriter.println( "}" );
	}
	
	private void generateView( View view )
	{
		String fqViewName = getFullyQualifiedViewName( taxonomy, view );
		String viewFileName = fqViewName.replace( ".", "/" ) + ".java";
		File viewFile = new File( baseDir, viewFileName );
		
		if ( !viewFile.getParentFile().exists() )
			__FileUtils.forceMkdir( null, viewFile.getParentFile() );
		
		OutputStream outputStream = __FileOutputStream.__new( viewFile );
		printWriter = new PrintWriter( outputStream );
		
		try
		{
			generateView( view, fqViewName );
		}
		finally
		{
			printWriter.close();
			__OutputStream.close( outputStream );
		}
	}
	
	// TODO: move this to common location (also used by ProxyRegistry).
	public static String getFullyQualifiedViewName( Taxonomy taxonomy, View view )
	{
		String fqViewName = taxonomy.getName() + "." + view.getPackageName() + "." + taxonomy.getSimpleName() + "_" + view.getSimpleNameOrAlias();
		
		return fqViewName;
	}
	
	public static String getFullyQualifiedCollectionName( Taxonomy taxonomy, Class< ? > collectionType )
	{
		String fqCollectionName = taxonomy.getName() + "." + collectionType.getPackage().getName() + "." + taxonomy.getSimpleName() + "_" + collectionType.getSimpleName();
		
		return fqCollectionName;
	}
	
	private void generateView( View view, String fqViewName )
	{
		printWriter.println( "package " + NamedElementLinked.getPackageName( fqViewName ) + ";" );
		printWriter.println();
		
		List< Annotation > viewAnnotations = view.getAnnotations();
		List< Annotation > classAnnotations = Arrays.asList( view.getViewedClass().getAnnotations() );
		
		printWriter.println( String.join( " ", getAllAnnotations( viewAnnotations, classAnnotations ) ) );
		printWriter.println( "public class " + NamedElementLinked.getSimpleName( fqViewName ) + " extends " + getSuperClassName( view ) );
		printWriter.println( "{" );
		
		generateConstructors( view, fqViewName );
		generateMembers( view.getDeclaredMembers() );
		
		printWriter.println( "}" );
		
	}
	
	private String getSuperClassName( View view )
	{
		String superClassName;
		View superView = taxonomy.getSuperview( view );
		
		if ( superView != null )
			superClassName = getFullyQualifiedViewName( taxonomy, superView );
		else
			superClassName = Proxy.class.getName();
		
		return superClassName;
	}

	private void generateConstructors( View view, String fqViewName  )
	{
		printWriter.println( "    public " + NamedElementLinked.getSimpleName( fqViewName ) + "()" );
		printWriter.println( "    {" );
		printWriter.println( "        super( \"" + taxonomy.getName() + "\", \"" + view.getName() + "\" );" );
		printWriter.println( "    }" );
		printWriter.println();
		
		printWriter.println( "    protected " + NamedElementLinked.getSimpleName( fqViewName ) + "( String taxonomyName, String viewName )" );
		printWriter.println( "    {" );
		printWriter.println( "        super( taxonomyName, viewName );" );
		printWriter.println( "    }" );
		printWriter.println();
	}
	
	private void generateMembers( List< Member > members )
	{
		for ( int i = 0 ; i < members.size() ; ++i )
		{
			Member member = members.get( i );
			String nameOrAlias = member.getNameOrAlias();
			String capName = nameOrAlias.substring( 0, 1 ).toUpperCase() + nameOrAlias.substring( 1 );
			String proxyGetMethodName = "get" + capName;
			String proxySetMethodName = "set" + capName;

			generateMember( proxyGetMethodName, proxySetMethodName, member );
			
			if ( i + 1 != members.size() )
				printWriter.println();
		}
	}

	private void generateMember( String proxyGetMethodName, String proxySetMethodName, Member member )
	{
		member.getAnnotations();
		
		if ( member instanceof Property )
			generateProperty( proxyGetMethodName, proxySetMethodName, (Property)member );
		else if ( member instanceof Field )
			generateField( proxyGetMethodName, proxySetMethodName, (Field)member );
	}
	
	private void generateProperty( String proxyGetMethodName, String proxySetMethodName, Property property )
	{
		generateProperty( proxyGetMethodName, property.getReadMethod(), property.getAnnotations() );
		printWriter.println();
		generateProperty( proxySetMethodName, property.getWriteMethod(), new ArrayList< Annotation >() );
	}
	
	private void generateProperty( String proxyMethodName, Method targetMethod, List< Annotation > viewAnnotations )
	{
		if ( targetMethod == null )
			return;
		
		List< Type > targetParamTypes = Arrays.asList( targetMethod.getGenericParameterTypes() );
		List< Type > proxyParamTypes = targetParamTypes;
		
		if ( Modifier.isStatic( targetMethod.getModifiers() ) )
			proxyParamTypes = new ArrayList< Type >( targetParamTypes.subList( 1, targetParamTypes.size() ) );
		
		Type[] targetParamTypesAsArray = targetParamTypes.toArray( new Type[ targetParamTypes.size() ] );
		Type[] proxyParamTypesAsArray = proxyParamTypes.toArray( new Type[ proxyParamTypes.size() ] );
		
		List< Annotation > classAnnotations = Arrays.asList( targetMethod.getAnnotations() );
		
		generateMethod( proxyMethodName, proxyParamTypesAsArray, viewAnnotations, classAnnotations, targetMethod.getName(), targetParamTypesAsArray, targetMethod.getGenericReturnType(), targetMethod.getExceptionTypes() );
	}
	
	private void generateField( String proxyGetMethodName, String proxySetMethodName, Field field )
	{
		String name = field.getName();
		String capName = name.substring( 0, 1 ).toUpperCase() + name.substring( 1 ); 
		List< Annotation > classAnnotations = Arrays.asList( field.getField().getAnnotations() );
		generateMethod( proxyGetMethodName, new Class< ? >[]{}, field.getAnnotations(), classAnnotations, "get" + capName, new Class< ? >[]{}, field.getField().getType(), new Class< ? >[]{} );
		generateMethod( proxySetMethodName, new Class< ? >[]{ field.getField().getType() }, new ArrayList< Annotation >(), new ArrayList< Annotation >(), "set" + capName, new Class< ? >[]{ field.getField().getType() }, void.class, new Class< ? >[]{} );
	}

	private void generateMethod( String proxyMethodName, Type[] proxyParamTypes, List< Annotation > viewAnnotations, List< Annotation > classAnnotations, String targetMethodName, Type[] targetParamTypes, Type retType, Class< ? >[] exTypes )
	{
		String flatAnnotations = String.join( " ", getAllAnnotations( viewAnnotations, classAnnotations ) );
		String retTypeName = getTypeName( retType );
		String parameters = String.join( ", ", getParameters( proxyParamTypes ) );
		String exceptions = String.join( ", ", getExceptions( exTypes ) );
		String paramsWithParens = ( parameters.isEmpty() ? "()" : "( " + parameters + " )" );
		String execptionsWithThrows = ( exceptions.isEmpty() ? "" : " throws " + exceptions );
		
		printWriter.println( "    " + flatAnnotations );
		printWriter.println( "    public " + retTypeName + " " + proxyMethodName + paramsWithParens + execptionsWithThrows );
		printWriter.println( "    {" );
		
		printWriter.println( "        try" );
		printWriter.println( "        {" );
		
		generateInvocation( targetMethodName, proxyParamTypes, targetParamTypes, retType, exTypes );
		
		printWriter.println( "        }" );
		
		// TODO: Need to do this in the same way as I did in deflector: the
		// order in which exceptions are caught matters!
		for ( Class< ? > exType : exTypes )
		{
			printWriter.println( "        catch ( " + exType.getName() + " e )" );
			printWriter.println( "        {" );
			printWriter.println( "            throw (" + exType.getName() + ")e;" );
			printWriter.println( "        }" );
		}

		printWriter.println( "        catch ( java.lang.RuntimeException e )" );
		printWriter.println( "        {" );
		printWriter.println( "            throw (java.lang.RuntimeException)e;" );
		printWriter.println( "        }" );
		printWriter.println( "        catch ( java.lang.Throwable e )" );
		printWriter.println( "        {" );
		printWriter.println( "            throw new java.lang.IllegalStateException( e );" );
		printWriter.println( "        }" );
		
		printWriter.println( "    }" );
	}
	
	private List< String > getAllAnnotations( List< Annotation > viewAnnotations, List< Annotation > classAnnotations )
	{
		List< String > allAnnotations = new ArrayList< String >();
		
		allAnnotations.addAll( getClassAnnotations( getMatchingClassAnnotations( classAnnotations ) ) );
		allAnnotations.addAll( getViewAnnotations( viewAnnotations ) );
		
		return allAnnotations;
	}
	
	private List< Annotation > getMatchingClassAnnotations( List< Annotation > classAnnotations )
	{
		return classAnnotations.stream().filter(
			a -> annotationNames.stream().anyMatch(
				an -> a.getClass().getInterfaces()[ 0 ].getName().matches( an ) )
		).collect( Collectors.toList() );
	}
	
	private List< String > getClassAnnotations( List< Annotation > classAnnotations )
	{
		return classAnnotations.stream().map( a -> getAnnotationLiteral( a ) ).collect( Collectors.toList() );
	}

	private List< String > getViewAnnotations( List< Annotation > viewAnnotations )
	{
		return viewAnnotations.stream().map( a -> ((UnparsedAnnotation)a).value() ).collect( Collectors.toList() );
	}
	
	private void generateInvocation( String targetMethodName, Type[] proxyParamTypes, Type[] targetParamTypes, Type retType, Class< ? >[] exTypes )
	{
		String parameterClasses = String.join( ", ", getParameterTypes( targetParamTypes ) );
		String parameterClassesWithClassArray = ( parameterClasses.isEmpty() ? "new Class< ? >[]{}" : "new Class< ? >[]{ " + parameterClasses + " }");
		String arguments = String.join( ", ", getArguments( proxyParamTypes ) );
		String argumentsWithObjectArray = ( parameterClasses.isEmpty() ? "new Object[]{}" : "new Object[]{ " + arguments + " }");
		String returnKeyword = ( retType != void.class ? "return " : "" );
		
		printWriter.println( "            " + returnKeyword + "invoke( \"" + targetMethodName + "\", " + parameterClassesWithClassArray + ", " + argumentsWithObjectArray + " );" );
	}
	
	private List< String > getParameters( Type[] paramTypes )
	{
		List< String > parameters = new ArrayList< String >();
		
		for ( int i = 0 ; i < paramTypes.length ; ++i )
			parameters.add( getTypeName( paramTypes[ i ] ) + " arg" + i );
			
		return parameters;
	}
	
	// TODO: For now, I am handling generic types in a highly
	// simplified way. In a second pass, we need to handle them
	// in the same fashion as deflector.
	private String getTypeName( Type type )
	{
		String typeName;
		
		if ( type instanceof Class )
		{
			Class< ? > aClass = (Class< ? >)type;
			
			if ( aClass.isArray() )
			{
				Class< ? > componentType = aClass.getComponentType();
				
				if ( componentType.isArray() )
					throw new IllegalStateException( "No support at this time for multi-dimensional arrays." );
					
				typeName = componentType.getName() + "[]";
			}
			else
			{
				View view = taxonomy.resolveView( aClass );
				
				if ( view == null )
					typeName = aClass.getName();
				else
					typeName = getFullyQualifiedViewName( taxonomy, view );
			}
		}
		else if ( type instanceof ParameterizedType )
		{
			ParameterizedType parameterizedType = (ParameterizedType)type;
			Type rawType = parameterizedType.getRawType();
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			String rawTypeConverted;
			List< String > actualTypeArgumentsConverted = new ArrayList< String >();
			Class< ? > proxyBaseClass = ProxyRegistry.getProxyBaseClass( (Class< ? >)rawType );
			
			if ( proxyBaseClass != null )
				rawTypeConverted = getFullyQualifiedCollectionName( taxonomy, proxyBaseClass );
			else
				throw new IllegalStateException( "No support for non-collection generic types at this time, type: " + type.getTypeName() );

			for ( Type actualTypeArgument : actualTypeArguments )
			{
				if ( actualTypeArgument instanceof Class )
					actualTypeArgumentsConverted.add( getTypeName( actualTypeArgument ) );
				else
					throw new IllegalStateException( "No support for general purpose generics at this time (only for collections), type: " + type.getTypeName() );
			}
			
			typeName = rawTypeConverted + "< " + String.join( ", ", actualTypeArgumentsConverted ) + " >";
		}
		else
		{
			throw new IllegalStateException( "Unexpected type for 'type': " + type.getClass().getName() );
		}
		
		return typeName;
	}
	
	private List< String > getExceptions( Class< ? >[] exTypes )
	{
		List< String > exceptions = new ArrayList< String >();
		
		for ( Class< ? > exType : exTypes )
			exceptions.add( exType.getName() );
		
		return exceptions;
	}

	private List< String > getParameterTypes( Type[] paramTypes )
	{
		List< String > parameters = new ArrayList< String >();
		
		for ( int i = 0 ; i < paramTypes.length ; ++i )
		{
			// TODO: The way I'm getting typeNameOfRawType is, frankly, a hack, but
			// as I've said, I plan on completely replacing the way generics are
			// handled with a deflector-like implementation in the near future.
			String typeName = getTypeName( paramTypes[ i ] );
			String typeNameOfRawType;
			
			if ( typeName.contains( "<" ) )
				typeNameOfRawType = typeName.substring( 0, typeName.indexOf( "<" ) );
			else
				typeNameOfRawType = typeName;
			
			parameters.add( typeNameOfRawType + ".class" );
		}
			
		return parameters;
	}

	private List< String > getArguments( Type[] paramTypes )
	{
		List< String > arguments = new ArrayList< String >();
		
		for ( int i = 0 ; i < paramTypes.length ; ++i )
			arguments.add( "arg" + i );
			
		return arguments;
	}
	
	private static final Set< Method > IGNOREABLE_METHODS = new HashSet< Method >();
	
	static
	{
		IGNOREABLE_METHODS.add( __Class.getDeclaredMethod( Annotation.class, "equals", new Class[] { Object.class } ) );
		IGNOREABLE_METHODS.add( __Class.getDeclaredMethod( Annotation.class, "hashCode", new Class[] {} ) );
		IGNOREABLE_METHODS.add( __Class.getDeclaredMethod( Annotation.class, "toString", new Class[] {} ) );
		IGNOREABLE_METHODS.add( __Class.getDeclaredMethod( Annotation.class, "annotationType", new Class[] {} ) );
	}
	
	private static String getAnnotationLiteral( Annotation annotation )
	{
		Class< ? > annotationInterface = annotation.getClass().getInterfaces()[ 0 ];
		
		String annotationString = "@" + annotationInterface.getName();
		annotationString += "( ";
		List< String > valueLiterals = new ArrayList< String >();
		
		for ( Method annotationMethod : annotationInterface.getMethods() )
		{
			if ( IGNOREABLE_METHODS.contains( annotationMethod ) )
				continue;
			
			Object value = __Method.invoke( annotationMethod, annotation, new Object[] {} );
			String valueLiteral = getValueLiteral( value );
			valueLiterals.add( annotationMethod.getName() + " = " + valueLiteral );
		}
		
		annotationString += String.join( ", ", valueLiterals );
		annotationString += " )";
		
		return annotationString;
	}
	
	private static String getValueLiteral( Object value )
	{
		String valueLiteral;
		
		if ( value instanceof Byte )
			valueLiteral = getByteLiteral( (Byte)value );
		else if ( value instanceof Short )
			valueLiteral = getShortLiteral( (Short)value );
		else if ( value instanceof Integer )
			valueLiteral = getIntegerLiteral( (Integer)value );
		else if ( value instanceof Long )
			valueLiteral = getLongLiteral( (Long)value );
		else if ( value instanceof Float )
			valueLiteral = getFloatLiteral( (Float)value );
		else if ( value instanceof Double )
			valueLiteral = getDoubleLiteral( (Double)value );
		else if ( value instanceof Character )
			valueLiteral = getCharacterLiteral( (Character)value );
		else if ( value instanceof Boolean )
			valueLiteral = getBooleanLiteral( (Boolean)value );
		else if ( value instanceof String )
			valueLiteral = getStringLiteral( (String)value );
		else if ( value instanceof Class )
			valueLiteral = getClassLiteral( (Class< ? >)value );
		else if ( value instanceof Enum )
			valueLiteral = getEnumLiteral( (Enum< ? >)value );
		else if ( value instanceof Annotation )
			valueLiteral = getAnnotationLiteral( (Annotation)value );
		else if ( value.getClass().isArray() )
			valueLiteral = getArrayLiteral( (Object[])value );
		else
			throw new IllegalStateException( "Unexpected type for value: " + value.getClass().getName() );
		
		return valueLiteral;
	}
	
	private static String getByteLiteral( Byte value )
	{
		return value.toString();
	}
	
	private static String getShortLiteral( Short value )
	{
		return value.toString();
	}
	
	private static String getIntegerLiteral( Integer value )
	{
		return value.toString();
	}
	
	private static String getLongLiteral( Long value )
	{
		return value.toString();
	}
	
	private static String getFloatLiteral( Float value )
	{
		return value.toString() + "F";
	}
	
	private static String getDoubleLiteral( Double value )
	{
		return value.toString();
	}
	
	private static String getCharacterLiteral( Character value )
	{
		return getByteLiteral( (byte)value.charValue() );
	}
	
	private static String getBooleanLiteral( Boolean value )
	{
		return value.toString();
	}
	
	private static String getStringLiteral( String value )
	{
		return "\"" + value + "\"";
	}
	
	private static String getClassLiteral( Class< ? > value )
	{
		return value.getName() + ".class";
	}
	
	private static String getEnumLiteral( Enum< ? > value )
	{
		return value.getDeclaringClass().getName() + "." + value.name();
	}
	
	private static String getArrayLiteral( Object[] values )
	{
		String arrayLiteral = "{ ";
		
		List< String > valueLiterals = new ArrayList< String >();
		
		for ( Object value : values )
			valueLiterals.add( getValueLiteral( value ) );
		
		arrayLiteral += String.join( ", ", valueLiterals );
		arrayLiteral += " }";
		
		return arrayLiteral;
	}	
}
