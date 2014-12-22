package ch.zhaw.inflection.demo;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings( "rawtypes" )
public class A< T, V >
{
	public B b;
	public B[] b2s;
	public B[][] b3s;
	public T[] b4s;
	public List< B > b5s;
	public List< Set< B > > b6s;
	public Map< String, B > b7s;
	public List b8s;
	public List< ? > b9s;
	public List< V > b10s;	// Due to type erasure, the actual type parameter for V is unknown at runtime.
	public Object b11s;	// Assigned a collection/map/array at runtime.
	public Object[] b12s;
	public B b13;
}
