package ch.liquidmind.inflection.operation.basic;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VisitorsInstance;


public class MetaModelToTextTraverser extends IndentingPrintWriterTraverser
{
	public static final String VISITORS = MetaModelToTextTraverser.class.getName() + VISITORS_SUFFIX;

	public MetaModelToTextTraverser( Taxonomy taxonomy )
	{
		this( taxonomy, getVisitors( VISITORS ) );
	}
	
	public MetaModelToTextTraverser( Taxonomy taxonomy, VisitorsInstance visitorsInstance )
	{
		super( taxonomy, visitorsInstance );
	}
}
