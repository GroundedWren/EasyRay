package geometry;

import java.util.ArrayList;
import java.lang.Math;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Color;

import rendering.TripletVector;
import rendering.Ray;
import rendering.Sampler;

/**
 * A sphere is a monochrome spherical object in the scene
 * @author alexaulabaugh
 *
 */
public class Sphere extends GeometricObject
{
	private TripletVector center;
	private double radius;
	//An image associated with the sphere
	private BufferedImage myImage;
	//The filename of the image associated with the rectangle, or "none"
	private String filename;
	
	/**
	 * The default constructor for a sphere
	 * @param position the center of the sphere
	 * @param sphereRadius
	 * @param sphereColor the default color
	 * @param spec the specular component
	 */
	public Sphere(TripletVector position, double sphereRadius, TripletVector sphereColor, String filen, double spec, double refl, double gloss, double refrct, double refrctInd, boolean emit)
	{
		center = new TripletVector(position);
		radius = sphereRadius;
		defaultColor = new TripletVector(sphereColor);
		filename = filen;
		specular = spec;
		reflectivity = refl;
		glossyExponent = gloss;
		refractivity = refrct;
		refractionIndex = refrctInd;
		emitter = emit;
		
		minBound = center.add(radius);
		maxBound = center.sub(radius);
	}
	
	/**
	 * The constructor for a sphere from a line in a scene file
	 * @param paramString the necessary parameters stored as a string
	 */
	public Sphere(String paramString)
	{
		String[] params = paramString.split(" ");
		String[] positionParams = params[0].split(",");
		center = new TripletVector(Double.parseDouble(positionParams[0]), Double.parseDouble(positionParams[1]), Double.parseDouble(positionParams[2]));
		radius = Double.parseDouble(params[1]);
		String[] colorParams = params[2].split(",");
		defaultColor = new TripletVector(Double.parseDouble(colorParams[0])/255.0, Double.parseDouble(colorParams[1])/255.0, Double.parseDouble(colorParams[2])/255.0);
		filename = params[3];
		specular = Double.parseDouble(params[4]);
		reflectivity = Double.parseDouble(params[5]);
		glossyExponent = Double.parseDouble(params[6]);
		refractivity = Double.parseDouble(params[7]);
		refractionIndex = Double.parseDouble(params[8]);
		emitter = params[9].equals("True");
		
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
		
		minBound = center.sub(radius);
		maxBound = center.add(radius);
	}

	@Override
	public TripletVector getColor(TripletVector point)
	{
		if(filename.equals("none"))
		{
			return defaultColor;
		}
		else
		{
			//Map to spherical coordinates
			double[] pointComponents = point.sub(center).getComponents();
			double theta = Math.atan2(pointComponents[2], pointComponents[0]) + Math.PI;
			double phi = Math.acos(pointComponents[1]/radius);
			double j = (1-(theta/(2*Math.PI)))*myImage.getWidth();
			double i = (phi/(Math.PI))*myImage.getHeight();
			Color pixCol = new Color(myImage.getRGB((int)j, (int)i));
			return new TripletVector(pixCol.getRed()/255.0, pixCol.getGreen()/255.0, pixCol.getBlue()/255.0);
		}
	}

	@Override
	public TripletVector getNormal(TripletVector point)
	{
		TripletVector my_normal =  new TripletVector(center.sub(point));
		my_normal.normalize();
		return my_normal;
	}

	@Override
	public ArrayList<Double> intersect(Ray intersectRay)
	{
		//Citation: Equation from https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection
		ArrayList<Double> result = new ArrayList<Double>();
		
		TripletVector rayDir = intersectRay.getDirection();
		TripletVector rayOrig = intersectRay.getOrigin();
		
		double a = rayDir.dot(rayDir);
		double b = rayOrig.sub(center).scale(2).dot(rayDir);
		double c = rayOrig.sub(center).dot(rayOrig.sub(center)) - (radius*radius);
		
		if(b*b-(4*a*c) == 0)
		{
			result.add(-b/(2*a));
		}
		else if(b*b-(4*a*c) > 0)
		{
			result.add((-b + Math.pow((b*b-(4*a*c)), 0.5))/(2*a));
	        result.add((-b - Math.pow((b*b-(4*a*c)), 0.5))/(2*a));
		}
		
		return result;
	}
	
	@Override
	public ArrayList<Ray> getSampleRays(TripletVector origin, String sampleType, int sampleSize)
	{
		//CITATION: http://tutorial.math.lamar.edu/Classes/CalcIII/SphericalCoords.aspx
		//http://mathworld.wolfram.com/SphericalCoordinates.html
		ArrayList<Ray> sampleRays = new ArrayList<Ray>();
		ArrayList<double[]> samples = Sampler.getSamples(sampleType, sampleSize);
		for(double[] sample : samples)
		{
			double theta = sample[0]*2*Math.PI;
			double phi = sample[1]*Math.PI;
			TripletVector pointOnSphere = new TripletVector(radius*Math.sin(phi)*Math.cos(theta), radius*Math.sin(phi)*Math.sin(theta), radius*Math.cos(phi));
			pointOnSphere = pointOnSphere.add(center);
			TripletVector vecFromObj = pointOnSphere.sub(origin);
			vecFromObj.normalize();
			TripletVector rayStart = origin.add(vecFromObj.scale(0.01));
			sampleRays.add(new Ray(rayStart, vecFromObj, -1, -1, 1, 1.0, 0));
		}
		samples = null;
		return sampleRays;
	}

}
