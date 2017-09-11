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
public class PointLightPanel extends ManualPanel
{
	//PointLight:x,y,z r,g,b
	
	private JTextField pointX;
	private JTextField pointY;
	private JTextField pointZ;
	
	//color	
	private JSlider redSlider;
	private JSlider greenSlider;
	private JSlider blueSlider;
	private JLabel colorLabel;
	
	private String index;
	
	ActionListener updateElement;
	
	public PointLightPanel(String loadString, ActionListener callback)
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
		currentY+=20;
		
		//Color
		addLabel("Color", 20, currentY, 140, 20, false, true);
		currentY+=20;
		redSlider = new JSlider(0, 255, 0);
		greenSlider = new JSlider(0, 255, 0);
		blueSlider = new JSlider(0, 255, 0);
		colorLabel = new JLabel();
		placeColorSliders(40, currentY, redSlider, greenSlider, blueSlider, colorLabel);
		currentY+=60;
		
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
		String[] rgb = attributes[1].split(",");
		redSlider.setValue(Integer.parseInt(rgb[0]));
		greenSlider.setValue(Integer.parseInt(rgb[1]));
		blueSlider.setValue(Integer.parseInt(rgb[2]));
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
		String objectString = index + ":PointLight:";
		objectString = objectString + pointX.getText() + "," + pointY.getText() + "," + pointZ.getText();
		objectString = objectString + " " + redSlider.getValue() + "," + greenSlider.getValue() + "," + blueSlider.getValue();
		
		updateElement.actionPerformed(new ActionEvent(objectString, ActionEvent.ACTION_PERFORMED, null));

	}

	@Override
	protected void consolidateValues()
	{
		attributeValues = new ArrayList<String>();
		attributeValues.add(pointX.getText());
		attributeValues.add(pointY.getText());
		attributeValues.add(pointZ.getText());
		attributeValues.add(redSlider.getValue() + "");
		attributeValues.add(greenSlider.getValue() + "");
		attributeValues.add(blueSlider.getValue() + "");
		
	}

	@Override
	protected void buildRestrictions()
	{
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		restrictions.add(new Double[] {0.0, 0.0});
		
		restrictions.add(new Double[] {0.0, 255.0});
		restrictions.add(new Double[] {0.0, 255.0});
		restrictions.add(new Double[] {0.0, 255.0});
		
	}

}
