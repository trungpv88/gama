/*********************************************************************************************
 *
 *
 * 'ModelsLibraryFolder.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.swt.graphics.Image;

import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class PluginsModelsFolder extends TopLevelFolder {

	public PluginsModelsFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public Image getImage() {
		if (PlatformHelper.isGtk())
			return IGamaIcons.FOLDER_PLUGIN_16.image();
		return IGamaIcons.FOLDER_PLUGIN.image(); // FOLDER_PLUGIN
	}

	@Override
	public Image getImageForStatus() {
		return GamaIcons.create("navigator/folder.status.plugin").image();
	}

	@Override
	public String getMessageForStatus() {
		return "Models present in GAMA plugins";
	}

	@Override
	public GamaUIColor getColorForStatus() {
		return IGamaColors.WARNING;
	}

	/**
	 * Method accepts()
	 * 
	 * @see ummisco.gama.ui.navigator.TopLevelFolder#accepts(org.eclipse.core.resources.IProjectDescription)
	 */
	@Override
	protected boolean accepts(final IProjectDescription desc) {
		return desc.hasNature(WorkbenchHelper.PLUGIN_NATURE);
	}

	/**
	 * Method getModelsLocation()
	 * 
	 * @see ummisco.gama.ui.navigator.TopLevelFolder#getModelsLocation()
	 */
	@Override
	protected Location getModelsLocation() {
		return Location.Plugins;
	}

}
