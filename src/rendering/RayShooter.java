package rendering;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * RayShooter handles tracing a portion of the total initial rays
 * @author alexaulabaugh
 *
 */
public class RayShooter implements Callable<Double>
{
	private ArrayList<Ray> myQueue;
	private int ID;
	private double iterations;
	private TracingCoordinator coordinator;
	
	public RayShooter(int id, TracingCoordinator cord)
	{
		myQueue = new ArrayList<Ray>();
		ID = id;
		coordinator = cord;
	}
	
	public void addRay(Ray r)
	{
		myQueue.add(r);
	}
	
	@Override
	public Double call() throws Exception
	{
		for(int i = 0; i < myQueue.size(); i++)
		{
			coordinator.shootRay(myQueue.get(i), ID);
			iterations++;
		}
		return iterations;
	}

}
