package rendering;

/**
 * A ray is a vector with an origin and possibly an associated pixel in the camera film
 * @author alexaulabaugh
 * Form: o+td where t is a positive real number
 */

public class Ray
{
	private TripletVector origin;
	private TripletVector direction;
	private int recurDepth;
	private double drawWeight;
	private double refractionIndex;
	//Only rays shot directly from the camera have an associated pixel, others have [-1,-1]
	private int[] pixel;
	
	/**
	 * Generic constructor of a ray
	 * @param o origin
	 * @param d direction
	 * @param i first index of the pixel, or -1 if none
	 * @param j second index of the pixel, or -1 if none
	 * @param weight the weighting given to this ray, dependent on mirror depth
	 * @param refract the refraction index of the medium the ray is going through
	 * @param depth the recursion depth of this ray
	 */
	public Ray(TripletVector o, TripletVector d, int i, int j, double weight, double refract, int depth)
	{
		origin = o;
		direction = d;
		direction.normalize();
		pixel = new int[2];
		pixel[0] = i;
		pixel[1] = j;
		drawWeight = weight;
		refractionIndex = refract;
		recurDepth = depth;
	}
	
	/**
	 * Copy constructor
	 * @param other_ray
	 */
	public Ray(Ray other_ray)
	{
		origin = new TripletVector(other_ray.getOrigin());
		direction = new TripletVector(other_ray.getDirection());
		direction.normalize();
	}
	
	/**
	 * point = o+td
	 * @param t how far down the direction the point is from the origin
	 * @return the point at o+td
	 */
	public TripletVector getPoint(double t)
	{
		double[] origin_components = origin.getComponents();
		double[] direction_components = direction.getComponents();
		double x = origin_components[0] + t*direction_components[0];
		double y = origin_components[1] + t*direction_components[1];
		double z = origin_components[2] + t*direction_components[2];
		return new TripletVector(x, y, z);
	}
	
	public TripletVector getOrigin()
	{
		return origin;
	}
	
	public TripletVector getDirection()
	{
		return direction;
	}
	
	public int[] getPixel()
	{
		return pixel;
	}
	
	public double getWeight()
	{
		return drawWeight;
	}
	
	public double getRefraction()
	{
		return refractionIndex;
	}
	
	public int getDepth()
	{
		return recurDepth;
	}
}
