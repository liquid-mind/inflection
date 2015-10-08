package ch.liquidmind.inflection.compiler;

import java.util.Map;
import java.util.Set;

import ch.liquidmind.inflection.grammar.InflectionParser.IdentifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyNameContext;
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

	@Override
	public void enterTaxonomyName( TaxonomyNameContext ctx )
	{
		IdentifierContext identifierContext = (IdentifierContext)ctx.getChild( 0 );
		String taxonomyName = getIdentifierFQName( identifierContext );
		Set< String > taxonomyNames = getTaxonomiesCompiled().keySet();
		
		if ( taxonomyNames.contains( taxonomyName ) )
		{
			InflectionErrorListener.displayError( getCompilationUnit().getFile(), getCompilationUnit().getTokens(), identifierContext.start, identifierContext.stop, "Duplicate inflection resource: " + taxonomyName + " already exists." );
			stopCompiling();
		}

		getTaxonomiesCompiled().put( taxonomyName, new TaxonomyCompiled( taxonomyName ) );
	}
}
