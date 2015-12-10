package ch.liquidmind.inflection.test;

import java.io.File;
import java.net.URLClassLoader;

import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;
import ch.liquidmind.inflection.test.mock.InflectionFileMock;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

public abstract class AbstractInflectionTest
{

	public static interface AssertCompilationResult
	{
		public void doAssert( CompilationJob job );
	}

	public void doTest( AssertCompilationResult assertCompilationResult, InflectionFileMock... inflectionFileMocks )
	{
		doTest( assertCompilationResult, null, null, inflectionFileMocks );
	}

	public void doTest( AssertCompilationResult assertCompilationResult, JavaFileMock[] javaFileMocksOnClasspath, InflectionFileMock[] inflectionFileMocksOnClasspath,
			InflectionFileMock... inflectionFileMocks )
	{
		ClassLoader javaClassLoader = ClassLoader.getSystemClassLoader();
		if ( javaFileMocksOnClasspath != null )
		{
			File compiledJavaDir = InflectionCompilerTestUtility.compileJava( javaFileMocksOnClasspath );
			javaClassLoader = new URLClassLoader( TestUtility.convertToURLArray( compiledJavaDir ), null );
		}

		TaxonomyLoader inflectionTaxonomyLoader = TaxonomyLoader.getSystemTaxonomyLoader();
		if ( inflectionFileMocksOnClasspath != null )
		{
			File compiledTaxonomyDirectory = InflectionCompilerTestUtility.compileInflection( inflectionFileMocksOnClasspath );
			inflectionTaxonomyLoader = TaxonomyTestUtility.createTaxonomyLoader( javaClassLoader, compiledTaxonomyDirectory );
		}

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( inflectionTaxonomyLoader, inflectionFileMocks );
		InflectionCompiler.compile( job );

		assertCompilationResult.doAssert( job );
	}

	public InflectionFileMock createInflectionFileMock( String fileName, String packageName, String content )
	{
		return new InflectionFileMock( fileName, packageName, content );
	}

	public InflectionFileMock createInflectionFileMock( String packageName, String content )
	{
		return new InflectionFileMock( packageName, content );
	}

	public InflectionFileMock createInflectionFileMock( String content )
	{
		return new InflectionFileMock( content );
	}

	public JavaFileMock createJavaFileMock( String fileName, String packageName, String content )
	{
		return new JavaFileMock( fileName, packageName, content );
	}

	public JavaFileMock createJavaFileMock( String packageName, String content )
	{
		return new JavaFileMock( packageName, content );
	}

	public JavaFileMock createJavaFileMock( String content )
	{
		return new JavaFileMock( content );
	}

}
