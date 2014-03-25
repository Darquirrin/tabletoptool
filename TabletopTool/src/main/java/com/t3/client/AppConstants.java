/*
 *  This software copyright by various authors including the RPTools.net
 *  development team, and licensed under the LGPL Version 3 or, at your
 *  option, any later version.
 *
 *  Portions of this software were originally covered under the Apache
 *  Software License, Version 1.1 or Version 2.0.
 *
 *  See the file LICENSE elsewhere in this distribution for license details.
 */

package com.t3.client;

import java.io.File;
import java.io.FilenameFilter;

import net.tsc.servicediscovery.ServiceGroup;

import com.t3.model.Token;
import com.t3.swing.ImageBorder;

public class AppConstants {

	public static final String APP_NAME = "TabletopTool";

	public static final File UNZIP_DIR = AppUtil.getAppHome("resource");

	public static final ServiceGroup SERVICE_GROUP = new ServiceGroup("tabletoptool");

	public static final ImageBorder GRAY_BORDER = new ImageBorder("com/t3/client/image/border/gray");
	public static final ImageBorder SHADOW_BORDER = new ImageBorder("com/t3/client/image/border/shadow");
	public static final ImageBorder HIGHLIGHT_BORDER = new ImageBorder("com/t3/client/image/border/highlight");
	public static final ImageBorder GREEN_BORDER = new ImageBorder("com/t3/client/image/border/green");
	public static final ImageBorder YELLOW_BORDER = new ImageBorder("com/t3/client/image/border/yellow");
	public static final ImageBorder PURPLE_BORDER = new ImageBorder("com/t3/client/image/border/purple");
	public static final ImageBorder FOW_TOOLS_BORDER = new ImageBorder("com/t3/client/image/border/fowtools");
	public static final int NOTE_PORTRAIT_SIZE = 200;

	public static final FilenameFilter IMAGE_FILE_FILTER = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			name = name.toLowerCase();
			// I can drop TIFF files into TokenTool and it works. Should that extension be
			// added here? The question is really whether the Java2D libraries can read TIFF
			// or my desktop GUI is converting the image during the drop operation... FJE
			return name.endsWith(".bmp") ||
			name.endsWith(".png") || name.endsWith(".gif") ||
			name.endsWith(".jpg") || name.endsWith(".jpeg") ||
			name.endsWith(Token.FILE_EXTENSION);			// RPTools Token format
		}
	};

	public static final String CAMPAIGN_FILE_EXTENSION = ".cmpgn";
	public static final String CAMPAIGN_PROPERTIES_FILE_EXTENSION = ".mtprops";
	public static final String MAP_FILE_EXTENSION = ".rpmap";
	public static final String MACRO_FILE_EXTENSION = ".mtmacro";
	public static final String MACROSET_FILE_EXTENSION = ".mtmacset";
	public static final String TABLE_FILE_EXTENSION = ".mttable";
}
