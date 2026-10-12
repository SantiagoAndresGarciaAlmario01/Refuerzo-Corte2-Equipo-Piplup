# Refuerzo Corte 2 - AquaPort: Piplup → Prinplup

**Curso:** Diseño y Arquitectura de Software (DOSW) · ECI · 2026-2 · Prof. Andrés Martín Cantór Urrego
**Línea evolutiva:** Piplup (v1.0.0) → **Prinplup (v2.0.0)** → Empoleon
**Proyecto:** **AquaPort**, sistema de drones acuáticos de la ECI para monitoreo y reparto de muestras de agua y sensores entre embalses, canales y zonas hídricas del campus.

## Enunciado Prinplup

La flota crece a **15 drones de 3 tipos** y el sistema **asigna automáticamente** el drone según zona, carga y condiciones del agua, con alertas cuando un drone falla.

| Tipo | Modelo | Carga máxima | Opera en |
|---|---|---|---|
| Superficial | Aqua-Surf 200 | 500 g | Superficie, agua tranquila |
| Semisumergido | Aqua-Semi 300 | 1500 g | Hasta 1 m, aguanta agua agitada |
| Buceador | Aqua-Diver 400 | 300 g | Hasta 30 m, no con agitación alta |

Estados del drone: `DISPONIBLE`, `EN_MISION`, `RECARGANDO`, `MANTENIMIENTO`, `SUMERGIDO`, `FALLO`. Prioridad de misión: `CRITICA`, `ALTA`, `NORMAL`, `BAJA`. Batería mínima para operar: **35 %**.

## Retos

| # | Tema | Entregable | Rama feature |
|---|---|---|---|
| 01 | Streams & Lambdas | 5 consultas (`groupingBy`, `partitioningBy`, `averagingInt`), comparador compuesto, `flatMap` · 12 pruebas | `feature/factory-drones` |
| 02 | GitHub y GitFlow | `feature/*` + PR, `release/v2.0`, tags `v1.0.0` y `v2.0.0`, procedimiento de hotfix | (release) |
| 03 | Patrones de Diseño | Factory Method, Strategy y Observer + integración con Mockito · 25 pruebas | `feature/asignacion-automatica` |
| 04 | Principios SOLID | Auditoría por clase, OCP con un 4.º tipo de drone, LSP, DIP · diagrama de clases | `feature/factory-drones` |
| 05 | Diagrama de Contexto C4 | v2 con 3 sistemas externos y técnico de mantenimiento, comparación con v1 | `feature/factory-drones` |
| 06 | RF vs RNF | AP-07 a AP-12, 5 RNF medibles, MoSCoW y tensión AP-07/AP-08 | `feature/factory-drones` |
| 07 | Plantilla DOSW | RF AP-07 con entradas compuestas y 5 flujos alternos | `feature/factory-drones` |
| 08 | Manual de Identidad y UX/UI | Sistema de diseño, componentes con estados, Fitts/Hick/Miller | `feature/alertas-centro-control` |
| 09 | Agilismo y Jira | Sprint con objetivo, 7 HU en Fibonacci, DoD, CSV para AP1 | `feature/alertas-centro-control` |
| 10 | Diagramas de Casos de Uso | Generalización, include encadenados, extend con condición | `feature/alertas-centro-control` |
| 11 | Mocks con IA | 4 prompts, flujo de 3 pantallas + alerta | `feature/alertas-centro-control` |
| 12 | TDD | `AsignadorAutomatico` Red → Green → Refactor con Mockito · 11 pruebas | `feature/asignacion-automatica` |
| 13 | JaCoCo | Regla por clase (líneas y ramas ≥ 80 %), ramas amarillas, 100 % · 75 pruebas | `feature/asignacion-automatica` |
| 14 | SonarQube | Quality Gate (deuda < 30 min, duplicación < 5 %), 5 issues resueltos · 87 pruebas | `feature/alertas-centro-control` |

Cada carpeta tiene su `README.md`. Las carpetas con código son proyectos Maven independientes (Java 17, JUnit 5.10, Mockito 5, JaCoCo 0.8.11):

```bash
cd "13 JaCoCo" && mvn verify
```

## Criterios del revisor DOSW (nivel Prinplup)

- Patrones bien aplicados (Factory sin `switch`, Strategy inyectada, Observer con publicador separado).
- SOLID verificable con pruebas (OCP, LSP, DIP).
- TDD demostrable en el historial de commits; pruebas AAA con Mockito.
- JaCoCo ≥ 80 % por clase; métodos ≤ 20 líneas y clases ≤ 200.
- Sin `null` como retorno, sin `for`/`while` en consultas, sin warnings de compilación.

## Versiones

| Tag | Rama de entrega | Contenido |
|---|---|---|
| `v1.0.0` | `Piplup` | MVP: 4 drones, asignación manual |
| `v2.0.0` | `Prinplup` | Flota de 15 drones, asignación automática y alertas |
