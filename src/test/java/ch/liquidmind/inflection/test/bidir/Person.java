package ch.liquidmind.inflection.test.bidir;
import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.bidir.Bidirectional;

public class Person extends IdentifiableObject
{
	private String firstName;
	private String lastName;
	
	@Bidirectional( "person" )
	private List< Address > addresses = new ArrayList< Address >();
	
	public Person()
	{
		super();
	}

	public Person( String firstName, String lastName )
	{
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName( String firstName )
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName( String lastName )
	{
		this.lastName = lastName;
	}

	public List< Address > getAddresses()
	{
		return addresses;
	}

	public void setAddresses( List< Address > addresses )
	{
		this.addresses = addresses;
	}
}
