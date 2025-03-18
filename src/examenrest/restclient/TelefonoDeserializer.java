package examenrest.restclient;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import examenrest.models.Operador;
import examenrest.models.Telefono;
import restclient.RestClientFacade;

public class TelefonoDeserializer implements JsonDeserializer<Telefono>, JsonSerializer<Telefono> {

        @Override
        public Telefono deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
                        throws JsonParseException {
                JsonObject jsonObject = arg0.getAsJsonObject();
                int telefono = jsonObject.get("numTelefono").getAsInt();
                int codOperador = jsonObject.get("codOperador").getAsInt();
                String titular = jsonObject.get("titular").getAsString();
                SendRequest sendRequest = new SendRequest();
                Operador operador = sendRequest.getOperador(codOperador).resultNow();
                Telefono telefonoObj = new Telefono(telefono, operador, titular);
                return telefonoObj;
        }

        @Override
        public JsonElement serialize(Telefono arg0, Type arg1, JsonSerializationContext arg2) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("numTelefono", arg0.getNumTelefono());
                jsonObject.addProperty("codOperador", arg0.getOperador().getCodOperador());
                jsonObject.addProperty("titular", arg0.getTitular());
                return jsonObject;
        }
}