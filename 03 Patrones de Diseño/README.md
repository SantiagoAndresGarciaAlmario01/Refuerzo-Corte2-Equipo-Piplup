# 03 Patrones de Diseño: Factory Method, Strategy y Observer en la asignación automática

**Nivel:** Prinplup · **Patrones:** Factory Method (creacional) · Strategy (comportamiento) · Observer (comportamiento) · Builder heredado de Piplup · **Pruebas:** JUnit 5 + Mockito

En Piplup el operador elegía el drone a mano y solo hacía falta un Builder. En Prinplup el sistema **elige solo** el drone, hay **3 tipos** de drone y **varios interesados** en lo que pasa con cada misión. Cada uno de esos tres cambios pide un patrón distinto.

## Problema → patrón

| Problema real de AquaPort v2 | Patrón | Clases |
|---|---|---|
| Hay 3 tipos de drone y llegarán más. Si cada clase que crea drones hace `new DroneBuceador(...)` o un `switch (tipo)`, cada tipo nuevo obliga a tocar varias clases. | **Factory Method** | `CreadorDrone` (interfaz funcional), `FabricaDrones` (registro `Map<String, CreadorDrone>`) |
| La regla para elegir drone cambia según el caso: a veces importa la batería, a veces la cercanía y para las misiones CRÍTICAS importa el más apto. Meter todo en `if/else` dentro del asignador lo vuelve imposible de probar y de extender. | **Strategy** | `EstrategiaSeleccion`, `MayorBateriaStrategy`, `ZonaCercanaStrategy`, `PrioridadCriticaStrategy` |
| Cuando se asigna una misión, cuando un drone falla o cuando una misión queda sin drone, deben enterarse el Centro de Control y el técnico de mantenimiento. El asignador no debería conocerlos. | **Observer** | `ObservadorMision`, `PublicadorEventosMision`, `EventoMision`, `CentroControlObserver`, `TecnicoMantenimientoObserver` |
| La misión tiene muchos campos y reglas (peso 1–1500 g, partida distinta de destino). | **Builder** (de Piplup) | `Mision.Builder` con validación en `build()` |

## Factory Method sin `switch`

```java
public static FabricaDrones conTiposEstandar() {
    FabricaDrones fabrica = new FabricaDrones();
    fabrica.registrar(DroneSuperficial.TIPO, DroneSuperficial::new);
    fabrica.registrar(DroneSemisumergido.TIPO, DroneSemisumergido::new);
    fabrica.registrar(DroneBuceador.TIPO, DroneBuceador::new);
    return fabrica;
}
```

- Cada tipo se registra con una **referencia al constructor** (`DroneBuceador::new` cumple `CreadorDrone`).
- Agregar un tipo es **una línea de registro**, no modificar un `switch` (en el punto 04 se demuestra con `DroneSumergibleProfundo`).
- Un tipo desconocido lanza `IllegalArgumentException` con mensaje en español; nunca retorna `null`.

## Strategy y la tensión AP-07 / AP-08

`AsignadorAutomatico` recibe la estrategia por constructor y solo llama a `estrategia.seleccionar(candidatos, mision)`. No sabe cuál es.

| Estrategia | Regla | Cuándo conviene |
|---|---|---|
| `MayorBateriaStrategy` | `max(comparingInt(getBateria))` | Misiones normales: reparte el desgaste de la flota. |
| `ZonaCercanaStrategy` | Primero los drones que ya están en la zona de partida, luego mayor batería | Misiones urgentes: menor tiempo de respuesta. |
| `PrioridadCriticaStrategy` | Si la misión es CRÍTICA usa una estrategia, si no usa otra | Resuelve AP-07 vs AP-08 (punto 06): las CRÍTICAS van al más apto/cercano y las demás al de mayor batería. |

`PrioridadCriticaStrategy` es a la vez una estrategia y una composición de dos estrategias, así que la política completa se arma afuera:

```java
new PrioridadCriticaStrategy(new ZonaCercanaStrategy(), new MayorBateriaStrategy())
```

## Observer: publicador y evento

```
AsignadorAutomatico ──publica──▶ PublicadorEventosMision ──notificar(EventoMision)──▶ CentroControlObserver
                                                                                   └─▶ TecnicoMantenimientoObserver
```

| Evento (`TipoEvento`) | Centro de Control | Técnico de mantenimiento |
|---|---|---|
| `MISION_ASIGNADA` | Registra “Misión M-801 asignada a AR-11” | Lo ignora |
| `DRONE_EN_FALLO` | Registra la alerta | Abre una orden de mantenimiento |
| `MISION_SIN_DRONE` | “ALERTA CRÍTICA” si la misión es CRÍTICA, “Aviso” si no | Lo ignora |

Se separó el **publicador** del **asignador**: el asignador solo decide, el publicador mantiene la lista de observadores (SRP). Los observadores se pueden agregar o quitar en tiempo de ejecución (`registrarObservador` / `removerObservador`).

## Integración de los tres patrones

`AquaPortPatronesApp` crea la flota con la fábrica, asigna 3 misiones con `PrioridadCriticaStrategy` y registra los dos observadores:

```
Misión M-801 asignada a AR-11      (CRÍTICA, sale primero, ZonaCercana: AR-11 ya está en Laguna Sur)
Misión M-803 asignada a AR-06      (ALTA, MayorBateria entre los que quedan)
Aviso: misión M-802 sin drone disponible   (NORMAL, ya no quedan drones aptos)
```

### Prueba de integración con Mockito (pedida por el reto)

`AsignacionConPatronesIntegracionTest`: la estrategia (mock) devuelve primero un drone en **FALLO** y después uno sano. Se registran **dos observadores mock** y se verifica que **cada uno recibe exactamente una vez** el evento `DRONE_EN_FALLO`:

```java
verify(centroControl, times(1)).notificar(argThat(e -> e.getTipo() == TipoEvento.DRONE_EN_FALLO));
verify(tecnicoMantenimiento, times(1)).notificar(argThat(e -> e.getTipo() == TipoEvento.DRONE_EN_FALLO));
```

## Pruebas (25)

| Clase de prueba | Qué verifica |
|---|---|
| `FabricaDronesTest` | Crea los 3 tipos, la instancia es de la clase correcta, tipo desconocido/nulo/vacío lanza excepción, registrar un tipo nuevo sin modificar la fábrica |
| `EstrategiasSeleccionTest` | Cada estrategia elige el drone correcto, desempates, lista vacía retorna `Optional.empty()`, `PrioridadCritica` cambia de estrategia según la prioridad |
| `ObservadoresTest` | Mensajes del Centro de Control para los 3 eventos, orden de mantenimiento, observador removido, conteo de observadores |
| `AsignacionConPatronesIntegracionTest` | Los 3 patrones juntos con 2 observadores mock |

## Ejecutar

```bash
mvn test
mvn compile exec:java -Dexec.mainClass=edu.eci.dosw.aquaport.AquaPortPatronesApp
```
