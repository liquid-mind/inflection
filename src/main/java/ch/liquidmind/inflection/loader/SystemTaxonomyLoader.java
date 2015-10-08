package ch.liquidmind.inflection.loader;

import java.util.Date;
import java.util.List;

import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.linked.TaxonomyLinked;
import ch.liquidmind.inflection.model.linked.ViewLinked;

public class SystemTaxonomyLoader extends TaxonomyLoader
{
	public static final String  JAVA_LANG = "java.lang";
	public static final String  CH_LIQUIDMIND_INFLECTION = "ch.liquidmind.inflection";
	public static final String BASETAXONOMY = CH_LIQUIDMIND_INFLECTION + ".BaseTaxonomy";
	
	private static TaxonomyLinked baseTaxonomy;
	
	static
	{
		baseTaxonomy = new TaxonomyLinked( BASETAXONOMY );
		baseTaxonomy.setDefaultAccessType( AccessType.Property );
		
		List< ViewLinked > viewsLinked = baseTaxonomy.getViewsLinked();
		
		// Basic types
		viewsLinked.add( createViewLinked( byte.class, baseTaxonomy ) );
		viewsLinked.add( createViewLinked( short.class, baseTaxonomy ) );
		viewsLinked.add( createViewLinked( int.class, baseTaxonomy ) );
		viewsLinked.add( createViewLinked( long.class, baseTaxonomy ) );
		viewsLinked.add( createViewLinked( float.class, baseTaxonomy ) );
		viewsLinked.add( createViewLinked( double.class, baseTaxonomy ) );
		viewsLinked.add( createViewLinked( boolean.class, baseTaxonomy ) );
		viewsLinked.add( createViewLinked( char.class, baseTaxonomy ) );
		
		// Other common (terminal) types
		viewsLinked.add( createViewLinked( String.class, baseTaxonomy ) );
		viewsLinked.add( createViewLinked( Date.class, baseTaxonomy ) );
	}
	
	private static ViewLinked createViewLinked( Class< ? > viewedClass, TaxonomyLinked parentTaxonomyLinked )
	{
		ViewLinked viewLinked = new ViewLinked( viewedClass.getName() );
		viewLinked.setViewedClass( viewedClass );
		viewLinked.setParentTaxonomyLinked( parentTaxonomyLinked );
		
		return viewLinked;
	}
	
	@Override
	public Taxonomy findTaxonomy( String name )
	{
		return baseTaxonomy;
	}
}
