package ch.liquidmind.inflection.compiler;

import java.io.File;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParsedCompilationUnit
{
	private File compilationUnit;
	private CommonTokenStream tokens;
	private ParseTree tree;
	private String packageName;
	
	public File getCompilationUnit()
	{
		return compilationUnit;
	}

	public void setCompilationUnit( File compilationUnit )
	{
		this.compilationUnit = compilationUnit;
	}

	public CommonTokenStream getTokens()
	{
		return tokens;
	}
	
	public void setTokens( CommonTokenStream tokens )
	{
		this.tokens = tokens;
	}
	
	public ParseTree getTree()
	{
		return tree;
	}
	
	public void setTree( ParseTree tree )
	{
		this.tree = tree;
	}
	
	public String getPackageName()
	{
		return packageName;
	}
	
	public void setPackageName( String packageName )
	{
		this.packageName = packageName;
	}
}

