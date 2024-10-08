/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.system.service.json;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.joml.Vector2i;
import org.joml.Vector3f;
import javagems3d.engine.system.graph.Graph;
import javagems3d.engine.system.graph.GraphChunk;
import javagems3d.engine.system.graph.GraphEdge;
import javagems3d.engine.system.graph.GraphVertex;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONGraphDeserializer implements JsonDeserializer<Graph> {

    private GraphVertex fromStringGV(String str) {
        str = str.replace("(", "").replace(")", "").trim().replace(",", ".");
        String[] parts = str.split("\\s+");
        float x = Float.parseFloat(parts[0]);
        float y = Float.parseFloat(parts[1]);
        float z = Float.parseFloat(parts[2]);
        return new GraphVertex(new Vector3f(x, y, z));
    }

    private GraphChunk fromStringGC(String str) {
        str = str.replace("(", "").replace(")", "").trim().replace(",", ".");
        String[] parts = str.split("\\s+");
        float x = Float.parseFloat(parts[0]);
        float z = Float.parseFloat(parts[1]);
        return new GraphChunk(new Vector2i((int) x, (int) z));
    }

    @Override
    public Graph deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Map<GraphVertex, List<GraphEdge>> graph = new HashMap<>();
        JsonObject graphJson = jsonObject.getAsJsonObject("graph");
        for (Map.Entry<String, JsonElement> entry : graphJson.entrySet()) {
            GraphVertex vertex = this.fromStringGV(context.deserialize(new JsonPrimitive(entry.getKey()), String.class));
            List<GraphEdge> edges = context.deserialize(entry.getValue(), new TypeToken<List<GraphEdge>>() {
            }.getType());
            graph.put(vertex, edges);
        }

        Map<GraphChunk, List<GraphVertex>> graphChunkGroups = new HashMap<>();
        JsonObject chunkGroupsJson = jsonObject.getAsJsonObject("graphChunkGroups");
        for (Map.Entry<String, JsonElement> entry : chunkGroupsJson.entrySet()) {
            GraphChunk chunk = this.fromStringGC(context.deserialize(new JsonPrimitive(entry.getKey()), String.class));
            List<GraphVertex> vertices = context.deserialize(entry.getValue(), new TypeToken<List<GraphVertex>>() {
            }.getType());
            graphChunkGroups.put(chunk, vertices);
        }

        GraphVertex start = context.deserialize(jsonObject.get("start"), GraphVertex.class);

        Graph graph1 = new Graph();
        graph1.setStart(start);
        graph1.setGraph(graph);
        graph1.setGraphChunkGroups(graphChunkGroups);
        return graph1;
    }
}