package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.ClassPath.ClassInfo;

import ch.liquidmind.inflection.ClassSelector;
import ch.liquidmind.inflection.exception.ExceptionWrapper;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class CompilationJob
{
	public enum CompilationMode
	{
		NORMAL,
		BOOTSTRAP
	}
	
	private TaxonomyLoader taxonomyLoader;
	private File targetDirectory;
	private CompilationMode compilationMode;
	private Map< String, TaxonomyCompiled > knownTaxonomiesCompiled = new HashMap< String, TaxonomyCompiled >();
	private List< CompilationUnit > compilationUnits = new ArrayList< CompilationUnit >();
	private Set< ClassInfo > allClassesInClassPath;
	private Set< Class< ? > > allClassesInClassPath2;	// TODO: rename allClassesInClassPath and allClassesInClassPath2 to something better.
	private Set< Method > classSelectorMethods;
	
	public CompilationJob( TaxonomyLoader taxonomyLoader, File targetDirectory, CompilationMode compilationMode, File ... sourceFiles )
	{
		super();
		this.taxonomyLoader = taxonomyLoader;
		this.targetDirectory = targetDirectory;
		this.compilationMode = compilationMode;
		
		for ( File sourceFile : sourceFiles )
			compilationUnits.add( new CompilationUnit( sourceFile, this ) );
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
	
	public List< CompilationFault > getCompilationFaults()
	{
		List< CompilationFault > compilationFaults = new ArrayList< CompilationFault >();
		
		for ( CompilationUnit compilationUnit : compilationUnits )
			compilationFaults.addAll( compilationUnit.getCompilationFaults() );
		
		return compilationFaults;
	}
	
	public boolean hasCompilationErrors()
	{
		return CompilationUnit.containsCompilationErrors( getCompilationFaults() );
	}
	
	public void printFaults()
	{
		printFaults( System.err );
	}
	
	public void printFaults( OutputStream os )
	{
		for ( CompilationFault fault : getCompilationFaults() )
			fault.print( os );
	}

	Set< ClassInfo > getAllClassesInClassPath()
	{
		if ( allClassesInClassPath == null )
		{
			ClassLoader loader = getTaxonomyLoader().getClassLoader();
			Set< ClassInfo > allClasses = ExceptionWrapper.ClassPath_from( loader ).getAllClasses();
			Set< ClassInfo > subsetOfClasses = new HashSet< ClassInfo >();
			
			for ( ClassInfo classInfo : allClasses )
			{
				try
				{
					Class< ? > aClass = classInfo.load();
					
					if ( aClass.isEnum() || aClass.isInterface() || aClass.isLocalClass() || aClass.isMemberClass() )
						continue;
				}
				catch ( LinkageError e )
				{
					// TBD: Use Javassist or some other method to examine the classes on the class path without
					// actually linking them (linking can result in an error when classes on the class path
					// depend on classes not on the class path). For now, I'm just letting any such classes
					// filter through, which is probably not a problem in practice (since model classes are usually
					// quite simple).
				}
				
				subsetOfClasses.add( classInfo );
			}
			
			allClassesInClassPath = subsetOfClasses;
		}
		
		return allClassesInClassPath;
	}
	
	Set< Class< ? > > getAllClassesInClassPath2()
	{
		if ( allClassesInClassPath2 == null )
		{
			allClassesInClassPath2 = new HashSet< Class< ? > >();
			Set< ClassInfo > allClassInfos = getAllClassesInClassPath();

			for ( ClassInfo classInfo : allClassInfos )
			{
				try
				{
					allClassesInClassPath2.add( classInfo.load() );
				}
				catch ( LinkageError e )
				{
				}
			}
		}
		
		return allClassesInClassPath2;
	}
	
	Set< Method > getClassSelectorMethods()
	{
		if ( classSelectorMethods == null )
		{
			classSelectorMethods = new HashSet< Method >();
			Set< ClassInfo > allClassInfos = getAllClassesInClassPath();
			
			for ( ClassInfo classInfo : allClassInfos )
			{
				try
				{
					Class< ? > aClass = classInfo.load();
					
					for ( Method method : aClass.getDeclaredMethods() )
					{
						for ( Annotation annotation : method.getDeclaredAnnotations() )
						{
							if ( annotation instanceof ClassSelector )
							{
								classSelectorMethods.add( method );
								break;
							}
						}
					}
				}
				catch ( LinkageError e )
				{}
			}
		}
		
		return classSelectorMethods;
	}
}
