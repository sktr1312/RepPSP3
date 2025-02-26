<?php
// Configuración de la base de datos
class Database
{
    private $host = "localhost";
    private $db_name = "tienda_db";
    private $username = "root";
    private $password = "";
    public $conn;

    public function getConnection()
    {
        $this->conn = null;
        try {
            $this->conn = new PDO(
                "mysql:host=" . $this->host . ";dbname=" . $this->db_name,
                $this->username,
                $this->password
            );
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch (PDOException $e) {
            echo "Error de conexión: " . $e->getMessage();
        }
        return $this->conn;
    }
}

// Clase base para el manejo de respuestas
class APIResponse
{
    public static function send($data, $status = 200)
    {
        header("Content-Type: application/json; charset=UTF-8");
        http_response_code($status);
        echo json_encode($data);
    }
}

// Clase Usuario
class Usuario
{
    private $conn;
    private $table_name = "usuarios";

    public function __construct($db)
    {
        $this->conn = $db;
    }

    // Obtener todos los usuarios
    public function getAll()
    {
        $query = "SELECT id, nombre, email, telefono FROM " . $this->table_name;
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    // Obtener un usuario por ID
    public function getById($id)
    {
        $query = "SELECT id, nombre, email, telefono FROM " . $this->table_name . " WHERE id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->execute([$id]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    // Crear usuario
    public function create($data)
    {
        $query = "INSERT INTO " . $this->table_name . " (nombre, email, telefono) VALUES (?, ?, ?)";
        $stmt = $this->conn->prepare($query);
        return $stmt->execute([$data['nombre'], $data['email'], $data['telefono']]);
    }

    // Actualizar usuario
    public function update($id, $data)
    {
        $query = "UPDATE " . $this->table_name . " SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
        $stmt = $this->conn->prepare($query);
        return $stmt->execute([$data['nombre'], $data['email'], $data['telefono'], $id]);
    }

    // Eliminar usuario
    public function delete($id)
    {
        $query = "DELETE FROM " . $this->table_name . " WHERE id = ?";
        $stmt = $this->conn->prepare($query);
        return $stmt->execute([$id]);
    }
}

// Clase Producto
class Producto
{
    private $conn;
    private $table_name = "productos";

    public function __construct($db)
    {
        $this->conn = $db;
    }

    // Obtener todos los productos
    public function getAll()
    {
        $query = "SELECT id, nombre, precio, stock FROM " . $this->table_name;
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    // Obtener un producto por ID
    public function getById($id)
    {
        $query = "SELECT id, nombre, precio, stock FROM " . $this->table_name . " WHERE id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->execute([$id]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    // Crear producto
    public function create($data)
    {
        $query = "INSERT INTO " . $this->table_name . " (nombre, precio, stock) VALUES (?, ?, ?)";
        $stmt = $this->conn->prepare($query);
        return $stmt->execute([$data['nombre'], $data['precio'], $data['stock']]);
    }

    // Actualizar producto
    public function update($id, $data)
    {
        $query = "UPDATE " . $this->table_name . " SET nombre = ?, precio = ?, stock = ? WHERE id = ?";
        $stmt = $this->conn->prepare($query);
        return $stmt->execute([$data['nombre'], $data['precio'], $data['stock'], $id]);
    }

    // Eliminar producto
    public function delete($id)
    {
        $query = "DELETE FROM " . $this->table_name . " WHERE id = ?";
        $stmt = $this->conn->prepare($query);
        return $stmt->execute([$id]);
    }
}

// Clase Pedido
class Pedido
{
    private $conn;
    private $table_name = "pedidos";

    public function __construct($db)
    {
        $this->conn = $db;
    }

    // Obtener todos los pedidos
    public function getAll()
    {
        $query = "SELECT p.id, p.usuario_id, p.fecha, p.total, u.nombre as nombre_usuario 
                 FROM " . $this->table_name . " p
                 JOIN usuarios u ON p.usuario_id = u.id";
        $stmt = $this->conn->prepare($query);
        $stmt->execute();
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
    }

    // Obtener un pedido por ID
    public function getById($id)
    {
        $query = "SELECT p.id, p.usuario_id, p.fecha, p.total, u.nombre as nombre_usuario 
                 FROM " . $this->table_name . " p
                 JOIN usuarios u ON p.usuario_id = u.id
                 WHERE p.id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->execute([$id]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }

    // Crear pedido
    public function create($data)
    {
        $query = "INSERT INTO " . $this->table_name . " (usuario_id, fecha, total) VALUES (?, NOW(), ?)";
        $stmt = $this->conn->prepare($query);
        return $stmt->execute([$data['usuario_id'], $data['total']]);
    }

    // Actualizar pedido
    public function update($id, $data)
    {
        $query = "UPDATE " . $this->table_name . " SET usuario_id = ?, total = ? WHERE id = ?";
        $stmt = $this->conn->prepare($query);
        return $stmt->execute([$data['usuario_id'], $data['total'], $id]);
    }

    // Eliminar pedido
    public function delete($id)
    {
        $query = "DELETE FROM " . $this->table_name . " WHERE id = ?";
        $stmt = $this->conn->prepare($query);
        return $stmt->execute([$id]);
    }
}

// Manejo de rutas y endpoints
$request_method = $_SERVER['REQUEST_METHOD'];
$path = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$path_parts = explode('/', trim($path, '/'));
$resource = $path_parts[1] ?? '';
$id = $path_parts[2] ?? null;

// Inicializar la conexión a la base de datos
$database = new Database();
$db = $database->getConnection();

// Routing
try {
    switch ($resource) {
        case 'usuarios':
            $usuario = new Usuario($db);
            handleResource($usuario, $request_method, $id);
            break;

        case 'productos':
            $producto = new Producto($db);
            handleResource($producto, $request_method, $id);
            break;

        case 'pedidos':
            $pedido = new Pedido($db);
            handleResource($pedido, $request_method, $id);
            break;

        default:
            throw new Exception("Recurso no encontrado", 404);
    }
} catch (Exception $e) {
    APIResponse::send(['error' => $e->getMessage()], $e->getCode() ?: 500);
}

// Función para manejar las solicitudes
function handleResource($resource, $method, $id)
{
    switch ($method) {
        case 'GET':
            if ($id) {
                $data = $resource->getById($id);
                if (!$data) {
                    throw new Exception("Recurso no encontrado", 404);
                }
                APIResponse::send($data);
            } else {
                APIResponse::send($resource->getAll());
            }
            break;

        case 'POST':
            $data = json_decode(file_get_contents("php://input"), true);
            if ($resource->create($data)) {
                APIResponse::send(['message' => 'Recurso creado exitosamente'], 201);
            }
            break;

        case 'PUT':
            if (!$id) {
                throw new Exception("ID no proporcionado", 400);
            }
            $data = json_decode(file_get_contents("php://input"), true);
            if ($resource->update($id, $data)) {
                APIResponse::send(['message' => 'Recurso actualizado exitosamente']);
            }
            break;

        case 'DELETE':
            if (!$id) {
                throw new Exception("ID no proporcionado", 400);
            }
            if ($resource->delete($id)) {
                APIResponse::send(['message' => 'Recurso eliminado exitosamente']);
            }
            break;

        default:
            throw new Exception("Método no permitido", 405);
    }
}