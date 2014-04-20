package com.ashenrider.game.userinterface;

import java.util.HashSet;
import java.util.Set;

import com.ashenrider.game.HackathonApp;
import com.ashenrider.game.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class LoadingScreen implements Screen {
    private HackathonApp app;
    private boolean loadedMainMenu = false;
    private ShapeRenderer shapeRenderer;

    // progress status
    int count = 0;
    Set<String> assets = new HashSet<String>();
    String previousFile = "";
    SpriteBatch batch;
    
    private static final int ASSET_COUNT = 116;
    
    public LoadingScreen(final HackathonApp app) {
        super();
        this.app = app;

        batch = new SpriteBatch();
        
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

            // Assetmanager counts all of the assets corresponding to an atlas as a single file for getProgress
            // the number of queued assets is also increased when it starts loading a given atlas
            // so to prevent the progress going down as new files are discovered, a constant number that is the asset count is hard coded above
            float progress = count / (float) Math.max(ASSET_COUNT, (count + Assets.manager.getQueuedAssets() - 1));
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, 1);
            shapeRenderer.rect(botLeft.x - edgeSize.x, botLeft.y - edgeSize.y, boxSize.x + edgeSize.x * 2, boxSize.y + edgeSize.y * 2);
            shapeRenderer.setColor(0.25f, 0, 0, 1);
            shapeRenderer.rect(botLeft.x, botLeft.y, boxSize.x, boxSize.y);
            shapeRenderer.setColor(0, 0.8f, 0, 1);
            shapeRenderer.rect(botLeft.x, botLeft.y, boxSize.x * progress, boxSize.y);
            shapeRenderer.end();

            int oldCount = count;
            count = Assets.manager.getLoadedAssets();
            if (count != oldCount) {
                Array<String> filenames = Assets.manager.getAssetNames();
                for (String filename : filenames) {
                    if (!assets.contains(filename)) {
                        assets.add(filename);
                        previousFile = filename;
                    }
                }
            }
            
            batch.begin();
            HackathonApp.buttonFont.drawMultiLine(batch, "Loading: " + previousFile, 0, center.y + boxSize.y/2 + 40, Gdx.graphics.getWidth(), HAlignment.CENTER);
            batch.end();
            
            if (Assets.manager.update()) {
                shapeRenderer.begin(ShapeType.Filled);
                shapeRenderer.setColor(0, 1, 0, 1);
                shapeRenderer.rect(botLeft.x, botLeft.y, boxSize.x, boxSize.y);
                shapeRenderer.end();
                
                if (ASSET_COUNT < count) {
                    Gdx.app.log("LoadingScreen", "ASSET COUNT IS WRONG! Loaded " + count + " assets.");
                }
                loadedMainMenu = true;
                app.setScreen(new MainMenuScreen(app));
            }
        } else {
            System.out.println("exiting");
            // remove the Loading Screen from the stack (so that going "back" from the menu doesnt try to load again)
            app.popBackStack();
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
