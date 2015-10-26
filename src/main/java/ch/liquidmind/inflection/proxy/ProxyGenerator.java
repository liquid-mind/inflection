package ch.liquidmind.inflection.proxy;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;

import __java.io.__FileOutputStream;
import __java.io.__OutputStream;
import __org.apache.commons.io.__FileUtils;
import ch.liquidmind.inflection.model.external.Field;
import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Property;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.model.linked.NamedElementLinked;

public class ProxyGenerator
{
	private File baseDir;
	private Taxonomy taxonomy;
	private PrintWriter printWriter;
	
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
		Taxonomy taxonomy = view.getParentTaxonomy();
		String fqViewName = taxonomy.getName() + "." + view.getPackageName() + "." + taxonomy.getSimpleName() + "_" + view.getSimpleName();
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

	private void generateView( View view, String fqViewName )
	{
		printWriter.println( "package " + NamedElementLinked.getPackageName( fqViewName ) + ";" );
		printWriter.println();
		
		printWriter.println( "public class " + NamedElementLinked.getSimpleName( fqViewName ) );
		printWriter.println( "{" );
		
		generateMembers( view.getDeclaredMembers() );
		
		printWriter.println( "}" );
		
	}
	
	private void generateMembers( List< Member > members )
	{
		for ( int i = 0 ; i < members.size() ; ++i )
		{
			generateMember( members.get( i ) );
			
			if ( i + 1 != members.size() )
				printWriter.println();
		}
	}

	private void generateMember( Member member )
	{
		if ( member instanceof Property )
			generateProperty( (Property)member );
		else if ( member instanceof Field )
			generateField( (Field)member );
	}
	
	private void generateProperty( Property property )
	{
		generateMethod( property.getReadMethod() );
		generateMethod( property.getWriteMethod() );
	}
	
	private void generateField( Field field )
	{
		String name = field.getName();
		String capName = name.substring( 0, 1 ).toUpperCase() + name.substring( 1 ); 
		generateMethod( "get" + capName, field.getField().getType(), new Class< ? >[]{}, new Class< ? >[]{} );
		generateMethod( "set" + capName, void.class, new Class< ? >[]{ field.getField().getType() }, new Class< ? >[]{} );
	}

	private void generateMethod( Method method )
	{
		if ( method == null )
			return;
		
		generateMethod( method.getName(), method.getReturnType(), method.getParameterTypes(), method.getExceptionTypes() );
	}
	
	private void generateMethod( String methodName, Class< ? > retType, Class< ? >[] paramTypes, Class< ? >[] exTypes )
	{
		
	}
}
