package acceleration;

/**
 * The simplest acceleartion structure - everything is in the same cell, so everythign is checked.
 */

import java.util.ArrayList;

import geometry.GeometricObject;
import rendering.HitPoint;
import rendering.Ray;

public class FreeObjects extends AccelerationStructure
{
	
	public FreeObjects()
	{
		sceneObjects = new ArrayList<GeometricObject>();
	}

	@Override
	protected void addGeometricObject(GeometricObject obj)
	{
		sceneObjects.add(obj);
	}

	@Override
	public void addGeometricObjectList(ArrayList<GeometricObject> objects)
	{
		for(GeometricObject object : objects)
		{
			addGeometricObject(object);
		}
	}

	@Override
	public HitPoint intersectRay(Ray sceneRay)
	{
		HitPoint closestHit = new HitPoint(null, null, sceneRay);
		double smallestT = Double.MAX_VALUE;
		
		for(GeometricObject obj : sceneObjects)
		{
			ArrayList<Double> intersections = obj.intersect(sceneRay);
			for(double tValue : intersections)
			{
				if(tValue < smallestT && tValue > 0)
				{
					smallestT = tValue;
					closestHit.setObject(obj);
					closestHit.setHitPoint(sceneRay.getPoint(smallestT));
				}
			}
		}
		return closestHit;
	}

}
