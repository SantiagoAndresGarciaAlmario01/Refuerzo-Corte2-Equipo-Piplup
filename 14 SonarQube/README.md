# 14 SonarQube: Análisis estático del MVP

**Nivel:** Piplup · **Meta:** 0 bugs · 0 vulnerabilidades · code smells resueltos · deuda técnica 0

## Qué se analiza

Este punto integra en **un solo proyecto Maven** todo el MVP construido en los puntos anteriores, que es lo que se le pasa a SonarQube:

```
src/main/java/edu/eci/dosw/aquaport
├── AquaPortApp.java   # raíz de composición (main)
├── consulta/ConsultorFlota.java   # punto 01 (Streams)
├── modelo/  DroneAcuatico · Mision (+Builder) · TipoCarga · EstadoMision   # punto 03
├── repositorio/ RepositorioMisiones · RepositorioMisionesEnMemoria   # punto 04
├── servicio/ RegistradorMisiones · ValidadorMision · GestorSolicitudesMision   # puntos 04 y 12
└── notificacion/ NotificadorOperador · NotificadorOperadorLog
src/test/java/...   # 58 pruebas
```

## Cómo correr el análisis

```bash
# 1. Levantar SonarQube Community (requiere Docker)
docker compose up -d                       # http://localhost:9000  → admin/admin, cambiar contraseña
# 2. En SonarQube: My Account → Security → Generate Token (tipo "Global Analysis Token")
# 3. Analizar (incluye pruebas + cobertura JaCoCo)
mvn clean verify sonar:sonar -Dsonar.token=TU_TOKEN
```

La configuración ya está en el `pom.xml`: `sonar.projectKey=aquaport-piplup`, `sonar.host.url`, ruta del XML de JaCoCo y exclusión de cobertura para `AquaPortApp` (el `main` solo crea objetos e inyecta dependencias; no tiene lógica que probar).

## Prioridad de issues

| Tipo | Descripción | Prioridad |
|---|---|---|
| Bug | Comportamiento claramente incorrecto | Resolver siempre, primero |
| Vulnerability | Explotable por un atacante | Resolver siempre |
| Code Smell | Difícil de mantener o entender | Resolver, pero al final |

**Nunca se usó `@SuppressWarnings`**: suprimir un issue lo esconde, no lo resuelve.

## Code smells resueltos (documentados uno por commit)

El proyecto se integró primero tal cual venía de los puntos 01–04 (commit `feat:Piplup-Punto 14 - Integrar MVP para analisis`) y luego se corrigió cada smell en su propio commit:

| # | Regla Sonar | Qué era el smell | Por qué era un problema | Cómo se corrigió | Commit |
|---|---|---|---|---|---|
| 1 | **java:S106**: *Standard outputs should not be used directly to log anything* | `System.out.println` en `AquaPortApp` y `NotificadorOperadorConsola(System.out)` | La salida estándar no tiene niveles (info/advertencia), no se puede redirigir ni apagar y mezcla mensajes del sistema con cualquier otra cosa que imprima la JVM. | Nuevo `NotificadorOperadorLog` con `java.util.logging.Logger` (INFO para registro, WARNING para rechazos) y `LOGGER` en `AquaPortApp`. Gracias a DIP bastó con cambiar la clase inyectada: el `GestorSolicitudesMision` no se tocó. Se agregó `NotificadorOperadorLogTest`. | `refactor:Piplup-Punto 14 - Sonar java:S106 ...` |
| 2 | **java:S2629**: *Logging arguments should not require evaluation* | Concatenar el mensaje antes de llamar al logger | Se arma el `String` aunque el nivel de log esté deshabilitado (trabajo inútil). | Se usa la sobrecarga con `Supplier`: `LOGGER.info(() -> "..." + dato)`. | mismo commit del #1 |
| 3 | **java:S1192**: *String literals should not be duplicated* | `"Aqua-Ranger 100"` repetido 4 veces en `AquaPortApp` | Si cambia el modelo hay que acordarse de editar 4 lugares; basta olvidar uno para tener datos inconsistentes. | Constantes `MODELO_FLOTA` y `DESTINO_LABORATORIO`. | `refactor:Piplup-Punto 14 - Sonar java:S1192 ...` |
| 4 | Regla del revisor DOSW: *lambda de más de 2 líneas* | Lambda de 3 líneas construyendo la `Mision` dentro de `map(...)` | Difícil de leer y de probar; esconde una responsabilidad sin nombre. | Extraída al método privado `crearMisionHaciaLaboratorio(DroneAcuatico)`; además `AquaPortApp` pasa a ser `final` con constructor privado (no se instancia). | mismo commit del #3 |

Además, al integrar se eliminó una duplicación: la regla “batería ≥ 35 %” existía en `ConsultorFlota` y en `ValidadorMision`. Ahora vive una sola vez en `DroneAcuatico.tieneBateriaParaOperar()` y ambos la reutilizan.

## Estado esperado del Quality Gate

| Métrica | Valor esperado |
|---|---|
| Bugs | 0 |
| Vulnerabilities | 0 |
| Code Smells | 0 |
| Deuda técnica | 0 min |
| Cobertura (JaCoCo) | 100 % líneas y ramas en las clases con lógica |
| Duplicaciones | 0 % |

<img width="1669" height="771" alt="image" src="https://github.com/user-attachments/assets/6870c608-05c4-4524-b6a4-03a434e831f5" />

