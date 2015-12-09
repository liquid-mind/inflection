package ch.liquidmind.inflection.test;

import java.io.File;

import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;

public abstract class AbstractInflectionTest
{

	public static interface AssertCompilationResult
	{
		public void doAssert(CompilationJob job);
	}

	public void doTest( AssertCompilationResult assertCompilationResult, InflectionFileMock... inflectionFileMocks )
	{
		doTest( assertCompilationResult, null, inflectionFileMocks );
	}
	
	
	public void doTest( AssertCompilationResult assertCompilationResult, InflectionFileMock[] inflectionFileMocksOnClasspath, InflectionFileMock... inflectionFileMocks )
	{
		TaxonomyLoader taxonomyLoader = TaxonomyLoader.getSystemTaxonomyLoader();
		if (inflectionFileMocksOnClasspath != null) {
			File compiledTaxonomyDirectory = InflectionCompilerTestUtility.compileInflection( inflectionFileMocksOnClasspath );
			taxonomyLoader = TaxonomyTestUtility.createTaxonomyLoader( compiledTaxonomyDirectory );
		}
		
		CompilationJob job = InflectionCompilerTestUtility.createCompilationJob( taxonomyLoader, inflectionFileMocks );
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

}
