package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.mbrlabs.mundus.utils.MathUtils;

/**
 * @author Marcus Brummer
 * @version 23-12-2015
 */
public class TerrainInstance {

    public Matrix4 transform;
    public Terrain terrain;

    private Vector3 position;

    public long id;
    public String name;

    // used for collision detection
    private final Vector3 c00 = new Vector3();
    private final Vector3 c01 = new Vector3();
    private final Vector3 c10 = new Vector3();
    private final Vector3 c11 = new Vector3();

    public TerrainInstance(Terrain terrain) {
        this.transform = new Matrix4();
        position = new Vector3();
        this.terrain = terrain;
    }

    public float getHeightAtWorldCoord(float worldX, float worldZ) {
        transform.getTranslation(c00);
        float terrainX = worldX - c00.x;
        float terrainZ = worldZ - c00.z;

        float gridSquareSize = terrain.terrainWidth / ((float) terrain.vertexResolution - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);

        if(gridX >= terrain.vertexResolution -1 || gridZ >= terrain.vertexResolution - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }

        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        c01.set(1,terrain.heightData[(gridZ+1) * terrain.vertexResolution + gridX], 0);
        c10.set(0,terrain.heightData[gridZ * terrain.vertexResolution + gridX+1], 1);

        // we are in upper left triangle of the square
        if(xCoord <= (1 - zCoord)) {
            c00.set(0,terrain.heightData[gridZ * terrain.vertexResolution + gridX], 0);
            return MathUtils.barryCentric(c00, c10, c01, new Vector2(zCoord, xCoord));
        }
        // bottom right triangle
        c11.set(1,terrain.heightData[(gridZ+1) * terrain.vertexResolution + gridX+1], 1);
        return MathUtils.barryCentric(c10, c11, c01, new Vector2(zCoord, xCoord));
    }

    public Vector3 getRayIntersection(Vector3 out, Ray ray) {
        // TODO improve performance. use binary search
        float curDistance = 2;
        int rounds = 0;

        long start = System.currentTimeMillis();

        ray.getEndPoint(out, curDistance);
        boolean isUnder = isUnderTerrain(out);

        while(true) {
            rounds++;
            ray.getEndPoint(out, curDistance);

            boolean u = isUnderTerrain(out);
            if(u != isUnder || rounds == 10000) {
           //     Log.debug("getRayIntersection rounds: " + rounds+ " time: " + (System.currentTimeMillis() - start));
                return out;
            }

            if(u) {
                curDistance -= 0.1f;
            } else {
                curDistance += 0.1f;
            }
        }

    }

    private boolean isUnderTerrain(Vector3 pointInWorldCoordinates) {
        float terrainHeight = getHeightAtWorldCoord(pointInWorldCoordinates.x, pointInWorldCoordinates.z);
        return terrainHeight > pointInWorldCoordinates.y;
    }

    public Vector3 getPosition() {
        transform.getTranslation(position);
        return position;
    }

}
