package ch.liquidmind.inflection.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.liquidmind.inflection.compiler.InflectionCompilerTest;
import ch.liquidmind.inflection.model.external.MemberTest;
import ch.liquidmind.inflection.model.external.TaxonomyTest;
import ch.liquidmind.inflection.model.external.ViewTest;
import ch.liquidmind.inflection.proxy.ProxyGeneratorTest;
import ch.liquidmind.inflection.test.blackbox.BlackboxTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	InflectionTest.class,
	BlackboxTest.class,
	InflectionCompilerTest.class,
	ProxyGeneratorTest.class,
	TaxonomyTest.class,
	ViewTest.class,
	MemberTest.class
	})
public class AllTests {

}
