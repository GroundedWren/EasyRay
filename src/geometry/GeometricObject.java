package geometry;
import java.util.*;

import rendering.TripletVector;
import rendering.Ray;
import rendering.Sampler;

/**
 * Geometric Object is an abstract class for all objects in the scene
 * @author alexaulabaugh
 *
 */
public abstract class GeometricObject
{
	//The default color of the object
	protected TripletVector defaultColor;
	//The specular exponent for the object
	protected double specular;
	//the fraction of light to be reflected by the object
	protected double reflectivity;
	//The exponent used for glossy reflection, 0 if standard reflection
	protected double glossyExponent;
	//the fraction of light to be refracted by the object
	protected double refractivity;
	protected double refractionIndex;
	//true if the object is a source of light
	protected boolean emitter;
	//Bounds used for Acceleration Structures
	protected TripletVector minBound;
	protected TripletVector maxBound;
	
	/**
	 * Finds the color at a point on the object
	 * @param point the point on the object of which we want the color
	 * @return the color at the specified point
	 */
	public abstract TripletVector getColor(TripletVector point);
	
	/**
	 * Finds the normal at a point on the object
	 * @param point the point on the object of which we want the normal
	 * @return the normal at the specified point
	 */
	public abstract TripletVector getNormal(TripletVector point);
	
	/**
	 * Finds all intersections a ray has with the object
	 * @param intersectRay the incoming ray for which to find intersections
	 * @return an ArrayList of all intersections, represented as the t component of the o+td model of the ray
	 */
	public abstract ArrayList<Double> intersect(Ray intersectRay);
	
	/**
	 * Specific for area lights
	 * @return true if the object is an area light, false otherwise
	 */
	public boolean isEmitter()
	{
		return emitter;
	}
	
	/**
	 * An implementation of sampling using rendering/sampler.java to be used
	 * for when the object emits light.
	 * @param origin the origin of the location that's requesting a sampling
	 * @param sampleType A string describing the sample type
	 * @param sampleSize Number of samples to be gathered
	 * @return A list of rays that will sample the object from the given origin
	 */
	public abstract ArrayList<Ray> getSampleRays(TripletVector origin, String sampleType, int sampleSize);
	
	/**
	 * 
	 * @return specular exponent of object
	 */
	public double getSpecular()
	{
		return specular;
	}
	
	/**
	 * 
	 * @return reflectivity fraction
	 */
	public double getReflectivity()
	{
		return reflectivity;
	}
	
	/**
	 * 
	 * @return glossy exponent or 0 if standard reflection
	 */
	public double getGlossyExponent()
	{
		return glossyExponent;
	}
	
	/**
	 * @return refractivity fraction
	 */
	public double getRefractivity()
	{
		return refractivity;
	}
	
	/**
	 * @return the index of refraction
	 */
	public double getRefractionIndex()
	{
		return refractionIndex;
	}
	
	/**
	 * Calculates which way the reflection rays go
	 * @param intersectRay the incoming ray
	 * @param point the intersection point
	 * @return a reflection ray
	 */
	public Ray getReflectionRay(Ray intersectRay, TripletVector point)
	{
		TripletVector rayD = intersectRay.getDirection();
		TripletVector norm = this.getNormal(point);
		TripletVector reflD = new TripletVector(rayD.add(norm.scale(norm.dot(rayD.scale(-1))*2)));
		TripletVector reflO = point.add(reflD.scale(0.0001));
		int[] pixel = intersectRay.getPixel();
		return new Ray(reflO, reflD, pixel[0], pixel[1], reflectivity*intersectRay.getWeight(), intersectRay.getRefraction(), intersectRay.getDepth()+1);
	}
	
	/**
	 * Calculates numRays glossy (noisy) reflection rays that are multi-jittered
	 * @param intersectRay the incident ray
	 * @param point the point of intersection
	 * @param numRays the number of glossy rays to shoot
	 * @return an arrayList of every glossy ray
	 */
	public ArrayList<Ray> getReflectionRaysGlossy(Ray intersectRay, TripletVector point, int numRays)
	{
		ArrayList<Ray> glossyRays = new ArrayList<Ray>();
		ArrayList<double[]> samples = Sampler.getSamples("MultiJitter", numRays);
		TripletVector rayD = intersectRay.getDirection();
		TripletVector norm = this.getNormal(point);
		TripletVector reflD = new TripletVector(rayD.add(norm.scale(norm.dot(rayD.scale(-1))*2)));
		
		TripletVector wAxis = new TripletVector(reflD);
        wAxis.normalize();
        TripletVector uAxis = wAxis.cross(new TripletVector(1, 1, 1));
        if(uAxis.getLength() == 0)
        {
        	uAxis = wAxis.cross(new TripletVector(1, 1, 2));
        }
        uAxis.normalize();
        TripletVector vAxis = wAxis.cross(uAxis);
        vAxis.normalize();
        TripletVector inverseNorm = new TripletVector(norm).scale(-1);
		
		for(int i = 0; i < numRays; i++)
		{	
			double psi = 2*Math.PI*samples.get(i)[0];
            double theta = 1-(samples.get(i)[1]);
            double reflExp = glossyExponent;
            theta = Math.pow(theta, (1.0/(reflExp+1)));
            theta = Math.acos(theta);
			
            TripletVector glossyDirection = new TripletVector((uAxis.scale(Math.sin(theta)*Math.cos(psi))).add((vAxis.scale(Math.sin(theta)*Math.sin(psi)))).add((wAxis.scale(Math.cos(theta)))));
            glossyDirection.normalize();
            
            if(glossyDirection.dot(inverseNorm) < 0)
            {
            	double[] vecComponents = glossyDirection.getComponents();
            	vecComponents[0] = vecComponents[0]*-1;
            	vecComponents[1] = vecComponents[1]*-1;
            }
			
			TripletVector reflO = point.add(glossyDirection.scale(0.1));
			int[] pixel = intersectRay.getPixel();
			glossyRays.add(new Ray(reflO, glossyDirection, pixel[0], pixel[1], reflectivity*intersectRay.getWeight()/(numRays*1.0), intersectRay.getRefraction(), intersectRay.getDepth()+1));
		}
		return glossyRays;
	}
	
	/**
	 * Calculates which way the refraction rays go
	 */
	public Ray getRefractionray(Ray intersectRay, TripletVector point)
	{
		TripletVector rayD = intersectRay.getDirection();
		TripletVector norm = this.getNormal(point);
		double relativeRefraction = intersectRay.getRefraction()/refractionIndex;
		double outRefraction = refractionIndex;
		if(relativeRefraction == 1.0)
		{
			relativeRefraction = intersectRay.getRefraction()/1.0;
			outRefraction = 1.0;
		}
		double cosine = rayD.dot(norm);
		
		//Citation: http://stackoverflow.com/questions/26087106/refraction-in-raytracing
		if(cosine > 0.0)
		{
			norm = norm.scale(-1);
		}
		else
		{
			cosine = -cosine;
		}
		
		double sin2theta = relativeRefraction*relativeRefraction*(1-(cosine*cosine));
		TripletVector tFinal = rayD.scale(relativeRefraction).add(norm.scale((relativeRefraction*cosine)-Math.sqrt(1-sin2theta)));
		
		int[] pixel = intersectRay.getPixel();
		return new Ray(point.add(tFinal.scale(0.0001)), tFinal, pixel[0], pixel[1], refractivity*intersectRay.getWeight(), outRefraction, intersectRay.getDepth()+1);
	}
	
	/**
	 * The bouning cube of this object
	 * Used for acceleation structures
	 * @return
	 */
	public TripletVector[] getBounds()
	{
		TripletVector[] bounds = new TripletVector[2];
		bounds[0] = minBound;
		bounds[1] = maxBound;
		return bounds;
	}
}
