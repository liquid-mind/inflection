package ch.liquidmind.inflection.model.compiled;

import java.util.ArrayList;
import java.util.List;

public abstract class AnnotatableElementCompiled extends NamedElementCompiled
{
	private static final long serialVersionUID = 1L;
	
	private List< AnnotationCompiled > annotationsCompiled = new ArrayList< AnnotationCompiled >();

	public AnnotatableElementCompiled( String name )
	{
		super( name );
	}

	public List< AnnotationCompiled > getAnnotationsCompiled()
	{
		return annotationsCompiled;
	}
}
