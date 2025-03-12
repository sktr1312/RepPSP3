package clienteHttp;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import clienteHttp.models.Historial;
import clienteHttp.models.Operador;
import clienteHttp.models.Telefono;

public class HistorialDeserializer implements JsonDeserializer<Historial>, JsonSerializer<Historial> {

    @Override
    public JsonElement serialize(Historial arg0, Type arg1, JsonSerializationContext arg2) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", arg0.getId());
        jsonObject.addProperty("telefono", arg0.getTelefono().getNumTelefono());
        jsonObject.addProperty("codOperadorAntiguo", arg0.getOperadorAntiguo().getCodOperador());
        jsonObject.addProperty("codOperadorNuevo", arg0.getOperadorNuevo().getCodOperador());
        jsonObject.addProperty("motivos", arg0.getMotivos());
        return jsonObject;
    }

    @Override
    public Historial deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
            throws JsonParseException {
        JsonObject jsonObject = arg0.getAsJsonObject();
        int id = jsonObject.get("id").getAsInt();
        Telefono telefono = new SendRequest().sendGetTelefonoRequest(jsonObject.get("telefono").getAsInt());
        Operador operadorAntiguo = new SendRequest()
                .sendGetOperadorRequest(jsonObject.get("codOperadorAntiguo").getAsInt());
        Operador operadorNuevo = new SendRequest()
                .sendGetOperadorRequest(jsonObject.get("codOperadorNuevo").getAsInt());
        String motivos = jsonObject.get("motivos").getAsString();

        Historial historial = new Historial(id, telefono, operadorAntiguo, operadorNuevo, motivos);
        return historial;
    }

}
