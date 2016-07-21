package ch.liquidmind.inflection;

import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.proxy.memory.AuxiliaryOwnedVirtualObjectReference;

@SuppressWarnings( "unused" )
public abstract class Auxiliary
{
	private Taxonomy taxonomy;
	private View view;
	private AuxiliaryOwnedVirtualObjectReference virtualObjectReference;
}
