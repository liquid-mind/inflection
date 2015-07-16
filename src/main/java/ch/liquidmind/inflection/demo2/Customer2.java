package ch.liquidmind.inflection.demo2;

import java.util.ArrayList;
import java.util.List;

public class Customer2
{
	private String firstName;
	private String lastName;
	private int age;
	private Gender gender;
	private List< Address > addresses = new ArrayList< Address >();
	private List< Account > accounts = new ArrayList< Account >();
	
	public Customer2()
	{}
	
	public Customer2( String firstName, String lastName, int age, Gender gender, List< Address > addresses, List< Account > accounts )
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

	public int getAge()
	{
		return age;
	}

	public void setAge( int age )
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
