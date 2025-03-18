# Documentación de la Aplicación Cliente REST

## Descripción General

Esta aplicación es un cliente REST que permite gestionar un sistema de telefonía. Proporciona una interfaz de línea de comandos con un sistema de menús para facilitar la interacción con varios servicios REST relacionados con operadores telefónicos, teléfonos, titulares e historial de cambios.

## Estructura del Código

La aplicación se organiza en los siguientes componentes principales:

### 1. Menú y navegación

La aplicación utiliza un sistema de menús jerárquico para facilitar la navegación:

- **Menú principal**: Punto de entrada del programa con submenús
- **Submenús**: Categorizan las operaciones disponibles (visualizar, actualizar, eliminar, insertar)
- **Elementos de menú**: Ejecutan acciones específicas cuando son seleccionados

### 2. Funcionalidades principales

- **Visualización de datos**: Ver operadores, titulares, teléfonos e historial
- **Actualización de datos**: Modificar el operador asociado a un teléfono
- **Inserción de datos**: Añadir nuevos teléfonos al sistema

### 3. Utilidades

- **Manejo de datos**: Métodos para obtener, procesar y visualizar datos
- **Interacción con usuario**: Funciones para solicitar y validar entradas del usuario
- **Comunicación REST**: Uso de un cliente REST para comunicarse con los servicios

## Modelos de Datos

La aplicación trabaja con los siguientes modelos:

- **Operador**: Compañía telefónica (código y nombre)
- **Teléfono**: Número de teléfono asociado a un operador y un titular
- **Historial**: Registro de cambios de operador para un teléfono

## Flujos de trabajo principales

### 1. Visualización de teléfonos

1. El usuario selecciona "Ver todos los teléfonos" en el submenú "Visualizar Registros"
2. La aplicación realiza una petición REST para obtener todos los teléfonos
3. Se muestran los datos en una tabla formateada

### 2. Cambio de operador para un teléfono

1. El usuario selecciona "Actualizar operador" en el submenú "Actualizar Registros"
2. Se muestra una lista de teléfonos y el usuario selecciona uno
3. Se muestra una lista de operadores y el usuario selecciona el nuevo operador
4. La aplicación actualiza el teléfono mediante una petición REST
5. El usuario introduce el motivo del cambio
6. La aplicación registra el cambio en el historial mediante otra petición REST

### 3. Añadir un nuevo teléfono

1. El usuario selecciona "Añadir teléfono" en el submenú "Insertar Registros"
2. Se muestra una lista de operadores y el usuario selecciona uno
3. El usuario introduce el número de teléfono (debe ser único y tener 9 dígitos)
4. Se muestra una lista de titulares y el usuario selecciona uno
5. La aplicación añade el teléfono mediante una petición REST

## Características técnicas

### Patrones de diseño

- **Facade**: `RestClientFacade` proporciona una interfaz simplificada para los servicios REST
- **Command**: Los elementos del menú implementan el patrón Command para ejecutar acciones
- **Composite**: El sistema de menús utiliza el patrón Composite para la jerarquía de menús

### Programación funcional

El código utiliza características de programación funcional de Java:

- **Supplier**: Para proveer datos de forma asíncrona
- **Consumer**: Para consumir y mostrar datos
- **Predicate**: Para validar entradas del usuario
- **CompletableFuture**: Para gestionar operaciones asíncronas

### Tratamiento de errores

- Validación de entradas del usuario
- Manejo de respuestas de las peticiones REST
- Mensajes de error descriptivos

## Diagrama de Clases Conceptual

```
+------------------+       +------------------+       +------------------+
|      App2        |------>|  RestClientFacade|------>|      REST API    |
+------------------+       +------------------+       +------------------+
        |
        |                  +------------------+
        +----------------->|      Menu        |
                           +------------------+
                                   |
                  +----------------+----------------+
                  |                                 |
        +------------------+               +------------------+
        |     SubMenu      |               |     MenuItem     |
        +------------------+               +------------------+
```

## Guía de ampliación

Para ampliar la aplicación con nuevas funcionalidades:

1. **Añadir nuevos modelos**: Crear clases en el paquete `examenrest.models`
2. **Implementar servicios REST**: Añadir métodos en `RestClientFacade`
3. **Crear funcionalidades**: Implementar métodos en `App2`
4. **Actualizar menús**: Añadir nuevos elementos de menú y submenús según sea necesario

## Consideraciones y mejoras posibles

1. **Persistencia de datos**: Actualmente la aplicación no guarda estado entre ejecuciones
2. **Manejo de excepciones**: Se podría mejorar para casos específicos
3. **Interfaz gráfica**: Migrar a una interfaz gráfica más amigable
4. **Pruebas unitarias**: Implementar pruebas para verificar el funcionamiento correcto
5. **Configuración externa**: Parametrizar URLs y otros valores en un archivo de configuración
