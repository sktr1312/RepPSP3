package restclient.core;

import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

//clase de ayuda para parsear el JSON de las respuestas de las peticiones
public class JsonResponseParser {

    public static JsonArray extractJsonData(HttpResponse<String> response) {
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonObject.getAsJsonArray("data");
    }

    public static String extractJsonMessage(HttpResponse<String> response) {
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        return jsonObject.get("message").getAsString();
    }

    public static boolean isSuccessResponse(HttpResponse<String> response) {
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

    public static <T> List<T> parseOfList(JsonArray jsonArray, Gson gson, Class<T> elementType) {
        Type listType = TypeToken.getParameterized(List.class, elementType).getType();
        return gson.fromJson(jsonArray, listType);
    }
}
