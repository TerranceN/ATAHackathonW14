package com.ashenrider.game.userinterface;

import com.ashenrider.game.HackathonApp;
import com.ashenrider.game.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class LoadingScreen implements Screen {
    private HackathonApp app;
    private boolean loadedMainMenu = false;
    private ShapeRenderer shapeRenderer;

    public LoadingScreen(final HackathonApp app) {
        super();
        this.app = app;

        Assets.load();

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        if (!loadedMainMenu) {
            Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            Vector2 screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Vector2 center = screenSize.cpy().scl(0.5f);
            Vector2 boxSize = new Vector2(400f, 60f);
            Vector2 botLeft = center.cpy().sub(boxSize.cpy().scl(0.5f));

            Vector2 edgeSize = new Vector2(6f, 6f);

            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, 1);
            shapeRenderer.rect(botLeft.x - edgeSize.x, botLeft.y - edgeSize.y, boxSize.x + edgeSize.x * 2, boxSize.y + edgeSize.y * 2);
            shapeRenderer.setColor(0.25f, 0, 0, 1);
            shapeRenderer.rect(botLeft.x, botLeft.y, boxSize.x, boxSize.y);
            shapeRenderer.setColor(0, 0.8f, 0, 1);
            shapeRenderer.rect(botLeft.x, botLeft.y, boxSize.x * Assets.manager.getProgress(), boxSize.y);
            shapeRenderer.end();

            if (Assets.manager.update()) {
                shapeRenderer.begin(ShapeType.Filled);
                shapeRenderer.setColor(0, 1, 0, 1);
                shapeRenderer.rect(botLeft.x, botLeft.y, boxSize.x, boxSize.y);
                shapeRenderer.end();

                System.out.println("done loading");
                loadedMainMenu = true;
                app.setScreen(new MainMenuScreen(app));
            }
        } else {
            System.out.println("exiting");
            app.popBackstack();
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        Assets.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
        hide();
    }

    @Override
    public void resume() {
        show();
    }
}
