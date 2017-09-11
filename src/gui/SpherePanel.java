package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import static java.nio.file.StandardCopyOption.*;

@SuppressWarnings("serial")
public class SpherePanel extends ManualPanel
{
	//Sphere:x,y,z radius r,g,b texture specular_exponent reflectivity glossyExponent refractivity refractionIndex emitter(True/False)
	
	private JTextField pointX;
	private JTextField pointY;
	private JTextField pointZ;
	
	private JTextField radius;
	
	//color input	
	private JSlider redSlider;
	private JSlider greenSlider;
	private JSlider blueSlider;
	private JLabel colorLabel;
	
	private JFileChooser newTexture;
	private JComboBox<String> textureFile;
	
	private JTextField specular;
	
	private JTextField reflect;
	private JTextField glossyExp;
	
	private JTextField refract;
	private JTextField indexOfRefraction;
	
	private JComboBox<String> emitter;
	
	private String index;
		
	ActionListener updateElement;
	
	public SpherePanel(String loadString, ActionListener callback)
	{
		restrictions = new ArrayList<Double[]>();
		buildRestrictions();
		
		index = "new";
		updateElement = callback;
		this.setLayout(null);
		this.setOpaque(true);
		this.setBackground(Color.lightGray);
		
		int currentY = 20;
		
		//Point
		addLabel("Point", 20, currentY, 140, 20, false, true);
		
		pointX = new JTextField(20);
		pointY = new JTextField(20);
		pointZ = new JTextField(20);
		placeTriplet(160, currentY, pointX, "x", pointY, "y", pointZ, "z");
		currentY += 20;
		
		//Radius
		addLabel("Radius", 20, currentY, 140, 20, false, true);
		
		radius = new JTextField(20);
		radius.setLocation(160, currentY);
		radius.setSize(40, 20);
		this.add(radius);
		currentY += 20;
		
		//DefaultColor
		addLabel("Default Color", 20, currentY, 140, 20, false, true);
		currentY+=20;
		redSlider = new JSlider(0, 255, 0);
		greenSlider = new JSlider(0, 255, 0);
		blueSlider = new JSlider(0, 255, 0);
		colorLabel = new JLabel();
		placeColorSliders(40, currentY, redSlider, greenSlider, blueSlider, colorLabel);
		currentY+=60;
		
		//Texture
		addLabel("Texture", 20, currentY, 140, 20, false, true);
		
		FilenameFilter textfileFilter = new VisibleFileFilter();
		
		File texturesFolder = new File("src/images");
		String[] textureFiles = new String[texturesFolder.listFiles(textfileFilter).length+1];
		int i = 0;
		for(File textureFile : texturesFolder.listFiles(textfileFilter))
		{
			textureFiles[i] = textureFile.getName();
			i+=1;
		}
		textureFiles[texturesFolder.listFiles(textfileFilter).length] = "none";
		
		textureFile = new JComboBox<String>(textureFiles);
		textureFile.setLocation(160, currentY);
		textureFile.setSize(200, 20);
		textureFile.setSelectedItem("none");
		this.add(textureFile);
		
		newTexture = new JFileChooser();
		JButton importTexture = new JButton("Import");
		importTexture.setLocation(360, currentY);
		importTexture.setSize(50, 20);
		importTexture.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//Citation: https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
				if(newTexture.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					File f = newTexture.getSelectedFile();
					if(!f.getAbsolutePath().endsWith(".png"))
						JOptionPane.showMessageDialog(null, "Error, PNG only!");
					else
					{
						System.out.println(f.getAbsolutePath());
						try
						{
							Files.copy(Paths.get(f.getAbsolutePath()), Paths.get("src/images/" + f.getName()), REPLACE_EXISTING);
							textureFile.addItem(f.getName());
							textureFile.setSelectedItem(f.getName());
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
							JOptionPane.showMessageDialog(null, "Error loading file!");
						}
					}
				}
			}
		});
		this.add(importTexture);
		
		currentY += 20;
		
		//Specular
		addLabel("Specular", 20, currentY, 140, 20, false, true);
		
		specular = new JTextField(20);
		specular.setLocation(160, currentY);
		specular.setSize(95, 20);
		this.add(specular);
		currentY += 20;
		
		//Reflectivity
		addLabel("Reflectivity", 20, currentY, 140, 20, false, true);
		
		reflect = new JTextField(20);
		reflect.setLocation(160, currentY);
		reflect.setSize(40, 20);
		this.add(reflect);
		currentY += 20;
		
		//GlossyExponent
		addLabel("Glossy", 20, currentY, 140, 20, false, true);
		
		glossyExp = new JTextField(20);
		glossyExp.setLocation(160, currentY);
		glossyExp.setSize(40, 20);
		this.add(glossyExp);
		currentY += 20;
		
		//Refractivity
		addLabel("Refractivity", 20, currentY, 140, 20, false, true);
		
		refract = new JTextField(20);
		refract.setLocation(160, currentY);
		refract.setSize(40, 20);
		this.add(refract);
		currentY += 20;
		
		//Index of Refraction
		addLabel("Refraction Index", 20, currentY, 140, 20, false, true);
		
		indexOfRefraction = new JTextField(20);
		indexOfRefraction.setLocation(160, currentY);
		indexOfRefraction.setSize(40, 20);
		this.add(indexOfRefraction);
		currentY += 20;
		
		//Emitter
		addLabel("Emitter", 20, currentY, 140, 20, false, true);
		
		String[] options = {"False", "True"};
		emitter = new JComboBox<String>(options);
		emitter.setLocation(160, currentY);
		emitter.setSize(100, 20);
		this.add(emitter);
		currentY += 20;
		
		//Save Buttons
		JButton saveButton = new JButton("Save Changes");
		saveButton.setName("Save");
		saveButton.setLocation(200, 400);
		saveButton.setSize(300, 100);
		saveButton.addActionListener(this);
		this.add(saveButton);
		
		if(loadString != null)
			loadFromString(loadString);
		
	}
	
	private void loadFromString(String loadString)
	{
		String[] components = loadString.split(":");
		index = components[0];
		String[] attributes = components[2].split(" ");
		String[] point = attributes[0].split(",");
		pointX.setText(point[0]);
		pointY.setText(point[1]);
		pointZ.setText(point[2]);
		radius.setText(attributes[1]);
		String[] rgb = attributes[2].split(",");
		redSlider.setValue(Integer.parseInt(rgb[0]));
		greenSlider.setValue(Integer.parseInt(rgb[1]));
		blueSlider.setValue(Integer.parseInt(rgb[2]));
		if(attributes[3].contains("/"))
			textureFile.setSelectedItem(attributes[3].split("/")[2]);
		else
			textureFile.setSelectedItem(attributes[3]);
		specular.setText(attributes[4]);
		reflect.setText(attributes[5]);
		glossyExp.setText(attributes[6]);
		refract.setText(attributes[7]);
		indexOfRefraction.setText(attributes[8]);
		emitter.setSelectedItem(attributes[9]);
		this.repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		consolidateValues();
		String errors = checkValues();
		if(!errors.equals(""))
		{
			JOptionPane.showMessageDialog(this, errors);
			return;
		}
		String objectString = index + ":Sphere:";
		objectString = objectString + pointX.getText() + "," + pointY.getText() + "," + pointZ.getText();
		objectString = objectString + " " + radius.getText();
		objectString = objectString + " " + redSlider.getValue() + "," + greenSlider.getValue() + "," + blueSlider.getValue();
		if(textureFile.getSelectedItem().equals("none"))
			objectString = objectString + " none";
		else
			objectString = objectString + " src/images/" + textureFile.getSelectedItem();
		objectString = objectString + " " + specular.getText() + " " + reflect.getText() + " " + glossyExp.getText();
		objectString = objectString + " " + refract.getText() + " " + indexOfRefraction.getText();
		objectString = objectString + " " + emitter.getSelectedItem();
		updateElement.actionPerformed(new ActionEvent(objectString, ActionEvent.ACTION_PERFORMED, null));
	}

	@Override
	protected void consolidateValues()
	{
		attributeValues = new ArrayList<String>();
		attributeValues.add(pointX.getText());
		attributeValues.add(pointY.getText());
		attributeValues.add(pointZ.getText());
		attributeValues.add(radius.getText());
		attributeValues.add(redSlider.getValue() + "");
		attributeValues.add(greenSlider.getValue() + "");
		attributeValues.add(blueSlider.getValue() + "");
		attributeValues.add(" src/images/" + textureFile.getSelectedItem());
		attributeValues.add(specular.getText());
		attributeValues.add(reflect.getText());
		attributeValues.add(glossyExp.getText());
		attributeValues.add(refract.getText());
		attributeValues.add(indexOfRefraction.getText());
		attributeValues.add(" " + emitter.getSelectedItem());
		
	}

	@Override
	protected void buildRestrictions()
	{
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		
		restrictions.add(new Double[] {0.0, Double.MAX_VALUE});
		
		restrictions.add(new Double[] {0.0, 255.0});
		restrictions.add(new Double[] {0.0, 255.0});
		restrictions.add(new Double[] {0.0, 255.0});
		
		restrictions.add(null);
		
		restrictions.add(new Double[] {-1.0, Double.MAX_VALUE});
		restrictions.add(new Double[] {0.0, 1.0});
		restrictions.add(new Double[] {0.0, Double.MAX_VALUE});
		
		restrictions.add(new Double[] {0.0, 1.0});
		restrictions.add(new Double[] {0.0, Double.MAX_VALUE});
		
		restrictions.add(null);
		
	}

}
