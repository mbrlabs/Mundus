package com.mbrlabs.mundus.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

/**
 * @author Marcus Brummer
 * @version 01-12-2015
 */
public class TerrainTest {

    public Terrain terrain;
    public Renderable terrainRenderable;
    public Environment environment;

    public TerrainTest(Shader shader) {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1.0f, -0.8f));

        terrain = new Terrain(64, 64, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal  );
        Pixmap heightMap = new Pixmap(Gdx.files.internal("data/heightmap.png"));
        terrain.loadHeightMap(heightMap);
        heightMap.dispose();

        terrainRenderable = new Renderable();
        terrainRenderable.environment = environment;
        terrainRenderable.meshPart.mesh = terrain.mesh;
        terrainRenderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        terrainRenderable.meshPart.offset = 0;
        terrainRenderable.meshPart.size = terrain.mesh.getNumIndices();
        terrainRenderable.meshPart.update();
        //terrainRenderable.shader = shader;
        //terrainRenderable.material = new Material(ColorAttribute.createDiffuse(Color.SLATE));
    }

}
