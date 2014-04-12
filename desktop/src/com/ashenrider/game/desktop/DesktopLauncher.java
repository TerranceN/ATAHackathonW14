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
        TexturePacker.process(settings, "animations", "pack", "animations");
        TexturePacker.process(settings, "fx", "pack", "fx");
        // intro is 1200 x 800
        // for now, only build the intro once and then reuse the packed version
        settings.maxWidth=2048;
        settings.maxHeight=1024;
        //TexturePacker.process(settings, "intro_frames", "pack", "intro");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new HackathonApp(), config);
	}
}
