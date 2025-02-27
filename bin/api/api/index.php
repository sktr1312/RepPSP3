<?php
// Cabeceras CORS
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With');

require_once 'Router.php';
require_once 'controllers/ClienteController.php';
require_once 'controllers/ProvinciaController.php';

$router = new Router();
//clientes
$router->get('/clientes', ['ClienteController', 'getAll']);
$router->post('/clientes', ['ClienteController', 'createCliente']);
$router->get('/clientes/{id}', ['ClienteController', 'getCliente']);
$router->put('/clientes/{id}', ['ClienteController', 'updateCliente']);
$router->delete('/clientes/{id}', ['ClienteController', 'deleteCliente']);

//provincias 
$router->get('/provincias', ['ProvinciaController', 'getAll']);
$router->post('/provincias', ['ProvinciaController', 'createProvincia']);
$router->get('/provincias/{id}', ['ProvinciaController', 'getProvincia']);
$router->put('/provincias/{id}', ['ProvinciaController', 'updateProvincia']);
$router->delete('/provincias/{id}', ['ProvinciaController', 'deleteProvincia']);




$router->run();

?>



