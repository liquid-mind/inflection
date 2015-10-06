package ch.liquidmind.inflection2.model.linked;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.liquidmind.inflection2.model.external.AnnotatableElement;

public abstract class AnnotatableElementLinked implements AnnotatableElement
{
	private List< Annotation > annotations = new ArrayList< Annotation >();

	public AnnotatableElementLinked( List< Annotation > annotations )
	{
		super();
		this.annotations = annotations;
	}

	public List< Annotation > getAnnotationsInternal()
	{
		return annotations;
	}

	public List< Annotation > getAnnotations()
	{
		return ImmutableList.copyOf( getAnnotationsInternal() );
	}
}
