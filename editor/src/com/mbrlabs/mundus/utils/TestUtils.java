package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.terrain.Terrain;

import java.util.Random;

/**
 * @author Marcus Brummer
 * @version 07-12-2015
 */
public class TestUtils {

    public static Array<ModelInstance> createABunchOfModelsOnTheTerrain(int count, Model model, Terrain terrain) {
        Array<ModelInstance> boxInstances = new Array<>();
        Random rand = new Random();
        for(int i = 0; i < count; i++) {
            ModelInstance mi = new ModelInstance(model);
            mi.transform.setTranslation(terrain.position);
            float x = terrain.terrainWidth*rand.nextFloat();
            float z = terrain.terrainDepth*rand.nextFloat();
            float y = terrain.getHeightAtWorldCoord(x, z);
            mi.transform.translate(x,  y, z);
            boxInstances.add(mi);
        }

        return boxInstances;
    }

}
