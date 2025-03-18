package restclient.core;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import restclient.utiles.TerminalUtils;

// Clase para la animacion de carga por si queremos bloquear el hilo principal para esperar la respuesta(HttpResponse) de la peticion asincrona
public class LoadingAnimation {

    public static void show(CompletableFuture<?> future) {
        String message = "Esperando respuesta del servidor";
        String[] frames = { ".", "..", "..." };
        int frameIndex = 0;

        System.out.print(message + frames);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> {
            printAnimation(future, message, frames, frameIndex, executor);
        }, 0, 300, TimeUnit.MILLISECONDS);

        try {
            future.join();
        } finally {
            executor.shutdown();
            TerminalUtils.clearScreen();
        }
    }


    private static void printAnimation(CompletableFuture<?> future, String message, String[] frames, int frameIndex,
            ScheduledExecutorService executor) {
        if (future.isDone()) {
            executor.shutdown();
            return;
        }
        frameIndex = (frameIndex + 1) % frames.length;
        System.out.print("\r" + message + frames[frameIndex]);
    }

    // public static void show(CompletableFuture<?> future) {
    // String message = "Esperando respuesta del servidor";
    // String[] frames = { ".", "..", "..." };
    // int frameIndex = 0;
    // // Mostrar mensaje inicial
    // System.out.print(message);

    // while (!future.isDone()) {
    // // Retornar al principio de la línea
    // System.out.print("\r");

    // // Mostrar mensaje base
    // System.out.print(message);

    // // Mostrar los puntos actuales
    // System.out.print(frames[frameIndex]);

    // frameIndex = (frameIndex + 1) % frames.length;

    // try {
    // Thread.sleep(300); // Pausa para que sea visible la animación
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // break;
    // }
    // }

    // TerminalUtils.clearScreen();
    // }
}