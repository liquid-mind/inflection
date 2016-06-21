package ch.liquidmind.inflection.print;

import java.io.PrintStream;
import java.io.Writer;

public abstract class AbstractPrinter
{
	public static final PrintStream DEFAULT_PRINT_STREAM = System.out;
	public static final boolean DEFAULT_SHOW_SIMPLE_NAMES = true;

	private IndentingPrintWriter printWriter;
	private boolean showSimpleNames;

	public AbstractPrinter()
	{
		super();
		this.printWriter = new IndentingPrintWriter( DEFAULT_PRINT_STREAM );
		this.showSimpleNames = DEFAULT_SHOW_SIMPLE_NAMES;
	}

	public AbstractPrinter( Writer writer )
	{
		super();
		this.printWriter = new IndentingPrintWriter( writer );
		this.showSimpleNames = DEFAULT_SHOW_SIMPLE_NAMES;
	}

	public AbstractPrinter( PrintStream printStream )
	{
		super();
		this.printWriter = new IndentingPrintWriter( printStream );
		this.showSimpleNames = DEFAULT_SHOW_SIMPLE_NAMES;
	}

	public AbstractPrinter( boolean showSimpleNames )
	{
		super();
		this.printWriter = new IndentingPrintWriter( DEFAULT_PRINT_STREAM );
		this.showSimpleNames = DEFAULT_SHOW_SIMPLE_NAMES;
	}

	public AbstractPrinter( Writer writer, boolean showSimpleNames )
	{
		super();
		this.showSimpleNames = showSimpleNames;
	}

	public AbstractPrinter( PrintStream printStream, boolean showSimpleNames )
	{
		super();
		this.showSimpleNames = showSimpleNames;
	}

	protected IndentingPrintWriter getPrintWriter()
	{
		return printWriter;
	}

	protected boolean getShowSimpleNames()
	{
		return showSimpleNames;
	}

	// TODO: replace this with a TypeWalker that can handle generic parameters.
	protected String getTypeName( String fqTypeName )
	{
		String typeName;
		
		if ( showSimpleNames )
			typeName = ( fqTypeName.contains( "." ) ? fqTypeName.substring( fqTypeName.lastIndexOf( "." ) + 1 ) : fqTypeName );
		else
			typeName = fqTypeName;
		
		return typeName;
	}
}
