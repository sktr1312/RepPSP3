<?php

require_once 'models/Cliente.php';
class ProvinciaController {

    private static function sendResponse($data, $statusCode = 200) { //repuesta estandar de la api
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');
        header('Access-Control-Allow-Origin: *');
        echo json_encode($data, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
        exit();
    }

    public static function getAll() {
        try {
            $provincias = Provincia::getAll();
            self::sendResponse($provincias);
        } catch (Exception $e) {
            self::sendResponse(["error" => "Error interno del servidor","message" => $e->getMessage()], 500);
        }
    }
    
    public static function getProvincia($id) {
        try {
            $provincia = Cliente::getById($id);
            if ($provincia) {
                self::sendResponse($provincia);
            } else {
                self::sendResponse(["error" => "Usuario no encontrado"], 404);
            }
        } catch (Exception $e) {
            self::sendResponse(["error" => "Error al obtener usuario"], 500);
        }
    }

    public static function createProvincia() {
        try {
            $data = json_decode(file_get_contents("php://input"), true);
            $newId = Provincia::create($data);
            if ($newId) {
                self::sendResponse([
                    "message" => "Cliente creado",
                    "id" => $newId
                ], 201);
            } else {
                self::sendResponse(["error" => "Error al crear cliente"], 500);
            }
        } catch (Exception $e) {
            self::sendResponse([
                "error" => "Error al crear cliente",
                "message" => $e->getMessage()
            ], 500);
        }
    }

    public static function updateProvincia($id) {
        try {
            $data = json_decode(file_get_contents("php://input"), true);
            if (Provincia::update($id, $data)) {
                self::sendResponse(["message" => "Usuario actualizado"]);
            } else {
                self::sendResponse(["error" => "Error al actualizar usuario"], 500);
            }
        } catch (Exception $e) {
            self::sendResponse(["error" => "Error al actualizar usuario"], 500);
        }
    }

    public static function deleteProvincia($id) {
        try {
            if (Provincia::delete($id)) {
                self::sendResponse(["message" => "Usuario eliminado"]);
            } else {
                self::sendResponse(["error" => "Error al eliminar usuario"], 500);
            }
        } catch (Exception $e) {
            self::sendResponse(["error" => "Error al eliminar usuario"], 500);
        }
    }
}