package util;

import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;

import spark.Response;

public class Message {
    private static Gson gson = new Gson();

    public static Map<String, String> Error(Response response, String message) {
        response.status(400);

        Map<String, String> data = new HashMap<>();
        data.put("status", "error");
        data.put("message", message);

        return data;
    }

    public static Object NotFound(Response response) {
        response.status(404);
        return "";
    }

    public static Map<String, String> Ok(Response response) {
        response.status(400);

        Map<String, String> data = new HashMap<>();
        data.put("status", "ok");

        return data;
    }
}
