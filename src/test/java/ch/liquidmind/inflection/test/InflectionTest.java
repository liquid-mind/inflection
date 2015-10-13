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

public class InflectionTest
{
	@Ignore
	@Test
	public void testTaxonomyLoader()
	{
		TaxonomyCompiled myTaxonomy = new TaxonomyCompiled( "com.mypackage.MyTaxonomy" );
		
		ViewCompiled myClass1 = new ViewCompiled( "ch.liquidmind.inflection.test.MyClass1" );
		myClass1.setParentTaxonomyCompiled( myTaxonomy );
		myClass1.setSelectionType( SelectionType.Include );
		
		MemberCompiled a = new MemberCompiled( "a" );
		a.setParentViewCompiled( myClass1 );
		a.setSelectionType( SelectionType.Include );
		
		myClass1.getMembersCompiled().add( a );
		myTaxonomy.getViewsCompiled().add( myClass1 );
		myTaxonomy.getExtendedTaxonomies().add( SystemTaxonomyLoader.BASETAXONOMY );
		
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
	
	@Test
	public void testCompiler()
	{
		CompilationJob job = new CompilationJob(
			TaxonomyLoader.getSystemTaxonomyLoader(),
			new File( "/Users/john/Documents/workspace-liquid-mind/inflection/build/inflection-test" ),
			CompilationMode.Normal, 
			new File( "/Users/john/Documents/workspace-liquid-mind/inflection/src/test/resources/ch/liquidmind/inflection/test/test.inflect" ) );
		InflectionCompiler.compile( job );
		job.printFaults();
	}
}
