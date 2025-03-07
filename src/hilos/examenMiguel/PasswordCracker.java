package hilos.examenMiguel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PasswordCracker {
    public static final int MAX_CHAR = 4;
    private static final int NUM_HACKERS = 3;
    private static final Scanner scanner = new Scanner(System.in);
    private static final RainbowTable rainbowTable = new RainbowTable();
    
    public static void main(String[] args) {
        System.out.println("Simulador de ataque de fuerza bruta con tabla rainbow");
        System.out.println("Tabla rainbow inicial: " + rainbowTable.size() + " entradas");

        while (true) {
            System.out.print("Introduzca una contraseña para atacar (o 'fin' para terminar): ");
            String userPassword = scanner.nextLine().trim();
            if (userPassword.equalsIgnoreCase("fin")) break;
            if (userPassword.length() > MAX_CHAR) {
                System.out.println("La contraseña debe tener como máximo " + MAX_CHAR + " caracteres.");
                continue;
            }
            attackPassword(userPassword);
        }
        System.out.println("Programa finalizado.");
    }
    
    private static void attackPassword(String password) {
        String targetHash = HashUtils.hashPassword(password);
        if (rainbowTable.containsHash(targetHash)) {
            System.out.println("¡Contraseña encontrada en la tabla rainbow!: " + rainbowTable.getPassword(targetHash));
            return;
        }
        
        AtomicBoolean passwordFound = new AtomicBoolean(false);
        AtomicInteger totalAttempts = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        Thread[] hackers = new Thread[NUM_HACKERS];
        for (int i = 0; i < NUM_HACKERS; i++) {
            hackers[i] = new Thread(new Hacker(i, targetHash, rainbowTable, passwordFound, totalAttempts));
            hackers[i].start();
        }
        
        for (Thread hacker : hackers) {
            try { hacker.join(); } catch (InterruptedException ignored) {}
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Intentos: " + totalAttempts.get() + " | Tiempo: " + elapsedTime + " ms");
    }
}

class Hacker implements Runnable {
    private final int id;
    private final String targetHash;
    private final RainbowTable rainbowTable;
    private final AtomicBoolean passwordFound;
    private final AtomicInteger totalAttempts;
    
    public Hacker(int id, String targetHash, RainbowTable rainbowTable, AtomicBoolean passwordFound, AtomicInteger totalAttempts) {
        this.id = id;
        this.targetHash = targetHash;
        this.rainbowTable = rainbowTable;
        this.passwordFound = passwordFound;
        this.totalAttempts = totalAttempts;
    }

    @Override
    public void run() {
        Random random = new Random();
        boolean isAlphabetic = id % 3 == 0;
        boolean isNumeric = id % 3 == 1;

        while (!passwordFound.get()) {
            String candidate = HashUtils.generateCandidate(random, isAlphabetic, isNumeric);
            totalAttempts.incrementAndGet();
            
            if (rainbowTable.containsValue(candidate) || targetHash.equals(HashUtils.hashPassword(candidate))) {
                passwordFound.set(true);
                System.out.println("Hacker " + id + " encontró la contraseña: " + candidate);
                rainbowTable.addEntry(targetHash, candidate);
                break;
            }
        }
    }
}

class RainbowTable {
    private final Map<String, String> table = new ConcurrentHashMap<>();
    
    public boolean containsHash(String hash) { return table.containsKey(hash); }
    public boolean containsValue(String value) { return table.containsValue(value); }
    public void addEntry(String hash, String password) { table.put(hash, password); }
    public String getPassword(String hash) { return table.get(hash); }
    public int size() { return table.size(); }
}

class HashUtils {
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                hexString.append((hex.length() == 1) ? "0" + hex : hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando hash", e);
        }
    }
    
    public static String generateCandidate(Random random, boolean alphabeticOnly, boolean numericOnly) {
        StringBuilder sb = new StringBuilder();
        int length = random.nextInt(PasswordCracker.MAX_CHAR) + 1;
        for (int i = 0; i < length; i++) {
            if (alphabeticOnly) sb.append(CHARS.charAt(random.nextInt(26)));
            else if (numericOnly) sb.append(CHARS.charAt(26 + random.nextInt(10)));
            else sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
