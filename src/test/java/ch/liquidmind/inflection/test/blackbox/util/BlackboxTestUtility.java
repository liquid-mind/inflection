package ch.liquidmind.inflection.test.blackbox.util;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;

public final class BlackboxTestUtility {

	public static Taxonomy getTestTaxonomy(String packageName, String taxonomyName) {
		return TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( packageName + "." + taxonomyName );
	}
	
}
