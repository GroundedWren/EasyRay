package gui;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;

/**
 * Panel to edit a camera config file
 * @author alexaulabaugh
 */

@SuppressWarnings("serial")
public class CameraPanel extends ManualPanel
{
	//Eyepoint input
	private JTextField eyeX;
	private JTextField eyeY;
	private JTextField eyeZ;
	
	//Lookat vector input
	private JTextField lookX;
	private JTextField lookY;
	private JTextField lookZ;
	
	//up vector input
	private JTextField upX;
	private JTextField upY;
	private JTextField upZ;
	
	//camera viewplane resolution settings
	private JTextField wres;
	private JTextField hres;
	
	private JTextField pixelWidth;
	private JTextField fov;
	
	//anti-aliasing
	private JComboBox<String> renderingChoice;
	private JComboBox<String> samplingChoice;	
	private JTextField sampleSize;
	
	//the file loaded from / saved to by default
	private String defaultFile;
	
	/**
	 * Loads UI elements then populates them from the specified config file if set
	 * @param filename
	 */
	public CameraPanel(String filename)
	{
		restrictions = new ArrayList<Double[]>();
		buildRestrictions();
		
		this.setLayout(null);
		this.setOpaque(true);
		this.setBackground(Color.lightGray);
		
		defaultFile = "camera_new";
		
		//Eyepoint
		addLabel("Eyepoint", 20, 20, 140, 20, false, true);
		
		eyeX = new JTextField(20);
		eyeY = new JTextField(20);
		eyeZ = new JTextField(20);
		placeTriplet(160, 20, eyeX, "x", eyeY, "y", eyeZ, "z");
		
		//Lookat
		addLabel("Lookat", 20, 40, 140, 20, false, true);
		
		lookX = new JTextField(20);
		lookY = new JTextField(20);
		lookZ = new JTextField(20);
		placeTriplet(160, 40, lookX, "x", lookY, "y", lookZ, "z");
		
		//Lookat
		addLabel("Up", 20, 60, 140, 20, false, true);
		
		upX = new JTextField(20);
		upY = new JTextField(20);
		upZ = new JTextField(20);
		placeTriplet(160, 60, upX, "x", upY, "y", upZ, "z");
		
		//Resolution
		addLabel("Resolution", 20, 80, 140, 20, false, true);
		
		wres = new JTextField(20);
		wres.setLocation(160, 80);
		wres.setSize(40, 20);
		this.add(wres);
		JLabel byLabel = new JLabel("by");
		byLabel.setSize(15, 20);
		byLabel.setLocation(200, 80);
		this.add(byLabel);
		hres = new JTextField(20);
		hres.setLocation(215, 80);
		hres.setSize(40, 20);
		this.add(hres);
		JLabel pxLabel = new JLabel("px");
		pxLabel.setSize(20, 20);
		pxLabel.setLocation(255, 80);
		this.add(pxLabel);
		
		//Pixel Width
		addLabel("Pixel Width", 20, 100, 140, 20, false, true);
		
		pixelWidth = new JTextField(20);
		pixelWidth.setLocation(160, 100);
		pixelWidth.setSize(40, 20);
		this.add(pixelWidth);
		
		//FOV
		addLabel("FOV", 20, 120, 140, 20, false, true);
		
		fov = new JTextField(20);
		fov.setLocation(160, 120);
		fov.setSize(40, 20);
		this.add(fov);
		
		JLabel degLabel = new JLabel("degrees");
		degLabel.setSize(100, 20);
		degLabel.setLocation(200, 120);
		this.add(degLabel);
		
		//Rendering Mode
		addLabel("Rendering Mode", 20, 180, 140, 20, true, true);
		
		String[] renderModeList = {"perspective", "orthographic"};
		renderingChoice = new JComboBox<String>(renderModeList);
		renderingChoice.setLocation(15, 205);
		renderingChoice.setSize(200, 20);
		this.add(renderingChoice);
		
		//Sampling Mode
		addLabel("Sampling Mode", 20, 240, 140, 20, true, true);
		
		String[] sampleModeList = {"None", "Random", "Uniform", "MultiJitter"};
		samplingChoice = new JComboBox<String>(sampleModeList);
		samplingChoice.setLocation(15, 265);
		samplingChoice.setSize(200, 20);
		this.add(samplingChoice);
		
		//Sample Size
		addLabel("Sample Size", 20, 295, 140, 20, false, true);
		
		sampleSize = new JTextField(20);
		sampleSize.setLocation(160, 295);
		sampleSize.setSize(40, 20);
		this.add(sampleSize);
		
		//Save Buttons
		JButton saveButton = new JButton("Save to File");
		saveButton.setName("Save");
		saveButton.setLocation(200, 400);
		saveButton.setSize(300, 100);
		saveButton.addActionListener(this);
		this.add(saveButton);
		
		if(filename != null)
		{
			defaultFile = filename.split("\\.")[0];
			loadFromFile(filename);
		}
		
	}
	
	/**
	 * Populates the UI elements from the specified camera config file
	 * @param filename path to the camera config file
	 */
	private void loadFromFile(String filename)
	{
		try
		{
			File cameraFile = new File("src/config/camera/" + filename);
			Scanner cameraFileScanner = new Scanner(cameraFile);
			while(cameraFileScanner.hasNextLine())
			{
				String line = cameraFileScanner.nextLine();
				String[] components = line.split(":");
				switch(components[0])
				{
					case "Eyepoint":
						String[] eyeTriplet = components[1].split(",");
						eyeX.setText(eyeTriplet[0]);
						eyeY.setText(eyeTriplet[1]);
						eyeZ.setText(eyeTriplet[2]);
						break;
					case "Lookat":
						String[] lookTriplet = components[1].split(",");
						lookX.setText(lookTriplet[0]);
						lookY.setText(lookTriplet[1]);
						lookZ.setText(lookTriplet[2]);
						break;
					case "Up":
						String[] upTriplet = components[1].split(",");
						upX.setText(upTriplet[0]);
						upY.setText(upTriplet[1]);
						upZ.setText(upTriplet[2]);
						break;
					case "PixelWidth":
						pixelWidth.setText(components[1]);
						break;
					case "FOV":
						fov.setText(components[1]);
						break;
					case "wres":
						wres.setText(components[1]);
						break;
					case "hres":
						hres.setText(components[1]);
						break;
					case "mode":
						renderingChoice.setSelectedItem(components[1]);
						break;
					case "SampleType":
						samplingChoice.setSelectedItem(components[1]);
						break;
					case "SampleSize":
						sampleSize.setText(components[1]);
						break;
				}
				
			}
			cameraFileScanner.close();
		}
		catch(Exception e)
		{}
	}
	
	/**
	 * Saves the configuration settings to file
	 * @param saveFilename
	 */
	private void saveToFile(String saveFilename)
	{
		consolidateValues();
		String errors = checkValues();
		if(!errors.equals(""))
		{
			JOptionPane.showMessageDialog(this, errors);
			return;
		}
		try
		{
			PrintWriter previewCameraWriter = new PrintWriter("src/config/camera/" + saveFilename, "UTF-8");
			previewCameraWriter.println("Eyepoint:" + eyeX.getText() + "," + eyeY.getText() + "," + eyeZ.getText());
			previewCameraWriter.println("Lookat:" + lookX.getText() + "," + lookY.getText() + "," + lookZ.getText());
			previewCameraWriter.println("Up:" + upX.getText() + "," + upY.getText() + "," + upZ.getText());
			previewCameraWriter.println("PixelWidth:" + pixelWidth.getText());
			previewCameraWriter.println("FOV:" + fov.getText());
			previewCameraWriter.println("wres:" + wres.getText());
			previewCameraWriter.println("hres:" + hres.getText());
			previewCameraWriter.println("mode:" + (String)renderingChoice.getSelectedItem());
			previewCameraWriter.println("SampleType:" + (String)samplingChoice.getSelectedItem());
			previewCameraWriter.println("SampleSize:" + sampleSize.getText());
			previewCameraWriter.close();
		}
		catch(Exception e)
		{}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String saveFilename = JOptionPane.showInputDialog("Enter Filename (without extension)", defaultFile);
		if(saveFilename != null)
			saveToFile(saveFilename + ".txt");
	}

	@Override
	protected void consolidateValues()
	{
		attributeValues = new ArrayList<String>();
		attributeValues.add(eyeX.getText());
		attributeValues.add(eyeY.getText());
		attributeValues.add(eyeZ.getText());
		attributeValues.add(lookX.getText());
		attributeValues.add(lookY.getText());
		attributeValues.add(lookZ.getText());
		attributeValues.add(upX.getText());
		attributeValues.add(upY.getText());
		attributeValues.add(upZ.getText());
		attributeValues.add(pixelWidth.getText());
		attributeValues.add(fov.getText());
		attributeValues.add(wres.getText());
		attributeValues.add(hres.getText());
		attributeValues.add((String)renderingChoice.getSelectedItem());
		attributeValues.add((String)samplingChoice.getSelectedItem());
		attributeValues.add(sampleSize.getText());
	}

	@Override
	protected void buildRestrictions()
	{
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		
		restrictions.add(new Double[] {0.0, Double.MAX_VALUE});
		restrictions.add(new Double[] {0.0, 180.0});
		
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(null);
		restrictions.add(null);
		restrictions.add(new Double[] {1.0, Double.MAX_VALUE});
	}

}
