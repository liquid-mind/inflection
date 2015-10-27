package ch.liquidmind.inflection.test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

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
import ch.liquidmind.inflection.test.model.FullTaxonomy.ch.liquidmind.inflection.test.model.FullTaxonomy_Person;
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
		Taxonomy taxonomy = taxonomyLoader.loadTaxonomy( "ch.liquidmind.inflection.test.model.FullTaxonomy" );
		File baseDir = new File( "/Users/john/Documents/workspace-liquid-mind/inflection/build/inflection-test" );
		ProxyGenerator proxyGenerator = new ProxyGenerator( baseDir, taxonomy );
		proxyGenerator.generateTaxonomy();
		
		TaxonomyLoader.setContextTaxonomyLoader( taxonomyLoader );
		FullTaxonomy_Person p = new FullTaxonomy_Person();
		p.setFirstName( "John" );
		System.out.println( "FullTaxonomy_Person.firstName = " + p.getFirstName() );
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
