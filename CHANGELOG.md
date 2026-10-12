# Changelog

Generado con scripts/generar-changelog.sh a partir de los mensajes de commit (Conventional Commits).

## v3.0.0 (2026-10-12)

### Funcionalidades

- **empoleon-03**: agregar el modelo Enterprise con Drone como contrato común (`94b29b5`)
- **empoleon-03**: validar cada tramo con una cadena de responsabilidad (`8e897cc`)
- **empoleon-03**: agregar telemetría y sello de custodia con decoradores (`6df29f8`)
- **empoleon-03**: aislar la API hídrica detrás de un adaptador (`378fae6`)
- **empoleon-03**: integrar cadena, decoradores y adaptador en el flujo multi-etapa (`a896b0b`)
- **empoleon-05**: agregar diagrama de contenedores C4 nivel 2 (`4b9d6d2`)
- **empoleon-01**: agregar el modelo de rutas multi-etapa y la flota de 60 drones (`9c7c548`)
- **empoleon-01**: planificar la ruta con reduce y un drone distinto por tramo (`af9d234`)
- **empoleon-01**: agregar eficiencia por zona, mejor historial y Collector de batería (`8f5d353`)
- **empoleon-12**: agregar el dominio y los puertos sobre los que se construye el planificador (`3e91479`)
- **empoleon-12**: green de PlanificadorRuta con la lógica mínima (`7c5a885`)
- **empoleon-12**: implementar ejecución, reasignación e infraestructura para los flujos alternos (`efeaa94`)
- **empoleon-10**: agregar diagrama de casos de uso con los 5 extend de fallo (`2ed0863`)
- **empoleon-08**: definir tokens de diseño y verificar contraste WCAG AA (`2c081ca`)
- **empoleon-08**: agregar el flujo de 6 pantallas y el mapa de flujos (`d61e1ef`)
- **empoleon-09**: planificar el roadmap de 3 sprints para importar en AP1 (`700d8fd`)
- **empoleon-11**: generar las 5 pantallas del flujo Enterprise con un prompt por pantalla (`e24bfaf`)
- **empoleon-13**: integrar la v3 completa con el quality gate de JaCoCo 85/75 (`c098b02`)

### Correcciones

- **empoleon-13**: restaurar la interrupción del hilo en ClienteHttpAPIHidrica (`c08bed3`)

### Refactorizaciones

- **empoleon-14**: reorganizar el proyecto en dominio, aplicacion e infraestructura (`8d56716`)
- **empoleon-12**: planificar con reduce y elegir el drone con streams (`3ea7149`)
- **empoleon-13**: simplificar estaSellado sin literales booleanos (`0d7aced`)
- **empoleon-13**: dividir EjecutorRuta.ejecutar en recorrer y terminar (`32ab12d`)

### Pruebas

- **empoleon-03**: probar con Mockito la cadena, los decoradores y el adaptador (`a69c89c`)
- **empoleon-14**: agregar prueba automática de dependencias entre capas (`53205eb`)
- **empoleon-01**: probar consultas, planificación y Collector en secuencial y paralelo (`1889e62`)
- **empoleon-12**: red de PlanificadorRuta por tramo antes del código (`fd36b6b`)
- **empoleon-12**: red de los 5 flujos alternos y del flujo integrado (`9bf8f94`)
- **empoleon-12**: pruebas del dominio y la infraestructura para cubrir ramas (`ef7711f`)
- **empoleon-13**: cubrir el cliente HTTP, la persistencia y los valores límite del dominio (`fa6efae`)

### Documentación

- **empoleon-02**: documentar Conventional Commits, protección de ramas y flujo de la v3 (`d56c63c`)
- **empoleon-03**: justificar Chain, Decorator y Adapter frente al problema Enterprise (`b9cc309`)
- **empoleon-14**: documentar la auditoría final de la arquitectura por capas (`b30aaed`)
- **empoleon-04**: generar el mapa de dependencias entre capas desde los imports (`b723786`)
- **empoleon-04**: auditar los 5 principios SOLID en la arquitectura Enterprise (`5345af7`)
- **empoleon-01**: justificar teeing, reduce, Collector propio y uso de stream paralelo (`490b0bf`)
- **empoleon-05**: documentar contenedores, protocolos y datos de cada flecha (`c53ca45`)
- **empoleon-12**: documentar las 3 capas de pruebas y el ciclo Red-Green-Refactor (`1421096`)
- **empoleon-06**: definir RF AP-13 a AP-17, RNF de producción y sus tensiones (`1ddf645`)
- **empoleon-07**: completar la plantilla DOSW del RF AP-15 con waypoints anidados (`16f23da`)
- **empoleon-10**: justificar includes, extends y condiciones del diagrama (`c6dce87`)
- **empoleon-08**: documentar el design system y las leyes UX por pantalla (`8e857b1`)
- **empoleon-09**: documentar sprint goals, capacidad, DoD Enterprise y retrospectiva (`0b45257`)
- **empoleon-11**: documentar los 5 prompts y la verificación con Nielsen (`b09f61e`)
- **empoleon-13**: documentar el quality gate Enterprise y los issues resueltos (`7ffd06d`)

### Integración continua

- **empoleon-02**: agregar hook commit-msg que valida Conventional Commits (`31d3ed9`)
- **empoleon-02**: agregar ruleset que protege main y develop de push directo (`9ef63fc`)

### Construcción

- **empoleon-02**: agregar script que genera el CHANGELOG desde los commits (`571ac4a`)
- **empoleon-14**: agregar auditoría de capas con grep y su reporte (`dac479b`)
