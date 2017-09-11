package rendering;

import java.lang.Math;

/**
 * A simple linear-algebra class for a 3d vector
 * @author alexaulabaugh
 *
 */

public class TripletVector
{
	private double[] components;
	
	/**
	 * Constructor with three components
	 * @param x
	 * @param y
	 * @param z
	 */
	public TripletVector(double x, double y, double z)
	{
		components = new double[3];
		components[0] = x;
		components[1] = y;
		components[2] = z;
	}
	
	/**
	 * Constructor from array
	 * @param comp
	 */
	public TripletVector(double[] comp)
	{
		components = new double[3];
		components[0] = comp[0];
		components[1] = comp[1];
		components[2] = comp[2];
	}
	
	/**
	 * Copy constructor
	 * @param other_vector
	 */
	public TripletVector(TripletVector other_vector)
	{
		components = new double[3];
		double[] otherComponents = other_vector.getComponents();
		components[0] = otherComponents[0];
		components[1] = otherComponents[1];
		components[2] = otherComponents[2];
	}
	
	/**
	 * Accesses the xyz components
	 * @return array of components
	 */
	public double[] getComponents()
	{
		return components;
	}
	
	/**
	 * Gets the length of the vector
	 * @return double length
	 */
	public double getLength()
	{
		return Math.sqrt(Math.pow(components[0], 2) + Math.pow(components[1], 2) + Math.pow(components[2], 2));
	}
	
	/**
	 * Normalizes this vector by its length
	 */
	public void normalize()
	{
		double length = getLength();
		components[0] = components[0]/length;
		components[1] = components[1]/length;
		components[2] = components[2]/length;
	}
	
	/**
	 * Adds another vector to this vector
	 * @param other_vector
	 * @return TripletVector the sum
	 */
	public TripletVector add(TripletVector other_vector)
	{
		double[] otherComponents = other_vector.getComponents();
		double x = components[0] + otherComponents[0];
		double y = components[1] + otherComponents[1];
		double z = components[2] + otherComponents[2];
		return new TripletVector(x, y , z);
	}
	
	public TripletVector add(double scalar)
	{
		return new TripletVector(components[0] + scalar, components[1] + scalar, components[2] + scalar);
	}
	
	/**
	 * Subtracts another vector from this vector
	 * @param other_vector
	 * @return TripletVector the difference between the vectors
	 */
	public TripletVector sub(TripletVector other_vector)
	{
		double[] otherComponents = other_vector.getComponents();
		double x = components[0] - otherComponents[0];
		double y = components[1] - otherComponents[1];
		double z = components[2] - otherComponents[2];
		return new TripletVector(x, y , z);
	}
	
	public TripletVector sub(double scalar)
	{
		return new TripletVector(components[0] - scalar, components[1] - scalar, components[2] - scalar);
	}
	
	/**
	 * Scales the vector by a scalar
	 * @param scalar
	 * @return scaled vector
	 */
	public TripletVector scale(double scalar)
	{
		double x = components[0] * scalar;
		double y = components[1] * scalar;
		double z = components[2] * scalar;
		return new TripletVector(x, y , z);
	}
	
	/**
	 * Scales the vector by multiple scalars
	 * @param other_vector the scalars
	 * @return scaled vector
	 */
	public TripletVector vectorScale(TripletVector other_vector)
	{
		double[] otherComponents = other_vector.getComponents();
		TripletVector scaled = new TripletVector(components[0]*otherComponents[0], components[1]*otherComponents[1], components[2]*otherComponents[2]);
		return scaled;
	}
	
	/**
	 * Computes the dot product of this vector with another
	 * @param other_vector
	 * @return double dot product
	 */
	public double dot(TripletVector other_vector)
	{
		double dot = 0;
		double[] otherComponents = other_vector.getComponents();
		dot += components[0] * otherComponents[0];
		dot += components[1] * otherComponents[1];
		dot += components[2] * otherComponents[2];
		return dot;
	}
	
	/**
	 * Computes the cross product of this vector with another
	 * @param other_vector
	 * @return TripletVector cross product
	 */
	public TripletVector cross(TripletVector other_vector)
	{
		double[] otherComponents = other_vector.getComponents();
		double x = components[1]*otherComponents[2] - components[2]*otherComponents[1];
		double y = components[2]*otherComponents[0] - components[0]*otherComponents[2];
		double z = components[0]*otherComponents[1] - components[1]*otherComponents[0];
		return new TripletVector(x, y , z);
	}
	
	/**
	 * A useful tool for debugging
	 */
	public void print()
	{
		System.out.println("TRIPLET VECTOR<" + components[0] + "," + components[1] + "," + components[2] + ">");
	}
}
