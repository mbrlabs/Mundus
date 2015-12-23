package com.mbrlabs.mundus.terrain.brushes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.core.project.ProjectContext;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class BrushManager implements InputProcessor, Disposable {

    private static final int KEY_LOWER_TERRAIN = Input.Buttons.RIGHT;
    private static final int KEY_RAISE_TERRAIN = Input.Buttons.LEFT;
    private static final int KEY_DEACTIVATE = Input.Keys.ESCAPE;

    public Array<Brush> brushes;
    private Brush activeBrush;

    private ProjectContext projectContext;
    private PerspectiveCamera cam;

    public BrushManager(ProjectContext projectContext, PerspectiveCamera camera) {
        this.cam = camera;
        this.projectContext = projectContext;
        brushes = new Array<>();
    }

    public void addBrush(Brush brush) {
        brushes.add(brush);
    }

    public void activate(Brush brush) {
        this.activeBrush = brush;
    }

    public void deactivate() {
        activeBrush = null;
    }

    public Brush getActiveBrush() {
        return this.activeBrush;
    }

    public void act() {
        if(activeBrush != null) {
            if(Gdx.input.isButtonPressed(KEY_RAISE_TERRAIN)) {
                activeBrush.draw(projectContext.terrainGroup, true);
            }

            if(Gdx.input.isButtonPressed(KEY_LOWER_TERRAIN)) {
                activeBrush.draw(projectContext.terrainGroup, false);
            }
        }
    }


    private Vector3 tempV3 = new Vector3();

    @Override
    public boolean scrolled(int amount) {
        if(activeBrush != null) {
            if(amount < 0) {
                activeBrush.scale(0.9f);
            } else {
                activeBrush.scale(1.1f);
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if(activeBrush != null && projectContext.terrainGroup.size() > 0) {
            Ray ray = cam.getPickRay(screenX, screenY);
            projectContext.terrainGroup.getRayIntersection(tempV3, ray);
            activeBrush.setTranslation(tempV3);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == KEY_DEACTIVATE) {
            deactivate();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public void dispose() {
        for(Brush b : brushes) {
            b.dispose();
        }
    }
}
