package ch.liquidmind.inflection.test.model;

import java.util.ArrayList;
import java.util.List;

public class Address extends IdentifiableObject
{
	private String street, city, zip, country;
	private List< Person > people = new ArrayList< Person >();
	
	public String getStreet()
	{
		return street;
	}

	public void setStreet( String street )
	{
		this.street = street;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity( String city )
	{
		this.city = city;
	}

	public String getZip()
	{
		return zip;
	}

	public void setZip( String zip )
	{
		this.zip = zip;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry( String country )
	{
		this.country = country;
	}

	public List< Person > getPeople()
	{
		return people;
	}
}
