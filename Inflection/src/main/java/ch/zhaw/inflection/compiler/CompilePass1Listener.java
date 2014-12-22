package ch.zhaw.inflection.compiler;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.CommonTokenStream;

import ch.zhaw.inflection.grammar.InflectionParser.ClassViewDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.HgroupDeclarationContext;
import ch.zhaw.inflection.grammar.InflectionParser.IdentifierContext;
import ch.zhaw.inflection.grammar.InflectionParser.VmapDeclarationContext;

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
	public void enterVmapDeclaration( VmapDeclarationContext vmapDeclarationContext )
	{
		IdentifierContext identifierContext = (IdentifierContext)vmapDeclarationContext.getChild( 1 );
		String vmapName = getIdentifierFQName( identifierContext );
		addInflectionResourcesCompiled( identifierContext, new VmapCompiled( vmapName ) );
	}
	
	@Override
	public void enterHgroupDeclaration( HgroupDeclarationContext hgroupDeclarationContext )
	{
		IdentifierContext identifierContext = (IdentifierContext)hgroupDeclarationContext.getChild( 1 );
		String hgroupName = getIdentifierFQName( identifierContext );
		addInflectionResourcesCompiled( identifierContext, new HgroupCompiled( hgroupName ) );
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
