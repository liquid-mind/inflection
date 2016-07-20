package ch.liquidmind.inflection.loader;

public class SystemTaxonomyLoader extends TaxonomyLoader
{
	SystemTaxonomyLoader()
	{
		super( getExtensionsTaxonomyLoader(), ClassLoader.getSystemClassLoader() );
	}
}
