package ch.liquidmind.inflection.proxy;

import org.junit.Test;

import com.google.common.io.Files;

import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.test.blackbox.util.BlackboxTestUtility;

public class ProxyGeneratorTest
{

	@Test
	public void testGenerate() throws Exception
	{
		Taxonomy taxonomy = BlackboxTestUtility.getTestTaxonomy( this.getClass().getPackage().getName(), "ProxyGeneratorSuperTaxonomy" );
		ProxyGenerator generator = new ProxyGenerator( Files.createTempDir(), taxonomy );
		generator.generateTaxonomy();
	}

}
