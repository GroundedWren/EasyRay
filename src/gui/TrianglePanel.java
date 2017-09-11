package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class TrianglePanel extends ManualPanel
{
	//Triangle:x1,y1,z1, x2,y2,z2 -x3,y3,z3 r,g,b specular_exponent reflectivity glossyExponent refractivity refractionIndex
	
	private JTextField point1X;
	private JTextField point1Y;
	private JTextField point1Z;
	
	private JTextField point2X;
	private JTextField point2Y;
	private JTextField point2Z;
	
	private JTextField point3X;
	private JTextField point3Y;
	private JTextField point3Z;
	
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
	
	public TrianglePanel(String loadString, ActionListener callback)
	{
		restrictions = new ArrayList<Double[]>();
		buildRestrictions();
		
		index = "new";
		updateElement = callback;
		this.setLayout(null);
		this.setOpaque(true);
		this.setBackground(Color.lightGray);
		
		int currentY = 20;
		
		//Point 1
		addLabel("Point1", 20, currentY, 140, 20, false, true);
		
		point1X = new JTextField(20);
		point1Y = new JTextField(20);
		point1Z = new JTextField(20);
		placeTriplet(160, currentY, point1X, "x", point1Y, "y", point1Z, "z");
		currentY += 20;
		
		//Point 2
		addLabel("Point2", 20, currentY, 140, 20, false, true);
		
		point2X = new JTextField(20);
		point2Y = new JTextField(20);
		point2Z = new JTextField(20);
		placeTriplet(160, currentY, point2X, "x", point2Y, "y", point2Z, "z");
		currentY += 20;
		
		//Point 3
		addLabel("Point3", 20, currentY, 140, 20, false, true);
		
		point3X = new JTextField(20);
		point3Y = new JTextField(20);
		point3Z = new JTextField(20);
		placeTriplet(160, currentY, point3X, "x", point3Y, "y", point3Z, "z");
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
		String[] point1 = attributes[0].split(",");
		point1X.setText(point1[0]);
		point1Y.setText(point1[1]);
		point1Z.setText(point1[2]);
		String[] point2 = attributes[1].split(",");
		point2X.setText(point2[0]);
		point2Y.setText(point2[1]);
		point2Z.setText(point2[2]);
		String[] point3 = attributes[2].split(",");
		point3X.setText(point3[0]);
		point3Y.setText(point3[1]);
		point3Z.setText(point3[2]);
		String[] rgb = attributes[3].split(",");
		redSlider.setValue(Integer.parseInt(rgb[0]));
		greenSlider.setValue(Integer.parseInt(rgb[1]));
		blueSlider.setValue(Integer.parseInt(rgb[2]));
		specular.setText(attributes[4]);
		reflect.setText(attributes[5]);
		glossyExp.setText(attributes[6]);
		refract.setText(attributes[7]);
		indexOfRefraction.setText(attributes[8]);
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
		String objectString = index + ":Triangle:";
		objectString = objectString + point1X.getText() + "," + point1Y.getText() + "," + point1Z.getText();
		objectString = objectString + " " + point2X.getText() + "," + point2Y.getText() + "," + point2Z.getText();
		objectString = objectString + " " + point3X.getText() + "," + point3Y.getText() + "," + point3Z.getText();
		objectString = objectString + " " + redSlider.getValue() + "," + greenSlider.getValue() + "," + blueSlider.getValue();
		objectString = objectString + " " + specular.getText() + " " + reflect.getText() + " " + glossyExp.getText();
		objectString = objectString + " " + refract.getText() + " " + indexOfRefraction.getText();
		updateElement.actionPerformed(new ActionEvent(objectString, ActionEvent.ACTION_PERFORMED, null));

	}

	@Override
	protected void consolidateValues()
	{
		attributeValues = new ArrayList<String>();
		attributeValues.add(point1X.getText());
		attributeValues.add(point1Y.getText());
		attributeValues.add(point1Z.getText());
		attributeValues.add(point2X.getText());
		attributeValues.add(point2Y.getText());
		attributeValues.add(point2Z.getText());
		attributeValues.add(point3X.getText());
		attributeValues.add(point3Y.getText());
		attributeValues.add(point3Z.getText());
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
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		
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
