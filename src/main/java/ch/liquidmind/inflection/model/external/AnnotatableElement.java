package ch.liquidmind.inflection.model.external;

import java.lang.annotation.Annotation;
import java.util.List;

public interface AnnotatableElement extends NamedElement
{
	public List< Annotation > getAnnotations();
}
