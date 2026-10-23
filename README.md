# Don Caffeto - Sistema de ventas (Avance 3)

Aplicación de escritorio en Java 17 (Swing) con MySQL para registrar los pedidos de la cafetería Don Caffeto.

## Alcance del avance (~30 %)

| Pantalla | Qué hace |
|---|---|
| Iniciar sesión | Usuario y contraseña, opción "Mostrar contraseña", bloqueo después de 3 intentos fallidos |
| Menú principal | Barra superior, menú lateral y tarjetas; solo muestra lo que permite el rol |
| Nuevo pedido | Carta con buscador y filtro por categoría, doble clic para agregar, botones + / − / Quitar, total en vivo; salón (mesa), para llevar o delivery |
| Pedidos del día | Búsqueda por fecha, estados con color, resumen del día, detalle, confirmar (enviar a cocina) y exportar a Excel |

### Interfaz y experiencia de usuario

- Paleta naranja y café de la marca, tarjetas blancas, tablas con filas alternas y montos en soles (`S/ 12.00`).
- Los campos que no aplican se desactivan (la mesa solo en salón, dirección y teléfono solo en delivery).
- Los botones solo se activan cuando tienen sentido (Guardar con productos, Confirmar solo pedidos REGISTRADOS).
- Confirmación antes de acciones importantes (enviar a cocina, borrar el pedido, cerrar sesión, salir).
- Mensajes claros, textos de ayuda dentro de los campos y atajos de teclado: **Enter** para ingresar,
  **Ctrl+N** nuevo pedido y **Ctrl+P** pedidos.
- Si una ventana ya está abierta, se trae al frente en lugar de abrir otra.
- El estilo se aplica por el nombre del componente (`btn`, `lbl`, `pnlCard`...), así que lo que agregues
  en la pestaña Design de NetBeans sale con el mismo diseño.

## Arquitectura

- **MVC**: `view` (JFrame Forms de NetBeans), `controller`, `model` + `service`.
- **DAO**: una interfaz por tabla (`dao`) y su implementación MySQL (`dao/impl`).
- **SOLID**: ver `docs/diagramas/solid.png`.
- **TDD**: pruebas con JUnit 4 en `src/test/java` (`PedidoServiceTest`, `LoginServiceTest`, `ValidadorTest`).
- **Seguridad**: contraseñas con BCrypt, `PreparedStatement` en todas las consultas, bloqueo tras 3 intentos,
  mismo mensaje si falla el usuario o la clave, menú según el rol y usuario de MySQL con permisos mínimos.

## Librerías

| Librería | Uso |
|---|---|
| Google Guava | `Preconditions` e `ImmutableList` en `PedidoService` |
| Apache POI | `ExportarExcel`: exporta los pedidos a .xlsx |
| Apache Commons Lang | `StringUtils` en `Validador` |
| Logback | Registro de eventos en consola y en `logs/doncaffeto.log` |

## Cómo ejecutar

1. En MySQL Workbench ejecutar `sql/don_caffeto.sql` y luego `sql/usuario_app.sql`.
2. Revisar la clave en `src/main/resources/config.properties`.
3. Abrir el proyecto en NetBeans → *Build with Dependencies* → *Run*.

Usuarios de prueba (clave `Caffeto2026!`): `propietario` (ADMIN), `mesero1` (MESERO), `cajero1` (CAJERO).

## Anexos

- Diagramas MVC, DAO y SOLID: `docs/diagramas/`
- Documentación Javadoc: `docs/javadoc/index.html`
- Capturas de pantalla: `docs/capturas/`

## Referencias

- Gamma, E., Helm, R., Johnson, R. y Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
- Martin, R. C. (2017). *Clean Architecture*. Prentice Hall.
- Beck, K. (2002). *Test-Driven Development: By Example*. Addison-Wesley.
- Oracle. (s. f.). *Data Access Object*. https://www.oracle.com/java/technologies/dataaccessobject.html
- OWASP Foundation. (2021). *OWASP Top Ten*. https://owasp.org/www-project-top-ten/
- Google. (s. f.). *Guava*. https://github.com/google/guava
- Apache Software Foundation. (s. f.). *Apache POI*. https://poi.apache.org/
- Apache Software Foundation. (s. f.). *Apache Commons Lang*. https://commons.apache.org/proper/commons-lang/
- QOS.ch. (s. f.). *Logback*. https://logback.qos.ch/
- JUnit. (s. f.). *JUnit 4*. https://junit.org/junit4/
