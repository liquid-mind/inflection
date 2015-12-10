package ch.liquidmind.inflection.test;

import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
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
			javaClassLoader = InflectionCompilerTestUtility.compileJava( javaFileMocksOnClasspath );
		}

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		if ( inflectionFileMocksOnClasspath != null )
		{
			TaxonomyLoader taxonomyLoader = InflectionCompilerTestUtility.compileInflection( javaClassLoader, inflectionFileMocksOnClasspath );
			classLoader = taxonomyLoader.getClassLoader();
		}

		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( classLoader, inflectionFileMocks );
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
