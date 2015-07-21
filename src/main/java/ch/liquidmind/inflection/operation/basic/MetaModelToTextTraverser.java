package ch.liquidmind.inflection.operation.basic;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VisitorsInstance;


public class MetaModelToTextTraverser extends IndentingPrintWriterTraverser
{
	public static final String CONFIGURATION = MetaModelToTextTraverser.class.getName() + CONFIGURATION_SUFFIX;

	public MetaModelToTextTraverser( Taxonomy taxonomy )
	{
		this( taxonomy, getConfiguration( CONFIGURATION ) );
	}
	
	public MetaModelToTextTraverser( Taxonomy taxonomy, VisitorsInstance visitorsInstance )
	{
		super( taxonomy, visitorsInstance );
	}
}
