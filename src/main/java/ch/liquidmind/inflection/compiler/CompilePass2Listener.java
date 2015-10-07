package ch.liquidmind.inflection.compiler;

import java.util.Map;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class CompilePass2Listener extends AbstractInflectionListener
{
	@SuppressWarnings( "unused" )
	private TaxonomyLoader taxonomyLoader;
	
	public CompilePass2Listener(
			CompilationUnit compilationUnit,
			Map< String, TaxonomyCompiled > taxonomiesCompiled,
			boolean bootstrap,
			TaxonomyLoader taxonomyLoader )
	{
		super( compilationUnit, taxonomiesCompiled, bootstrap );
		this.taxonomyLoader = taxonomyLoader;
	}
}
