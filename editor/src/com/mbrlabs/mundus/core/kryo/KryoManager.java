package com.mbrlabs.mundus.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.mbrlabs.mundus.core.Files;
import com.mbrlabs.mundus.core.home.HomeData;
import com.mbrlabs.mundus.core.project.ProjectRef;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Marcus Brummer
 * @version 12-12-2015
 */
public class KryoManager {

    private Kryo kryo;

    public KryoManager() {
        kryo = new Kryo();
        kryo.setDefaultSerializer(TaggedFieldSerializer.class);
        // !!!!! DO NOT CHANGE THIS, OTHERWISE ALREADY SERIALIZED OBJECT WILL BE UNREADABLE !!!!
        kryo.register(ArrayList.class, 0);
        kryo.register(Date.class, 1);
        kryo.register(ProjectRef.class, 2);
        kryo.register(HomeData.class, 3);
        kryo.register(HomeData.Settings.class, 4);
    }

    public HomeData loadHomeData() {
        try {
            Input input = new Input(new FileInputStream(Files.HOME_DATA_FILE));
            HomeData homeData = kryo.readObjectOrNull(input, HomeData.class);
            if(homeData == null) {
                homeData = new HomeData();
            }
            return homeData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new HomeData();
    }

    public void saveHomeData(HomeData homeData) {
        try {
            Output output = new Output(new FileOutputStream(Files.HOME_DATA_FILE));
            kryo.writeObject(output, homeData);
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



}
