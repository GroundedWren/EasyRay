package rendering;

import java.util.*;
import java.lang.Math;

/**
 * A class to handle all kinds of sampling performed
 * @author alexaulabaugh
 *
 */

public class Sampler
{
	/**
	 * CITATION: https://graphics.pixar.com/library/MultiJitteredSampling/paper.pdf
	 * Multi-Jittered sampling is pseudo-random and guarantees better coverage than simple random.
	 * @param numSamples number of total samples
	 * @param rng random number generator
	 * @return a list of 2D double arrays, with each double in the array ranging [0,1)
	 */
	private static ArrayList<double[]> multiJitterSamples(int numSamples, Random rng)
	{
		ArrayList<double[]> samples = new ArrayList<double[]>();
		int courseGridDims = (int)Math.sqrt(numSamples);
		Tuple[][] courseGrid = new Tuple[courseGridDims][courseGridDims];
		
		//initialize grid
		for(int courseX = 0; courseX < courseGridDims; courseX++)
		{
			for(int courseY = 0; courseY < courseGridDims; courseY++)
			{
				courseGrid[courseX][courseY] = new Tuple(courseY, courseX);
			}
		}
				
		//Swap X
		for(int courseX = 0; courseX < courseGridDims; courseX++)
		{
			for(int courseY = 0; courseY < courseGridDims; courseY++)
			{
				int newFineX = rng.nextInt(courseGridDims);
				for(int innerY = 0; innerY < courseGridDims; innerY++)
				{
					if(courseGrid[courseX][innerY].x == newFineX)
					{
						courseGrid[courseX][innerY].x = courseGrid[courseX][courseY].x;
						courseGrid[courseX][courseY].x = newFineX;
					}
				}
			}
		}
		
		//Swap Y
		for(int courseX = 0; courseX < courseGridDims; courseX++)
		{
			for(int courseY = 0; courseY < courseGridDims; courseY++)
			{
				int newFineY = rng.nextInt(courseGridDims);
				for(int innerX = 0; innerX < courseGridDims; innerX++)
				{
					if(courseGrid[innerX][courseY].y == newFineY)
					{
						courseGrid[innerX][courseY].y = courseGrid[courseX][courseY].y;
						courseGrid[courseX][courseY].y = newFineY;
					}
				}
			}
		}
		//Comment in to check multi-jittering accuracy
		//printGrid(courseGrid);
		
		for(int courseX = 0; courseX < courseGridDims; courseX++)
		{
			for(int courseY = 0; courseY < courseGridDims; courseY++)
			{
				double[] samplePoint = new double[2];
				samplePoint[0] = courseX*(1.0/(double)courseGridDims);
				samplePoint[0] += (courseGrid[courseX][courseY].x+rng.nextDouble())*(1.0/(double)numSamples);
				
				samplePoint[1] = courseY*(1.0/(double)courseGridDims);
				samplePoint[1]+= (courseGrid[courseX][courseY].y+rng.nextDouble())*(1.0/(double)numSamples);
				
				samples.add(samplePoint);
			}
		}
		
		return samples;
	}
	
	/**
	 * A simple random sample
	 * @param numSamples
	 * @param rng
	 * @return a list of 2D double arrays, with each double in the array ranging [0,1)
	 */
	private static ArrayList<double[]> randomSamples(int numSamples, Random rng)
	{
		ArrayList<double[]> samples = new ArrayList<double[]>();
		
		for(int i = 0; i < numSamples; i++)
		{
			double[] sample = new double[2];
			sample[0] = rng.nextDouble();
			sample[1] = rng.nextDouble();
			samples.add(sample);
		}
		
		return samples;
	}
	
	/**
	 * Simple uniform sampling
	 * @param numSamples
	 * @return a list of 2D double arrays, with each double in the array ranging [0,1)
	 */
	private static ArrayList<double[]> uniformSamples(int numSamples)
	{
		ArrayList<double[]> samples = new ArrayList<double[]>();
		
		int sideDimension = (int)Math.sqrt(numSamples);
		
		double sampleSpacing = 1.0/(sideDimension+1.0);
		
		for(double xOffset = sampleSpacing; xOffset <= sideDimension*sampleSpacing; xOffset += sampleSpacing)
		{
			for(double yOffset = sampleSpacing; yOffset <= sideDimension*sampleSpacing; yOffset += sampleSpacing)
			{
				double [] sample = new double[2];
				sample[0] = xOffset;
				sample[1] = yOffset;
				samples.add(sample);
			}
		}
		return samples;
	}
	
	/**
	 * Performs the type of sampling requested
	 * @param sampleType string describing method
	 * @param numSamples
	 * @return a list of 2D double arrays, with each double in the array ranging [0,1)
	 */
	public static ArrayList<double[]> getSamples(String sampleType, int numSamples)
	{
		Random rng = new Random(System.currentTimeMillis());
		ArrayList<double[]> samples = new ArrayList<double[]>();
		switch(sampleType)
		{
			case "Uniform":
				samples = uniformSamples(numSamples);
				break;
			case "Random":
				samples = randomSamples(numSamples, rng);
				break;
			case "MultiJitter":
				samples = multiJitterSamples(numSamples, rng);
				break;
			default:
				double[] defaultSample = new double[2];
				defaultSample[0] = 0.5;
				defaultSample[1] = 0.5;
				samples.add(defaultSample);
				break;
		}
		return samples;
	}
}
