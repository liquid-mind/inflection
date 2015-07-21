package ch.liquidmind.inflection.operation.basic;

import java.io.OutputStream;

import ch.liquidmind.inflection.model.Taxonomy;
import ch.liquidmind.inflection.model.VmapInstance;
import ch.liquidmind.inflection.operation.IdentifiableObjectPair;
import ch.liquidmind.inflection.operation.LeftGraphTraverser;

public class IndentingPrintWriterTraverser extends LeftGraphTraverser
{
	private OutputStream outputStream;
	private IndentingPrintWriter printWriter;
	
	public IndentingPrintWriterTraverser( Taxonomy taxonomy, VmapInstance configurationInstance )
	{
		super( taxonomy, configurationInstance );
	}

	@Override
	public void traverse( IdentifiableObjectPair identifiableObjectPair )
	{
		this.printWriter = new IndentingPrintWriter( outputStream );
		super.traverse( identifiableObjectPair );
		printWriter.flush();
		printWriter.close();
	}

	public OutputStream getOutputStream()
	{
		return outputStream;
	}

	public void setOutputStream( OutputStream outputStream )
	{
		this.outputStream = outputStream;
	}

	public IndentingPrintWriter getPrintWriter()
	{
		return printWriter;
	}
}
