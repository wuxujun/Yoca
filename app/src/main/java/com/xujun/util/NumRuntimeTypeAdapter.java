package com.xujun.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by xujunwu on 13-7-31.
 */
public class NumRuntimeTypeAdapter implements JsonDeserializer<NumRuntime>,JsonSerializer<NumRuntime>{
    @Override
    public NumRuntime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        int runtime;
        try{
            runtime= jsonElement.getAsInt();
        }catch (NumberFormatException e){
            runtime=0;
        }
        return new NumRuntime(runtime);
    }

    @Override
    public JsonElement serialize(NumRuntime numRuntime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(numRuntime.getValue());
    }
}
