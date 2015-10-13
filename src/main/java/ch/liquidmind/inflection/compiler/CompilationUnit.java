package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class CompilationUnit
{
	public static class CompilationUnitRaw
	{
		private File sourceFile;

		public CompilationUnitRaw( File sourceFile )
		{
			super();
			this.sourceFile = sourceFile;
		}

		public File getSourceFile()
		{
			return sourceFile;
		}

		public void setSourceFile( File sourceFile )
		{
			this.sourceFile = sourceFile;
		}
	}

	public static class CompilationUnitParsed
	{
		private ParseTree parseTree;
		private CommonTokenStream tokens;
		private String[] sourceFileContent;
		
		public CompilationUnitParsed()
		{
			super();
		}

		public ParseTree getParseTree()
		{
			return parseTree;
		}

		public void setParseTree( ParseTree parseTree )
		{
			this.parseTree = parseTree;
		}

		public CommonTokenStream getTokens()
		{
			return tokens;
		}

		public void setTokens( CommonTokenStream tokens )
		{
			this.tokens = tokens;
		}

		public String[] getSourceFileContent()
		{
			return sourceFileContent;
		}

		public void setSourceFileContent( String[] sourceFileContent )
		{
			this.sourceFileContent = sourceFileContent;
		}
	}
	
	public static class CompilationUnitCompiled
	{
		public static class ClassesWithCommonSimpleName
		{
			private List< String > classes = new ArrayList< String >();

			public List< String > getClasses()
			{
				return classes;
			}
		}
		
		private String packageName;
		private Map< String, ClassesWithCommonSimpleName > importedClasses = new HashMap< String, ClassesWithCommonSimpleName >();
		private List< TaxonomyCompiled > taxonomiesCompiled = new ArrayList< TaxonomyCompiled >();
		
		public CompilationUnitCompiled()
		{
			super();
		}

		public String getPackageName()
		{
			return packageName;
		}

		public void setPackageName( String packageName )
		{
			this.packageName = packageName;
		}
		
		public Map< String, ClassesWithCommonSimpleName > getImportedClasses()
		{
			return importedClasses;
		}

		public List< TaxonomyCompiled > getTaxonomiesCompiled()
		{
			return taxonomiesCompiled;
		}
	}
	
	private CompilationUnitRaw compilationUnitRaw;
	private CompilationUnitParsed compilationUnitParsed;
	private CompilationUnitCompiled compilationUnitCompiled;
	
	// Note that compilation errors and warnings may be generated during
	// parsing (syntax error) or during compilation (grammar error), which
	// is why compilationFaults belong here rather than in CompilationUnitParsed
	// or CompilationUnitCompiled.
	private List< CompilationFault > compilationFaults = new ArrayList< CompilationFault >();
	
	public CompilationUnit( File sourceFile )
	{
		super();
		compilationUnitRaw = new CompilationUnitRaw( sourceFile );
		compilationUnitParsed = new CompilationUnitParsed();
		compilationUnitCompiled = new CompilationUnitCompiled();
	}

	public CompilationUnitRaw getCompilationUnitRaw()
	{
		return compilationUnitRaw;
	}

	public CompilationUnitParsed getCompilationUnitParsed()
	{
		return compilationUnitParsed;
	}

	public CompilationUnitCompiled getCompilationUnitCompiled()
	{
		return compilationUnitCompiled;
	}

	public List< CompilationFault > getCompilationFaults()
	{
		return compilationFaults;
	}
}
