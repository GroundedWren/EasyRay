package gui;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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

@SuppressWarnings("serial")
public class TriangleMeshPanel extends ManualPanel
{
	//Mesh:filename x,y,z scale stretch_x,stretch_y,stretch_z theta,phi r,g,b specular_exponent reflectivity glossyExponent refractivity refractionIndex
	
	private JFileChooser newObjFile;
	private JComboBox<String> objFilename;
	
	private JTextField pointX;
	private JTextField pointY;
	private JTextField pointZ;
	
	private JTextField scale;
	
	private JTextField stretchX;
	private JTextField stretchY;
	private JTextField stretchZ;
	
	private JTextField theta;
	private JTextField phi;
	
	//color input	
	private JSlider redSlider;
	private JSlider greenSlider;
	private JSlider blueSlider;
	private JLabel colorLabel;
	
	private JTextField specular;
	
	private JTextField reflect;
	private JTextField glossyExp;
	
	private JTextField refract;
	private JTextField indexOfRefraction;
	
	private String index;
		
	ActionListener updateElement;
	
	public TriangleMeshPanel(String loadString, ActionListener callback)
	{
		restrictions = new ArrayList<Double[]>();
		buildRestrictions();
		
		index = "new";
		updateElement = callback;
		this.setLayout(null);
		this.setOpaque(true);
		this.setBackground(Color.lightGray);
		
		int currentY = 20;
		
		//object file
		addLabel("Obj File", 20, currentY, 140, 20, false, true);
		
		FilenameFilter textfileFilter = new VisibleFileFilter();
		
		File meshesFolder = new File("src/meshes");
		String[] objFiles = new String[meshesFolder.listFiles(textfileFilter).length];
		int i = 0;
		for(File objFile : meshesFolder.listFiles(textfileFilter))
		{
			objFiles[i] = objFile.getName();
			i+=1;
		}
		objFilename = new JComboBox<String>(objFiles);
		objFilename.setLocation(160, currentY);
		objFilename.setSize(200, 20);
		this.add(objFilename);
		
		newObjFile = new JFileChooser();
		JButton importTexture = new JButton("Import");
		importTexture.setLocation(360, currentY);
		importTexture.setSize(50, 20);
		importTexture.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				//Citation: https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
				if(newObjFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					File f = newObjFile.getSelectedFile();
					if(!f.getAbsolutePath().endsWith(".obj"))
						JOptionPane.showMessageDialog(null, "Error, OBJ only!");
					else
					{
						System.out.println(f.getAbsolutePath());
						try
						{
							Files.copy(Paths.get(f.getAbsolutePath()), Paths.get("src/meshes/" + f.getName()), REPLACE_EXISTING);
							objFilename.addItem(f.getName());
							objFilename.setSelectedItem(f.getName());
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
		
		//Point
		addLabel("Point", 20, currentY, 140, 20, false, true);
		
		pointX = new JTextField(20);
		pointY = new JTextField(20);
		pointZ = new JTextField(20);
		placeTriplet(160, currentY, pointX, "x", pointY, "y", pointZ, "z");
		currentY += 20;
		
		//Scale
		addLabel("Scale", 20, currentY, 140, 20, false, true);
		
		scale = new JTextField(20);
		scale.setLocation(160, currentY);
		scale.setSize(95, 20);
		this.add(scale);
		currentY += 20;
		
		//Stretch
		addLabel("Stretch", 20, currentY, 140, 20, false, true);
		
		stretchX = new JTextField(20);
		stretchY = new JTextField(20);
		stretchZ = new JTextField(20);
		placeTriplet(160, currentY, stretchX, "x", stretchY, "y", stretchZ, "z");
		currentY += 20;
		
		
		//Rotation
		addLabel("Rotation", 20, currentY, 140, 20, false, true);
		
		theta = new JTextField(20);
		theta.setLocation(160, currentY);
		theta.setSize(40, 20);
		this.add(theta);
		JLabel thetaLabel = new JLabel("θ");
		thetaLabel.setSize(15, 20);
		thetaLabel.setLocation(200, currentY);
		this.add(thetaLabel);
		phi = new JTextField(20);
		phi.setLocation(215, currentY);
		phi.setSize(40, 20);
		this.add(phi);
		JLabel phiLabel = new JLabel("φ");
		phiLabel.setSize(20, 20);
		phiLabel.setLocation(255, currentY);
		this.add(phiLabel);
		currentY += 20;
		
		//DefaultColor
		addLabel("Color", 20, currentY, 140, 20, false, true);
		currentY+=20;
		redSlider = new JSlider(0, 255, 0);
		greenSlider = new JSlider(0, 255, 0);
		blueSlider = new JSlider(0, 255, 0);
		colorLabel = new JLabel();
		placeColorSliders(40, currentY, redSlider, greenSlider, blueSlider, colorLabel);
		currentY+=60;
		
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
		objFilename.setSelectedItem(attributes[0].split("/")[2]);
		String[] point = attributes[1].split(",");
		pointX.setText(point[0]);
		pointY.setText(point[1]);
		pointZ.setText(point[2]);
		scale.setText(attributes[2]);
		String[] stretch = attributes[3].split(",");
		stretchX.setText(stretch[0]);
		stretchY.setText(stretch[1]);
		stretchZ.setText(stretch[2]);
		String[] angle = attributes[4].split(",");
		theta.setText(angle[0]);
		phi.setText(angle[1]);
		String[] rgb = attributes[5].split(",");
		redSlider.setValue(Integer.parseInt(rgb[0]));
		greenSlider.setValue(Integer.parseInt(rgb[1]));
		blueSlider.setValue(Integer.parseInt(rgb[2]));
		specular.setText(attributes[6]);
		reflect.setText(attributes[7]);
		glossyExp.setText(attributes[8]);
		refract.setText(attributes[9]);
		indexOfRefraction.setText(attributes[10]);
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
		String objectString = index + ":Mesh:";
		objectString = objectString + "src/meshes/" + (String)objFilename.getSelectedItem();
		objectString = objectString + " " + pointX.getText() + "," + pointY.getText() + "," + pointZ.getText();
		objectString = objectString + " " + scale.getText();
		objectString = objectString + " " + stretchX.getText() + "," + stretchY.getText() + "," + stretchZ.getText();
		objectString = objectString + " " + theta.getText() + "," + phi.getText();
		objectString = objectString + " " + redSlider.getValue() + "," + greenSlider.getValue() + "," + blueSlider.getValue();
		objectString = objectString + " " + specular.getText() + " " + reflect.getText() + " " + glossyExp.getText();
		objectString = objectString + " " + refract.getText() + " " + indexOfRefraction.getText();
		
		updateElement.actionPerformed(new ActionEvent(objectString, ActionEvent.ACTION_PERFORMED, null));
	}

	@Override
	protected void consolidateValues()
	{
		attributeValues = new ArrayList<String>();
		attributeValues.add((String)objFilename.getSelectedItem());
		attributeValues.add(pointX.getText());
		attributeValues.add(pointY.getText());
		attributeValues.add(pointZ.getText());
		attributeValues.add(scale.getText());
		attributeValues.add(stretchX.getText());
		attributeValues.add(stretchY.getText());
		attributeValues.add(stretchZ.getText());
		attributeValues.add(theta.getText());
		attributeValues.add(phi.getText());
		attributeValues.add(redSlider.getValue() + "");
		attributeValues.add(greenSlider.getValue() + "");
		attributeValues.add(blueSlider.getValue() + "");
		attributeValues.add(specular.getText());
		attributeValues.add(reflect.getText());
		attributeValues.add(glossyExp.getText());
		attributeValues.add(refract.getText());
		attributeValues.add(indexOfRefraction.getText());		
	}

	@Override
	protected void buildRestrictions()
	{
		restrictions.add(null);
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
				
		restrictions.add(new Double[] {0.0, 0.0});
		
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		
		restrictions.add(new Double[] {0.0, 2*Math.PI});
		restrictions.add(new Double[] {0.0, Math.PI});
		
		restrictions.add(new Double[] {0.0, 255.0});
		restrictions.add(new Double[] {0.0, 255.0});
		restrictions.add(new Double[] {0.0, 255.0});
		
		restrictions.add(new Double[] {-1.0, Double.MAX_VALUE});
		restrictions.add(new Double[] {0.0, 1.0});
		restrictions.add(new Double[] {0.0, Double.MAX_VALUE});
		
		restrictions.add(new Double[] {0.0, 1.0});
		restrictions.add(new Double[] {0.0, Double.MAX_VALUE});
	}

}
