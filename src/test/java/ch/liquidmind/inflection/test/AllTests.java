package ch.liquidmind.inflection.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.liquidmind.inflection.test.blackbox.InflectionBlackboxTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	InflectionTest.class,
	InflectionBlackboxTest.class
	})
public class AllTests {

}
