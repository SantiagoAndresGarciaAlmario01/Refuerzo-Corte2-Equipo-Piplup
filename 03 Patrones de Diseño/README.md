# 03 Patrones de Diseño: Builder para construir misiones válidas

**Nivel:** Piplup · **Patrón:** Builder · validación en `build()` · encadenamiento fluido

## El problema

Un constructor con 6 parámetros (`new Mision("M-001", drone, "Embalse Norte", "Laboratorio Hídrico", TipoCarga.MUESTRA_AGUA, EstadoMision.PENDIENTE)`) es frágil: `puntoPartida` y `puntoLlegada` son ambos `String`, así que invertirlos compila sin error, y nada impide crear una misión con campos nulos o con un drone ocupado.

## La solución

```java
Mision mision = new Mision.Builder()
        .id("M-001")
        .drone(droneAsignado)
        .puntoPartida("Embalse Norte")
        .puntoLlegada("Laboratorio Hídrico")
        .tipoCarga(TipoCarga.MUESTRA_AGUA)
        .build();   // IllegalStateException si algo no es válido
```

## Estructura

```
src/main/java/edu/eci/dosw/aquaport
├── AquaPortBuilderApp.java   # demo: una misión válida y una rechazada
└── modelo/
    ├── Mision.java   # clase inmutable + Builder estático anidado
    ├── DroneAcuatico.java
    ├── TipoCarga.java               (MUESTRA_AGUA, SENSOR, PAQUETE_LIGERO)
    └── EstadoMision.java            (PENDIENTE, EN_TRANSITO, ENTREGADA, FALLIDA)
src/test/java/.../modelo/MisionBuilderTest.java   # 9 pruebas
```

## Reglas que valida `build()`

| Regla | Excepción |
|---|---|
| `id`, `puntoPartida`, `puntoLlegada` no nulos ni vacíos/en blanco | `IllegalStateException` “el campo 'X' es obligatorio y no puede estar vacío” |
| `drone`, `tipoCarga` y `estado` no nulos | `IllegalStateException` “el campo 'X' es obligatorio” |
| El drone debe estar **disponible** al momento de construir | `IllegalStateException` “el drone AR-03 no está disponible” |

`estado` es opcional al construir: si no se indica, la misión nace en `PENDIENTE`, que es el estado inicial del flujo del MVP.

## Justificación de la implementación

- **Constructor privado de `Mision`** que solo recibe el `Builder`: la única forma de obtener una `Mision` es pasando por `build()`, así que nunca existe un objeto en estado inválido.
- **`Mision` es inmutable** (`final`, campos `private final`, solo getters). Una vez validada, nadie puede dejarla inválida después.
- **Cada setter del Builder retorna `this`**, lo que permite el encadenamiento fluido y deja claro qué valor va en qué campo.
- **Validación centralizada en `build()`** con métodos privados de nombre descriptivo (`validarTextoObligatorio`, `validarObligatorio`, `validarDroneDisponible`). Cada método tiene menos de 25 líneas.
- **Mensajes en español que nombran el campo que falla**, para que el operador o el desarrollador sepan exactamente qué corregir (Nielsen #9).
- **Builder estático anidado** (`Mision.Builder`): queda junto a la clase que construye y tiene acceso a su constructor privado.

## Pruebas (9)

Misión completa válida · estado por defecto `PENDIENTE` · sin id · punto de partida en blanco · sin punto de llegada (y el mensaje nombra el campo) · sin drone · sin tipo de carga · estado nulo explícito · drone no disponible.
