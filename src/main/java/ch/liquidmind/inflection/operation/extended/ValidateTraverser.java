package ch.liquidmind.inflection.operation.extended;

import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VisitorsInstance;
import ch.liquidmind.inflection.operation.LeftGraphTraverser;

public class ValidateTraverser extends LeftGraphTraverser
{
	public static final String DEFAULT_CONFIGURATION = ValidateTraverser.class.getName() + CONFIGURATION_SUFFIX;
	
	private List< ValidationError > validationErrors = new ArrayList< ValidationError >();
	
	public ValidateTraverser( Taxonomy taxonomy )
	{
		this( taxonomy, getConfiguration( DEFAULT_CONFIGURATION ) );
	}
	
	public ValidateTraverser( Taxonomy taxonomy, VisitorsInstance configurationInstance )
	{
		super( taxonomy, configurationInstance );
	}

	public List< ValidationError > getValidationErrors()
	{
		return validationErrors;
	}
}
