package com.ashenrider.game.desktop;

import com.ashenrider.game.HackathonApp;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.io.File;

public class DesktopLauncher {
	public static void main (String[] arg) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth=512;
        settings.maxHeight=512;

        // In order to update animations you must delete the .atlas file associated with it

        String[] atlases = {"gui", "animations", "fx"};

        for (String atlasName : atlases) {
            if (!new File("pack/" + atlasName + ".atlas").exists()) {
                TexturePacker.process(settings, atlasName, "pack", atlasName);
            }
        }

        if (!new File("pack/intro.atlas").exists()) {
            // intro is 1200 x 800
            settings.maxWidth=2048;
            settings.maxHeight=1024;
            TexturePacker.process(settings, "intro_frames", "pack", "intro");
        }

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		new LwjglApplication(new HackathonApp(), config);
	}
}
