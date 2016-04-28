package ch.liquidmind.inflection.test;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Ignore;
import org.junit.Test;

import ch.liquidmind.inflection.Inflection;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.test.model.Address;
import ch.liquidmind.inflection.test.model.Gender;
import ch.liquidmind.inflection.test.model.Person;
import ch.liquidmind.inflection.test.model.FullTaxonomy.ch.liquidmind.inflection.test.model.FullTaxonomy_Person;
import ch.liquidmind.inflection.test.model.UseCase1.ch.liquidmind.inflection.test.model.UseCase1_Person;
import ch.liquidmind.inflection.test.model.UseCase2.ch.liquidmind.inflection.test.model.UseCase2_Person;
import ch.liquidmind.inflection.test.model.UseCase3.ch.liquidmind.inflection.test.model.UseCase3_Person;
import ch.liquidmind.inflection.test.model.UseCase4.ch.liquidmind.inflection.test.model.UseCase4_Address;
import ch.liquidmind.inflection.util.InflectionPrinter;

public class InflectionTest
{
	private static PrintStream printStream = System.out;
	
	@Test
	public void testPrinterMulti() throws Throwable
	{
		long before = System.currentTimeMillis();
		
		for ( int i = 0 ; i < 1 ; ++i )
			testPrinter();

		long after = System.currentTimeMillis();
		long delta = after - before;
		
		System.out.println( "testPrinterMulti(): " + delta + "ms" );
	}

	public void testPrinter()
	{
		String[] taxonomyNames = new String[] { "FullTaxonomy", "UseCase1", "UseCase2", "UseCase3", "UseCase4" };
		
		for ( String taxonomyName : taxonomyNames )
		{
			Taxonomy taxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( "ch.liquidmind.inflection.test.model." + taxonomyName );
			InflectionPrinter printer = new InflectionPrinter( printStream, true, true );
			printer.printTaxonomy( taxonomy );
			printStream.println();
		}
	}
	
	@Ignore
	@Test
	public void testProxiesMulti() throws Throwable
	{
		long before = System.currentTimeMillis();
		
		for ( int i = 0 ; i < 1 ; ++i )
			testProxies();

		long after = System.currentTimeMillis();
		long delta = after - before;
		
		System.out.println( "testProxiesMulti(): " + delta + "ms" );
	}

	@Test
	public void testProxies() throws Throwable
	{
		Calendar cal = new GregorianCalendar();
		cal.set( 1972, 8, 8 );
		Person person = new Person( 42, "John", "Brush", "Mr.", "+41 79 235 17 56", "+41 79 235 17 56", "jebrush@gmail.com", Gender.MALE, cal.getTime() );
		Address address = new Address( 43, "Feldgüetliweg 82", "Feldmeilen", "8706", "Switzerland" );
		person.getAddresses().add( address );
		address.getPeople().add( person );
		person.setProfileImage( new byte[] { 0x4b, 0x69, 0x6c, 0x72, 0x6f, 0x79, 0x20, 0x77, 0x61, 0x73, 0x20, 0x68, 0x65, 0x72, 0x65 } );
		
		FullTaxonomy_Person fullTaxonomyPerson = Inflection.cast( "ch.liquidmind.inflection.test.model.FullTaxonomy", person );
		UseCase1_Person useCase1Person = Inflection.cast( "ch.liquidmind.inflection.test.model.UseCase1", person );
		UseCase2_Person useCase2Person = Inflection.cast( "ch.liquidmind.inflection.test.model.UseCase2", person );
		UseCase3_Person useCase3Person = Inflection.cast( "ch.liquidmind.inflection.test.model.UseCase3", person );
		UseCase4_Address useCase4Address = Inflection.cast( "ch.liquidmind.inflection.test.model.UseCase4", address );
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		ObjectReader reader = mapper.reader().withType( FullTaxonomy_Person.class );
		
		reader.readValue( writer.writeValueAsString( fullTaxonomyPerson ) );
		
		printStream.println( "FullTaxonomy_Person:" );
		printStream.println( writer.writeValueAsString( fullTaxonomyPerson ) );
		printStream.println();
		printStream.println( "UseCase1_Person:" );
		printStream.println( writer.writeValueAsString( useCase1Person ) );
		printStream.println();
		printStream.println( "UseCase2_Person:" );
		printStream.println( writer.writeValueAsString( useCase2Person ) );
		printStream.println();
		printStream.println( "UseCase3_Person:" );
		printStream.println( writer.writeValueAsString( useCase3Person ) );
		printStream.println();
		printStream.println( "UseCase4_Address:" );
		printStream.println( writer.writeValueAsString( useCase4Address ) );
		printStream.println();
	}
}
