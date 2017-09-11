package rendering;

/**
 * A point light has no area, and emits equally in all directions
 * @author alexaulabaugh
 *
 */
public class PointLight
{
	TripletVector location;
	TripletVector color;
	
	/**
	 * Generic constructor
	 * @param loc the location of the light
	 * @param col the color the light emits
	 */
	public PointLight(TripletVector loc, TripletVector col)
	{
		location = loc;
		color = col;
	}
	
	public TripletVector getLocation()
	{
		return location;
	}
	
	public TripletVector getColor()
	{
		return color;
	}
}
