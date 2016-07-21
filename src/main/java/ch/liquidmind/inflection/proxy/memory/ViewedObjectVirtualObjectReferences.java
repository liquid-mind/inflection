package ch.liquidmind.inflection.proxy.memory;

import java.util.HashMap;
import java.util.Map;

import ch.liquidmind.inflection.model.external.Taxonomy;

public class ViewedObjectVirtualObjectReferences extends VirtualObjectReference
{
	private Map< Taxonomy, ViewedObjectVirtualObjectReference > references = new HashMap< Taxonomy, ViewedObjectVirtualObjectReference >();

	public Map< Taxonomy, ViewedObjectVirtualObjectReference > getReferences()
	{
		return references;
	}
}