package ch.liquidmind.inflection.test;

import java.io.File;

import org.junit.Test;

import ch.liquidmind.inflection.compiler.InflectionCompiler;

public class InflectionCompilerTest
{
	@Test
	public void test()
	{
		InflectionCompiler compiler = new InflectionCompiler( new File[] { new File( "/Users/john/Documents/workspace-liquid-mind/inflection/src/main/resources/ch/liquidmind/inflection/compiler/InflectionCompilerTest.inflect" ) }, new File( "undefined" ) );
		compiler.compile();
	}
}
