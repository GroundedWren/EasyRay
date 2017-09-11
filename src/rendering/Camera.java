package rendering;

import java.util.*;

import javax.imageio.ImageIO;
import java.lang.Math;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Contains all information about the pinhole camera and the view plane
 * Not scene-specific, the same camera can be used for multiple scenes
 * Not output-dimension specific
 * @author alexaulabaugh
 *
 */
public class Camera
{
	//The eyepoint of the pinhole camera
	private TripletVector eyePoint;
	//The direction the camera faces
	private TripletVector w;
	//The up vector of the camera
	private TripletVector u;
	//The remaining cameraspace dimension
	private TripletVector v;
	
	//Anti-Aliasing controls
	private String sampleType;
	private int sampleSize;
	
	//The number of pixels on the view plane
	private int filmWidth;
	private int filmHeight;
	
	private double pixelWidth;
	//The field of view in radians
	private double fov;
	private String renderMode;
	//The colors on the view plane, film is an analogy
	private TripletVector[][] film;
	
	/**
	 * The constructor for a camera
	 * @param eyePt eyePoint
	 * @param lookat the lookat vector, or the point in the scene the camera faces
	 * @param up the up vector
	 * @param wres filmWidth
	 * @param hres filmHeight
	 * @param pwidth pixelWidth
	 * @param fovDeg fov in degrees
	 * @param mode render mode: "perspective" or "orthographic"
	 */
	public Camera(TripletVector eyePt, TripletVector lookat, TripletVector up, int wres, int hres, double pwidth, double fovDeg, String mode, String smplType, int smplSize)
	{
		up.normalize();
		eyePoint = eyePt;
		w = lookat.sub(eyePt);
		u = up.cross(w);
		v = u.cross(w);
		w.normalize();
		u.normalize();
		v.normalize();
		
		filmWidth = wres;
		filmHeight = hres;
		pixelWidth = pwidth;
		
		fov = Math.toRadians(fovDeg);
		
		renderMode = mode;
		
		film = new TripletVector[filmWidth][filmHeight];
		for(int i = 0; i < filmWidth; i++)
		{
			for(int j = 0; j < filmHeight; j++)
			{
				film[i][j] = new TripletVector(0, 0, 0);
			}
		}
		
		sampleType = smplType;
		sampleSize = smplSize;
	}
	
	/**
	 * Assuming one ray is shot from the center of each pixel, generates an arrayList of rays for the
	 * camera configuration
	 * @return
	 */
	public ArrayList<Ray>getRays()
	{
		ArrayList<Ray> cameraRays = new ArrayList<Ray>();
				
		double aspectRatio = filmWidth / filmHeight;
		
		for(int pixelx = 0; pixelx < filmWidth; pixelx++)
		{
			for(int pixely = 0; pixely < filmHeight; pixely++)
			{
				ArrayList<double[]> samples = Sampler.getSamples(sampleType, sampleSize);
				for(double[] sample : samples)
				{
					double pixelNDCx = (pixelx+0.5+(sample[0]-0.5))/filmWidth;
					double pixelNDCy = (pixely+0.5+(sample[1]-0.5))/filmHeight;
				
					double pixelScreenX = 2*pixelNDCx - 1;
					double pixelScreenY = 1 - 2*pixelNDCy;
															
					//Rays are shot from the eyepoint
					if(renderMode.equals("perspective"))
					{
						double pixelCameraX = pixelScreenX*aspectRatio*Math.tan(fov/2.0)*pixelWidth;
						double pixelCameraY = pixelScreenY*Math.tan(fov/2.0)*pixelWidth;
					
						TripletVector rayDirection = new TripletVector(u.scale(pixelCameraX).add(v.scale(pixelCameraY).add(w.scale(1))));
						rayDirection.normalize();
						cameraRays.add(new Ray(eyePoint, rayDirection, pixelx, pixely, 1.0, 1.0, 0));
					}
					//Rays are shot perpendicular to the view plane
					else if(renderMode.equals("orthographic"))
					{
						double pixelCameraX = pixelScreenX*aspectRatio*pixelWidth;
						double pixelCameraY = pixelScreenY*pixelWidth;
						TripletVector rayDirection = new TripletVector(w);
						rayDirection.normalize();
						cameraRays.add(new Ray(new TripletVector(pixelCameraX, pixelCameraY, 0).add(w.scale(1)), rayDirection, pixelx, pixely, 1.0, 1.0, 0));
					}
				}
				
			}
		}
		return cameraRays;
	}

	/**
	 * Add a color to a pixel on the film, weighting it appropriately
	 * @param newColor the color to be added
	 * @param coords the location of the pixel
	 */
	public void updatePixel(TripletVector newColor, int[] coords)
	{
		int i = coords[0];
		int j = coords[1];
		film[i][j] = film[i][j].add(newColor.scale(1.0/(double)sampleSize));
	}
	
	/**
	 * Prints the film to a file
	 * @param filename
	 * @param outputDim the dimensions of the output
	 */
	public void writeImage(String filename, int[] outputDim)
	{
		BufferedImage outputImage = new BufferedImage(outputDim[0], outputDim[1], BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < outputDim[0]; i++)
		{
			for(int j = 0; j < outputDim[1]; j++)
			{
				int iAdjusted = (int)((i/(float)outputDim[0])*filmWidth);
				int jAdjusted = (int)((j/(float)outputDim[1])*filmHeight);
				double[] filmRGB = film[iAdjusted][jAdjusted].getComponents();
				for(int rgbIndex = 0; rgbIndex < 3; rgbIndex+=1)
				{
					if(filmRGB[rgbIndex] > 1)
						filmRGB[rgbIndex] = 1;
				}
				Color pixelColor = new Color((float)filmRGB[0], (float)filmRGB[1], (float)filmRGB[2]);
				outputImage.setRGB(i, j, pixelColor.getRGB());
			}
		}
		File outputfile = new File(filename);
		try
		{
			ImageIO.write(outputImage, "png", outputfile);
		}
		catch(IOException e)
		{
			System.out.println("Error: Unable to write image to file: " + filename);
		}
	}
}
