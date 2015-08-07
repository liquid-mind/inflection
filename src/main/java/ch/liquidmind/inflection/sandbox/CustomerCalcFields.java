package ch.liquidmind.inflection.sandbox;

import ch.liquidmind.inflection.demo2.Customer;

public class CustomerCalcFields
{
	public static String getFullName( Customer customer )
	{
		return customer.getFirstName() + " " + customer.getLastName();
	}
	
	// Note that we are ignoring validation and special cases for illustrative purposes.
	public static void setFullName( Customer customer, String fullName )
	{
		String[] fullNameSplit = fullName.split( " " );
		customer.setFirstName( fullNameSplit[ 0 ] );
		customer.setLastName( fullNameSplit[ 1 ] );
	}
}
