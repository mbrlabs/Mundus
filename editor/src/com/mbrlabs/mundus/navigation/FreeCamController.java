package com.mbrlabs.mundus.navigation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

import java.security.Key;

/**
 * @author Marcus Brummer
 * @version 24-11-2015
 */
public class FreeCamController extends InputAdapter {
    private final Camera camera;
    private final IntIntMap keys = new IntIntMap();
    private int STRAFE_LEFT = Input.Keys.A;
    private int STRAFE_RIGHT = Input.Keys.D;
    private int FORWARD = Input.Keys.W;
    private int BACKWARD = Input.Keys.S;
    private int UP = Input.Keys.Q;
    private int DOWN = Input.Keys.E;
    private float velocity = 5;
    private float degreesPerPixel = 0.5f;
    private final Vector3 tmp = new Vector3();

    public FreeCamController (Camera camera) {
        this.camera = camera;
    }

    @Override
    public boolean keyDown (int keycode) {
        keys.put(keycode, keycode);
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        keys.remove(keycode, 0);
        return false;
    }

    /** Sets the velocity in units per second for moving forward, backward and strafing left/right.
     * @param velocity the velocity in units per second */
    public void setVelocity (float velocity) {
        this.velocity = velocity;
    }

    /** Sets how many degrees to rotate per pixel the mouse moved.
     * @param degreesPerPixel */
    public void setDegreesPerPixel (float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
            float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
            camera.direction.rotate(camera.up, deltaX);
            tmp.set(camera.direction).crs(camera.up).nor();
            camera.direction.rotate(tmp, deltaY);
        }
        return false;
    }

    public void update () {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update (float deltaTime) {
        if (keys.containsKey(FORWARD)) {
            tmp.set(camera.direction).nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(BACKWARD)) {
            tmp.set(camera.direction).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(STRAFE_LEFT)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(STRAFE_RIGHT)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(UP)) {
            tmp.set(camera.up).nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(DOWN)) {
            tmp.set(camera.up).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }
        camera.update(true);
    }
}
