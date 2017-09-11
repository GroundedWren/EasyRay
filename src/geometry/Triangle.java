package geometry;

import java.util.ArrayList;

import rendering.Ray;
import rendering.TripletVector;

/**
 * A triangle is a 2d monochrome object in the scene
 * @author alexaulabaugh
 *
 */

public class Triangle extends GeometricObject
{
	private TripletVector[] vertices;
	private TripletVector normal;
	
	/**
	 * The default constructor for a triangle
	 * @param vert an array of the three endpoints of the triangle
	 * @param col the default color
	 * @param spec the specular component
	 */
	public Triangle(TripletVector[] vert, TripletVector col, double spec, double refl, double gloss, double refrct, double refrctInd)
	{
		vertices = new TripletVector[3];
		vertices[0] = vert[0];
		vertices[1] = vert[1];
		vertices[2] = vert[2];
		
		normal = vertices[1].sub(vertices[0]).cross(vertices[2].sub(vertices[1]));
		normal.normalize();
		defaultColor = col.scale(1/255.0);
		specular = spec;
		reflectivity = refl;
		glossyExponent = gloss;
		refractivity = refrct;
		refractionIndex = refrctInd;
		emitter = false;
		
		updateBounds();
	}
	
	/**
	 * The constructor for a triangle from a line in a scene file
	 * @param paramString the necessary parameters represented as a string
	 */
	public Triangle(String paramString)
	{
		String[] params = paramString.split(" ");
		String[] vert0Str = params[0].split(",");
		TripletVector vert0 = new TripletVector(Double.parseDouble(vert0Str[0]), Double.parseDouble(vert0Str[1]), Double.parseDouble(vert0Str[2]));
		String[] vert1Str = params[1].split(",");
		TripletVector vert1 = new TripletVector(Double.parseDouble(vert1Str[0]), Double.parseDouble(vert1Str[1]), Double.parseDouble(vert1Str[2]));
		String[] vert2Str = params[2].split(",");
		TripletVector vert2 = new TripletVector(Double.parseDouble(vert2Str[0]), Double.parseDouble(vert2Str[1]), Double.parseDouble(vert2Str[2]));
		String[] colStr = params[3].split(",");
		vertices = new TripletVector[3];
		vertices[0] = vert0;
		vertices[1] = vert1;
		vertices[2] = vert2;
		
		normal = vertices[1].sub(vertices[0]).cross(vertices[2].sub(vertices[1]));
		normal.normalize();
		
		defaultColor = new TripletVector(Double.parseDouble(colStr[0])/255.0, Double.parseDouble(colStr[1])/255.0, Double.parseDouble(colStr[2])/255.0);
		
		specular = Double.parseDouble(params[4]);
		reflectivity = Double.parseDouble(params[5]);
		glossyExponent = Double.parseDouble(params[6]);
		refractivity = Double.parseDouble(params[7]);
		refractionIndex = Double.parseDouble(params[8]);
		
		emitter = false;
		
		updateBounds();
	}
	
	/**
	 * A helper function for keeping the bounding box up to date
	 */
	private void updateBounds()
	{
		double[] minBoundComponents = vertices[0].getComponents().clone();
		double[] maxBoundComponents = vertices[0].getComponents().clone();
		
		for(int i = 1; i < 3; i++)
		{
			double[] components = vertices[i].getComponents();
			for(int j = 0; j < 3; j++)
			{
				if(minBoundComponents[j] > components[j])
				{
					minBoundComponents[j] = components[j];
				}
				if(maxBoundComponents[j] < components[j])
				{
					maxBoundComponents[j] = components[j];
				}
			}
		}
		minBound = new TripletVector(minBoundComponents);
		maxBound = new TripletVector(maxBoundComponents);
	}
	
	/**
	 * Simply scales all vertices
	 * @param scalar
	 */
	public void scale(double scalar)
	{
		vertices[0] = vertices[0].scale(scalar);
		vertices[1] = vertices[1].scale(scalar);
		vertices[2] = vertices[2].scale(scalar);
		updateBounds();
	}
	
	/**
	 * Translates all vertices
	 * @param transition
	 */
	public void translate(TripletVector transition)
	{
		vertices[0] = vertices[0].add(transition);
		vertices[1] = vertices[1].add(transition);
		vertices[2] = vertices[2].add(transition);
		updateBounds();
	}
	
	/**
	 * Stretches the triangle's vertices in one direction
	 * @param index the dimension to stretch
	 * @param scalar the stretching factor
	 */
	public void stretch(int index, double scalar)
	{
		for(int i = 0; i < 3; i++)
		{
			double[] comp = vertices[i].getComponents();
			comp[index] = comp[index]*scalar;
			vertices[i] = new TripletVector(comp);
		}
		normal = vertices[1].sub(vertices[0]).cross(vertices[2].sub(vertices[1]));
		normal.normalize();
		updateBounds();
	}
	
	/**
	 * Rotates the triangle about the origin by theta and phi spherical degrees
	 * CITATION: referenced from http://www.learningaboutelectronics.com/Articles/Cartesian-rectangular-to-spherical-coordinate-converter-calculator.php
	 * @param theta spherical coordinates variable [0, 2pi]
	 * @param phi spherical coordinates variable [0, pi]
	 */
	public void rotate(double theta, double phi)
	{
		for(int i = 0; i < 3; i++)
		{
			double[] comp = vertices[i].getComponents();
			double radius = vertices[i].getLength();
			double myTheta = Math.acos(comp[2]/radius);
			double myPhi = Math.atan(comp[1]/comp[0]);
			comp[0] = -1*radius*Math.sin(myTheta+theta)*Math.cos(myPhi+phi);
			comp[1] = -1*radius*Math.sin(myTheta+theta)*Math.sin(myPhi+phi);
			comp[2] = radius*Math.cos(myTheta+theta);
			if(myTheta > 0 && myPhi > 0)
			{
				comp[0] = comp[0]*-1;
				comp[1] = comp[1]*-1;
			}
			vertices[i] = new TripletVector(comp);
		}
		normal = vertices[1].sub(vertices[0]).cross(vertices[2].sub(vertices[1]));
		normal.normalize();
		updateBounds();
	}
	
	/**
	 * CITATION: CS410 lecture slides, Ray Tracing from the Ground up
	 * Computes the signed area for a triangle with the provided three endpoints
	 * @param vert0
	 * @param vert1
	 * @param vert2
	 * @return signed area of triangle
	 */
	private double computeArea(TripletVector vert0, TripletVector vert1, TripletVector vert2)
	{
		TripletVector crossp = vert1.sub(vert0).cross(vert2.sub(vert1));
		double sign = 1;
		double[] normalComp = normal.getComponents();
		double[] crosspComp = crossp.getComponents();
		for(int index = 0; index < 3; index++)
		{
			if((normalComp[index] > 0 && crosspComp[index] < 0) || (normalComp[index] < 0 && crosspComp[index] > 0))
			{
				sign = -1;
			}
		}
		return crossp.getLength()*0.5*sign;
	}

	/**
	 * CITATION: CS410 Slides, Ray Tracing from the Ground Up
	 */
	@Override
	public ArrayList<Double> intersect(Ray intersectRay)
	{
		ArrayList<Double> result = new ArrayList<Double>();
		TripletVector dir = intersectRay.getDirection();
		TripletVector org = intersectRay.getOrigin();
		
		if(dir.dot(normal) == 0)
			return result;
		
		double t = (vertices[0].sub(org).dot(normal))/(dir.dot(normal));
		
		if(t <= 0)
			return result;
				
		TripletVector p = org.add(dir.scale(t));
		
		double a0 = computeArea(vertices[0], vertices[1], p);
	    double a1 = computeArea(vertices[1], vertices[2], p);
	    double a2 = computeArea(vertices[2], vertices[0], p);
	    
	    double triArea = computeArea(vertices[0], vertices[1], vertices[2]);
	    
	    if(a0/triArea > 1 || a0/triArea < 0)
	        return result;
	    if(a1/triArea > 1 || a1/triArea < 0)
	        return result;
	    if(a2/triArea > 1 || a2/triArea < 0)
	        return result;
	    
		result.add(t);
		
		return result;
	}
	
	@Override
	public TripletVector getColor(TripletVector point)
	{
		return defaultColor;
	}

	@Override
	public TripletVector getNormal(TripletVector point)
	{
		return normal;
	}
	
	@Override
	public ArrayList<Ray> getSampleRays(TripletVector origin, String sampleType, int sampleSize)
	{
		ArrayList<Ray> sampleRays = new ArrayList<Ray>();
		//Not implemented
		return sampleRays;
	}
	
	/**
	 * A useful debugging function
	 */
	public String toString()
	{
		String toRet = vertices[0].getComponents()[0] + " " + vertices[0].getComponents()[1] + " " + vertices[0].getComponents()[2] + "\n";
		toRet = toRet + vertices[1].getComponents()[0] + " " + vertices[1].getComponents()[1] + " " + vertices[1].getComponents()[2] + "\n";
		toRet = toRet + vertices[2].getComponents()[0] + " " + vertices[2].getComponents()[1] + " " + vertices[2].getComponents()[2];
		return toRet;
	}

}
