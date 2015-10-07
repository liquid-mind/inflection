package ch.liquidmind.inflection.compiler;

import java.io.File;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class CompilationUnit
{
	private File file;
	private CommonTokenStream tokens;
	private ParseTree tree;
	private String packageName;
	
	public CompilationUnit( File file )
	{
		super();
		this.file = file;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile( File file )
	{
		this.file = file;
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

