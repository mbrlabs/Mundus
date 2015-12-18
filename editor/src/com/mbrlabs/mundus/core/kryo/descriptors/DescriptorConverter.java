package com.mbrlabs.mundus.core.kryo.descriptors;

import com.mbrlabs.mundus.core.project.ProjectContext;
import com.mbrlabs.mundus.model.MModel;
import com.mbrlabs.mundus.terrain.Terrain;

/**
 * @author Marcus Brummer
 * @version 17-12-2015
 */
public class DescriptorConverter {

    public static ModelDescriptor convert(MModel model) {
        ModelDescriptor descriptor = new ModelDescriptor();
        descriptor.setName(model.name);
        descriptor.setId(model.id);
        descriptor.setG3dbPath(model.g3dbPath);
        return descriptor;
    }

    public static MModel convert(ModelDescriptor modelDescriptor) {
        MModel model = new MModel();
        model.id = modelDescriptor.getId();
        model.name = modelDescriptor.getName();
        model.g3dbPath = modelDescriptor.getG3dbPath();

        return model;
    }

    public static TerrainDescriptor convert(Terrain terrain) {
        TerrainDescriptor descriptor = new TerrainDescriptor();
        descriptor.setId(terrain.id);
        descriptor.setName(terrain.name);
        descriptor.setPath(terrain.terraPath);
        descriptor.setWidth(terrain.terrainWidth);
        descriptor.setDepth(terrain.terrainDepth);
        descriptor.setPosX(terrain.position.x);
        descriptor.setPosZ(terrain.position.z);
        descriptor.setVertexResolution(terrain.vertexResolution);
        return descriptor;
    }

    public static Terrain convert(TerrainDescriptor terrainDescriptor) {
        Terrain terrain = new Terrain(terrainDescriptor.getVertexResolution());
        terrain.terrainWidth = terrainDescriptor.getWidth();
        terrain.terrainDepth = terrainDescriptor.getDepth();
        terrain.position.x = terrainDescriptor.getPosX();
        terrain.position.z = terrainDescriptor.getPosZ();
        terrain.terraPath = terrainDescriptor.getPath();
        terrain.id = terrainDescriptor.getId();
        terrain.name = terrainDescriptor.getName();

        return terrain;
    }

    public static ProjectDescriptor convert(ProjectContext project) {
        ProjectDescriptor descriptor = new ProjectDescriptor();
        descriptor.setName(project.name);
        descriptor.setId(project.id);
        descriptor.setNextAvailableID(project.getNextAvailableID());
        // terrains
        for(Terrain terrain : project.terrains.getTerrains()) {
            descriptor.getTerrains().add(convert(terrain));
        }
        // models
        for(MModel model : project.models) {
            descriptor.getModels().add(convert(model));
        }
        // TODO instances

        return descriptor;
    }

    public static ProjectContext convert(ProjectDescriptor projectDescriptor) {
        ProjectContext context = new ProjectContext(projectDescriptor.getNextAvailableID());
        context.name = projectDescriptor.getName();
        // models
        for(ModelDescriptor model : projectDescriptor.getModels()) {
            context.models.add(convert(model));
        }
        // terrains
        for(TerrainDescriptor terrain : projectDescriptor.getTerrains()) {
            context.terrains.add(convert(terrain));
        }

        context.loaded = false;
        return context;
    }



}
