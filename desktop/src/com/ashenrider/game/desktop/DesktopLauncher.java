package com.ashenrider.game.desktop;

import com.ashenrider.game.HackathonApp;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class DesktopLauncher {
	public static void main (String[] arg) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth=512;
        settings.maxHeight=512;
        TexturePacker.process(settings, "gui", "pack", "gui");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new HackathonApp(), config);
	}
}
