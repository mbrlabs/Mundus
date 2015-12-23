package com.mbrlabs.mundus.utils;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.terrain.Terrain;
import com.mbrlabs.mundus.terrain.TerrainInstance;

import java.util.Random;

/**
 * @author Marcus Brummer
 * @version 07-12-2015
 */
public class TestUtils {

    public static Array<ModelInstance> createABunchOfModelsOnTheTerrain(int count, Model model, TerrainInstance terrain) {
        Array<ModelInstance> boxInstances = new Array<>();
        Random rand = new Random();

        Vector3 tv3 = new Vector3();

        for(int i = 0; i < count; i++) {
            ModelInstance mi = new ModelInstance(model);
            terrain.transform.getTranslation(tv3);
            mi.transform.setTranslation(tv3);
            float x = terrain.terrain.terrainWidth*rand.nextFloat();
            float z = terrain.terrain.terrainDepth*rand.nextFloat();
            float y = terrain.getHeightAtWorldCoord(x, z);
            mi.transform.translate(x,  y, z);
            boxInstances.add(mi);
        }

        return boxInstances;
    }

}
