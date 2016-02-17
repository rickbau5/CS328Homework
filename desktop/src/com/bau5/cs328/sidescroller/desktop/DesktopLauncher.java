package com.bau5.cs328.sidescroller.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.bau5.cs328.sidescroller.Main;
import com.bau5.cs328.sidescroller.Vals;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Vals.screenWidth();
		config.height = Vals.screenHeight();
		new LwjglApplication(new Main(), config);
	}
}
