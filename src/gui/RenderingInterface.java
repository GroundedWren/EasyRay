package gui;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;

/**
 * The window brought up on launch to edit a rendering program
 * Holds a settingsPanel
 * Launches other windows from the menu bar
 * @author alexaulabaugh
 */

public class RenderingInterface implements ActionListener
{
	//The UI window & panel
	private JFrame window;
	private JPanel panel;
	
	//Lists of filenames of rendering specificaitons
	String[] settingsFiles;
	String[] cameraFiles;
	String[] sceneFiles;
	
	/**
	 * Basic constructor, launches window & paints default settings panel
	 */
	public RenderingInterface()
	{
		updateFiles();
		window = new JFrame("EasyRay");
		window.setSize(700, 550);
		setUpMenu(window);
		panel = new SettingsPanel(null, cameraFiles, sceneFiles);
		window.setContentPane(panel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
	
	/**
	 * Refresh the list of files for configuration
	 */
	private void updateFiles()
	{
		FilenameFilter textfileFilter = new VisibleFileFilter();
		File settingsFolder = new File("src/config/settings");
		settingsFiles = new String[settingsFolder.listFiles(textfileFilter).length];
		int i = 0;
		for(File settingsFile : settingsFolder.listFiles(textfileFilter))
		{
			settingsFiles[i] = settingsFile.getName();
			i+=1;
		}
		
		File cameraFolder = new File("src/config/camera");
		cameraFiles = new String[cameraFolder.listFiles(textfileFilter).length];
		i = 0;
		for(File cameraFile : cameraFolder.listFiles(textfileFilter))
		{
			cameraFiles[i] = cameraFile.getName();
			i+=1;
		}
		
		File sceneFolder = new File("src/config/scene");
		sceneFiles = new String[sceneFolder.listFiles(textfileFilter).length];
		i = 0;
		for(File sceneFile : sceneFolder.listFiles(textfileFilter))
		{
			sceneFiles[i] = sceneFile.getName();
			i+=1;
		}
	}
	
	/**
	 * Builds the top menu
	 * @param window the window the menu goes in
	 */
	private void setUpMenu(JFrame window)
	{
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		
		JMenu menuNew = new JMenu("New");
		menuNew.addActionListener(this);
		
		JMenuItem renderingNew = new JMenuItem("Rendering");
		renderingNew.setName("NewSettings");
		renderingNew.addActionListener(this);
		menuNew.add(renderingNew);
		
		JMenuItem sceneNew = new JMenuItem("Scene");
		sceneNew.setName("NewScene");
		sceneNew.addActionListener(this);
		menuNew.add(sceneNew);
		
		JMenuItem cameraNew = new JMenuItem("Camera");
		cameraNew.setName("NewCamera");
		cameraNew.addActionListener(this);
		menuNew.add(cameraNew);
		
		file.add(menuNew);
		
		JMenu menuLoad = new JMenu("Load");
		menuNew.addActionListener(this);
		
		JMenuItem renderingLoad = new JMenuItem("Rendering");
		renderingLoad.setName("LoadSettings");
		renderingLoad.addActionListener(this);
		menuLoad.add(renderingLoad);
		
		JMenuItem sceneLoad = new JMenuItem("Scene");
		sceneLoad.setName("LoadScene");
		sceneLoad.addActionListener(this);
		menuLoad.add(sceneLoad);
		
		JMenuItem cameraLoad = new JMenuItem("Camera");
		cameraLoad.setName("LoadCamera");
		cameraLoad.addActionListener(this);
		menuLoad.add(cameraLoad);
		
		file.add(menuLoad);
		
		JMenuItem reloadFiles = new JMenuItem("Reload Files");
		reloadFiles.setName("ReloadFiles");
		reloadFiles.addActionListener(this);
		file.add(reloadFiles);
				
		menubar.add(file);
		window.setJMenuBar(menubar);
	}
	
	/**
	 * Handles a menu item selection with the appropriate action
	 * @param menuItem an item from the top menu
	 */
	private void menuItemClicked(JMenuItem menuItem)
	{
		String filename  = null;
		JFrame newFrame;
		switch(menuItem.getName())
		{
			case "LoadSettings":
				filename = (String)JOptionPane.showInputDialog(window, "Select File", "Load File", JOptionPane.PLAIN_MESSAGE, null, settingsFiles, settingsFiles[0]);
				window.remove(panel);
				panel = new SettingsPanel(filename, cameraFiles, sceneFiles);
				window.setContentPane(panel);
				window.setVisible(true);
				panel.repaint();
				break;
			case "LoadScene":
				filename = (String)JOptionPane.showInputDialog(window, "Select File", "Load File", JOptionPane.PLAIN_MESSAGE, null, sceneFiles, sceneFiles[0]);
				newFrame = new JFrame("Scene: " + filename);
				newFrame.setSize(700, 550);
				ScenePanel loadedScene = new ScenePanel(filename);
				newFrame.setContentPane(loadedScene);
				newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				newFrame.setVisible(true);
				break;
			case "LoadCamera":
				filename = (String)JOptionPane.showInputDialog(window, "Select File", "Load File", JOptionPane.PLAIN_MESSAGE, null, cameraFiles, cameraFiles[0]);
				newFrame = new JFrame("Camera: " + filename);
				newFrame.setSize(700, 550);
				CameraPanel loadedCamera = new CameraPanel(filename);
				newFrame.setContentPane(loadedCamera);
				newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				newFrame.setVisible(true);
				break;
			case "NewSettings":
				window.remove(panel);
				panel = new SettingsPanel(filename, cameraFiles, sceneFiles);
				window.setContentPane(panel);
				window.setVisible(true);
				panel.repaint();
				break;
			case "NewScene":
				newFrame = new JFrame("New Scene");
				newFrame.setSize(700, 550);
				ScenePanel newScene = new ScenePanel(filename);
				newFrame.setContentPane(newScene);
				newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				newFrame.setVisible(true);
				break;
			case "NewCamera":
				newFrame = new JFrame("New Camera");
				newFrame.setSize(700, 550);
				CameraPanel newCamera = new CameraPanel(filename);
				newFrame.setContentPane(newCamera);
				newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				newFrame.setVisible(true);
				break;
			case "ReloadFiles":
				updateFiles();
				((SettingsPanel)panel).updateFiles(cameraFiles, sceneFiles);
				break;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object clickedObj = e.getSource();
		if(clickedObj instanceof JMenuItem)
		{
			menuItemClicked((JMenuItem)clickedObj);
		}
	}
	
	public static void main(String[] args)
	{
		new RenderingInterface();
	}

}
