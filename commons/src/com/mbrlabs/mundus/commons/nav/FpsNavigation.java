///*
// * Copyright (c) 2016. See AUTHORS file.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.mbrlabs.mundus.commons.nav;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.InputAdapter;
//import com.badlogic.gdx.graphics.Camera;
//import com.badlogic.gdx.math.Vector3;
//import com.badlogic.gdx.utils.IntIntMap;
//import com.mbrlabs.mundus.terrain.Terrain;
//import com.mbrlabs.mundus.terrain.TerrainGroup;
//
///**
// * @author Marcus Brummer
// * @version 04-01-2016
// */
//public class FpsNavigation extends InputAdapter {
//
//    private Camera camera;
//    private TerrainGroup terrainGroup;
//
//    private final IntIntMap keys = new IntIntMap();
//    private int STRAFE_LEFT = Input.Keys.A;
//    private int STRAFE_RIGHT = Input.Keys.D;
//    private int FORWARD = Input.Keys.W;
//    private int BACKWARD = Input.Keys.S;
//    private int UP = Input.Keys.Q;
//    private int DOWN = Input.Keys.E;
//    private float velocity = 200;
//    private float degreesPerPixel = 0.5f;
//    private final Vector3 tmp = new Vector3();
//
//    private float height = 10f;
//
//    public FpsNavigation (Camera camera, TerrainGroup terrainGroup) {
//        this.camera = camera;
//        this.terrainGroup = terrainGroup;
//    }
//
//    public void setTerrainGroup(TerrainGroup terrainGroup) {
//        this.terrainGroup = terrainGroup;
//    }
//
//    public void setCamera(Camera camera) {
//        this.camera = camera;
//    }
//
//    @Override
//    public boolean keyDown (int keycode) {
//        keys.put(keycode, keycode);
//        return false;
//    }
//
//    @Override
//    public boolean keyUp (int keycode) {
//        keys.remove(keycode, 0);
//        return false;
//    }
//
//    /** Sets the velocity in units per second for moving forward, backward and strafing left/right.
//     * @param velocity the velocity in units per second */
//    public void setVelocity (float velocity) {
//        this.velocity = velocity;
//    }
//
//    public void setHeight(float height) {
//        this.height = height;
//    }
//
//    public float getHeight() {
//        return this.height;
//    }
//
//    /** Sets how many degrees to rotate per pixel the mouse moved.
//     * @param degreesPerPixel */
//    public void setDegreesPerPixel (float degreesPerPixel) {
//        this.degreesPerPixel = degreesPerPixel;
//    }
//
//    @Override
//    public boolean mouseMoved(int screenX, int screenY) {
//        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
//        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
//        camera.direction.rotate(camera.up, deltaX);
//        tmp.set(camera.direction).crs(camera.up).nor();
//        camera.direction.rotate(tmp, deltaY);
//
//        return false;
//    }
//
//    public void update () {
//        update(Gdx.graphics.getDeltaTime());
//    }
//
//    public void update (float deltaTime) {
//        if (keys.containsKey(FORWARD)) {
//            tmp.set(camera.direction).nor().scl(deltaTime * velocity);
//            camera.position.add(tmp);
//        }
//        if (keys.containsKey(BACKWARD)) {
//            tmp.set(camera.direction).nor().scl(-deltaTime * velocity);
//            camera.position.add(tmp);
//        }
//        if (keys.containsKey(STRAFE_LEFT)) {
//            tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
//            camera.position.add(tmp);
//        }
//        if (keys.containsKey(STRAFE_RIGHT)) {
//            tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
//            camera.position.add(tmp);
//        }
//        if (keys.containsKey(UP)) {
//            tmp.set(camera.up).nor().scl(deltaTime * velocity);
//            camera.position.add(tmp);
//        }
//        if (keys.containsKey(DOWN)) {
//            tmp.set(camera.up).nor().scl(-deltaTime * velocity);
//            camera.position.add(tmp);
//        }
//
//        Terrain terrain = terrainGroup.getTerrain(camera.position.x, camera.position.z);
//        if(terrain != null) {
//            camera.position.y = terrain.getHeightAtWorldCoord(camera.position.x, camera.position.z) + height;
//        }
//
//        camera.update(true);
//    }
//
//}