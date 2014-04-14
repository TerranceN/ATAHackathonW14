package com.ashenrider.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.IOException;

public class Assets {
    public static final AssetManager manager = new AssetManager();

    public static void load() {
        // Texture Atlases
        if (Gdx.files.internal("pack/").isDirectory()) {
            // we are not packaged into a jar, so make an assets list file to remember the paths of assets for when the game is packaged
            try {
                PrintWriter textureAtlasesList = new PrintWriter("textureAtlases.list");
                FileHandle[] packFiles = Gdx.files.internal("pack/").list();
                for (FileHandle file : packFiles) {
                    if (!file.isDirectory() && file.extension().equals("atlas")) {
                        manager.load(file.path(), TextureAtlas.class);
                        textureAtlasesList.write(file.path() + "\n");
                    }
                }
                textureAtlasesList.close();

                // Textures
                PrintWriter texturesList = new PrintWriter("textures.list");
                FileHandle[] globalAssets = Gdx.files.internal(".").list();
                for (FileHandle file : globalAssets) {
                    if (!file.isDirectory() && file.extension().equals("png")) {
                        String removedDotSlash = file.path().substring(2);
                        manager.load(removedDotSlash, Texture.class);
                        texturesList.write(removedDotSlash + "\n");
                    }
                }
                texturesList.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            // if packaged into a jar, we can't look for the files, so get asset list from assets list
            String[] atlasPaths = Gdx.files.internal("textureAtlases.list").readString().split("\n");
            for (String path : atlasPaths) {
                manager.load(path, TextureAtlas.class);
            }

            String[] texturePaths = Gdx.files.internal("textures.list").readString().split("\n");
            for (String path : texturePaths) {
                manager.load(path, Texture.class);
            }
        }
    }

    public static void dispose() {
        manager.dispose();
    }
}
