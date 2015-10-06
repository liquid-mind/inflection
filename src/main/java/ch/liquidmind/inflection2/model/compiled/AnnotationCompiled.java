package ch.liquidmind.inflection2.model.compiled;

public class AnnotationCompiled
{
	// Note that this is a placeholder for what should eventually be a
	// fully parsed Java annotation. Will take some work to adapt the
	// Java grammar, so I'm taking this short-cut for now. Note that it
	// will still be possible to generate Java classes with these
	// annotations that are then (re-)compiled by the Java compiler.
	private String unparsedAnnotation;

	public AnnotationCompiled( String unparsedAnnotation )
	{
		super();
		this.unparsedAnnotation = unparsedAnnotation;
	}

	public String getUnparsedAnnotation()
	{
		return unparsedAnnotation;
	}

	public void setUnparsedAnnotation( String unparsedAnnotation )
	{
		this.unparsedAnnotation = unparsedAnnotation;
	}
}
