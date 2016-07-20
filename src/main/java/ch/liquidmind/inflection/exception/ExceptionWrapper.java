package ch.liquidmind.inflection.exception;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectWriter;

import com.google.common.reflect.ClassPath;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import __java.io.__IOException;
import __java.lang.__IllegalAccessException;
import __java.lang.__NoSuchMethodException;
import __java.lang.reflect.__InvocationTargetException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

// TODO Replace with deflected libraries.
public class ExceptionWrapper
{
	public static ClassPath ClassPath_from( ClassLoader classLoader )
	{
		try
		{
			return ClassPath.from( classLoader );
		}
		catch ( IOException e )
		{
			throw new __IOException( e );
		}
	}
	
	public static Object PropertyUtils_getProperty( Object bean, String name )
	{
		try
		{
			return PropertyUtils.getProperty( bean, name );
		}
		catch ( NoSuchMethodException e )
		{
			throw new __NoSuchMethodException( e );
		}
		catch ( InvocationTargetException e )
		{
			throw new __InvocationTargetException( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new __IllegalAccessException( e );
		}
	}
	
	public static void PropertyUtils_setProperty( Object bean, String name, Object value )
	{
		try
		{
			PropertyUtils.setProperty( bean, name, value );
		}
		catch ( NoSuchMethodException e )
		{
			throw new __NoSuchMethodException( e );
		}
		catch ( InvocationTargetException e )
		{
			throw new __InvocationTargetException( e );
		}
		catch ( IllegalAccessException e )
		{
			throw new __IllegalAccessException( e );
		}
	}
	
	public static String ObjectWriter_writeValueAsString( ObjectWriter writer, Object value )
	{
		try
		{
			return writer.writeValueAsString( value );
		}
		catch ( JsonGenerationException e )
		{
			throw new RuntimeException( e );
		}
		catch ( JsonMappingException e )
		{
			throw new RuntimeException( e );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static VirtualMachine VirtualMachine_attach( String id )
	{
		try
		{
			return VirtualMachine.attach( id );
		}
		catch ( AttachNotSupportedException e )
		{
			throw new RuntimeException( e );
		}
		catch ( IOException e )
		{
			throw new __IOException( e );
		}
	}
	
	public static void VirtualMachine_loadAgent( VirtualMachine vm, String agent )
	{
		try
		{
			vm.loadAgent( agent );
		}
		catch ( IOException e )
		{
			throw new __IOException( e );
		}
		catch ( AgentLoadException e )
		{
			throw new RuntimeException( e );
		}
		catch ( AgentInitializationException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static void VirtualMachine_detach( VirtualMachine vm )
	{
		try
		{
			vm.detach();
		}
		catch ( IOException e )
		{
			throw new __IOException( e );
		}
	}
	
	public static CtClass ClassPool_get( ClassPool cp, String classname )
	{
		try
		{
			return cp.get( classname );
		}
		catch ( NotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}
	
	public static CtClass CtClass_getSuperclass( CtClass ctClass )
	{
		try
		{
			return ctClass.getSuperclass();
		}
		catch ( NotFoundException e )
		{
			throw new RuntimeException( e );
		}
	}
}
