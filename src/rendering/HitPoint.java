package rendering;

import geometry.GeometricObject;

/**
 * Represents the location a ray intersected an object
 * @author alexaulabaugh
 *
 */
public class HitPoint
{
	//The object intersected
	private GeometricObject hitObject;
	//The location of the intersection
	private TripletVector hitPoint;
	//The in vector
	private Ray inRay;
	
	/**
	 * Generic constructor
	 * @param obj the GeometricObject hit
	 * @param pt the location of the intersection
	 * @param in the ray that made the hit
	 */
	public HitPoint(GeometricObject obj, TripletVector pt, Ray in)
	{
		hitObject = obj;
		hitPoint = pt;
		inRay = in;
	}
	
	public void setObject(GeometricObject obj)
	{
		hitObject = obj;
	}
	
	public void setHitPoint(TripletVector pt)
	{
		hitPoint = pt;
	}
	
	public GeometricObject getObject()
	{
		return hitObject;
	}
	
	public TripletVector getPoint()
	{
		return hitPoint;
	}
	
	public Ray getInRay()
	{
		return inRay;
	}
}
