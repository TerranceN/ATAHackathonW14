package com.ashenrider.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class HackathonGame extends ApplicationAdapter {
    TiledMap map;
    OrthogonalTiledMapRenderer tiledRenderer;
    OrthographicCamera camera;

	@Override
	public void create () {
        String file = "test.tmx";
        map = new TmxMapLoader().load(file);
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
        tiledRenderer = new OrthogonalTiledMapRenderer(map, Gdx.graphics.getHeight() / (100f * 32f));
        System.out.println(layer.getWidth() + ", " + layer.getHeight());

        camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        tiledRenderer.setView(camera);
        tiledRenderer.render();
	}
}
