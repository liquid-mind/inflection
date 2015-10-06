package ch.liquidmind.inflection.compiler;

import java.io.File;

public class BootstrapCompiler extends InflectionCompiler
{
	public BootstrapCompiler( File[] compilationUnits, File targetLocation )
	{
		super( compilationUnits, targetLocation );
	}
}
