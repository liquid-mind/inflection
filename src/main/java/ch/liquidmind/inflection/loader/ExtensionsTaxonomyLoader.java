package ch.liquidmind.inflection.loader;

public class ExtensionsTaxonomyLoader extends TaxonomyLoader
{
	ExtensionsTaxonomyLoader()
	{
		super( getBootstrapTaxonomyLoader(), ClassLoader.getSystemClassLoader().getParent() );
	}
}
