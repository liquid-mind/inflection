package ch.liquidmind.inflection2.model.compiled;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AnnotatableElementCompiled implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private List< AnnotationCompiled > annotationsCompiled = new ArrayList< AnnotationCompiled >();

	public AnnotatableElementCompiled( List< AnnotationCompiled > annotationsCompiled )
	{
		super();
		this.annotationsCompiled = annotationsCompiled;
	}

	public List< AnnotationCompiled > getAnnotationsCompiled()
	{
		return annotationsCompiled;
	}
}
