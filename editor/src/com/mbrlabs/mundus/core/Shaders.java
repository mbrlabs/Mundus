package com.mbrlabs.mundus.core;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.mbrlabs.mundus.shader.BrushShader;
import com.mbrlabs.mundus.shader.EntityShader;
import com.mbrlabs.mundus.shader.TerrainShader;

/**
 * @author Marcus Brummer
 * @version 08-12-2015
 */
public class Shaders {

    public BrushShader   brushShader;
    public TerrainShader terrainShader;
    public EntityShader  entityShader;

    public Shaders() {
        ShaderProgram.pedantic = false;
        brushShader = new BrushShader();
        brushShader.init();
        terrainShader = new TerrainShader();
        terrainShader.init();
        entityShader = new EntityShader();
        entityShader.init();
    }

    public void dispose() {
        brushShader.dispose();
        terrainShader.dispose();
        entityShader.dispose();
    }

}
