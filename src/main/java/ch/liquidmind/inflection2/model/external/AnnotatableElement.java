package ch.liquidmind.inflection2.model.external;

import java.lang.annotation.Annotation;
import java.util.List;

public interface AnnotatableElement
{
	public List< Annotation > getAnnotations();
}
