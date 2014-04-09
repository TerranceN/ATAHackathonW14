package com.ashenrider.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;


public class HackathonGame extends ApplicationAdapter {
    OrthographicCamera camera;
    Scene scene;
    
	@Override
	public void create () {
        scene = new Scene("test.tmx");
        
        camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
    }

	@Override
	public void render () {
		float dt = 1/30.0f;
		scene.update(dt);
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
        camera.update();

        scene.render(camera);
	}
}
