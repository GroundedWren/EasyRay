package geometry;

import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.Color;

import rendering.Ray;
import rendering.Sampler;
import rendering.TripletVector;

/**
 * A rectangle is an object in the scene that either has a solid color or
 * an image texture
 * @author alexaulabaugh
 *
 */

public class Rectangle extends GeometricObject
{
	//The coordinates of the bottom left of the rectangle
	private TripletVector bottomLeft;
	//A vector from the bottom left of the rectangle to the top left of the rectangle
	private TripletVector aVector;
	//A vector from the bottom left of the rectangle to the bottom right of the rectangle
	private TripletVector bVector;
	//The normal vector of the rectangle
	private TripletVector normal;
	//An image associated with the rectangle
	private BufferedImage myImage;
	//The filename of the image associated with the rectangle, or "none"
	private String filename;
	
	/**
	 * The default constructor for a rectangle
	 * @param botL bottomLeft
	 * @param aVecIn aVector
	 * @param bVecIn bVector
	 * @param col defaultColor
	 * @param filen filename
	 * @param spec the specular component
	 */
	public Rectangle(TripletVector botL, TripletVector aVecIn, TripletVector bVecIn, TripletVector col, String filen, double spec, double refl, double gloss, double refrct, double refrctInd, boolean emit)
	{
		bottomLeft = botL;
		aVector = aVecIn;
		bVector = bVecIn;
		defaultColor = col;
		filename = filen;
		specular = spec;
		reflectivity = refl;
		glossyExponent = gloss;
		refractivity = refrct;
		refractionIndex = refrctInd;
		emitter = emit;
		
		minBound = bottomLeft;
		maxBound = bottomLeft.add(aVector).add(bVector);
	}
	
	/**
	 * The constructor for a rectangle from a line in a scene file
	 * @param paramString the necessary parameters for a rectangle stored as a string
	 */
	public Rectangle(String paramString)
	{
		String[] params = paramString.split(" ");
		String[] botLStr = params[0].split(",");
		bottomLeft = new TripletVector(Double.parseDouble(botLStr[0]), Double.parseDouble(botLStr[1]), Double.parseDouble(botLStr[2]));
		String[] aVecStr = params[1].split(",");
		aVector = new TripletVector(Double.parseDouble(aVecStr[0]), Double.parseDouble(aVecStr[1]), Double.parseDouble(aVecStr[2]));
		String[] bVecStr = params[2].split(",");
		bVector = new TripletVector(Double.parseDouble(bVecStr[0]), Double.parseDouble(bVecStr[1]), Double.parseDouble(bVecStr[2]));
		String[] colStr = params[3].split(",");
		defaultColor = new TripletVector(Double.parseDouble(colStr[0])/255.0, Double.parseDouble(colStr[1])/255.0, Double.parseDouble(colStr[2])/255.0);
		filename = params[4];
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
		
		specular = Double.parseDouble(params[5]);
		reflectivity = Double.parseDouble(params[6]);
		glossyExponent = Double.parseDouble(params[7]);
		refractivity = Double.parseDouble(params[8]);
		refractionIndex = Double.parseDouble(params[9]);
		
		emitter = params[10].equals("True");
		
		normal = new TripletVector(aVector.cross(bVector));
		normal.normalize();

		minBound = bottomLeft;
		maxBound = bottomLeft.add(aVector).add(bVector);
	}
	
	@Override
	public TripletVector getColor(TripletVector point)
	{
		if(filename.equals("none"))
			return defaultColor;
		else
		{
			//Calculate the pixel on the image for the color at this point
			TripletVector offPoint = new TripletVector(point);
		    offPoint = offPoint.sub(bottomLeft);
		    TripletVector proja = aVector.scale((offPoint.dot(aVector))/(aVector.dot(aVector)));
		    double u = proja.getLength()/(aVector.getLength());
		    TripletVector projb = (bVector.scale((offPoint.dot(bVector))/(bVector.dot(bVector))));
		    double v = projb.getLength()/(bVector.getLength());
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
		
		double t = ((bottomLeft.sub(org)).dot(normal))/(dir.dot(normal));
		
		if(t < 0)
			return result;
		
		TripletVector point = (org).add(dir.scale(t));
		//Citation: Ray Tracing from the Ground Up
		TripletVector d = point.sub(bottomLeft);
		
		double ddota = d.dot(aVector);
		if(ddota < 0.0 || ddota > aVector.getLength()*aVector.getLength())
			return result;
		
		double ddotb = d.dot(bVector);
		if(ddotb < 0.0 || ddotb > bVector.getLength()*bVector.getLength())
			return result;
		
		result.add(t);
		return result;
	}
	
	@Override
	public ArrayList<Ray> getSampleRays(TripletVector origin, String sampleType, int sampleSize)
	{
		ArrayList<Ray> sampleRays = new ArrayList<Ray>();
		ArrayList<double[]> samples = Sampler.getSamples(sampleType, sampleSize);
		for(double[] sample : samples)
		{
			TripletVector pointOnRect = bottomLeft.add(aVector.scale(sample[0])).add(bVector.scale(sample[1]));
			TripletVector vecFromObj = pointOnRect.sub(origin);
			vecFromObj.normalize();
			TripletVector rayStart = origin.add(vecFromObj.scale(0.01));
			sampleRays.add(new Ray(rayStart, vecFromObj, -1, -1, 1, 1.0, 0));
		}
		samples = null;
		return sampleRays;
	}

}
