# Gemelo Tributario — Backend (MVC, MySQL local)

Spring Boot 4.1.1 · Java 21 · MySQL (XAMPP) · Swagger/OpenAPI 3

## Cómo levantarlo

1. Asegúrate de que **XAMPP tenga MySQL corriendo**.
2. La base `gemelotributario` se crea sola al arrancar (`createDatabaseIfNotExist=true`)
   — o ya existe si la creaste en phpMyAdmin, en cuyo caso Hibernate solo
   agrega/ajusta las tablas (`ddl-auto=update`).
3. `./mvnw spring-boot:run`
4. Abre `http://localhost:8080/swagger-ui.html` para ver y probar los 18 CRUD.
5. Verifica las tablas en phpMyAdmin:
   `http://localhost/phpmyadmin/index.php?route=/database/structure&db=gemelotributario`

## Estructura (MVC — por capa, no por feature)

```
model/        18 entidades JPA + ProgresoGuiaUsuarioId (clave compuesta)
repository/   18 interfaces JpaRepository
service/      18 clases @Service con la logica de negocio
controller/   18 @RestController — CRUD completo por recurso
config/       CORS, Swagger, manejo global de errores
```

## Las 18 entidades y sus rutas base

| Entidad | Ruta |
|---|---|
| Usuario | `/api/usuarios` |
| PerfilTributario | `/api/perfiles-tributarios` |
| ObligacionCatalogo | `/api/obligaciones-catalogo` |
| CalendarioNovenoDigito | `/api/calendario` |
| ObligacionUsuario | `/api/obligaciones-usuario` |
| PeriodoFiscalDatos | `/api/periodos-fiscales` |
| TablaTarifaRimpe | `/api/tarifas-rimpe` |
| TablaTramoRenta | `/api/tramos-renta` |
| TablaRetencion | `/api/retenciones` |
| CalculoHistorial | `/api/calculo-historial` |
| GuiaAprendizaje | `/api/guias-aprendizaje` |
| ProgresoGuiaUsuario | `/api/progreso-guias` |
| PreguntaFrecuente | `/api/preguntas-frecuentes` |
| FragmentoConocimientoChat | `/api/fragmentos-chat` |
| ConsultaChat | `/api/consultas-chat` |
| Notificacion | `/api/notificaciones` |
| PreferenciaNotificacion | `/api/preferencias-notificacion` |
| ComentarioOpinion | `/api/opiniones` |

## Notas importantes

- **Sin planes/suscripciones** — tal como se pidió, no hay campo `plan` ni tabla de suscripciones.
- **Sin Spring Security activado** — todos los endpoints estan abiertos por ahora,
  para no bloquear Swagger mientras se prueba. El login (verificar contrasena)
  todavia no tiene endpoint propio.
- **Sin integracion real con IA en el chat** — `ConsultaChat` es CRUD puro
  (guarda pregunta/respuesta). La logica de responder automaticamente con
  `FragmentoConocimientoChat` se conecta despues, cuando se decida el enfoque.
- **`ruc_cedula` acepta 10 o 13 digitos** (sin CHECK de longitud fija), igual
  que en el formulario de registro del frontend.
- El "Perfil tributario" se crea con `POST /api/perfiles-tributarios/usuario/{idUsuario}`
  (no lleva el id de usuario en el body, va en la URL).
