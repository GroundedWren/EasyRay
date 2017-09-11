package gui;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;

/**
 * A panel to edit a scene config file
 * @author alexaulabaugh
 */

@SuppressWarnings("serial")
public class ScenePanel extends ManualPanel
{
	//Arrays for every item in the file (lights or geometry)
	private ArrayList<String> itemLines;
	private ArrayList<JLabel> itemLabels;
	private ArrayList<JButton> editButtons;
	private ArrayList<JButton> deleteButtons;
	
	//background color input	
	private JSlider redSlider;
	private JSlider greenSlider;
	private JSlider blueSlider;
	private JLabel colorLabel;
	
	//sampling input
	private JComboBox<String> emitterSamplingType;
	private JTextField emitterSamplesize;
	
	private JFrame editFrame;
	private JPanel editPanel;
	
	//if we're reading from a file
	private String defaultFile;
	
	/**
	 * Sets up the UI and populates it with info in the config file at filename if
	 * filename isn't null
	 * @param filename the location of a config file to load from or null
	 */
	public ScenePanel(String filename)
	{
		restrictions = new ArrayList<Double[]>();
		buildRestrictions();
		
		editFrame = null;
		editPanel = null;
		
		this.setLayout(null);
		this.setOpaque(true);
		this.setBackground(Color.lightGray);
		
		defaultFile = "scene_New";
		
		itemLines = new ArrayList<String>();
		itemLabels = new ArrayList<JLabel>();
		editButtons = new ArrayList<JButton>();
		deleteButtons = new ArrayList<JButton>();
		
		int currentY = 20;
		
		//Background
		addLabel("Background Color", 20, currentY, 140, 20, false, true);
		currentY+= 20;
		
		redSlider = new JSlider(0, 255, 0);
		greenSlider = new JSlider(0, 255, 0);
		blueSlider = new JSlider(0, 255, 0);
		colorLabel = new JLabel();
		placeColorSliders(40, currentY, redSlider, greenSlider, blueSlider, colorLabel);
		currentY+=60;
		
		//Sampling Mode
		addLabel("Emitter Sampling", 20, currentY, 140, 20, true, true);
		
		emitterSamplesize = new JTextField(20);
		emitterSamplesize.setLocation(160, currentY);
		emitterSamplesize.setSize(40, 20);
		this.add(emitterSamplesize);
		JLabel sizeLabel = new JLabel("samples");
		sizeLabel.setSize(100, 20);
		sizeLabel.setLocation(200, currentY);
		this.add(sizeLabel);
		currentY += 25;
		
		String[] sampleModeList = {"Uniform", "Random", "MultiJitter"};
		emitterSamplingType = new JComboBox<String>(sampleModeList);
		emitterSamplingType.setLocation(15, currentY);
		emitterSamplingType.setSize(200, 20);
		this.add(emitterSamplingType);
		
		addLabel("Items", 330, currentY, 40, 20, true, true);
		
		JButton addItem = new JButton("Add Item");
		addItem.setName("none+add+none");
		addItem.setLocation(400, currentY);
		addItem.setSize(200, 20);
		addItem.addActionListener(this);
		this.add(addItem);
		
		//Save Buttons
		JButton saveButton = new JButton("Save to File");
		saveButton.setName("none+Save+none");
		saveButton.setLocation(400, 10);
		saveButton.setSize(200, 50);
		saveButton.addActionListener(this);
		this.add(saveButton);
		
		//Load from file if applicable
		if(filename != null)
		{
			defaultFile = filename.split("\\.")[0];
			buildFromFile(filename);
		}
		
		//Adds all itmes in itemLines to the UI
		buildFromLines();
	}
	
	/**
	 * Loads config settings from a specified filename into the UI
	 * @param filename path to the scene config file
	 */
	private void buildFromFile(String filename)
	{
		try
		{
			File sceneFile = new File("src/config/scene/" + filename);
			Scanner sceneFileScanner = new Scanner(sceneFile);
			while(sceneFileScanner.hasNextLine())
			{
				String line = sceneFileScanner.nextLine();
				String[] components = line.split(":");
				switch(components[0])
				{
					case "Background":
						String[] backTriplet = components[1].split(",");
						redSlider.setValue(Integer.parseInt(backTriplet[0]));
						greenSlider.setValue(Integer.parseInt(backTriplet[1]));
						blueSlider.setValue(Integer.parseInt(backTriplet[2]));
						break;
					case "EmitterSampleType":
						emitterSamplingType.setSelectedItem(components[1]);
						break;
					case "EmitterSampleSize":
						emitterSamplesize.setText(components[1]);
						break;
					default:
						itemLines.add(line);
				}
			}
			sceneFileScanner.close();
		}
		catch(Exception e)
		{}
	}
	
	/**
	 * Wipes all item listings from the panel
	 */
	private void removeItems()
	{
		for(JLabel itemL : itemLabels)
		{
			this.remove(itemL);
		}
		for(JButton editB : editButtons)
		{
			this.remove(editB);
		}
		for(JButton delB : deleteButtons)
		{
			this.remove(delB);
		}
		itemLabels = new ArrayList<JLabel>();
		editButtons = new ArrayList<JButton>();
		deleteButtons = new ArrayList<JButton>();
	}
	
	private String getColor(String description)
	{
		String[] descriptionComp = description.split(":");
		String elementColor = "rgb(0,0,0)";
		String[] attributes = descriptionComp[1].split(" ");
		String[] colorComponents = null;
		switch(descriptionComp[0])
		{
			case "Plane":
				colorComponents = attributes[2].split(",");
			break;
			case "Rectangle":
				colorComponents = attributes[3].split(",");
			break;
			case "Sphere":
				colorComponents = attributes[2].split(",");
			break;
			case "Triangle":
				colorComponents = attributes[3].split(",");
			break;
			case "Mesh":
				colorComponents = attributes[5].split(",");
			break;
			case "PointLight":
				colorComponents = attributes[1].split(",");
			break;
		}
		if(colorComponents != null)
			elementColor = "rgb(" + colorComponents[0] + "," + colorComponents[1] + "," + colorComponents[2] + ")";
		return elementColor;
	}
	
	/**
	 * Populates the bottom of the panel with all items in the scene
	 */
	private void buildFromLines()
	{
		removeItems();
		int yPosition = 150;
		int lineNum = 0;
		for(String item : itemLines)
		{
			String[] components = item.split(":");
			String itemColor = getColor(item);
			JLabel newLabel = new JLabel("<html><span style='background-color: " + itemColor + "'>&emsp;</span>&emsp;" +  components[0] + "</html>");
			newLabel.setLocation(20, yPosition);
			newLabel.setSize(330, 20);
			this.add(newLabel);
			itemLabels.add(newLabel);
						
			JButton edit = new JButton("Edit");
			edit.setName(item + "+" + "Edit" + "+" + lineNum);
			edit.addActionListener(this);
			edit.setLocation(275, yPosition);
			edit.setSize(30, 20);
			this.add(edit);
			editButtons.add(edit);
			
			JButton delete = new JButton("Delete");
			delete.setName(item + "+" + "Delete" + "+" + lineNum);
			delete.addActionListener(this);
			delete.setLocation(320, yPosition);
			delete.setSize(50, 20);
			this.add(delete);
			deleteButtons.add(delete);
			
			JLabel descriptionLabel = new JLabel(components[1]);
			descriptionLabel.setLocation(400, yPosition);
			descriptionLabel.setSize(300, 20);
			this.add(descriptionLabel);
			itemLabels.add(descriptionLabel);
			
			yPosition += 25;
			lineNum++;
		}
		this.repaint();
	}
	
	/**
	 * Writes the scene config file out
	 * @param saveFilename the filename to save to
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
			PrintWriter previewSceneWriter = new PrintWriter("src/config/scene/" + saveFilename, "UTF-8");
			previewSceneWriter.println("Background:" + redSlider.getValue() + "," + greenSlider.getValue() + "," + blueSlider.getValue());
			if(!emitterSamplesize.getText().equals(""))
			{
				previewSceneWriter.println("EmitterSampleType:" + (String)emitterSamplingType.getSelectedItem());
				previewSceneWriter.println("EmitterSampleSize:" + emitterSamplesize.getText());
			}
			for(String itemLine : itemLines)
			{
				previewSceneWriter.println(itemLine);
			}
			previewSceneWriter.close();	
		}
		catch(Exception e)
		{}
	}
	
	private void parseButtonClick(JButton clickedObj)
	{
		String[] buttonComponents = clickedObj.getName().split("\\+");
		if(buttonComponents[1].equals("Delete"))
		{
			itemLines.remove(Integer.parseInt(buttonComponents[2]));
		}
		else if(buttonComponents[1].equals("Edit") || buttonComponents[1].equals("add"))
		{
			String insertString = null;
			String type = buttonComponents[0].split(":")[0];
			if(buttonComponents[1].equals("Edit"))
			{
				insertString = itemLines.indexOf(buttonComponents[0]) + ":" + buttonComponents[0];
			}
			else
			{
				String[] types = {"Plane", "Rectangle", "Sphere", "Triangle", "Mesh", "PointLight"};
				type = (String)JOptionPane.showInputDialog(this, "Select Type", "", JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
			}
			/*
			String replacement = JOptionPane.showInputDialog("Enter Edits", buttonComponents[0]);
			itemLines.set(Integer.parseInt(buttonComponents[2]), replacement);
			*/
			editFrame = null;
			editPanel = null;
			if(type == null)
				return;
			switch(type)
			{
				case "Plane":
					editFrame = new JFrame("Plane");
					editPanel = new PlanePanel(insertString, this);
					break;
				case "Rectangle":
					editFrame = new JFrame("Rectangle");
					editPanel = new RectanglePanel(insertString, this);
					break;
				case "Sphere":
					editFrame = new JFrame("Sphere");
					editPanel = new SpherePanel(insertString, this);
					break;
				case "Triangle":
					editFrame = new JFrame("Triangle");
					editPanel = new TrianglePanel(insertString, this);
					break;
				case "Mesh":
					editFrame = new JFrame("Mesh");
					editPanel = new TriangleMeshPanel(insertString, this);
					break;
				case "PointLight":
					editFrame = new JFrame("PointLight");
					editPanel = new PointLightPanel(insertString, this);
					break;
			}
			if(editPanel != null)
			{
				editFrame.setSize(700, 550);
				editFrame.setContentPane(editPanel);
				editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				editFrame.setVisible(true);
			}
			else
			{
				editFrame = null;
			}
		}
		else if(buttonComponents[1].equals("Save"))
		{
			String saveFilename = JOptionPane.showInputDialog("Enter Filename (without extension)", defaultFile);
			if(saveFilename != null)
				saveToFile(saveFilename + ".txt");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() instanceof String)
		{
			String[] components = ((String)(e.getSource())).split(":");
			String description = components[1] + ":" + components[2];
			if(components[0].equals("new"))
			{
				itemLines.add(description);
			}
			else
			{
				int insertIndex = Integer.parseInt(components[0]);
				itemLines.set(insertIndex, description);
			}
			if(editFrame != null)
			{
				editFrame.dispose();
				editFrame = null;
				editPanel = null;
			}
		}
		else if(e.getSource() instanceof JButton)
		{
			parseButtonClick((JButton)e.getSource());
		}
		buildFromLines();
	}

	@Override
	protected void consolidateValues()
	{
		attributeValues = new ArrayList<String>();
		attributeValues.add(redSlider.getValue() + "");
		attributeValues.add(greenSlider.getValue() + "");
		attributeValues.add(blueSlider.getValue() + "");
		
		attributeValues.add((String)emitterSamplingType.getSelectedItem());
		attributeValues.add(emitterSamplesize.getText());
		
	}

	@Override
	protected void buildRestrictions()
	{
		restrictions.add(new Double[] {0.0, 255.0});
		restrictions.add(new Double[] {0.0, 255.0});
		restrictions.add(new Double[] {0.0, 255.0});
		restrictions.add(null);
		restrictions.add(new Double[] {1.0, Double.MAX_VALUE});
		
	}

}
