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
import java.util.List;

import __java.io.__FileOutputStream;
import __java.io.__OutputStream;
import __org.apache.commons.io.__FileUtils;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Field;
import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Property;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.model.linked.NamedElementLinked;
import ch.liquidmind.inflection.model.linked.UnparsedAnnotation;

// TODO Proxies of abstract classes should be abstract themselves (since it doesn't
// make sense to be able to instantiate a view of an un-instantiable class).
// TODO Make ProxyGenerator work like InflectionCompiler, i.e., introduce a ProxyGeneratorJob, etc.
public class ProxyGenerator
{
	private File baseDir;
	private Taxonomy taxonomy;
	private PrintWriter printWriter;
	
	// TODO Introduce apache commons cli, analogous to deflector.
	public static void main( String[] args )
	{
		File baseDir = new File( args[ 0 ] );
		String[] taxonomyNames = new String[ args.length - 1 ];
		
		for ( int i = 0 ; i < taxonomyNames.length ; ++i )
			taxonomyNames[ i ] = args[ i + 1 ];
		
		for ( String taxonomyName : taxonomyNames )
		{
			Taxonomy taxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( taxonomyName );
			// TODO handle if taxonomy is null
			new ProxyGenerator( baseDir, taxonomy ).generateTaxonomy();
		}
	}
	
	public ProxyGenerator( File baseDir, Taxonomy taxonomy )
	{
		super();
		this.baseDir = baseDir;
		this.taxonomy = taxonomy;
	}

	public void generateTaxonomy()
	{
		if ( !baseDir.exists() )
			__FileUtils.forceMkdir( null, baseDir );
		
		for ( View view : taxonomy.getViews() )
			generateView( view );
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
	
	private void generateView( View view, String fqViewName )
	{
		printWriter.println( "package " + NamedElementLinked.getPackageName( fqViewName ) + ";" );
		printWriter.println();
		
		printWriter.println( String.join( " ", getAnnotations( view.getAnnotations() ) ) );
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
	
	private void generateProperty( String proxyMethodName, Method targetMethod, List< Annotation > annotations )
	{
		if ( targetMethod == null )
			return;
		
		List< Type > targetParamTypes = Arrays.asList( targetMethod.getGenericParameterTypes() );
		List< Type > proxyParamTypes = targetParamTypes;
		
		if ( Modifier.isStatic( targetMethod.getModifiers() ) )
			proxyParamTypes = new ArrayList< Type >( targetParamTypes.subList( 1, targetParamTypes.size() ) );
		
		Type[] targetParamTypesAsArray = targetParamTypes.toArray( new Type[ targetParamTypes.size() ] );
		Type[] proxyParamTypesAsArray = proxyParamTypes.toArray( new Type[ proxyParamTypes.size() ] );
		
		generateMethod( proxyMethodName, proxyParamTypesAsArray, annotations, targetMethod.getName(), targetParamTypesAsArray, targetMethod.getGenericReturnType(), targetMethod.getExceptionTypes() );
	}
	
	private void generateField( String proxyGetMethodName, String proxySetMethodName, Field field )
	{
		String name = field.getName();
		String capName = name.substring( 0, 1 ).toUpperCase() + name.substring( 1 ); 
		generateMethod( proxyGetMethodName, new Class< ? >[]{}, field.getAnnotations(), "get" + capName, new Class< ? >[]{}, field.getField().getType(), new Class< ? >[]{} );
		generateMethod( proxySetMethodName, new Class< ? >[]{ field.getField().getType() }, new ArrayList< Annotation >(), "set" + capName, new Class< ? >[]{ field.getField().getType() }, void.class, new Class< ? >[]{} );
	}

	private void generateMethod( String proxyMethodName, Type[] proxyParamTypes, List< Annotation > annotations, String targetMethodName, Type[] targetParamTypes, Type retType, Class< ? >[] exTypes )
	{
		String flatAnnotations = String.join( " ", getAnnotations( annotations ) );
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
		printWriter.println( "            throw new java.lang.IllegalStateException();" );
		printWriter.println( "        }" );
		
		printWriter.println( "    }" );
	}
	
	@SuppressWarnings( "unchecked" )
	private String[] getAnnotations( List< Annotation > annotations )
	{
		List< UnparsedAnnotation > unparsedAnnotations = (List< UnparsedAnnotation >)(Object)annotations;
		List< String > annotationsAsText = new ArrayList< String >();
		
		for ( UnparsedAnnotation unparsedAnnotation : unparsedAnnotations )
			annotationsAsText.add( unparsedAnnotation.value() );
		
		return annotationsAsText.toArray( new String[ annotationsAsText.size() ] );
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
			View view = taxonomy.resolveView( aClass );
			
			if ( view == null )
				typeName = aClass.getName();
			else
				typeName = getFullyQualifiedViewName( taxonomy, view );
		}
		else if ( type instanceof ParameterizedType )
		{
			ParameterizedType parameterizedType = (ParameterizedType)type;
			Type rawType = parameterizedType.getRawType();
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			String rawTypeConverted;
			List< String > actualTypeArgumentsConverted = new ArrayList< String >();
			
			if ( ProxyRegistry.proxiesByCollection.containsKey( rawType ) )
				rawTypeConverted = ProxyRegistry.proxiesByCollection.get( rawType ).getName();
			else
				throw new IllegalStateException( "No support for non-collection generic types at this time." );

			for ( Type actualTypeArgument : actualTypeArguments )
			{
				if ( actualTypeArgument instanceof Class )
					actualTypeArgumentsConverted.add( getTypeName( actualTypeArgument ) );
				else
					throw new IllegalStateException( "No support for general purpose generics at this time (only for collections)." );
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
}
