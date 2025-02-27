<?php
class Router {
    private $routes = [];
    public function get($path, $handler) {
        $this->routes['GET'][$path] = $handler;
    }

    public function post($path, $handler) {
        $this->routes['POST'][$path] = $handler;
    }

    public function put($path, $handler) {
        $this->routes['PUT'][$path] = $handler;
    }

    public function delete($path, $handler) {
        $this->routes['DELETE'][$path] = $handler;
    }
    public function run() {
        $method = $_SERVER['REQUEST_METHOD'];
        $uri = $_SERVER['REQUEST_URI'];
        $uri = str_replace('/api/index.php', '', $uri);
        $uri = strtok($uri, '?'); // eliminar query strings
        $uri = rtrim($uri, '/');
        if (empty($uri)) {
            $uri = '/';
        }
        //depurando 
        error_log("Metodo: " . $method);
        error_log("URI procesada: " . $uri);
        error_log("Routas: " . print_r($this->routes, true));

        if (!isset($this->routes[$method])) {
            http_response_code(405);
            echo json_encode(["error" => "Método no permitido"]);
            return;
        }
        foreach ($this->routes[$method] as $route => $handler) {
            $pattern = preg_replace('/\{(\w+)\}/', '(?P<$1>[^/]+)', $route);
            if (preg_match("#^$pattern$#", $uri, $matches)) {
                array_shift($matches);
                call_user_func_array($handler, array_filter($matches, 'is_string', ARRAY_FILTER_USE_KEY));
                return;
            }
        }
        http_response_code(404);
        echo json_encode(["error" => "Ruta no encontrada"]);
    }}

?>
