package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.files.FileHandle;

public class Assets {
    public static final AssetManager manager = new AssetManager();

    public static void load() {
        // Texture Atlases
        FileHandle[] packFiles = Gdx.files.internal("pack/").list();
        for (FileHandle file : packFiles) {
            if (!file.isDirectory() && file.extension().equals("atlas")) {
                manager.load(file.path(), TextureAtlas.class);
            }
        }

        // Textures
        FileHandle[] globalAssets = Gdx.files.internal("./").list();
        for (FileHandle file : globalAssets) {
            if (!file.isDirectory() && file.extension().equals("png")) {
                System.out.println(file.path());
                manager.load(file.path(), Texture.class);
            }
        }
    }

    public static void dispose() {
        manager.dispose();
    }
}
