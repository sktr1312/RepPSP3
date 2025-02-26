package savetheearth.clases;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class HWWC {
    private List<Meteorito> meteoritos;

    public HWWC(int numMetoritos) {
        meteoritos = new ArrayList<>();
        for (int i = 0; i < numMetoritos; i++) {
            meteoritos.add(new Meteorito(i));
        }
    }

    public synchronized Meteorito obtenerMeteoritoDisponible() {
        List<Meteorito> disponibles = meteoritos.stream()
                .filter(m -> !m.estaDestruido())
                .collect(Collectors.toList());
        Meteorito meteorito = null;
        if (!disponibles.isEmpty()) {
            Random rand = new Random();
            meteorito = disponibles.get(rand.nextInt(disponibles.size()));
        }

        return meteorito;
    }

    public boolean todosDestruidos() {
        return meteoritos.stream().allMatch(Meteorito::estaDestruido);
    }

}