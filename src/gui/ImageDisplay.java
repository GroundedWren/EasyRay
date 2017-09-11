package gui;

import javax.swing.*;

/**
 * Launces a new window to showcase the just rendered image
 * @author alexaulabaugh
 */

public class ImageDisplay
{
	/**
	 * Launches a window containing only the image
	 * @param filename where the image is located on disk
	 * @param width dimension of image
	 * @param height dimension of image
	 */
	public ImageDisplay(String filename, int width, int height)
	{
		JFrame window = new JFrame("Image");
		window.setSize(width, height);
		ImageIcon image = new ImageIcon(filename, "The Image");
		image.getImage().flush();
		JLabel imageLabel = new JLabel(image);
		imageLabel.setOpaque(true);
		imageLabel.setLocation(400, 20);
		imageLabel.setSize(200, 200);
		window.getContentPane().add(imageLabel);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);
		window.getContentPane().repaint();
	}
}
