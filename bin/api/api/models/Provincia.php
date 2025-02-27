<?php
require_once 'Database.php';

class Provincia {
    public static function getAll() {
        $conn = Database::getInstance();
        $stmt = $conn->query("SELECT * FROM provincias");
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public static function getById($id) {
        $conn = Database::getInstance();
        $stmt = $conn->prepare("SELECT * FROM provincias WHERE codProvincia = ?");
        $stmt->execute([$id]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }
    
    public static function create($data) {
        try {
            $conn = Database::getInstance();
            $stmt = $conn->prepare("INSERT INTO provincias (nombre ) VALUES (?)");
            if ($stmt->execute([$data['nombre']])) {
                return $conn->lastInsertId(); // ID del cliente recién creado
            }
            return false;
        } catch (PDOException $e) {
            error_log("Error en create: " . $e->getMessage());
            throw $e;
        }
    }

    public static function update($id, $data) {
        try {
            $conn = Database::getInstance();
            $fields = [];
            $values = [];
            foreach ($data as $key => $value) {
                if ($key !== 'codCliente') { // evitar actualizar la clave 
                    $fields[] = "$key = ?";
                    $values[] = $value;
                }
            }
            $values[] = $id;
            $sql = "UPDATE provincias SET " . implode(', ', $fields) . " WHERE codProvincia = ?";
            error_log("SQL generado: " . $sql);
            $stmt = $conn->prepare($sql);
            return $stmt->execute($values);
        } catch (PDOException $e) {
            error_log("Error en update: " . $e->getMessage());
            throw $e;
        }
    }

    public static function delete($id) {
        $conn = Database::getInstance();
        $stmt = $conn->prepare("DELETE FROM provincias WHERE codProvincia = ?");
        return $stmt->execute([$id]);
    }
}
?>