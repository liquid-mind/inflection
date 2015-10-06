package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.util.Map;

import org.antlr.v4.runtime.CommonTokenStream;

import ch.liquidmind.inflection.grammar.InflectionBaseListener;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class CompilePass2Listener extends InflectionBaseListener
{

	public CompilePass2Listener(
			File compilationUnit,
			CommonTokenStream tokens,
			String packageName,
			Map< String, TaxonomyCompiled > taxonomiesCompiled,
			TaxonomyLoader taxonomyLoader,
			boolean bootstrap )
	{
		// TODO Auto-generated constructor stub
	}
}
