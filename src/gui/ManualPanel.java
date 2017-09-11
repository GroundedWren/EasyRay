package gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.Color;

/**
 * Superclass for all panels - contains common functions to place labels and input fields
 * @author alexaulabaugh
 */

@SuppressWarnings("serial")
public abstract class ManualPanel extends JPanel implements ActionListener
{	
	protected ArrayList<Double[]> restrictions;
	protected ArrayList<String> attributeValues;
	
	/**
	* Helper function to add a label to the UI
	* @param text text in the label
	* @param x coordinate
	* @param y coordinate
	* @param width
	* @param height
	* @param center true if text is aligned center, false if aligned left
	*/
	protected void addLabel(String text, int x, int y, int width, int height, boolean center, boolean background)
	{
		JLabel newLabel = new JLabel(text);
		if(center)
		{
			newLabel = new JLabel(text, SwingConstants.CENTER);
		}
		newLabel.setLocation(x, y);
		newLabel.setSize(width, height);
		if(background)
		{
			newLabel.setBackground(Color.white);
			newLabel.setOpaque(true);
		}
		this.add(newLabel);
	}
	
	/**
	* Helper method to place three sequential input boxes
	* @param x xlocaiton
	* @param y ylocation
	* @param field1 the first input
	* @param labelString1 label to follow first input
	* @param field2
	* @param labelString2
	* @param field3
	* @param labelString3
	*/
	protected void placeTriplet(int x, int y, JTextField field1, String labelString1, JTextField field2, String labelString2, JTextField field3, String labelString3)
	{
		field1.setLocation(x, y);
		field1.setSize(40, 20);
		this.add(field1);
		JLabel xLabel = new JLabel(labelString1);
		xLabel.setSize(20, 20);
		xLabel.setLocation(x+40, y);
		this.add(xLabel);
		//y
		field2.setLocation(x+55, y);
		field2.setSize(40, 20);
		this.add(field2);
		JLabel yLabel = new JLabel(labelString2);
		yLabel.setSize(20, 20);
		yLabel.setLocation(x+95, y);
		this.add(yLabel);
		//z
		field3.setLocation(x+110, y);
		field3.setSize(40, 20);
		this.add(field3);
		JLabel zLabel = new JLabel(labelString3);
		zLabel.setSize(20, 20);
		zLabel.setLocation(x+150, y);
		this.add(zLabel);
	}
	
	/**
	 * Helper method to place RGB color sliders into the scene
	 * @param x the x location of the sliders
	 * @param y the y location of the sliders
	 * @param red the red slider
	 * @param green the green slider
	 * @param blue the blue slider
	 * @param colorDisplay the JLabel that reflects the current color of the sliders
	 */
	protected void placeColorSliders(int x,int y, JSlider red, JSlider green, JSlider blue, JLabel colorDisplay)
	{
		addLabel("Red", x, y, 50, 20, false, false);
		red.setLocation(x+50, y);
		red.setSize(150, 20);
		red.addChangeListener(new ColorSliderListener(red, green, blue, colorDisplay));
		this.add(red);
		
		addLabel("Green", x, y+20, 50, 20, false, false);
		green.setLocation(x+50, y+20);
		green.setSize(150, 20);
		green.addChangeListener(new ColorSliderListener(red, green, blue, colorDisplay));
		this.add(green);
		
		addLabel("Blue", x, y+40, 50, 20, false, false);
		blue.setLocation(x+50, y+40);
		blue.setSize(150, 20);
		blue.addChangeListener(new ColorSliderListener(red, green, blue, colorDisplay));
		this.add(blue);
		
		colorDisplay.setLocation(x-20, y);
		colorDisplay.setSize(20, 60);
		colorDisplay.setBackground(Color.black);
		colorDisplay.setOpaque(true);
		this.add(colorDisplay);
	}
	
	protected abstract void consolidateValues();
	protected abstract void buildRestrictions();
	
	/**
	 * Checks all input values against their constraints, builds a string that complains about things that
	 * are out of the acceptable range
	 * @return string description of errors
	 */
	protected String checkValues()
	{
		String violations = "";
		
		for(int i = 0; i < restrictions.size(); i++)
		{
			Double[] restriction = restrictions.get(i);
			if(restriction != null)
			{
				try
				{
					if(restriction[0].equals(restriction[1]))
					{
						@SuppressWarnings("unused")
						double value = Double.parseDouble(attributeValues.get(i));
					}
					else
					{
						double value = Double.parseDouble(attributeValues.get(i));
						if(value < restriction[0] || value > restriction[1])
							violations = violations + "Field " + i + " must be in the range [" + restriction[0] + ", " + restriction[1] + "]\n";
					}
				}
				catch(Exception e)
				{
					violations = violations + "Field " + i + " must be a number\n";
				}
			}
		}
		
		return violations;
	}
	
}
