package ch.liquidmind.inflection.compiler;

import java.util.Map;

import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class CompilePass1Listener extends AbstractInflectionListener
{
	public CompilePass1Listener(
			CompilationUnit compilationUnit,
			Map< String, TaxonomyCompiled > taxonomiesCompiled,
			boolean bootstrap )
	{
		super( compilationUnit, taxonomiesCompiled, bootstrap );
	}
	
}
