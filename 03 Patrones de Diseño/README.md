# 03 Patrones de Diseño: Chain of Responsibility, Decorator y Adapter

**Nivel:** Empoleon · **Patrones nuevos:** Chain of Responsibility · Decorator · Adapter · **Se mantienen:** Factory Method, Builder, Observer · **Pruebas:** JUnit 5 + Mockito

## Problema → patrón

| Problema de la red Enterprise | Patrón | Clases |
|---|---|---|
| Antes de asignar un tramo hay que revisar 4 reglas distintas (batería, capacidad, zona destino activa y condiciones del canal). Si una falla no tiene sentido revisar las demás, y cada regla tiene su propia dependencia (estado de zonas, API hídrica). | **Chain of Responsibility** | `EslabonValidacion`, `ValidadorBateria`, `ValidadorCapacidadCarga`, `ValidadorZonaActiva`, `ValidadorCondicionesHidricas`, `CadenaValidacion` |
| A cada drone hay que agregarle telemetría y un sello para la cadena de custodia sin tocar `DroneAcuatico` ni sus 3 subclases, y poder combinar ambas cosas. | **Decorator** | `Drone` (interfaz), `DroneDecorador`, `DroneConMonitoreo`, `DroneConCifrado` |
| La API externa de condiciones responde JSON en inglés (`waterLevel`, `turbidity`, `waveHeight`) con sus propias unidades. El dominio no debe saber que existe esa API ni Jackson. | **Adapter** | `ProveedorCondicionesHidricas` (puerto del dominio), `AdaptadorAPIHidrica`, `ClienteAPIHidrica`, `RespuestaApiHidrica` |

## Chain of Responsibility

```
ValidadorBateria → ValidadorCapacidadCarga → ValidadorZonaActiva → ValidadorCondicionesHidricas → aprobado
```

```java
public ResultadoValidacion validar(SolicitudTramo solicitud) {
    Optional<String> motivoRechazo = revisar(solicitud);
    if (motivoRechazo.isPresent()) {
        return ResultadoValidacion.rechazado(getClass().getSimpleName(), motivoRechazo.get());
    }
    return siguiente == null ? ResultadoValidacion.exitoso() : siguiente.validar(solicitud);
}
```

- Cada eslabón solo implementa `revisar` (su regla). La decisión de **procesar o pasar al siguiente** está en la clase base, igual para todos.
- **Caso en que ningún eslabón rechaza**: el último no tiene siguiente y retorna `exitoso()`.
- El orden es intencional: primero las reglas baratas (batería, peso), después las que consultan algo (estado de zonas) y al final la que llama a la API externa. Una solicitud rechazada por batería nunca genera tráfico a la API.
- `CadenaValidacion.enterprise(...)` arma la cadena en un solo lugar; `ConfiguracionAquaPort` la convierte en el `CriterioAptitud` que usa el planificador.

## Decorator

```java
List<Drone> flota = configuracion.decorar(drones);   // DroneConCifrado(DroneConMonitoreo(drone))
```

| Decorador | Qué agrega a `recorrer(tramo)` | Qué no cambia |
|---|---|---|
| `DroneConMonitoreo` | Registra una `LecturaTelemetria` (drone, zona, batería, tramo) en el `RegistroTelemetria` | El resultado del tramo: es el mismo objeto que retorna el drone envuelto |
| `DroneConCifrado` | Sella el resultado con SHA-256 para la cadena de custodia | Batería, zona y estado del drone |

Se apilan en cualquier orden. `DroneDecorador` delega todos los métodos del contrato `Drone`, así que un drone decorado entra a la cadena de validación, al planificador y al ejecutor sin que ninguno sepa que está decorado (LSP). Un drone **sin** el decorador de cifrado no produce sello y la `CadenaCustodia` lo rechaza: es el flujo alterno “cadena de custodia interrumpida”.

## Adapter

| JSON de la API (inglés) | Dominio | Regla de traducción |
|---|---|---|
| `waterLevel` (m) | `NivelAgua(metros)` | negativo → `IllegalArgumentException`; navegable desde 0,5 m |
| `turbidity` (NTU) | `Turbidez` | < 5 CLARA · 5 a < 50 MODERADA · ≥ 50 ALTA |
| `waveHeight` (m) | `NivelAgitacion` | < 0,3 BAJO · 0,3 a < 1,0 MEDIO · ≥ 1,0 ALTO |
| `requiredDepth` (m) | `profundidadRequeridaMetros` | igual |
| campos desconocidos | se ignoran | `@JsonIgnoreProperties(ignoreUnknown = true)` |

`AdaptadorAPIHidrica` vive en `infraestructura.api` y es la única clase que lee JSON con Jackson (la configuración solo le entrega el `ObjectMapper`). El dominio solo ve el puerto `ProveedorCondicionesHidricas.consultar(CanalHidrico)`. Cambiar de proveedor es escribir otro adaptador.

## Los 3 patrones integrados en el flujo

1. **Adapter**: el `DesviadorRuta` consulta cada canal; si el canal es adverso, la ruta se desvía.
2. **Chain**: el `PlanificadorRuta` solo considera los drones que la cadena aprueba para cada tramo.
3. **Decorator**: al recorrer cada tramo el drone deja su lectura de telemetría y su sello; el `EjecutorRuta` los pasa a la `CadenaCustodia`.

`FlujoMultiEtapaIntegracionTest` ejecuta este flujo con las clases reales (sin mocks): solicitud, planificación, asignación por tramos y notificación de waypoints.

## Pruebas pedidas por el reto (Mockito)

| Lo que pide el reto | Prueba |
|---|---|
| La cadena se detiene en el primer validador que falla | `CadenaValidacionTest.validar_bateriaBaja_seDetieneEnElPrimerEslabon`: con 34 % de batería, `verify(zonas, never())` y `verify(condiciones, never())`. Además, `validar_eslabonFalla_siguienteNoSeLlama` enlaza un **eslabón mock** detrás de `ValidadorCapacidadCarga` y verifica que nunca se invoca. |
| El decorator registra sin alterar el comportamiento | `DecoradoresTest.monitoreo_recorrer_registraUnaLectura` (`verify(telemetria, times(1))`) y `monitoreo_recorrer_mismoResultadoQueSinDecorar` (resultado igual al del drone sin decorar). |
| El adapter convierte campos con valores límite | `AdaptadorAPIHidricaTest`: parametrizadas con 4,99 / 5,0 / 49,99 / 50,0 NTU y 0,29 / 0,3 / 0,99 / 1,0 m; nivel negativo; JSON malformado; campos extra. El cliente HTTP es un mock. |

Total: 33 pruebas (`CadenaValidacionTest` 5, `DecoradoresTest` 5, `AdaptadorAPIHidricaTest` 6 métodos con 14 ejecuciones, `FabricaDronesTest` 2, `FlujoMultiEtapaIntegracionTest` 7).

```bash
mvn test
mvn compile exec:java -Dexec.mainClass=edu.eci.dosw.aquaport.infraestructura.configuracion.AquaPortEnterpriseApp
```
