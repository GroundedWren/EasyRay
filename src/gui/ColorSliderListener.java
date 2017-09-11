package gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorSliderListener implements ChangeListener
{
	private JSlider red;
	private JSlider green;
	private JSlider blue;
	private JLabel colorDisplay;
	
	public ColorSliderListener(JSlider r, JSlider g, JSlider b, JLabel display)
	{
		red = r;
		green = g;
		blue = b;
		colorDisplay = display;
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		int redVal = red.getValue();
		int greenVal = green.getValue();
		int blueVal = blue.getValue();
		colorDisplay.setBackground(new Color(redVal, greenVal, blueVal));
	}

}
