package acceleration;

import java.util.ArrayList;
import java.util.HashMap;

import geometry.GeometricObject;
import rendering.HitPoint;
import rendering.Ray;
import rendering.TripletVector;

/**
 * A Regular Grid Acceleration Structure
 * A lattice of cells of equal size, each containing a list of objects whose
 * bounding boxes intersect them.
 * @author alexaulabaugh
 *
 */

public class RegularGrid extends AccelerationStructure
{
	//bounding box of the whole grid
	private TripletVector minBound;
	private TripletVector maxBound;
	
	//constant to determine the ratio of cells to objects
	private int m_constant;
	
	//width of each dimension
	private double wx;
	private double wy;
	private double wz;
	
	//number of cells each dimension
	private double nx;
	private double ny;
	private double nz;
	
	//A hash from cell index to list of objects
	//should be sparse
	private HashMap<String, ArrayList<GeometricObject>> cells;
	
	//Objects that can't be contained in the grid but still need to be
	//intersected
	//e.g. planes
	private ArrayList<GeometricObject> extraGridObjects;
	
	public RegularGrid(int m)
	{
		m_constant = m;
		cells = new HashMap<String, ArrayList<GeometricObject>>();
		sceneObjects = new ArrayList<GeometricObject>();
		extraGridObjects = new ArrayList<GeometricObject>();
	}
	
	@Override
	protected void addGeometricObject(GeometricObject obj)
	{
		TripletVector[] objectBounds = obj.getBounds();
		double[] nullBoundsTest = objectBounds[0].sub(objectBounds[1]).getComponents();
		if(nullBoundsTest[0] == 0 && nullBoundsTest[1] == 0 && nullBoundsTest[2] == 0)
		{
			extraGridObjects.add(obj);
		}
		else
		{
			sceneObjects.add(obj);
			if(minBound == null)
			{
				minBound = new TripletVector(objectBounds[0]);
			}
			if(maxBound == null)
			{
				maxBound = new TripletVector(objectBounds[1]);
			}
			double[] minBoundComponents = minBound.getComponents();
			double[] maxBoundComponents = maxBound.getComponents();
			
			for(int i = 0; i < 2; i++)
			{
				double[] components = objectBounds[i].getComponents();
				for(int j = 0; j < 3; j++)
				{
					if(minBoundComponents[j] > components[j])
					{
						minBoundComponents[j] = components[j];
					}
					if(maxBoundComponents[j] < components[j])
					{
						maxBoundComponents[j] = components[j];
					}
				}
			}
			minBound = new TripletVector(minBoundComponents);
			maxBound = new TripletVector(maxBoundComponents);
		}
	}

	@Override
	public void addGeometricObjectList(ArrayList<GeometricObject> objects)
	{
		System.out.println("Building Regular Grid");
		for(GeometricObject object: objects)
		{
			addGeometricObject(object);
		}
		setupCells();
		placeObjects();
		System.out.println("Regular Grid Complete");
	}

	@Override
	public HitPoint intersectRay(Ray sceneRay)
	{
		double[] sceneRayDirectionComp = sceneRay.getDirection().getComponents();
		HitPoint closestHit = new HitPoint(null, null, sceneRay);
		double smallestT = Double.MAX_VALUE;
		for(GeometricObject obj : extraGridObjects)
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
		TripletVector currentPoint = new TripletVector(sceneRay.getOrigin());
		int[] currentCoords = new int[3];
		double t = 0;
		if(pointInBoundingBox(sceneRay.getOrigin(), minBound, maxBound))
		{
			currentCoords = getCoords(sceneRay.getOrigin());
		}
		else
		{
			t = boundingBoxIntersect(sceneRay, minBound, maxBound);
			if(t < 0.0)
			{
				return closestHit;
			}
			currentPoint = sceneRay.getPoint(t);
			currentCoords = getCoords(currentPoint);
		}
		//Walk through the lattice
		while(true)
		{
			HitPoint newHit = cellHitCheck(sceneRay, currentCoords[0], currentCoords[1], currentCoords[2]);
			if(newHit.getObject() != null)
			{
				int[] newHitCoords = getCoords(newHit.getPoint());
				if(newHitCoords[0] == currentCoords[0] && newHitCoords[1] == currentCoords[1] && newHitCoords[2] == currentCoords[2])
				{
					if(newHit.getPoint().sub(sceneRay.getOrigin()).getLength() < smallestT)
						return newHit;
					else
						return closestHit;
				}
			}
			
			TripletVector[] ourBox = getCellBoundingBox(currentCoords[0], currentCoords[1], currentCoords[2]);
			double[] currentXYZ = currentPoint.getComponents();
	        double nextX = currentXYZ[0];
	        double nextY = currentXYZ[1];
	        double nextZ = currentXYZ[2];
	        
	        if(sceneRayDirectionComp[0] > 0) { nextX = ourBox[1].getComponents()[0];}
	        if(sceneRayDirectionComp[0] < 0) { nextX = ourBox[0].getComponents()[0];}
	        
	        if(sceneRayDirectionComp[1] > 0) { nextY = ourBox[1].getComponents()[1];}
	        if(sceneRayDirectionComp[1] < 0) { nextY = ourBox[0].getComponents()[1];}
	        
	        if(sceneRayDirectionComp[2] > 0) { nextZ = ourBox[1].getComponents()[2];}
	        if(sceneRayDirectionComp[2] < 0) { nextZ = ourBox[0].getComponents()[2];}
	        
	        double deltaX = Double.MAX_VALUE;
	        if(sceneRayDirectionComp[0] != 0)
	        	deltaX = (nextX - currentPoint.getComponents()[0])/(sceneRayDirectionComp[0]);
	        double deltaY = Double.MAX_VALUE;
	        if(sceneRayDirectionComp[1] != 0)
	        	deltaY = (nextY - currentPoint.getComponents()[1])/(sceneRayDirectionComp[1]);
	        double deltaZ = Double.MAX_VALUE;
	        if(sceneRayDirectionComp[2] != 0)
	        	deltaZ = (nextZ - currentPoint.getComponents()[2])/(sceneRayDirectionComp[2]); 
	        
	        if(deltaX < deltaY && deltaX < deltaZ)
	        {
	            if(sceneRayDirectionComp[0] > 0)
	            {
	            	currentCoords[0]++;
	            }
	            else
	            {
	            	currentCoords[0]--;
	            }
	        }
	        else if(deltaY < deltaZ)
	        {
	            if(sceneRayDirectionComp[1] > 0)
	            {
	            	currentCoords[1]++;
	            }
	            else
	            {
	            	currentCoords[1]--;
	            }
	        }
	        else 
	        {
	            if(sceneRayDirectionComp[2] > 0)
	            {
	            	currentCoords[2]++;
	            }
	            else
	            {
	            	currentCoords[2]--;
	            }
	        }
	        
	        for(int i = 0; i < 3; i++)
	        {
	        	if(currentCoords[i] < 0 || currentCoords[i] >= nx){return closestHit;}
	        }
	        TripletVector[] nextBox = getCellBoundingBox(currentCoords[0], currentCoords[1], currentCoords[2]);
	        t = boundingBoxIntersect(sceneRay, nextBox[0], nextBox[1]);
	        if(t < 0.0){return closestHit;}
	        currentPoint = sceneRay.getPoint(t);
		}		
	}
	
	/**
	 * Get the worldspace bounding box of a cell by its index
	 * @param ix
	 * @param iy
	 * @param iz
	 * @return bounding box
	 */
	TripletVector[] getCellBoundingBox(int ix, int iy, int iz)
	{
		double[] minBoxCoords = new double[3];
		double[] maxBoxCoords = new double[3];
		double[] minBoundComponents = minBound.getComponents();
		
		minBoxCoords[0] = minBoundComponents[0] + (wx/(1.0*nx))*ix;
		minBoxCoords[1] = minBoundComponents[1] + (wy/(1.0*ny))*iy;
		minBoxCoords[2] = minBoundComponents[2] + (wz/(1.0*nz))*iz;
	    
		maxBoxCoords[0] = minBoxCoords[0] + (wx/(1.0*nx));
		maxBoxCoords[1] = minBoxCoords[1] + (wy/(1.0*ny));
		maxBoxCoords[2] = minBoxCoords[2] + (wz/(1.0*nz));
	    
	    TripletVector[] returnVals = new TripletVector[2];
	    returnVals[0] = new TripletVector(minBoxCoords);
	    returnVals[1] = new TripletVector(maxBoxCoords);
	    return returnVals;
	}
	
	/**
	 * Determines if a given point falls in a bounding box
	 * @param point
	 * @param minBound
	 * @param maxBound
	 * @return
	 */
	boolean pointInBoundingBox(TripletVector point, TripletVector minBound, TripletVector maxBound)
	{
		double[] minBoundComponents = minBound.getComponents();
		double[] maxBoundComponents = maxBound.getComponents();
		double[] pointComponents = point.getComponents();
		
		for(int i = 0; i < 3; i++)
		{
		    if(pointComponents[i] >= maxBoundComponents[i] || pointComponents[i] < minBoundComponents[i])
		    {
		    	return false;
		    }
		}
	    return true;
	}
	
	/**
	 * Intersects a ray with a bounding box, determines the t value
	 * @param viewRay
	 * @param minBound
	 * @param maxBound
	 * @return the t value
	 */
	//Citation: Ray Tracing From The Ground Up, Suffern, Kevin
	private double boundingBoxIntersect(Ray viewRay, TripletVector minBound, TripletVector maxBound)
	{
		double[] minBoundComponents = minBound.getComponents();
		double[] maxBoundComponents = maxBound.getComponents();
	    double[] originComponents = viewRay.getOrigin().getComponents();
	    double[] directionComponents = viewRay.getDirection().getComponents();
	    
	    double[] t_min = new double[3];
	    double[] t_max = new double[3];
	    
	    for(int i = 0; i < 3; i++)
	    {
	    	double a = 1.0/directionComponents[i];
		    if( a >= 0)
		    {
		        t_min[i] = (minBoundComponents[i] - originComponents[i])*a;
		        t_max[i] = (maxBoundComponents[i] - originComponents[i])*a;
		    }
		    else
		    {
		        t_min[i] = (maxBoundComponents[i] - originComponents[i])*a;
		        t_max[i] = (minBoundComponents[i] - originComponents[i])*a;
		    }
	    }
	    
	    double t0 = -1;
	    
	    if(t_min[0] > t_min[1]) {t0 = t_min[0];}
	    else {t0 = t_min[1];}
	    if(t_min[2] > t0) {t0 = t_min[2];}
	    
	    double t1 = -1;
	    if(t_max[0] < t_max[1])
	        t1 = t_max[0];
	    else
	        t1 = t_max[1];
	    if(t_max[2] < t1)
	        t1 = t_max[2];
	    
	    if(t0 >= t1 || t1 <= 0.0001){return -1;}
	    
	    return t0;
	}
	
	/**
	 * Checks if a ray passing through a cell hits any objects within
	 * @param sceneRay
	 * @param ix
	 * @param iy
	 * @param iz
	 * @return
	 */
	private HitPoint cellHitCheck(Ray sceneRay, int ix, int iy, int iz)
	{
	    HitPoint closestHit = new HitPoint(null, null, sceneRay);
		double smallestT = Double.MAX_VALUE;

	    String key = getKey(ix, iy, iz);
	    ArrayList<GeometricObject> objectList = cells.get(key);
	    
	    if(objectList == null) {return closestHit;}
	    		
		for(GeometricObject obj : objectList)
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
	
	/**
	 * Key to the index->arraylist hash function
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private String getKey(int x, int y, int z)
	{
		return x + "_" + y + "_" + z;
	}
	
	/**
	 * Builds the lattice after the final dimensions and object count are known
	 */
	private void setupCells()
	{
		double[] maxBoundComponents = maxBound.getComponents();
		double[] minBoundComponents = minBound.getComponents();
		wx = Math.abs(maxBoundComponents[0] - minBoundComponents[0]);
	    wy = Math.abs(maxBoundComponents[1] - minBoundComponents[1]);
	    wz = Math.abs(maxBoundComponents[2] - minBoundComponents[2]);
	    
	    double s = Math.pow((wx*wy*wz / (sceneObjects.size()*1.0)), 1.0/3.0);
	    
	    nx = (int)(m_constant*wx/s) + 1;
	    ny = (int)(m_constant*wy/s) + 1;
	    nz = (int)(m_constant*wz/s) + 1;
	    
	}
	
	/**
	 * Determines which index bounding box holds a point
	 * @param point
	 * @return
	 */
	private int[] getCoords(TripletVector point)
	{
		double[] pointComponents = point.getComponents();
		double[] minBoundComponents = minBound.getComponents();
		
		int[] coords = new int[3];
		
		double fpx = (pointComponents[0] - minBoundComponents[0])/(wx);
	    int ix = (int)(nx*fpx);
	    if(ix == nx){ix--;}
	    
	    double fpy = (pointComponents[1] - minBoundComponents[1])/(wy);
	    int iy = (int)(ny*fpy);
	    if(iy == ny){iy--;}
	    
	    double fpz = (pointComponents[2] - minBoundComponents[2])/(wz);
	    int iz = (int)(nz*fpz);
	    if(iz == nz){iz--;}
	    
	    coords[0] = ix;
	    coords[1] = iy;
	    coords[2] = iz;
	    
	    return coords;
		
	}
	
	/**
	 * Places all sceneObjects into the lattice
	 */
	private void placeObjects()
	{
		for(GeometricObject obj : sceneObjects)
		{
			TripletVector[] bounds = obj.getBounds();
			int[] minRes = getCoords(bounds[0]);
			int[] maxRes = getCoords(bounds[1]);
		
			for(int ix = minRes[0]; ix <= maxRes[0]; ix++)
	        {
	            for(int iy = minRes[1]; iy <= maxRes[1]; iy++)
	            {
	                for(int iz = minRes[2]; iz <= maxRes[2]; iz++)
	                {
	                	String cellKey = getKey(ix, iy, iz);
	                    if(cells.get(cellKey) == null)
	                    {
	                        cells.put(cellKey, new ArrayList<GeometricObject>());  
	                    }
	                    ArrayList<GeometricObject> itemsSoFar = cells.get(cellKey);
	                    itemsSoFar.add(obj);
	                    cells.put(cellKey, itemsSoFar);
	                }
	            }
	        }
		}
	}
}
