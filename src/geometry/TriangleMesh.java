package geometry;

/**
 * A class for reading in and positioning a mesh of triangles from a .obj file.
 * @author alexaulabaugh
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

import rendering.TripletVector;

public class TriangleMesh
{
	private ArrayList<Triangle> triangles;
	private TripletVector center;
	private TripletVector color;
	private double specular;
	private double reflectivity;
	private double glossyExponent;
	private double refractivity;
	private double refractionIndex;
	
	/**
	 * Initializes all values
	 * @param filename
	 * @param cent the center of the mesh
	 * @param scalar
	 * @param col the default color of the triangles
	 * @param spec the default specular component of the triangles
	 * @param refl the default reflectivity fraction of the triangles
	 * @param gloss the exponent for glossy reflection
	 * @param refrct the refractivity fraction
	 * @param refrctInd the index of refraction
	 * @param stretchFactors how much to stretch along each axis
	 * @param rotationFactors the spherical variables to rotate by 
	 */
	public TriangleMesh(String filename, TripletVector cent, double scalar, TripletVector col, double spec, double refl, double gloss, double refrct, double refrctInd, double[] stretchFactors, double[] rotationFactors)
	{
		center = cent;
		color = col;
		specular = spec;
		reflectivity = refl;
		glossyExponent = gloss;
		refractivity = refrct;
		refractionIndex = refrctInd;
		triangles = new ArrayList<Triangle>();
		readFile(filename);
		rotate(rotationFactors[0], rotationFactors[1]);
		stretch(0, stretchFactors[0]);
		stretch(1, stretchFactors[1]);
		stretch(2, stretchFactors[2]);
		scale(scalar);
		translate(center);
	}
	
	/**
	 * Generates the mesh given the params as a one line string
	 * @param paramString
	 */
	public TriangleMesh(String paramString)
	{
		String[] params = paramString.split(" ");
		String filename = params[0];
		String[] centerString = params[1].split(",");
		center = new TripletVector(Double.parseDouble(centerString[0]), Double.parseDouble(centerString[1]), Double.parseDouble(centerString[2]));
		double scalar = Double.parseDouble(params[2]);
		String[] stretchFactors = params[3].split(",");
		String[] rotateFactors = params[4].split(",");
		String[] colString = params[5].split(",");
		color = new TripletVector(Double.parseDouble(colString[0]), Double.parseDouble(colString[1]), Double.parseDouble(colString[2]));
		specular = Double.parseDouble(params[6]);
		reflectivity = Double.parseDouble(params[7]);
		glossyExponent = Double.parseDouble(params[8]);
		refractivity = Double.parseDouble(params[9]);
		refractionIndex = Double.parseDouble(params[10]);
		triangles = new ArrayList<Triangle>();
		readFile(filename);
		rotate(Double.parseDouble(rotateFactors[0]), Double.parseDouble(rotateFactors[1]));
		for(int i = 0; i < 3; i++)
		{
			stretch(i, Double.parseDouble(stretchFactors[i]));
		}
		scale(scalar);
		translate(center);
	}
	
	/**
	 * Helper function for computing exponents
	 * @param exp string representation of an exponent
	 * @return
	 */
	private double convertExp(String exp)
	{
	    String[] comp = exp.split("e");
	    double base = Double.parseDouble(comp[0]);
	    double exponent = Double.parseDouble(comp[1]);
	    double ten = Math.pow(10.0, exponent);
	    return base*ten;
	}
	
	/**
	 * Reads in an obj file and populates the triangles structure with triangles
	 * @param filename
	 */
	private void readFile(String filename)
	{
		ArrayList<TripletVector> points = new ArrayList<TripletVector>();
		try
		{
			File sceneFile = new File(filename);
			Scanner objFileScanner = new Scanner(sceneFile);
			objFileScanner.nextLine(); //header
			objFileScanner.nextLine(); //vertex count
			while(objFileScanner.hasNextLine())
			{
				String[] lineTokens = objFileScanner.nextLine().split(" ");
				if(lineTokens[0].equals("v"))
				{
					TripletVector newPoint = new TripletVector(convertExp(lineTokens[1]), convertExp(lineTokens[2]), convertExp(lineTokens[3]));
					points.add(newPoint);
				}
				else if(lineTokens[0].equals("f"))
				{
					TripletVector[] vertices = new TripletVector[3];
					
					vertices[1] = new TripletVector(points.get(Integer.parseInt(lineTokens[1])-1));
					vertices[0] = new TripletVector(points.get(Integer.parseInt(lineTokens[2])-1));
					vertices[2] = new TripletVector(points.get(Integer.parseInt(lineTokens[3])-1));
					
					Triangle meshTriangle = new Triangle(vertices, color, specular, reflectivity, glossyExponent, refractivity, refractionIndex);
					triangles.add(meshTriangle);
				}
			}
			objFileScanner.close();
		}
		catch(Exception e)
		{
			System.out.println("Error, could not load object file: " + filename);
		}
	}
	
	/**
	 * Translate every triangle
	 * @param translation
	 */
	public void translate(TripletVector translation)
	{
		for(Triangle tri : triangles)
		{
			tri.translate(translation);
		}
	}
	
	/**
	 * Scales every triangle
	 * @param factor
	 */
	public void scale(double factor)
	{
		for(Triangle tri : triangles)
		{
			tri.scale(factor);
		}
	}
	
	/**
	 * Scales every triangle along an axis
	 * @param index the axis to scale along
	 * @param factor the factor to scale by
	 */
	public void stretch(int index, double factor)
	{
		for(Triangle tri : triangles)
		{
			tri.stretch(index,  factor);
		}
	}
	
	/**
	 * Rotates every triangle by spherical coordinates theta and pi
	 * @param theta [0, 2pi]
	 * @param phi [0, pi]
	 */
	public void rotate(double theta, double phi)
	{
		for(Triangle tri : triangles)
		{
			tri.rotate(theta, phi);
		}
	}
	
	/**
	 * Returns all triangles
	 * @return
	 */
	public ArrayList<Triangle> getTriangles()
	{
		return triangles;
	}
}
