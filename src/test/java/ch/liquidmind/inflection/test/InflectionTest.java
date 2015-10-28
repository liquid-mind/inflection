package ch.liquidmind.inflection.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Ignore;
import org.junit.Test;

import __java.net.__URI;
import __org.apache.commons.io.__FileUtils;
import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.CompilationJob.CompilationMode;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.loader.SystemTaxonomyLoader;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.model.compiled.MemberCompiled;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;
import ch.liquidmind.inflection.model.compiled.ViewCompiled;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.proxy.ProxyGenerator;
import ch.liquidmind.inflection.proxy.ProxyHelper;
import ch.liquidmind.inflection.test.model.Address;
import ch.liquidmind.inflection.test.model.Gender;
import ch.liquidmind.inflection.test.model.Person;
import ch.liquidmind.inflection.test.model.FullTaxonomy.ch.liquidmind.inflection.test.model.FullTaxonomy_Person;
import ch.liquidmind.inflection.test.model.UseCase1.ch.liquidmind.inflection.test.model.UseCase1_Person;
import ch.liquidmind.inflection.test.model.UseCase2.ch.liquidmind.inflection.test.model.UseCase2_Person;
import ch.liquidmind.inflection.test.model.UseCase3.ch.liquidmind.inflection.test.model.UseCase3_Person;
import ch.liquidmind.inflection.test.model.UseCase4.ch.liquidmind.inflection.test.model.UseCase4_Address;
import ch.liquidmind.inflection.util.InflectionPrinter;

public class InflectionTest
{
	@Ignore
	@Test
	public void testTaxonomyLoader()
	{
		TaxonomyCompiled myTaxonomy = new TaxonomyCompiled( "com.mypackage.MyTaxonomy" );
		
		ViewCompiled myClass1 = new ViewCompiled( "ch.liquidmind.inflection.test.MyClass1" );
		myClass1.setParentTaxonomyCompiled( myTaxonomy );
		myClass1.setSelectionType( SelectionType.INCLUDE );
		
		MemberCompiled a = new MemberCompiled( "a" );
		a.setParentViewCompiled( myClass1 );
		a.setSelectionType( SelectionType.INCLUDE );
		
		myClass1.getMembersCompiled().add( a );
		myTaxonomy.getViewsCompiled().add( myClass1 );
		myTaxonomy.getExtendedTaxonomies().add( SystemTaxonomyLoader.TAXONOMY );
		
		File inflectionTest = new File( "/Users/john/Documents/workspace-liquid-mind/inflection/build/inflection-test" );
		File bin = new File( "/Users/john/Documents/workspace-liquid-mind/inflection/bin" );
		__FileUtils.forceMkdir( null, inflectionTest );
		myTaxonomy.save( inflectionTest );
		
		URLClassLoader currentClassLoader = (URLClassLoader)InflectionTest.class.getClassLoader();
		URL[] currentUrls = currentClassLoader.getURLs();
		URL[] urls = new URL[ currentUrls.length + 2 ];
		System.arraycopy( currentUrls, 0, urls, 0, currentUrls.length );
		urls[ urls.length - 2 ] = __URI.toURL( inflectionTest.toURI() );
		urls[ urls.length - 1 ] = __URI.toURL( bin.toURI() );
		URLClassLoader newClassLoader = new URLClassLoader( urls );
		
		TaxonomyLoader taxonomyLoader = new TaxonomyLoader( TaxonomyLoader.getSystemTaxonomyLoader(), newClassLoader );
		taxonomyLoader.loadTaxonomy( myTaxonomy.getName() );
	}
	
	@Ignore
	@Test
	public void testCompiler()
	{
		TaxonomyLoader taxonomyLoader = compileTestTaxonomies();
		
		String[] taxonomyNames = new String[] { "FullTaxonomy", "UseCase1", "UseCase2", "UseCase3", "UseCase4", "UseCase5" };
		
		for ( String taxonomyName : taxonomyNames )
		{
			Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "ch.liquidmind.inflection.test.model." + taxonomyName );
			InflectionPrinter printer = new InflectionPrinter();
			printer.printTaxonomy( taxonomy );
			System.out.println();
		}
	}

	@Test
	public void testGenerator()
	{
		TaxonomyLoader taxonomyLoader = compileTestTaxonomies();
		File baseDir = new File( "/Users/john/Documents/workspace-liquid-mind/inflection/build/inflection-test" );
		Taxonomy fullTaxonomy = taxonomyLoader.loadTaxonomy( "ch.liquidmind.inflection.test.model.FullTaxonomy" );
		Taxonomy useCase1 = taxonomyLoader.loadTaxonomy( "ch.liquidmind.inflection.test.model.UseCase1" );
		Taxonomy useCase2 = taxonomyLoader.loadTaxonomy( "ch.liquidmind.inflection.test.model.UseCase2" );
		Taxonomy useCase3 = taxonomyLoader.loadTaxonomy( "ch.liquidmind.inflection.test.model.UseCase3" );
		Taxonomy useCase4 = taxonomyLoader.loadTaxonomy( "ch.liquidmind.inflection.test.model.UseCase4" );

		new ProxyGenerator( baseDir, fullTaxonomy ).generateTaxonomy();
		new ProxyGenerator( baseDir, useCase1 ).generateTaxonomy();
		new ProxyGenerator( baseDir, useCase2 ).generateTaxonomy();
		new ProxyGenerator( baseDir, useCase3 ).generateTaxonomy();
		new ProxyGenerator( baseDir, useCase4 ).generateTaxonomy();
		
		TaxonomyLoader.setContextTaxonomyLoader( taxonomyLoader );
	}

	@Test
	public void testProxies() throws JsonGenerationException, JsonMappingException, IOException
	{
		testGenerator();
		
		Calendar cal = new GregorianCalendar();
		cal.set( 1972, 8, 8 );
		Person person = new Person( 42, "John", "Brush", "Mr.", "+41 79 235 17 56", "+41 79 235 17 56", "jebrush@gmail.com", Gender.MALE, cal.getTime() );
		Address address = new Address( 43, "Feldgüetliweg 82", "Feldmeilen", "8706", "Switzerland" );
		person.getAddresses().add( address );
		address.getPeople().add( person );
		
		FullTaxonomy_Person fullTaxonomyPerson = ProxyHelper.getProxy( "ch.liquidmind.inflection.test.model.FullTaxonomy", person );
		UseCase1_Person useCase1Person = ProxyHelper.getProxy( "ch.liquidmind.inflection.test.model.UseCase1", person );
		UseCase2_Person useCase2Person = ProxyHelper.getProxy( "ch.liquidmind.inflection.test.model.UseCase2", person );
		UseCase3_Person useCase3Person = ProxyHelper.getProxy( "ch.liquidmind.inflection.test.model.UseCase3", person );
		UseCase4_Address useCase4Address = ProxyHelper.getProxy( "ch.liquidmind.inflection.test.model.UseCase4", address );
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		
		System.out.println( "FullTaxonomy_Person:" );
		System.out.println( writer.writeValueAsString( fullTaxonomyPerson ) );
		System.out.println();
		System.out.println( "UseCase1_Person:" );
		System.out.println( writer.writeValueAsString( useCase1Person ) );
		System.out.println();
		System.out.println( "UseCase2_Person:" );
		System.out.println( writer.writeValueAsString( useCase2Person ) );
		System.out.println();
		System.out.println( "UseCase3_Person:" );
		System.out.println( writer.writeValueAsString( useCase3Person ) );
		System.out.println();
		System.out.println( "UseCase4_Address:" );
		System.out.println( writer.writeValueAsString( useCase4Address ) );
		System.out.println();
	}
	
	private TaxonomyLoader compileTestTaxonomies()
	{
		File targetDir = new File( "/Users/john/Documents/workspace-liquid-mind/inflection/build/inflection-test" );
		File inflectionTest = new File( "/Users/john/Documents/workspace-liquid-mind/inflection/src/test/resources/ch/liquidmind/inflection/test/model/test.inflect" );
		
		if ( targetDir.exists() )
			__FileUtils.forceDelete( null, targetDir );
		
		// Note that the CompilationMode.BOOTSTRAP option is to avoid an error
		// due to the reserved namespace "ch.liquidmind.inflection".
		CompilationJob job = new CompilationJob( TaxonomyLoader.getSystemTaxonomyLoader(), targetDir, CompilationMode.BOOTSTRAP, inflectionTest );
		InflectionCompiler.compile( job );
		job.printFaults();

		URLClassLoader currentClassLoader = (URLClassLoader)InflectionTest.class.getClassLoader();
		URL[] currentUrls = currentClassLoader.getURLs();
		URL[] urls = new URL[ currentUrls.length + 2 ];
		System.arraycopy( currentUrls, 0, urls, 0, currentUrls.length );
		urls[ urls.length - 2 ] = __URI.toURL( targetDir.toURI() );
		URLClassLoader newClassLoader = new URLClassLoader( urls );
		
		TaxonomyLoader taxonomyLoader = new TaxonomyLoader( TaxonomyLoader.getSystemTaxonomyLoader(), newClassLoader );
		
		return taxonomyLoader;
	}
}
