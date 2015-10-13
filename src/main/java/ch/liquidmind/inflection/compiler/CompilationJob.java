package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class CompilationJob
{
	public enum CompilationMode
	{
		Normal,
		Boostrap
	}
	
	private TaxonomyLoader taxonomyLoader;
	private File targetDirectory;
	private CompilationMode compilationMode;
	private Map< String, TaxonomyCompiled > knownTaxonomiesCompiled = new HashMap< String, TaxonomyCompiled >();
	private List< CompilationUnit > compilationUnits = new ArrayList< CompilationUnit >();
	
	public CompilationJob( TaxonomyLoader taxonomyLoader, File targetDirectory, CompilationMode compilationMode, File ... sourceFiles )
	{
		super();
		this.taxonomyLoader = taxonomyLoader;
		this.targetDirectory = targetDirectory;
		this.compilationMode = compilationMode;
		
		for ( File sourceFile : sourceFiles )
			compilationUnits.add( new CompilationUnit( sourceFile ) );
	}

	public TaxonomyLoader getTaxonomyLoader()
	{
		return taxonomyLoader;
	}
	
	public File getTargetDirectory()
	{
		return targetDirectory;
	}
	
	public CompilationMode getCompilationMode()
	{
		return compilationMode;
	}

	public Map< String, TaxonomyCompiled > getKnownTaxonomiesCompiled()
	{
		return knownTaxonomiesCompiled;
	}

	public List< CompilationUnit > getCompilationUnits()
	{
		return ImmutableList.copyOf( compilationUnits );
	}
}
