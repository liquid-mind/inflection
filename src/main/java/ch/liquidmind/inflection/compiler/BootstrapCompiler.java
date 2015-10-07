package ch.liquidmind.inflection.compiler;

import ch.liquidmind.inflection.loader.TaxonomyLoader;

public class BootstrapCompiler extends InflectionCompiler
{
	public BootstrapCompiler( String[] compilationUnitNames, String targetLocation )
	{
		super( compilationUnitNames, targetLocation );
	}

	public BootstrapCompiler( String[] compilationUnitNames, String targetLocation, TaxonomyLoader taxonomyLoader )
	{
		super( compilationUnitNames, targetLocation, taxonomyLoader );
	}
}
