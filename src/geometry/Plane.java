package geometry;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import rendering.Ray;
import rendering.TripletVector;

/**
 * A plane is an infinite monocrhome sheet in the scene
 * @author alexaulabaugh
 *
 */
public class Plane extends GeometricObject
{
	//A point on the plane
	TripletVector coordinates;
	//The normal vector for the plane
	TripletVector normal;
	//The other possible color
	TripletVector alternateColor;
	//Direction of pattern
	TripletVector patternDirection;
	//Size of pattern
	double patternSize;
	//Pattern image
	private BufferedImage myImage;
	private String filename;
	
	/**
	 * Default constructor for a plane object
	 * @param crd a point on the plane
	 * @param norm the normal vector for the plane
	 * @param col the default color of the plane
	 * @param spec the specular component
	 */
	public Plane(TripletVector crd, TripletVector norm, TripletVector col, TripletVector col2, double spec, double refl, double gloss, double refrct, double refrctInd, TripletVector patternD, double patternS)
	{
		coordinates = crd;
		normal = norm;
		normal.normalize();
		defaultColor = col;
		alternateColor = col2;
		specular = spec;
		reflectivity = refl;
		glossyExponent = gloss;
		refractivity = refrct;
		refractionIndex = refrctInd;
		emitter = false;
		patternDirection = patternD;
		patternDirection.normalize();
		patternSize = patternS;
		
		minBound = new TripletVector(0, 0, 0);
		maxBound = new TripletVector(0, 0, 0);
	}
	
	/**
	 * Constructor from scene config line for a plane
	 * @param paramString the necessary parameters stored as a string
	 */
	public Plane(String paramString)
	{
		String[] params = paramString.split(" ");
		String [] coordsStrings = params[0].split(",");
		coordinates = new TripletVector(Double.parseDouble(coordsStrings[0]), Double.parseDouble(coordsStrings[1]), Double.parseDouble(coordsStrings[2]));
		String [] normStrings = params[1].split(",");
		normal = new TripletVector(Double.parseDouble(normStrings[0]), Double.parseDouble(normStrings[1]), Double.parseDouble(normStrings[2]));
		normal.normalize();
		String [] colStrings = params[2].split(",");
		defaultColor = new TripletVector(Double.parseDouble(colStrings[0])/255.0, Double.parseDouble(colStrings[1])/255.0, Double.parseDouble(colStrings[2])/255.0);
		colStrings = params[3].split(",");
		alternateColor = new TripletVector(Double.parseDouble(colStrings[0])/255.0, Double.parseDouble(colStrings[1])/255.0, Double.parseDouble(colStrings[2])/255.0);
		specular = Double.parseDouble(params[4]);
		reflectivity = Double.parseDouble(params[5]);
		glossyExponent = Double.parseDouble(params[6]);
		refractivity = Double.parseDouble(params[7]);
		refractionIndex = Double.parseDouble(params[8]);
		String [] directionComponents = params[9].split(",");
		patternDirection = new TripletVector(Double.parseDouble(directionComponents[0]), Double.parseDouble(directionComponents[1]), Double.parseDouble(directionComponents[2]));
		//Citation: http://stackoverflow.com/questions/9605556/how-to-project-a-3d-point-to-a-3d-plane
		patternDirection = patternDirection.sub(coordinates);
		double dist = patternDirection.dot(normal);
		patternDirection = patternDirection.sub(normal.scale(dist));
		patternSize = Double.parseDouble(params[10]);
		patternDirection.normalize();
		emitter = false;
		
		filename = params[11];
		if(filename.equals("none"))
		{
			myImage = null;
		}
		//Citation: https://docs.oracle.com/javase/tutorial/2d/images/loadimage.html
		else
		{
			try
			{
				myImage = ImageIO.read(new File(filename));
			}
			catch(Exception e)
			{
				System.out.println("Error! Unable to load image: " + filename + ". Continuing with default");
				filename = "none";
			}
		}
		
		minBound = new TripletVector(0, 0, 0);
		maxBound = new TripletVector(0, 0, 0);
	}
	
	@Override
	public TripletVector getColor(TripletVector point)
	{
		TripletVector patternDirection2 = normal.cross(patternDirection);
		double xDistance = patternDirection.dot(point);
		double yDistance = patternDirection2.dot(point);
		if(filename.equals("none"))
		{
			patternDirection2.normalize();
			int xDistanceInt = (int)(patternDirection.dot(point)/patternSize);
			int yDistanceInt = (int)(patternDirection2.dot(point)/patternSize);
			//CITATION: http://stackoverflow.com/questions/7342237/check-whether-number-is-even-or-odd
			boolean even = ((xDistanceInt + yDistanceInt) & 1) == 0;
			if(xDistance*yDistance < 0)
				even = !even;
			if(even)
				return defaultColor;
			else
				return alternateColor;
		}
		else
		{
			double u = Math.abs((xDistance%patternSize)/patternSize);
			double v = Math.abs((yDistance%patternSize)/patternSize);
			double j = (1-u)*(myImage.getHeight()-1);
		    double i = v*(myImage.getWidth()-1);

			Color pixCol = new Color(myImage.getRGB((int)i, (int)j));
			
			return new TripletVector(pixCol.getRed()/255.0, pixCol.getGreen()/255.0, pixCol.getBlue()/255.0);
		}
	}

	@Override
	public TripletVector getNormal(TripletVector point)
	{
		return normal;
	}

	@Override
	public ArrayList<Double> intersect(Ray intersectRay)
	{
		ArrayList<Double> result = new ArrayList<Double>();
		TripletVector dir = intersectRay.getDirection();
		TripletVector org = intersectRay.getOrigin();
		if(dir.dot(normal) == 0)
			return result;
		double t = (coordinates.sub(org).dot(normal))*(1/dir.dot(normal));
		if(t >= 0)
			result.add(t);
		return result;
	}
	
	@Override
	public ArrayList<Ray> getSampleRays(TripletVector origin, String sampleType, int sampleSize)
	{
		ArrayList<Ray> sampleRays = new ArrayList<Ray>();
		
		return sampleRays;
	}

}
