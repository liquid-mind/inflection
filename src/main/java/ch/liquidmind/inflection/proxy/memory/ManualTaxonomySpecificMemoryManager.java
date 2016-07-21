package ch.liquidmind.inflection.proxy.memory;

import java.util.HashMap;
import java.util.Map;

import ch.liquidmind.inflection.model.external.Taxonomy;

public class ManualTaxonomySpecificMemoryManager extends TaxonomySpecificMemoryManager
{
	private Map< Integer, ObjectsTuple > objectsTuples = new HashMap< Integer, ObjectsTuple >();

	public ManualTaxonomySpecificMemoryManager( Taxonomy taxonomy )
	{
		super( taxonomy );
	}

	@Override
	protected ObjectsTuple getObjectTuple( Object key )
	{
		ObjectsTuple objectsTuple = objectsTuples.get( System.identityHashCode( key ) );

		if ( objectsTuple == null )
		{
			objectsTuple = createObjectTuple( key );
			objectsTuples.put( System.identityHashCode( objectsTuple.getObject() ), objectsTuple );
			objectsTuples.put( System.identityHashCode( objectsTuple.getProxy() ), objectsTuple );

			if ( objectsTuple.getAuxiliary() != null )
				objectsTuples.put( System.identityHashCode( objectsTuple.getAuxiliary() ), objectsTuple );
		}

		return objectsTuple;
	}
}
