package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.CommonTokenStream;

import ch.liquidmind.inflection.grammar.InflectionParser.ClassViewDeclarationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TaxonomyDeclarationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IdentifierContext;
import ch.liquidmind.inflection.grammar.InflectionParser.VisitorsDeclarationContext;

public class CompilePass1Listener extends AbstractInflectionListener
{
	public CompilePass1Listener( File compilationUnit, CommonTokenStream commonTokenStream, String packageName, Map< String, InflectionResourceCompiled > inflectionResourcesCompiled, boolean bootstrap )
	{
		super( compilationUnit, commonTokenStream, packageName, inflectionResourcesCompiled, bootstrap );
	}
	
	@Override
	public void enterClassViewDeclaration( ClassViewDeclarationContext classViewDeclarationContext )
	{
		IdentifierContext identifierContext = (IdentifierContext)classViewDeclarationContext.getChild( 1 );
		String classViewName = getIdentifierFQName( identifierContext );
		addInflectionResourcesCompiled( identifierContext, new ClassViewCompiled( classViewName ) );
	}

	@Override
	public void enterVisitorsDeclaration( VisitorsDeclarationContext visitorsDeclarationContext )
	{
		IdentifierContext identifierContext = (IdentifierContext)visitorsDeclarationContext.getChild( 1 );
		String visitorsName = getIdentifierFQName( identifierContext );
		addInflectionResourcesCompiled( identifierContext, new VisitorsCompiled( visitorsName ) );
	}
	
	@Override
	public void enterTaxonomyDeclaration( TaxonomyDeclarationContext taxonomyDeclarationContext )
	{
		IdentifierContext identifierContext = (IdentifierContext)taxonomyDeclarationContext.getChild( 1 );
		String taxonomyName = getIdentifierFQName( identifierContext );
		addInflectionResourcesCompiled( identifierContext, new TaxonomyCompiled( taxonomyName ) );
	}

	private void addInflectionResourcesCompiled( IdentifierContext identifierContext, InflectionResourceCompiled inflectionResourceCompiled )
	{
		Set< String > classViewResourceNames = getInflectionResourcesCompiled().keySet();
		
		if ( classViewResourceNames.contains( inflectionResourceCompiled.getName() ) )
		{
			ClassViewErrorListener.displayError( getCompilationUnit(), getCommonTokenStream(), identifierContext.start, identifierContext.stop, "Duplicate inflection resource: " + inflectionResourceCompiled.getName() + " already exists." );
			stopCompiling();
		}

		getInflectionResourcesCompiled().put( inflectionResourceCompiled.getName(), inflectionResourceCompiled );
	}
}
