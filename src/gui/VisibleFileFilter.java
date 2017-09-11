package gui;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filters all hidden files (for use in menus)
 * @author alexaulabaugh
 */

public class VisibleFileFilter implements FilenameFilter
{
	@Override
	public boolean accept(File dir, String name)
	{
		return !name.startsWith(".");
	}
}
