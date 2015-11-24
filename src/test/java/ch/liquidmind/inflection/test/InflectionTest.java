package ch.liquidmind.inflection.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Test;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.proxy.Proxy;
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
	@Test
	public void testPrinter()
	{
		String[] taxonomyNames = new String[] { "FullTaxonomy", "UseCase1", "UseCase2", "UseCase3", "UseCase4" };
		
		for ( String taxonomyName : taxonomyNames )
		{
			Taxonomy taxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( "ch.liquidmind.inflection.test.model." + taxonomyName );
			InflectionPrinter printer = new InflectionPrinter();
			printer.printTaxonomy( taxonomy );
			System.out.println();
		}
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
		
		FullTaxonomy_Person fullTaxonomyPerson = Proxy.getProxy( "ch.liquidmind.inflection.test.model.FullTaxonomy", person );
		UseCase1_Person useCase1Person = Proxy.getProxy( "ch.liquidmind.inflection.test.model.UseCase1", person );
		UseCase2_Person useCase2Person = Proxy.getProxy( "ch.liquidmind.inflection.test.model.UseCase2", person );
		UseCase3_Person useCase3Person = Proxy.getProxy( "ch.liquidmind.inflection.test.model.UseCase3", person );
		UseCase4_Address useCase4Address = Proxy.getProxy( "ch.liquidmind.inflection.test.model.UseCase4", address );
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		
		System.out.println( "FullTaxonomy_Person:" );
		System.out.println( writer.writeValueAsString( fullTaxonomyPerson ) );
		System.out.println();
		System.out.println( "UseCase1_Person:" );
		System.out.println( writer.writeValueAsString( useCase1Person ) );
		System.out.println();
		System.out.println( "UseCase2_Person:" );
		System.out.println( writer.writeValueAsString( useCase2Person ) );
		System.out.println();
		System.out.println( "UseCase3_Person:" );
		System.out.println( writer.writeValueAsString( useCase3Person ) );
		System.out.println();
		System.out.println( "UseCase4_Address:" );
		System.out.println( writer.writeValueAsString( useCase4Address ) );
		System.out.println();
	}
}
