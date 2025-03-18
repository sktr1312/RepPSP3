package restclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import examenrest.models.Historial;
import examenrest.models.Telefono;
import restclient.deserializer.TelefonoDeserializer;
import restclient.deserializer.HistorialDeserializer;


//clase para configuraciones del cliente REST, como el Gson con los deserializadores personalizados
public class RestClientConfig {

    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Telefono.class, new TelefonoDeserializer())
                .registerTypeAdapter(Historial.class, new HistorialDeserializer())
                .create();
    }
}