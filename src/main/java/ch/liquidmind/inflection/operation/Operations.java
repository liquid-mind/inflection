package ch.liquidmind.inflection.operation;

import java.io.OutputStream;

import ch.liquidmind.inflection.InflectionResourceLoader;
import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VMap;

public abstract class Operations
{
	public static final OutputStream DEFAULT_OUTPUT_STREAM = System.out;
	public static final Class< ? > DEFAULT_DEFAULT_ROOT_CLASS = Object.class;

	protected static InflectionResourceLoader getDefaultInflectionResourceLoader()
	{
		return InflectionResourceLoader.getContextInflectionResourceLoader();
	}
	
	protected static Taxonomy getTaxonomy( String taxonomy )
	{
		return getDefaultInflectionResourceLoader().loadTaxonomy( taxonomy );
	}
	
	protected static VMap getVMap( String vmap )
	{
		return getDefaultInflectionResourceLoader().loadVmap( vmap );
	}
}
