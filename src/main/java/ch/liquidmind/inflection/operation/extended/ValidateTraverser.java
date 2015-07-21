package ch.liquidmind.inflection.operation.extended;

import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VisitorsInstance;
import ch.liquidmind.inflection.operation.LeftGraphTraverser;

public class ValidateTraverser extends LeftGraphTraverser
{
	public static final String DEFAULT_VISITORS = ValidateTraverser.class.getName() + VISITORS_SUFFIX;
	
	private List< ValidationError > validationErrors = new ArrayList< ValidationError >();
	
	public ValidateTraverser( Taxonomy taxonomy )
	{
		this( taxonomy, getVisitors( DEFAULT_VISITORS ) );
	}
	
	public ValidateTraverser( Taxonomy taxonomy, VisitorsInstance visitorsInstance )
	{
		super( taxonomy, visitorsInstance );
	}

	public List< ValidationError > getValidationErrors()
	{
		return validationErrors;
	}
}
