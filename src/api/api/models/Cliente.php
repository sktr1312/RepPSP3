<?php
require_once 'Database.php';

class Cliente {
    public static function getAll() {
        $conn = Database::getInstance();
        $stmt = $conn->query("SELECT * FROM clientes");
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public static function getById($id) {
        $conn = Database::getInstance();
        $stmt = $conn->prepare("SELECT * FROM clientes WHERE codCliente = ?");
        $stmt->execute([$id]);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    public static function create($data) {
        try {
            $conn = Database::getInstance();       
            
            $stmt = $conn->prepare("INSERT INTO clientes (nombre, apellidos, codProvincia, vip) VALUES (?, ?, ?,?)");
            if ($stmt->execute([$data['nombre'], $data['apellidos'], $data['codProvincia'], $data['vip']])) {
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
            // Primero verificamos si el cliente existe
            $checkStmt = $conn->prepare("SELECT codCliente FROM clientes WHERE codCliente = ?");
            $checkStmt->execute([$id]);
            if ($checkStmt->rowCount() === 0) {
                return false; // Cliente no existe
            }
            $fields = [];
            $values = [];
            foreach ($data as $key => $value) {
                if ($key !== 'codCliente') { // evitar actualizar la clave 
                    $fields[] = "$key = ?";
                    $values[] = $value;
                }
            }
            $values[] = $id;
            $sql = "UPDATE clientes SET " . implode(', ', $fields) . " WHERE codCliente = ?";
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
        try {
            // Primero verificamos si el cliente existe
            $checkStmt = $conn->prepare("SELECT codCliente FROM clientes WHERE codCliente = ?");
            $checkStmt->execute([$id]);
            
            if ($checkStmt->rowCount() === 0) {
                return false; // Cliente no existe
            }
            // Si existe, procedemos con la eliminación
            $stmt = $conn->prepare("DELETE FROM clientes WHERE codCliente = ?");
            return $stmt->execute([$id]);
        } catch(PDOException $e) {
            error_log("Error en delete: " . $e->getMessage());
            throw $e;
        }
    }
}

?>