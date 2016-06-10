package ch.liquidmind.inflection.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;

public class CompilationUnit
{
	public static class CompilationUnitRaw
	{
		// TODO: should be using streams instead of files (allows for
		// other sources of source code).
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
			if ( sourceFileContent == null )
			{
				String sourceFileAsString = tokens.getTokenSource().getInputStream().toString();
				sourceFileContent = sourceFileAsString.split( System.lineSeparator() );
			}
			
			return sourceFileContent;
		}
	}
	
	public static class CompilationUnitCompiled
	{
		// Imports
		public static abstract class Import
		{
			private String name;
			private ParserRuleContext parserRuleContext;
			private boolean wasReferenced = false;
			
			public Import( String name )
			{
				super();
				this.name = name;
			}
			
			public Import( String name, ParserRuleContext parserRuleContext )
			{
				super();
				this.name = name;
				this.parserRuleContext = parserRuleContext;
			}

			public String getName()
			{
				return name;
			}

			public ParserRuleContext getParserRuleContext()
			{
				return parserRuleContext;
			}

			public boolean getWasReferenced()
			{
				return wasReferenced;
			}

			public void setWasReferenced( boolean wasReferenced )
			{
				this.wasReferenced = wasReferenced;
			}

			@Override
			public int hashCode()
			{
				final int prime = 31;
				int result = 1;
				result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
				return result;
			}

			@Override
			public boolean equals( Object obj )
			{
				if ( this == obj )
					return true;
				if ( obj == null )
					return false;
				if ( getClass() != obj.getClass() )
					return false;
				Import other = (Import)obj;
				if ( name == null )
				{
					if ( other.name != null )
						return false;
				}
				else if ( !name.equals( other.name ) )
					return false;
				return true;
			}
		}
		
		public static class TypeImport extends Import
		{
			public TypeImport( String name )
			{
				super( name );
			}

			public TypeImport( String name, ParserRuleContext parserRuleContext )
			{
				super( name, parserRuleContext );
			}
		}
		
		public static class PackageImport extends Import
		{
			public enum PackageImportType
			{
				OWN_PACKAGE,
				OTHER_PACKAGE
			}
			
			private PackageImportType type;
			
			public PackageImport( String name )
			{
				super( name );
			}
			
			public PackageImport( String name, ParserRuleContext parserRuleContext )
			{
				super( name, parserRuleContext );
			}

			public PackageImport( String name, ParserRuleContext parserRuleContext, PackageImportType type )
			{
				super( name, parserRuleContext );
				this.type = type;
			}

			public PackageImportType getType()
			{
				return type;
			}
		}
		
		public static class StaticMemberImport extends Import
		{
			public StaticMemberImport( String name )
			{
				super( name );
			}

			public StaticMemberImport( String name, ParserRuleContext parserRuleContext )
			{
				super( name, parserRuleContext );
			}
		}
		
		public static class StaticClassImport extends Import
		{
			public StaticClassImport( String name )
			{
				super( name );
			}

			public StaticClassImport( String name, ParserRuleContext parserRuleContext )
			{
				super( name, parserRuleContext );
			}
		}
		
		private String packageName;
		private Map< String, TypeImport > typeImports = new HashMap< String, TypeImport >();
		private Set< PackageImport > packageImports = new HashSet< PackageImport >();
		private Map< String, StaticMemberImport > staticMemberImports = new HashMap< String, StaticMemberImport >();
		private Set< StaticClassImport > staticClassImports = new HashSet< StaticClassImport >();
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

		public Map< String, TypeImport > getTypeImports()
		{
			return typeImports;
		}

		public Set< PackageImport > getPackageImports()
		{
			return packageImports;
		}

		public Map< String, StaticMemberImport > getStaticMemberImports()
		{
			return staticMemberImports;
		}

		public Set< StaticClassImport > getStaticClassImports()
		{
			return staticClassImports;
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
	private InflectionErrorListener errorListener = new InflectionErrorListener( this );
	private CompilationJob parentCompilationJob;
	
	public CompilationUnit( File sourceFile, CompilationJob parentCompilationJob )
	{
		super();
		compilationUnitRaw = new CompilationUnitRaw( sourceFile );
		compilationUnitParsed = new CompilationUnitParsed();
		compilationUnitCompiled = new CompilationUnitCompiled();
		this.parentCompilationJob = parentCompilationJob;
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
	
	public InflectionErrorListener getErrorListener()
	{
		return errorListener;
	}

	public CompilationJob getParentCompilationJob()
	{
		return parentCompilationJob;
	}

	public boolean hasCompilationErrors()
	{
		return containsCompilationErrors( compilationFaults );
	}
	
	public static boolean containsCompilationErrors( List< CompilationFault > compilationFaults )
	{
		boolean hasCompilationErrors = false;
		
		for ( CompilationFault compilationFault : compilationFaults )
		{
			if ( compilationFault instanceof CompilationError )
			{
				hasCompilationErrors = true;
				break;
			}
		}
		
		return hasCompilationErrors;
	}
}
