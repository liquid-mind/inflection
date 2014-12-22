package ch.liquidmind.inflection.compiler;

import java.io.File;

import ch.liquidmind.inflection.InflectionResourceLoader;

public class InflectionCompilerBootstrap
{
	// TODO Refactor using commons CLI.
	public static void main( String[] args )
	{
		File targetLocation = new File( args[ 0 ] );
		File[] compilationUnits = new File[ args.length - 1 ];
		
		for ( int i = 0 ; i < compilationUnits.length ; ++i )
			compilationUnits[ i ] = new File( args[ i + 1 ] );
		
		compile( compilationUnits, targetLocation, InflectionResourceLoader.getSystemInflectionResourceLoader() );
	}
	
	public static void compile( File[] compilationUnits, File targetLocation, InflectionResourceLoader inflectionResourceLoader )
	{
		InflectionCompiler.compile( compilationUnits, targetLocation, inflectionResourceLoader, true );
	}
}
