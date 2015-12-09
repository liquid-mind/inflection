package ch.liquidmind.inflection.test;

import ch.liquidmind.inflection.compiler.CompilationJob;
import ch.liquidmind.inflection.compiler.InflectionCompiler;
import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.loader.TaxonomyLoader;

public abstract class AbstractInflectionTest
{

	public static interface AssertCompilationResult
	{
		public void doAssert(CompilationJob job);
	}

	public void doTest( AssertCompilationResult assertCompilationResult, InflectionFileMock... inflectionFileMocks )
	{
		doTest( assertCompilationResult, TaxonomyLoader.getSystemTaxonomyLoader(), inflectionFileMocks );
	}
	
	
	public void doTest( AssertCompilationResult assertCompilationResult, TaxonomyLoader taxonomyLoader, InflectionFileMock... inflectionFileMocks )
	{
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
