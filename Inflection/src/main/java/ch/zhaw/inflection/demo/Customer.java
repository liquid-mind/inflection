package ch.zhaw.inflection.demo;

import java.util.ArrayList;
import java.util.List;

public class Customer
{
	public enum Sex { MALE, FEMALE };
	
	private String firstName;
	private String lastName;
	private Integer age;
	private Sex sex;
	private List< Address > addresses = new ArrayList< Address >();
	
	public Customer( String firstName, String lastName, Integer age, Sex sex, Address ... addresses )
	{
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.sex = sex;
		
		for ( Address address : addresses )
			this.addresses.add( address );
	}

	public List< Address > getAddresses()
	{
		return addresses;
	}

	public void setAddresses( List< Address > addresses )
	{
		this.addresses = addresses;
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

	public Integer getAge()
	{
		return age;
	}

	public void setAge( Integer age )
	{
		this.age = age;
	}

	public Sex getSex()
	{
		return sex;
	}

	public void setSex( Sex sex )
	{
		this.sex = sex;
	}
}
