package ch.liquidmind.inflection.model.linked;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.liquidmind.inflection.model.external.AnnotatableElement;

public abstract class AnnotatableElementLinked extends NamedElementLinked implements AnnotatableElement
{
	private List< Annotation > annotations = new ArrayList< Annotation >();
	
	public AnnotatableElementLinked( String name )
	{
		super( name );
	}

	public List< Annotation > getAnnotationsInternal()
	{
		return annotations;
	}

	@Override
	public List< Annotation > getAnnotations()
	{
		return ImmutableList.copyOf( getAnnotationsInternal() );
	}
}
