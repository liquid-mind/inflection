package ch.liquidmind.inflection.test.blackbox;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.test.model.Address;

public final class BlackboxTestUtility {

	public static Taxonomy getTestTaxonomy(String taxonomyName) {
		String packageName = Address.class.getPackage().getName();
		return TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( packageName + "." + taxonomyName );
	}
	
}
