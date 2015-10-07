package ch.liquidmind.inflection.test;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.BootstrapCompiler;
import ch.liquidmind.inflection.compiler.InflectionCompiler;

public class InflectionCompilerTest
{
	@Test
	public void test()
	{
		InflectionCompiler compiler = new BootstrapCompiler( new String[] { "/Users/john/Documents/workspace-liquid-mind/inflection/src/main/resources/ch/liquidmind/inflection/compiler/InflectionCompilerTest.inflect" }, new String( "undefined" ) );
		compiler.compile();
	}
}
