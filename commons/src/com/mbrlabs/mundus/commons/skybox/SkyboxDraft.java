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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by marcus on 1/7/16.
 */
public class SkyboxDraft implements Disposable {

    Matrix4 tranformation;
    ShaderProgram program;
    int u_projTrans;
    int u_worldTrans;
    int u_tex;

    Texture[] textures;

    Mesh quad;
    boolean invert = false;

    protected String vertexShader =
            " attribute vec4 a_position; "+
                    " attribute vec2 a_texCoord0; "+
                    " varying vec2 v_texCoord; "+
                    " uniform mat4 u_worldTrans; "+
                    " uniform mat4 u_projTrans; "+
                    " void main() "+
                    " {  "+
                    "   gl_Position = u_projTrans * u_worldTrans * vec4(a_position);     "+
                    "   v_texCoord = a_texCoord0;    "+
                    " } ";

    protected String fragmentShader =
            " #ifdef GL_ES \n"+
                    " precision mediump float; \n"+
                    " #endif \n"+
                    " uniform sampler2D s_diffuse; "+
                    " varying vec2 v_texCoord; "+
                    " void main() "+
                    " { "+
                    "   gl_FragColor = texture2D( s_diffuse, v_texCoord );   "+
                    " } ";

    public String getDefaultVertexShader(){
        return vertexShader;
    }

    public String getDefaultFragmentShader(){
        return fragmentShader;
    }


    public SkyboxDraft(Pixmap positiveX, Pixmap negativeX, Pixmap positiveY, Pixmap negativeY, Pixmap positiveZ, Pixmap negativeZ) {

        textures = new Texture[6];

        textures[3] = new Texture(positiveX);
        textures[2] = new Texture(negativeX);

        textures[4] = new Texture(positiveY);
        textures[5] = new Texture(negativeY);

        textures[0] = new Texture(positiveZ);
        textures[1] = new Texture(negativeZ);

        positiveX.dispose();
        positiveX=null;

        negativeX.dispose();
        negativeX=null;

        positiveY.dispose();
        positiveY=null;

        negativeY.dispose();
        negativeY=null;

        positiveZ.dispose();
        positiveZ=null;

        negativeZ.dispose();
        negativeZ=null;

        init();
    }

    public SkyboxDraft(FileHandle positiveX, FileHandle negativeX, FileHandle positiveY, FileHandle negativeY, FileHandle positiveZ, FileHandle negativeZ) {
        this(new Pixmap(positiveX), new Pixmap(negativeX), new Pixmap(positiveY), new Pixmap(negativeY), new Pixmap(positiveZ), new Pixmap(negativeZ));
    }

    public SkyboxDraft(Pixmap cubemap) {
        int w = cubemap.getWidth();
        int h = cubemap.getHeight();

        Pixmap[] data = new Pixmap[6];
        for(int i=0; i<6; i++) data[i] = new Pixmap(w/4, h/3, Pixmap.Format.RGB888);
        for(int x=0; x<w; x++)
            for(int y=0; y<h; y++){
                //-X
                if(x>=0 && x<=w/4 && y>=h/3 && y<=h*2/3) data[1].drawPixel(x, y-h/3, cubemap.getPixel(x, y));
                //+Y
                if(x>=w/4 && x<=w/2+1 && y>=0 && y<=h/3) data[2].drawPixel(x-w/4, y, cubemap.getPixel(x, y));
                //+Z
                if(x>=w/4 && x<=w/2 && y>=h/3 && y<=h*2/3) data[4].drawPixel(x-w/4, y-h/3, cubemap.getPixel(x, y));
                //-Y
                if(x>=w/4 && x<=w/2 && y>=h*2/3 && y<=h) data[3].drawPixel(x-w/4, y-h*2/3, cubemap.getPixel(x, y));
                //+X
                if(x>=w/2 && x<=w*3/4 && y>=h/3 && y<=h*2/3) data[0].drawPixel(x-w/2, y-h/3, cubemap.getPixel(x, y));
                //-Z
                if(x>=w*3/4 && x<=w && y>=h/3 && y<=h*2/3) data[5].drawPixel(x-w*3/4, y-h/3, cubemap.getPixel(x, y));
            }

        textures = new Texture[6];

        textures[0] = new Texture(data[4]);
        textures[1] = new Texture(data[5]);

        textures[2] = new Texture(data[1]);
        textures[3] = new Texture(data[0]);

        textures[4] = new Texture(data[2]);
        textures[5] = new Texture(data[3]);

        for(int i=0; i<6; i++) {
            data[i].dispose();
            data[i] = null;
        }
        cubemap.dispose();
        cubemap=null;

        init();
    }

    public SkyboxDraft(FileHandle cubemap){
        this(new Pixmap(cubemap));
    }

    public Mesh createTexturedQuad(){
        Mesh quad = new Mesh(true, 4, 6, VertexAttribute.Position(), new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"));
        quad.setVertices(new float[]
                {-1f, -1f, 0, 0, 1,
                        1f, -1f, 0, 1, 1,
                        1f, 1f, 0, 1, 0,
                        -1f, 1f, 0, 0, 0});
        quad.setIndices(new short[] {0, 1, 2, 2, 3, 0});
        return quad;
    }

    public void setInvert(boolean enable){
        invert = enable;
    }

    public void init() {
        program = new ShaderProgram(vertexShader, fragmentShader);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        else Gdx.app.log("shader", "shader compiled successfully!");
        u_projTrans = program.getUniformLocation("u_projTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_tex = program.getUniformLocation("s_diffuse");

        tranformation = new Matrix4();
        quad = createTexturedQuad();

    }


    public void render(Camera camera){

        Gdx.graphics.getGL20().glCullFace(GL20.GL_BACK);

        program.begin();
        program.setUniformMatrix(u_projTrans, camera.combined);

        //front
        tranformation.idt();
        tranformation.translate(camera.position.x, camera.position.y, camera.position.z);
        tranformation.translate(0, 0, -1);
        if(invert) tranformation.rotate(Vector3.Y, 180);
        program.setUniformMatrix(u_worldTrans, tranformation);
        textures[0].bind(0);
        program.setUniformi("s_diffuse", 0);
        quad.render(program, GL20.GL_TRIANGLES);

        //left
        tranformation.idt();
        tranformation.translate(camera.position.x, camera.position.y, camera.position.z);
        tranformation.rotate(Vector3.Y, 90);
        tranformation.translate(0, 0, -1);
        if(invert) tranformation.rotate(Vector3.Y, 180);
        program.setUniformMatrix(u_worldTrans, tranformation);
        textures[ invert ? 3 : 2].bind(0);
        program.setUniformi("s_diffuse", 0);
        quad.render(program, GL20.GL_TRIANGLES);

        //right
        tranformation.idt();
        tranformation.translate(camera.position.x, camera.position.y, camera.position.z);
        tranformation.rotate(Vector3.Y, -90);
        tranformation.translate(0, 0, -1);
        if(invert) tranformation.rotate(Vector3.Y, 180);
        program.setUniformMatrix(u_worldTrans, tranformation);
        textures[invert ? 2 : 3].bind(0);
        program.setUniformi("s_diffuse", 0);
        quad.render(program, GL20.GL_TRIANGLES);

        //bottom
        tranformation.idt();
        tranformation.translate(camera.position.x, camera.position.y, camera.position.z);
        tranformation.rotate(Vector3.X, -90);
        tranformation.translate(0, 0, -1);
        if(invert) tranformation.rotate(Vector3.Y, 180);
        program.setUniformMatrix(u_worldTrans, tranformation);
        textures[5].bind(0);
        program.setUniformi("s_diffuse", 0);
        quad.render(program, GL20.GL_TRIANGLES);

        //top
        tranformation.idt();
        tranformation.translate(camera.position.x, camera.position.y, camera.position.z);
        tranformation.rotate(Vector3.X, 90);
        tranformation.translate(0, 0, -1);
        if(invert) tranformation.rotate(Vector3.Y, 180);
        program.setUniformMatrix(u_worldTrans, tranformation);
        textures[4].bind(0);
        program.setUniformi("s_diffuse", 0);
        quad.render(program, GL20.GL_TRIANGLES);

        //back
        tranformation.idt();
        tranformation.translate(camera.position.x, camera.position.y, camera.position.z);
        tranformation.rotate(Vector3.Y, 180);
        tranformation.translate(0, 0, -1);
        if(invert) tranformation.rotate(Vector3.Y, 180);
        program.setUniformMatrix(u_worldTrans, tranformation);
        textures[1].bind(0);
        program.setUniformi("s_diffuse", 0);
        quad.render(program, GL20.GL_TRIANGLES);

        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
        quad.dispose();
        for(int i=0; i<6; i++){
            textures[i].dispose();
            textures[i]=null;
        }
    }
}
