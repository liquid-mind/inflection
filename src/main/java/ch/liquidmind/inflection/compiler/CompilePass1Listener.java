package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.util.Map;

import org.antlr.v4.runtime.CommonTokenStream;

import ch.liquidmind.inflection.grammar.InflectionBaseListener;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyContext;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class CompilePass1Listener extends InflectionBaseListener
{
	public CompilePass1Listener(
			File compilationUnit,
			CommonTokenStream tokens,
			String packageName,
			Map< String, TaxonomyCompiled > taxonomiesCompiled,
			boolean bootstrap )
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public void enterTaxonomy( TaxonomyContext ctx )
	{
		super.enterTaxonomy( ctx );
	}
}
