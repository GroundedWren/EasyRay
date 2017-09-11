package gui;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;

import rendering.TracingCoordinator;

/**
 * The first panel displayed to edit a settings config file & show rendering previews
 * @author alexaulabaugh
 */

@SuppressWarnings("serial")
public class SettingsPanel extends ManualPanel
{
	//These are items that the user can edit to change the settings file
	private JTextField ambient;
	private JTextField specular;
	private JTextField outputWidth;
	private JTextField outputHeight;
	private JTextField outputFilename;
	private JTextField recursionDepth;
	private JComboBox<String> cameraChoice;
	private JComboBox<String> sceneChoice;
	private JComboBox<String> structureChoice;
	private JLabel previewLabel;
	
	/**
	 * Builds all of the UI elements as empty then if filename is not null populates them
	 * @param filename the file we're loading from or null
	 * @param cameraFiles list of avaliable camera config files
	 * @param sceneFiles list of avaliable scene config files
	 */
	public SettingsPanel(String filename, String[] cameraFiles, String[] sceneFiles)
	{
		restrictions = new ArrayList<Double[]>();
		buildRestrictions();
		
		//everything is placed manually
		this.setLayout(null);
		
		this.setOpaque(true);
		this.setBackground(Color.lightGray);
		
		//Ambient weight
		addLabel("Ambient", 20, 20, 140, 20, false, true);
		
		ambient = new JTextField(20);
		ambient.setLocation(160, 20);
		ambient.setSize(40, 20);
		this.add(ambient);
		
		//Specular weight
		addLabel("Specular", 20, 40, 140, 20, false, true);
		
		specular = new JTextField(20);
		specular.setLocation(160, 40);
		specular.setSize(40, 20);
		this.add(specular);
		
		//Dimensions
		addLabel("Output Dimensions", 20, 60, 140, 20, false, true);
		
		outputWidth = new JTextField(20);
		outputWidth.setLocation(160, 60);
		outputWidth.setSize(40, 20);
		this.add(outputWidth);
		JLabel byLabel = new JLabel("by");
		byLabel.setSize(15, 20);
		byLabel.setLocation(200, 60);
		this.add(byLabel);
		outputHeight = new JTextField(20);
		outputHeight.setLocation(215, 60);
		outputHeight.setSize(40, 20);
		this.add(outputHeight);
		JLabel pxLabel = new JLabel("px");
		pxLabel.setSize(20, 20);
		pxLabel.setLocation(255, 60);
		this.add(pxLabel);
		
		//Output Filename
		addLabel("Output Filename", 20, 80, 140, 20, false, true);
		
		outputFilename = new JTextField(95);
		outputFilename.setLocation(160, 80);
		outputFilename.setSize(95, 20);
		this.add(outputFilename);
		JLabel pngLabel = new JLabel(".png");
		pngLabel.setSize(30, 20);
		pngLabel.setLocation(255, 80);
		this.add(pngLabel);
		
		//Recursion Depth
		addLabel("Max Recursion", 20, 100, 140, 20, false, true);
		
		recursionDepth = new JTextField(20);
		recursionDepth.setLocation(160, 100);
		recursionDepth.setSize(40, 20);
		this.add(recursionDepth);
		
		//Camera Choice
		addLabel("Camera File", 20, 180, 140, 20, true, true);
		
		cameraChoice = new JComboBox<String>(cameraFiles);
		cameraChoice.setLocation(15, 205);
		cameraChoice.setSize(200, 20);
		this.add(cameraChoice);
		
		//Scene Choice
		addLabel("Scene File", 20, 240, 140, 20, true, true);
		
		sceneChoice = new JComboBox<String>(sceneFiles);
		sceneChoice.setLocation(15, 265);
		sceneChoice.setSize(200, 20);
		this.add(sceneChoice);
		
		//Acceleration Structure
		String[] structures = {"FreeObjects", "RegularGrid"};
		addLabel("Acceleration Structure", 20, 300, 140, 20, true, true);
		
		structureChoice = new JComboBox<String>(structures);
		structureChoice.setLocation(15, 325);
		structureChoice.setSize(200, 20);
		this.add(structureChoice);
		
		//Image Preview
		ImageIcon preview = new ImageIcon("src/images/EmptyPreview.png", "Preview");
		previewLabel = new JLabel(preview);
		previewLabel.setLocation(435, 20);
		previewLabel.setSize(200, 200);
		this.add(previewLabel);
		
		JButton previewButton = new JButton("Render Preview");
		previewButton.setName("Preview");
		previewButton.setLocation(435, 220);
		previewButton.setSize(200, 20);
		previewButton.addActionListener(this);
		this.add(previewButton);
		
		//Save & Render Buttons
		JButton saveButton = new JButton("Save to File");
		saveButton.setName("Save");
		saveButton.setLocation(15, 400);
		saveButton.setSize(300, 100);
		saveButton.addActionListener(this);
		this.add(saveButton);
		
		JButton renderButton = new JButton("Render");
		renderButton.setName("Render");
		renderButton.setLocation(385, 400);
		renderButton.setSize(300, 100);
		renderButton.addActionListener(this);
		this.add(renderButton);
		
		if(filename != null)
		{
			loadFromFile(filename);
		}
	}
	
	/**
	 * Populates values into the UI elements from a settings config file
	 * @param filename path to the config file
	 */
	private void loadFromFile(String filename)
	{
		try
		{
			File settingsFile = new File("src/config/settings/" + filename);
			Scanner settingsFileScanner = new Scanner(settingsFile);
			while(settingsFileScanner.hasNextLine())
			{
				String line = settingsFileScanner.nextLine();
				String[] components = line.split(":");
				switch(components[0])
				{
					case "Ambient":
						ambient.setText(components[1]);
						break;
					case "Specular":
						specular.setText(components[1]);
						break;
					case "OutputDim":
						String[] dims = components[1].split(" ");
						outputWidth.setText(dims[0]);
						outputHeight.setText(dims[1]);
						break;
					case "OutputFilename":
						outputFilename.setText(components[1].split("\\.")[0]);
						break;
					case "CameraFilename":
						cameraChoice.setSelectedItem(components[1]);
						break;
					case "SceneFilename":
						sceneChoice.setSelectedItem(components[1]);
						break;
					case "MaxRecur":
						recursionDepth.setText(components[1]);
						break;
					case "FreeObjects":
						structureChoice.setSelectedItem(components[0]);
						break;
					case "RegularGrid":
						structureChoice.setSelectedItem(components[0]);
						break;
				}
				
			}
			settingsFileScanner.close();
		}
		catch(Exception e)
		{}
	}
	
	/**
	 * Converts the text on the selected acceleration structure to
	 * what would actually go in the config file
	 * @return
	 */
	private String getAccelerationString()
	{
		String structureName = (String)structureChoice.getSelectedItem();
		switch(structureName)
		{
			case "RegularGrid":
				//Regular grid constant is fixed at 3 for now
				return "RegularGrid:3";
			default:
				return "FreeObjects";
		}
	}
	
	/**
	 * Edits the scene config files for the specified scene config file
	 * to be more friendly for a quick preview
	 * and places them in scenePreview
	 * @param sceneFilename the scene file to be watered down
	 */
	private void generatePreviewScene(String sceneFilename)
	{
		try
		{
			PrintWriter previewSceneWriter = new PrintWriter("src/config/scene/scene_Preview.txt", "UTF-8");
			File sceneFile = new File("src/config/scene/" + sceneFilename);
			Scanner sceneFileScanner = new Scanner(sceneFile);
			while(sceneFileScanner.hasNextLine())
			{
				String line = sceneFileScanner.nextLine();
				if(line.contains("EmitterSampleType"))
				{
					previewSceneWriter.println("EmitterSampleType:Uniform");
				}
				else if(line.contains("EmitterSampleSize"))
				{
					previewSceneWriter.println("EmitterSampleSize:1");
				}
				else
				{
					previewSceneWriter.println(line);
				}
			}
			sceneFileScanner.close();
			previewSceneWriter.close();
		}
	catch(Exception e)
	{}
	}
	
	/**
	 * see generatePreviewString(String sceneFilename)
	 * This is the same, but for the specified camera
	 * @param cameraFilename
	 */
	private void generatePreviewCamera(String cameraFilename)
	{
		try
		{
			PrintWriter previewCameraWriter = new PrintWriter("src/config/camera/camera_Preview.txt", "UTF-8");
			File cameraFile = new File("src/config/camera/" + cameraFilename);
			Scanner cameraFileScanner = new Scanner(cameraFile);
			while(cameraFileScanner.hasNextLine())
			{
				String line = cameraFileScanner.nextLine();
				if(line.contains("SampleType"))
				{
					previewCameraWriter.println("SampleType:None");
				}
				else if(line.contains("SampleSize"))
				{
					previewCameraWriter.println("SampleSize:1");
				}
				else if(line.contains("wres"))
				{
					previewCameraWriter.println("wres:100");
				}
				else if(line.contains("hres"))
				{
					previewCameraWriter.println("hres:100");
				}
				else
				{
					previewCameraWriter.println(line);
				}
			}
			cameraFileScanner.close();
			previewCameraWriter.close();
		}
		catch(Exception e)
		{}
	}
	
	/**
	 * Loads a preview of the specified scene into the preview image and displays it
	 */
	private void generatePreview()
	{
		consolidateValues();
		String errors = checkValues();
		if(!errors.equals(""))
		{
			JOptionPane.showMessageDialog(this, errors);
			return;
		}
		//Citation: http://stackoverflow.com/questions/2885173/how-do-i-create-a-file-and-write-to-it-in-java
		try
		{	
			saveToFile("settings_Preview.txt", true);
			
			TracingCoordinator previewCoordinator = new TracingCoordinator();
			previewCoordinator.loadFromFile("settings_Preview.txt");
			previewCoordinator.renderToFile("src/images/preview.png");
			
			this.remove(previewLabel);
			ImageIcon preview = new ImageIcon("src/images/preview.png", "Preview");
			preview.getImage().flush();
			previewLabel = new JLabel(preview);
			previewLabel.setLocation(435, 20);
			previewLabel.setSize(200, 200);
			this.add(previewLabel);
			this.repaint();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Failed to Generate Preview");
			this.remove(previewLabel);
			ImageIcon preview = new ImageIcon("src/images/EmptyPreview.png", "Preview");
			previewLabel = new JLabel(preview);
			previewLabel.setLocation(435, 20);
			previewLabel.setSize(200, 200);
			this.add(previewLabel);
			this.repaint();
		}
		removeFile("settings_preview");
	}
	
	private void removeFile(String filename)
	{
		File toRemove = new File("src/config/settings/" + filename);
		try
		{
			toRemove.delete();
		}
		catch(Exception e)
		{}
	}
	
	/**
	 * Writes the settings configuration to file
	 * @param saveFilename
	 * @param isPreview true if this is for a preview
	 */
	private void saveToFile(String saveFilename, Boolean isPreview)
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
			PrintWriter previewSettingsWriter = new PrintWriter("src/config/settings/" + saveFilename, "UTF-8");
			if(isPreview)
			{
				generatePreviewScene((String)sceneChoice.getSelectedItem());
				generatePreviewCamera((String)cameraChoice.getSelectedItem());
				
				previewSettingsWriter.println("OutputDim:200 200");
				previewSettingsWriter.println("OutputFilename:preview.png");
				previewSettingsWriter.println("CameraFilename:camera_Preview.txt");
				previewSettingsWriter.println("SceneFilename:scene_Preview.txt");
				previewSettingsWriter.println("MaxRecur:2");
			}
			else
			{
				previewSettingsWriter.println("OutputDim:" + outputWidth.getText() + " " + outputHeight.getText());
				previewSettingsWriter.println("OutputFilename:" + outputFilename.getText() + ".png");
				previewSettingsWriter.println("CameraFilename:" + (String)cameraChoice.getSelectedItem());
				previewSettingsWriter.println("SceneFilename:" + (String)sceneChoice.getSelectedItem());
				previewSettingsWriter.println("MaxRecur:" + recursionDepth.getText());
			}
			previewSettingsWriter.println("Ambient:" + ambient.getText());
			previewSettingsWriter.println("Specular:" + specular.getText());
			previewSettingsWriter.println(getAccelerationString());
			previewSettingsWriter.close();
		}
		catch(Exception e)
		{}
	}
	
	/**
	 * Takes an updated config file list and places it into the drop down boxes
	 * @param cameraFiles
	 * @param sceneFiles
	 */
	public void updateFiles(String[] cameraFiles, String[] sceneFiles)
	{
		cameraChoice.removeAllItems();
		for(String camera : cameraFiles)
			cameraChoice.addItem(camera);
		
		sceneChoice.removeAllItems();
		for(String scene : sceneFiles)
			sceneChoice.addItem(scene);
		
		this.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JButton clickedButton = (JButton)e.getSource();
		if(clickedButton.getName().equals("Preview"))
		{
			try
			{
				generatePreview();
			}
			catch(Exception err)
			{
				JOptionPane.showMessageDialog(this, "Error loading preview");
			}
		}
		else if(clickedButton.getName().equals("Save"))
		{
			String saveFilename = JOptionPane.showInputDialog("Enter Filename (without extension)");
			saveToFile(saveFilename + ".txt", false);
		}
		else if(clickedButton.getName().equals("Render"))
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
				saveToFile("settings_CurrentRender.txt", false);
				TracingCoordinator renderCoordinator = new TracingCoordinator();
				renderCoordinator.loadFromFile("settings_CurrentRender.txt");
				renderCoordinator.renderToFile(null);
				@SuppressWarnings("unused")
				ImageDisplay display = new ImageDisplay(outputFilename.getText() + ".png", Integer.parseInt(outputWidth.getText()), Integer.parseInt(outputHeight.getText()));
			}
			catch(Exception err)
			{
				JOptionPane.showMessageDialog(this, "Error rendering scene");
			}
			removeFile("settings_CurrentRender.txt");
		}
	}

	@Override
	protected void consolidateValues()
	{
		/**
		 * 
			private JTextField ambient;
			private JTextField specular;
			private JTextField outputWidth;
			private JTextField outputHeight;
			private JTextField outputFilename;
			private JTextField recursionDepth;
			private JComboBox<String> cameraChoice;
			private JComboBox<String> sceneChoice;
			private JComboBox<String> structureChoice;
		 */
		attributeValues = new ArrayList<String>();
		attributeValues.add(ambient.getText());
		attributeValues.add(specular.getText());
		attributeValues.add(outputWidth.getText());
		attributeValues.add(outputHeight.getText());
		attributeValues.add(outputFilename.getText());
		attributeValues.add(recursionDepth.getText());
		attributeValues.add((String)cameraChoice.getSelectedItem());
		attributeValues.add((String)sceneChoice.getSelectedItem());
		attributeValues.add((String)structureChoice.getSelectedItem());
		
	}

	@Override
	protected void buildRestrictions()
	{
		restrictions.add(new Double[] {0.0, Double.MAX_VALUE});
		restrictions.add(new Double[] {-1.0, Double.MAX_VALUE});
		restrictions.add(new Double[] {0.0, Double.MAX_VALUE});
		restrictions.add(new Double[] {0.0, Double.MAX_VALUE});
		restrictions.add(null);
		restrictions.add(new Double[] {1.0, Double.MAX_VALUE});
		restrictions.add(null);
		restrictions.add(null);
		restrictions.add(null);
	}
}
