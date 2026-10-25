# Don Caffeto - Sistema de ventas

Aplicación de escritorio en **Java 17 (Swing)** con **MySQL 8**, hecha para abrirse en **Apache NetBeans**.
Todos los equipos (mesero, cocina, caja y administrador) trabajan con la misma base de datos MySQL.

| Rol | Qué puede hacer |
|---|---|
| **Mesero** | Trabaja desde el **mapa de 7 mesas**: toma el pedido de una mesa libre, ve el pedido de cada mesa solo entrando a ella, pide la cuenta y anula pedidos que la cocina aún no empezó. También registra pedidos para llevar y delivery |
| **Cocina** | Ver los pedidos pendientes y en preparación (se actualiza solo cada 15 s y suena al llegar uno nuevo) y cambiar su estado |
| **Cajero** | Cobrar los pedidos listos (efectivo con vuelto, Yape, Plin o tarjeta) |
| **Administrador** | Todo lo anterior, más la lista de todos los pedidos del día, gestionar productos y usuarios y ver reportes de ventas |

Flujo de un pedido: **Pendiente → En preparación → Listo → Pagado** (o **Cancelado** si se anula antes de cocinarse).

### Mapa de mesas (mesero)

| Color | Estado de la mesa | Qué pasó |
|---|---|---|
| Gris | Libre | No tiene pedido. Al tocarla se toma el pedido de esa mesa |
| Naranja | En cocina | El pedido se tomó y se envió a cocina. Empieza el **cronómetro** de la mesa |
| Verde | Cuenta pedida | La cocina terminó y el mesero pidió la cuenta. Recién ahí aparece en caja |
| Gris otra vez | Libre | El cajero registró el pago; la mesa se libera y el cronómetro se detiene |

- El cronómetro lo calcula MySQL (`ocupada_desde`), así todos los equipos ven el mismo tiempo.
- El mapa se actualiza solo cada 10 segundos: cuando la caja cobra, la mesa vuelve a gris sin hacer nada.
- Los pedidos de salón solo se ven **entrando a la mesa**. La pantalla de la mesa es como la de Restometrics:
  - **Izquierda:** el pedido (Cnt · Descripción · Importe; ✓ = ya está en cocina, *Nuevo* = falta confirmar),
    el total y un **teclado numérico** (escribe la cantidad y toca el producto; con una línea nueva elegida,
    el teclado y + / − cambian su cantidad; *Eliminar* la quita y *Vaciar* borra todo lo nuevo).
  - **En medio:** **categorías** y **productos en botones grandes**, por páginas, con buscador.
  - **Derecha:** *Confirmar* (envía a cocina; si la mesa estaba libre crea el pedido y la pone en naranja),
    *Nota* (para el producto elegido), *Observación* (para todo el pedido), *Pedir cuenta*, *Anular* y *Mesas*.
  - Lo agregado después aparece en cocina con la nota «Agregado»; si el pedido ya estaba listo, vuelve a Pendiente.
    Con la cuenta pedida la carta se bloquea.

Capturas de todas las pantallas: `docs/capturas/`.

---

## 1. Requisitos

| Programa | Versión |
|---|---|
| JDK | 17 o superior (funciona con JDK 26) |
| Apache NetBeans | 17 o superior (funciona con NetBeans 30; ya trae Maven) |
| MySQL Server + MySQL Workbench | 8.4 o superior (incluida la serie 9.x) |

El proyecto compila con `maven.compiler.release` 17: aunque uses un JDK más nuevo, el código queda
compatible con Java 17, que es la versión que pide el curso.

Avisos que pueden salir en la consola con JDK 24 o superior (no son errores):
- `WARNING: A restricted method in java.lang.System has been called`: lo produce FlatLaf al cargar su
  librería nativa. Para ocultarlo: Properties → Run → VM Options → `--enable-native-access=ALL-UNNAMED`.
- Si alguna librería mostrara un error con `sun.misc.Unsafe`, agrega en VM Options
  `--sun-misc-unsafe-memory-access=warn`.

## 2. Crear la base de datos (Windows)

**Opción rápida:** doble clic en **`instalar_bd.bat`** (carpeta principal del proyecto) y escribe la
contraseña de `root` de MySQL. Crea la base `don_caffeto_apf3`, sus 11 tablas, los datos de demostración,
las funciones, las vistas y el usuario `app_apf3` (clave `1234`) que usa la aplicación.

**Desde la consola** (lo mismo que hace el .bat):

```
cd "C:\Program Files\MySQL\MySQL Server 8.4\bin"
mysql -u root -p --default-character-set=utf8mb4 < "RUTA_DEL_PROYECTO\sql\don_caffeto.sql"
```

El usuario `app_apf3` solo puede consultar, insertar y actualizar (no puede borrar tablas ni cambiar la
estructura). Para ver la base en MySQL Workbench, entra con `root` y actualiza el panel **Schemas**.

Tablas: `roles`, `usuarios`, `mesas`, `categorias`, `productos`, `pedidos`, `detalle_pedido`,
`observaciones_pedido`, `observaciones_detalle`, `pagos` y `ventas`.

Funciones de MySQL incluidas en el mismo script (se prueban con `SELECT` en Workbench):

| Función | Qué devuelve | Ejemplo |
|---|---|---|
| `fn_igv(total)` | IGV 18 % incluido en un total | `SELECT fn_igv(22.00);` → 3.36 |
| `fn_valor_venta(total)` | Total sin IGV | `SELECT fn_valor_venta(22.00);` → 18.64 |
| `fn_total_pedido(id)` | Suma del detalle de un pedido | `SELECT fn_total_pedido(3);` → 21.00 |
| `fn_minutos_ocupada(id_mesa)` | Minutos que lleva ocupada una mesa | `SELECT numero, fn_minutos_ocupada(id_mesa) FROM mesas;` |
| `fn_ventas_del_dia(fecha)` | Total cobrado en un día | `SELECT fn_ventas_del_dia(CURDATE());` |
| `fn_unidades_vendidas(id, desde, hasta)` | Unidades vendidas de un producto | `SELECT fn_unidades_vendidas(2, CURDATE(), CURDATE());` |
| `fn_estado_texto(estado)` | Estado en texto amigable | `SELECT fn_estado_texto('EN_PREPARACION');` |

También hay dos vistas que usan esas funciones: `vw_mesas` (mesas con su cronómetro) y `vw_pedidos`
(pedidos con valor de venta e IGV).

## 3. Configurar la conexión

El archivo **`config.properties`** (carpeta principal, junto al `pom.xml`) le dice a la app a qué base
conectarse:

```properties
db.url=jdbc:mysql://localhost:3306/don_caffeto_apf3?serverTimezone=America/Lima&useSSL=false&allowPublicKeyRetrieval=true
db.user=app_apf3
db.password=1234
```

Si no existe, copia `config.example.properties`, renómbrala a `config.properties` y escribe la clave.
`config.properties` está en `.gitignore`, así que la clave **no se sube a GitHub**.

## 4. Ejecutar desde Apache NetBeans

1. **File → Open Project…** y elige la carpeta `DonCaffeto-APF3` (NetBeans la reconoce por el `pom.xml`).
2. Clic derecho en el proyecto → **Clean and Build** (la primera vez descarga las librerías de internet).
3. Botón **Run** (F6). La clase principal es `com.doncaffeto.App`.
4. Para editar una pantalla, abre cualquier `Frm…` de `com.doncaffeto.view` y usa la pestaña **Design**.

### Usuarios de demostración

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `1234` | Administrador |
| `mesero1` | `1234` | Mesero |
| `cocina1` | `1234` | Cocina |
| `cajero1` | `1234` | Cajero |

Para ver que todos comparten los mismos datos:
1. Con `mesero1` abre **Mesas** y toca una mesa gris → agrega productos → **Enviar a cocina** (la mesa queda naranja).
2. Con `cocina1` marca el pedido como **listo**.
3. Con `mesero1` entra a la mesa → **Pedir la cuenta** (la mesa queda verde).
4. Con `cajero1` cóbralo en **Caja**: la mesa vuelve a gris en el mapa.

> Para volver a los datos de demostración, ejecuta otra vez `instalar_bd.bat`.

## 5. Pruebas

En NetBeans: clic derecho en el proyecto → **Test** (o `mvn test`). Son pruebas **JUnit 4** que usan
DAOs falsos en memoria, así que no necesitan MySQL:

| Clase | Qué prueba |
|---|---|
| `PedidoServiceTest` | Total del pedido, notas, cantidades, mesa y delivery obligatorios, anulación, pedir la cuenta solo cuando la cocina terminó, agregar productos a una mesa, el cajero no puede crear pedidos |
| `CocinaServiceTest` | Pendiente → En preparación → Listo; el mesero no cambia estados de cocina |
| `PagoServiceTest` | IGV incluido, vuelto, efectivo insuficiente, una mesa solo se cobra con la cuenta pedida, el mesero no puede cobrar |
| `LoginServiceTest` | Contraseña BCrypt, mismo mensaje si el usuario no existe, bloqueo tras 3 intentos, permisos por rol |
| `UsuarioServiceTest` | La clave se guarda cifrada, mínimo 8 caracteres, solo el administrador gestiona usuarios |
| `ValidadorTest` | Textos vacíos, teléfono, montos y fechas |

## 6. Arquitectura y seguridad

- **MVC**: `view` (JFrame Forms), `controller`, `service` + `model`.
- **DAO**: 7 interfaces en `dao` y sus implementaciones MySQL en `dao/impl` (JDBC + MySQL Connector/J).
- **SOLID**: los servicios reciben las interfaces DAO por el constructor; solo la clase `Servicios` crea
  las clases MySQL (ver `docs/diagramas/solid.png`).
- **Seguridad**:
  - Contraseñas guardadas con hash **BCrypt**.
  - Todas las consultas con **PreparedStatement**.
  - **Transacciones** al guardar un pedido y al cobrar (pago + venta + estado + mesa, todo o nada).
  - **Autorización por rol en los servicios**, no solo en los botones (`Autorizacion.exigir`).
  - Bloqueo tras 3 intentos y el mismo mensaje si falla el usuario o la clave.
  - Usuario de MySQL con permisos mínimos y clave fuera del repositorio.

### Librerías

| Librería | Uso |
|---|---|
| Google Guava | `Preconditions`, `ImmutableList` (PedidoService) e `ImmutableMap` (estados de cocina) |
| Apache Commons Lang | `StringUtils` en validaciones, servicios y tema |
| Apache POI | Exportar pedidos y reportes a Excel |
| Logback | Registro de eventos en consola y en `logs/doncaffeto.log` |
| jBCrypt · FlatLaf · MySQL Connector/J | Hash de contraseñas · tema visual · conexión JDBC |

### Interfaz y experiencia de usuario

- Paleta naranja y crema; tarjetas blancas; montos como `S/ 12.00`; estados con color.
- El menú y las tarjetas muestran solo lo que el rol puede usar.
- Buscador y filtro por categoría en la carta; doble clic para agregar; botones + / − / Quitar.
- Los campos que no aplican se desactivan (mesa solo en salón; dirección y teléfono solo en delivery).
- Los botones solo se activan cuando tienen sentido (Cobrar solo cuando el pago alcanza, etc.).
- El vuelto se calcula mientras se escribe; IGV desglosado en la caja.
- Confirmación antes de enviar, anular, cobrar, borrar el pedido, cerrar sesión o salir.
- Si una ventana ya está abierta, se trae al frente en lugar de abrir otra.
- El estilo se aplica por el nombre del componente (`btn`, `lbl`, `pnlCard`…): lo que agregues en
  la pestaña Design sale con el mismo diseño.

## 7. Anexos

- Diagramas MVC, DAO y SOLID: `docs/diagramas/`
- Documentación Javadoc: `docs/javadoc/index.html`
- Capturas: `docs/capturas/`

## 8. Referencias

- Gamma, E., Helm, R., Johnson, R. y Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
- Martin, R. C. (2017). *Clean Architecture*. Prentice Hall.
- Beck, K. (2002). *Test-Driven Development: By Example*. Addison-Wesley.
- Oracle. (s. f.). *Data Access Object*. https://www.oracle.com/java/technologies/dataaccessobject.html
- Oracle. (s. f.). *JDBC Basics: Using Transactions*. https://docs.oracle.com/javase/tutorial/jdbc/basics/transactions.html
- OWASP Foundation. (2021). *OWASP Top Ten*. https://owasp.org/www-project-top-ten/
- Google. (s. f.). *Guava*. https://github.com/google/guava
- Apache Software Foundation. (s. f.). *Apache POI*. https://poi.apache.org/
- Apache Software Foundation. (s. f.). *Apache Commons Lang*. https://commons.apache.org/proper/commons-lang/
- QOS.ch. (s. f.). *Logback*. https://logback.qos.ch/
- JUnit. (s. f.). *JUnit 4*. https://junit.org/junit4/
