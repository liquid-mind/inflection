package ch.liquidmind.inflection.demo2;

import java.util.ArrayList;
import java.util.List;

public class Customer
{
	private String firstName;
	private String lastName;
	private Integer age;
	private Gender gender;
	private List< Address > addresses = new ArrayList< Address >();
	private List< Account > accounts = new ArrayList< Account >();
	
	public Customer( String firstName, String lastName, Integer age, Gender gender, List< Address > addresses, List< Account > accounts )
	{
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.gender = gender;
		this.addresses = addresses;
		this.accounts = accounts;
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

	public Gender getGender()
	{
		return gender;
	}

	public void setGender( Gender gender )
	{
		this.gender = gender;
	}

	public List< Address > getAddresses()
	{
		return addresses;
	}

	public void setAddresses( List< Address > addresses )
	{
		this.addresses = addresses;
	}

	public List< Account > getAccounts()
	{
		return accounts;
	}

	public void setAccounts( List< Account > accounts )
	{
		this.accounts = accounts;
	}
}
