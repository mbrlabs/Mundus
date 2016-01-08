/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.commons.skybox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.utils.Disposable;
import com.mbrlabs.mundus.commons.utils.ShaderUtils;

/**
 * Created by marcus on 1/8/16.
 */
public class Skybox implements Disposable {

    private static final float SIZE = 500f;

    private String VERTEX_SHADER = "com/mbrlabs/mundus/commons/skybox/skybox.vert.glsl";
    private String FRAGMENT_SHADER = "com/mbrlabs/mundus/commons/skybox/skybox.frag.glsl";

    private String UNIFORM_TRANSFORM = "u_transMatrix";
    private String UNIFORM_PROJ = "u_projViewMatrix";
    //private String UNIFORM_TEXTURE = "u_texture";

    protected final Pixmap[] data = new Pixmap[6];
    protected ShaderProgram shader;

    protected int loc_trans;
    protected int loc_proj;

    protected Mesh boxMesh;
    private Matrix4 transform;
    private Quaternion q;

    public Skybox(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY,
                  FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ) {
        this(new Pixmap(positiveX), new Pixmap(negativeX), new Pixmap(positiveY), new
                Pixmap(negativeY), new Pixmap(positiveZ), new Pixmap(negativeZ));
    }

    private Skybox(Pixmap positiveX, Pixmap negativeX, Pixmap positiveY, Pixmap negativeY,
                   Pixmap positiveZ, Pixmap negativeZ) {
        data[0]=positiveX;
        data[1]=negativeX;

        data[2]=positiveY;
        data[3]=negativeY;

        data[4]=positiveZ;
        data[5]=negativeZ;

        init();
    }

    private void init(){
        shader = ShaderUtils.compile(VERTEX_SHADER, FRAGMENT_SHADER, true);
        loc_trans = shader.getUniformLocation(UNIFORM_TRANSFORM);
        loc_proj = shader.getUniformLocation(UNIFORM_PROJ);

        transform = new Matrix4();
        q = new Quaternion();

        boxMesh = createMesh();

        initCubemap();
    }

    private void initCubemap(){
        //bind cubemap
        Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, 0);
        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL20.GL_RGB, data[0].getWidth(),
                data[0].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[0].getPixels());
        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL20.GL_RGB, data[1].getWidth(),
                data[1].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[1].getPixels());

        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL20.GL_RGB, data[2].getWidth(),
                data[2].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[2].getPixels());
        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL20.GL_RGB, data[3].getWidth(),
                data[3].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[3].getPixels());

        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL20.GL_RGB, data[4].getWidth(),
                data[4].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[4].getPixels());
        Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL20.GL_RGB, data[5].getWidth(),
                data[5].getHeight(), 0, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, data[5].getPixels());

        //Gdx.gl20.glGenerateMipmap(GL20.GL_TEXTURE_CUBE_MAP);
        //Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR);

        Gdx.gl20.glTexParameteri ( GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_MIN_FILTER,GL20.GL_LINEAR_MIPMAP_LINEAR );
        Gdx.gl20.glTexParameteri ( GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_MAG_FILTER,GL20.GL_LINEAR );
        Gdx.gl20.glTexParameteri ( GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE );
        Gdx.gl20.glTexParameteri ( GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE );

        Gdx.gl20.glGenerateMipmap(GL20.GL_TEXTURE_CUBE_MAP);
    }

    public void render(Camera camera){
        shader.begin();

        transform.idt();
        transform.translate(camera.position);
        shader.setUniformMatrix(loc_trans, transform);

        shader.setUniformMatrix(loc_proj, camera.combined);
        boxMesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
    }

    public Mesh createMesh(){
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(SIZE, SIZE, SIZE,
                new Material(), VertexAttributes.Usage.Position);
        return model.meshes.first();
    }

    @Override
    public void dispose() {
        shader.dispose();
        boxMesh.dispose();
        for(int i=0; i<6; i++)
            data[i].dispose();
    }

}
