package me.name.bot.Common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JSONLoad {
    public static <T> T LoadJSON(String path, Class<T> classType) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(path);
        T obj = null;
        try {
            obj = objectMapper.readValue(file, classType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }
}
