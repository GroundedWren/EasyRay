package acceleration;
import java.util.*;

import geometry.GeometricObject;
import rendering.HitPoint;
import rendering.Ray;

/**
 * Abstract class for all AccelerationStructure types
 * @author alexaulabaugh
 *
 */

public abstract class AccelerationStructure
{
	//All objects held by the structure
	protected ArrayList<GeometricObject> sceneObjects;
	
	/**
	 * Adds a new GeometricObject to the structure
	 * @param obj
	 */
	protected abstract void addGeometricObject(GeometricObject obj);
	
	/**
	 * Adds an entire list of GeometricObjects to the structure
	 * @param objects
	 */
	public abstract void addGeometricObjectList(ArrayList<GeometricObject> objects);
	
	/**
	 * Given an in-ray, determine a hit point on one of the objects (or none)
	 * @param sceneRay
	 * @return
	 */
	public abstract HitPoint intersectRay(Ray sceneRay);
}
