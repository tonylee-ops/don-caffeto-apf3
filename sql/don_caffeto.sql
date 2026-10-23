CREATE DATABASE IF NOT EXISTS don_caffeto CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE don_caffeto;
SET NAMES utf8mb4;

CREATE TABLE rol (
  id_rol      INT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(20) NOT NULL,
  descripcion VARCHAR(120) NULL,
  CONSTRAINT uq_rol_nombre UNIQUE (nombre),
  CONSTRAINT ck_rol_nombre CHECK (nombre IN ('ADMIN','MESERO','COCINA','CAJERO'))
) ENGINE=InnoDB;

CREATE TABLE usuario (
  id_usuario    INT AUTO_INCREMENT PRIMARY KEY,
  id_rol        INT NOT NULL,
  username      VARCHAR(40) NOT NULL,
  password_hash CHAR(60) NOT NULL,                       -- BCrypt
  nombres       VARCHAR(80) NOT NULL,
  apellidos     VARCHAR(80) NOT NULL,
  activo        TINYINT(1) NOT NULL DEFAULT 1,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_usuario_username UNIQUE (username),
  CONSTRAINT ck_usuario_bcrypt CHECK (password_hash LIKE '$2%'),
  CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX ix_usuario_rol (id_rol)
) ENGINE=InnoDB;

CREATE TABLE cliente (
  id_cliente    INT AUTO_INCREMENT PRIMARY KEY,
  nombres       VARCHAR(120) NOT NULL,
  dni           CHAR(8) NULL,
  ruc           CHAR(11) NULL,
  razon_social  VARCHAR(150) NULL,
  telefono      VARCHAR(15) NULL,
  direccion     VARCHAR(200) NULL,
  total_visitas INT NOT NULL DEFAULT 0,                  -- fidelización (se incrementa al pagar)
  ultima_visita DATETIME NULL,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_cliente_dni UNIQUE (dni),
  CONSTRAINT uq_cliente_ruc UNIQUE (ruc),
  CONSTRAINT ck_cliente_dni CHECK (dni IS NULL OR dni REGEXP '^[0-9]{8}$'),
  CONSTRAINT ck_cliente_ruc CHECK (ruc IS NULL OR ruc REGEXP '^[0-9]{11}$'),
  CONSTRAINT ck_cliente_visitas CHECK (total_visitas >= 0),
  INDEX ix_cliente_visitas (total_visitas),
  INDEX ix_cliente_nombres (nombres)
) ENGINE=InnoDB;

CREATE TABLE mesa (
  id_mesa   INT AUTO_INCREMENT PRIMARY KEY,
  numero    SMALLINT NOT NULL,
  capacidad SMALLINT NOT NULL,
  estado    ENUM('LIBRE','OCUPADA','INACTIVA') NOT NULL DEFAULT 'LIBRE',
  CONSTRAINT uq_mesa_numero UNIQUE (numero),
  CONSTRAINT ck_mesa_capacidad CHECK (capacidad > 0),
  INDEX ix_mesa_estado (estado)
) ENGINE=InnoDB;

CREATE TABLE categoria_producto (
  id_categoria INT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(60) NOT NULL,
  tipo         ENUM('BEBIDA','COMIDA','POSTRE','OTRO') NOT NULL,
  activo       TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT uq_categoria_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE producto (
  id_producto   INT AUTO_INCREMENT PRIMARY KEY,
  id_categoria  INT NOT NULL,
  nombre        VARCHAR(100) NOT NULL,
  precio        DECIMAL(10,2) NOT NULL,                  -- precio de venta con IGV incluido
  disponible    TINYINT(1) NOT NULL DEFAULT 1,
  imagen        VARCHAR(255) NULL,
  controla_stock TINYINT(1) NOT NULL DEFAULT 0,          -- solo productos terminados
  stock_actual  INT NOT NULL DEFAULT 0,
  stock_minimo  INT NOT NULL DEFAULT 0,
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_producto_nombre UNIQUE (nombre),
  CONSTRAINT ck_producto_precio CHECK (precio >= 0),
  CONSTRAINT ck_producto_stock CHECK (stock_actual >= 0),
  CONSTRAINT ck_producto_minimo CHECK (stock_minimo >= 0),
  CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria_producto(id_categoria)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX ix_producto_categoria (id_categoria, disponible),
  INDEX ix_producto_stock (controla_stock, stock_actual)
) ENGINE=InnoDB;

CREATE TABLE promocion (
  id_promocion INT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(100) NOT NULL,
  tipo         ENUM('PORCENTAJE','MONTO','DOS_POR_UNO') NOT NULL,
  valor        DECIMAL(10,2) NOT NULL DEFAULT 0,         -- % o S/ según tipo (0 en 2x1)
  dia_semana   TINYINT NULL,                             -- 1=lunes ... 7=domingo; NULL = todos
  aplica_a     ENUM('BEBIDA','COMIDA','POSTRE','OTRO') NULL,  -- NULL = toda la carta
  fecha_inicio DATE NOT NULL,
  fecha_fin    DATE NULL,
  prioridad    INT NOT NULL DEFAULT 1,                   -- menor = se evalúa primero
  activa       TINYINT(1) NOT NULL DEFAULT 1,
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_promo_valor CHECK (tipo = 'DOS_POR_UNO' OR valor > 0),
  CONSTRAINT ck_promo_pct CHECK (tipo <> 'PORCENTAJE' OR valor <= 100),
  CONSTRAINT ck_promo_dia CHECK (dia_semana IS NULL OR dia_semana BETWEEN 1 AND 7),
  CONSTRAINT ck_promo_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
  INDEX ix_promo_vigencia (activa, dia_semana, fecha_inicio, fecha_fin)
) ENGINE=InnoDB;

CREATE TABLE caja (
  id_caja        INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario     INT NOT NULL,                           -- cajero
  fecha_apertura DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  monto_inicial  DECIMAL(10,2) NOT NULL,
  fecha_cierre   DATETIME NULL,
  monto_esperado DECIMAL(10,2) NULL,                     -- efectivo que debería haber
  monto_contado  DECIMAL(10,2) NULL,                     -- arqueo (efectivo contado)
  diferencia     DECIMAL(10,2) GENERATED ALWAYS AS (monto_contado - monto_esperado) STORED,
  estado         ENUM('ABIERTA','CERRADA') NOT NULL DEFAULT 'ABIERTA',
  observacion    VARCHAR(200) NULL,
  CONSTRAINT ck_caja_inicial CHECK (monto_inicial >= 0),
  CONSTRAINT ck_caja_contado CHECK (monto_contado IS NULL OR monto_contado >= 0),
  CONSTRAINT ck_caja_estado CHECK (
    (estado = 'ABIERTA' AND fecha_cierre IS NULL)
    OR (estado = 'CERRADA' AND fecha_cierre IS NOT NULL AND fecha_cierre >= fecha_apertura
        AND monto_contado IS NOT NULL AND monto_esperado IS NOT NULL)),
  CONSTRAINT fk_caja_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX ix_caja_cajero (id_usuario, estado),
  INDEX ix_caja_apertura (fecha_apertura)
) ENGINE=InnoDB;

CREATE TABLE pedido (
  id_pedido         INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente        INT NULL,                            -- NULL = cliente sin registrar
  id_mesa           INT NULL,                            -- solo modalidad SALON
  id_usuario        INT NOT NULL,                        -- mesero que registra
  id_promocion      INT NULL,                            -- promoción aplicada
  modalidad         ENUM('SALON','PARA_LLEVAR','DELIVERY') NOT NULL,
  estado            ENUM('REGISTRADO','CONFIRMADO','EN_PREPARACION','LISTO','ENTREGADO','PAGADO','CANCELADO')
                    NOT NULL DEFAULT 'REGISTRADO',
  subtotal          DECIMAL(10,2) NOT NULL DEFAULT 0,    -- bruto (antes de descuentos)
  descuento         DECIMAL(10,2) NOT NULL DEFAULT 0,
  costo_envio       DECIMAL(10,2) NOT NULL DEFAULT 0,
  total             DECIMAL(10,2) NOT NULL DEFAULT 0,    -- subtotal - descuento + envío (IGV incluido)
  direccion_entrega VARCHAR(200) NULL,
  telefono_contacto VARCHAR(15) NULL,
  observacion       VARCHAR(255) NULL,
  created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT ck_pedido_montos CHECK (subtotal >= 0 AND descuento >= 0 AND costo_envio >= 0 AND total >= 0
                                     AND descuento <= subtotal),
  CONSTRAINT ck_pedido_mesa CHECK ((modalidad = 'SALON' AND id_mesa IS NOT NULL)
                                   OR (modalidad <> 'SALON' AND id_mesa IS NULL)),
  CONSTRAINT ck_pedido_delivery CHECK (modalidad <> 'DELIVERY'
                                   OR (direccion_entrega IS NOT NULL AND telefono_contacto IS NOT NULL)),
  CONSTRAINT ck_pedido_envio CHECK (modalidad = 'DELIVERY' OR costo_envio = 0),
  CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_pedido_mesa FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pedido_promocion FOREIGN KEY (id_promocion) REFERENCES promocion(id_promocion)
    ON UPDATE CASCADE ON DELETE SET NULL,
  INDEX ix_pedido_estado_fecha (estado, created_at),
  INDEX ix_pedido_mesa_estado (id_mesa, estado),
  INDEX ix_pedido_fecha (created_at),
  INDEX ix_pedido_modalidad (modalidad, created_at),
  INDEX ix_pedido_cliente (id_cliente),
  INDEX ix_pedido_mesero (id_usuario, created_at)
) ENGINE=InnoDB;

CREATE TABLE detalle_pedido (
  id_detalle      INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido       INT NOT NULL,
  id_producto     INT NOT NULL,
  cantidad        INT NOT NULL,
  precio_unitario DECIMAL(10,2) NOT NULL,                -- precio histórico al momento de la venta
  descuento       DECIMAL(10,2) NOT NULL DEFAULT 0,
  subtotal        DECIMAL(10,2) GENERATED ALWAYS AS (cantidad * precio_unitario - descuento) STORED,
  es_adicional    TINYINT(1) NOT NULL DEFAULT 0,
  nota            VARCHAR(200) NULL,
  CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),
  CONSTRAINT ck_detalle_precio CHECK (precio_unitario >= 0),
  CONSTRAINT ck_detalle_descuento CHECK (descuento >= 0 AND descuento <= cantidad * precio_unitario),
  CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX ix_detalle_pedido (id_pedido),
  INDEX ix_detalle_producto (id_producto)
) ENGINE=InnoDB;

CREATE TABLE historial_estado_pedido (
  id_historial    BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_pedido       INT NOT NULL,
  estado_anterior ENUM('REGISTRADO','CONFIRMADO','EN_PREPARACION','LISTO','ENTREGADO','PAGADO','CANCELADO') NULL,
  estado_nuevo    ENUM('REGISTRADO','CONFIRMADO','EN_PREPARACION','LISTO','ENTREGADO','PAGADO','CANCELADO') NOT NULL,
  id_usuario      INT NOT NULL,
  fecha           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  observacion     VARCHAR(200) NULL,
  CONSTRAINT fk_hist_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_hist_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX ix_hist_pedido (id_pedido, fecha)
) ENGINE=InnoDB;

CREATE TABLE movimiento_stock (                          -- kardex de productos terminados
  id_movimiento    BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_producto      INT NOT NULL,
  id_pedido        INT NULL,
  tipo             ENUM('VENTA','ANULACION','AJUSTE') NOT NULL,
  cantidad         INT NOT NULL,                         -- con signo: VENTA<0, ANULACION>0, AJUSTE +/-
  stock_resultante INT NOT NULL,
  motivo           VARCHAR(200) NULL,
  id_usuario       INT NOT NULL,
  fecha            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_mov_cantidad CHECK ((tipo = 'VENTA' AND cantidad < 0) OR (tipo = 'ANULACION' AND cantidad > 0)
                                    OR (tipo = 'AJUSTE' AND cantidad <> 0)),
  CONSTRAINT ck_mov_resultante CHECK (stock_resultante >= 0),
  CONSTRAINT fk_mov_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_mov_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_mov_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX ix_mov_producto (id_producto, fecha),
  INDEX ix_mov_pedido (id_pedido)
) ENGINE=InnoDB;

CREATE TABLE pago (
  id_pago    INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido  INT NOT NULL,
  id_caja    INT NOT NULL,
  medio      ENUM('EFECTIVO','YAPE','PLIN','TARJETA') NOT NULL,
  monto      DECIMAL(10,2) NOT NULL,                     -- aplicado al pedido
  propina    DECIMAL(10,2) NOT NULL DEFAULT 0,           -- opcional, fuera del total del pedido
  referencia VARCHAR(40) NULL,                           -- nº de operación
  id_usuario INT NOT NULL,
  fecha      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_pago_monto CHECK (monto > 0),
  CONSTRAINT ck_pago_propina CHECK (propina >= 0),
  CONSTRAINT fk_pago_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pago_caja FOREIGN KEY (id_caja) REFERENCES caja(id_caja)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_pago_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX ix_pago_pedido (id_pedido),
  INDEX ix_pago_caja (id_caja, medio),
  INDEX ix_pago_medio_fecha (medio, fecha)
) ENGINE=InnoDB;

CREATE TABLE serie_comprobante (                         -- control del correlativo
  id_serie      INT AUTO_INCREMENT PRIMARY KEY,
  tipo          ENUM('BOLETA','FACTURA') NOT NULL,
  serie         CHAR(4) NOT NULL,
  ultimo_numero INT UNSIGNED NOT NULL DEFAULT 0,
  activa        TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT uq_serie UNIQUE (serie),
  CONSTRAINT ck_serie_prefijo CHECK ((tipo = 'BOLETA' AND serie LIKE 'B%') OR (tipo = 'FACTURA' AND serie LIKE 'F%'))
) ENGINE=InnoDB;

CREATE TABLE comprobante (
  id_comprobante INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido      INT NOT NULL,
  id_cliente     INT NULL,
  tipo           ENUM('BOLETA','FACTURA') NOT NULL,
  serie          CHAR(4) NOT NULL,
  numero         INT UNSIGNED NOT NULL,
  cliente_nombre VARCHAR(150) NOT NULL DEFAULT 'CLIENTES VARIOS',
  ruc_cliente    CHAR(11) NULL,
  base_imponible DECIMAL(10,2) NOT NULL,
  igv            DECIMAL(10,2) NOT NULL,                 -- 18 %
  total          DECIMAL(10,2) NOT NULL,
  estado         ENUM('EMITIDO','ANULADO') NOT NULL DEFAULT 'EMITIDO',
  id_usuario     INT NOT NULL,
  fecha_emision  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_comprobante_serie_numero UNIQUE (serie, numero),
  CONSTRAINT ck_comp_montos CHECK (base_imponible >= 0 AND igv >= 0 AND total = base_imponible + igv),
  CONSTRAINT ck_comp_factura_ruc CHECK (tipo <> 'FACTURA' OR (ruc_cliente IS NOT NULL AND ruc_cliente REGEXP '^[0-9]{11}$')),
  CONSTRAINT ck_comp_serie CHECK ((tipo = 'BOLETA' AND serie LIKE 'B%') OR (tipo = 'FACTURA' AND serie LIKE 'F%')),
  CONSTRAINT fk_comp_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_comp_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT fk_comp_serie FOREIGN KEY (serie) REFERENCES serie_comprobante(serie)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_comp_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX ix_comp_pedido (id_pedido),
  INDEX ix_comp_fecha (fecha_emision),
  INDEX ix_comp_estado (estado, tipo)
) ENGINE=InnoDB;

CREATE TABLE entrega (
  id_entrega    INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido     INT NOT NULL,
  id_usuario    INT NOT NULL,                            -- mesero que entrega
  recibido_por  VARCHAR(100) NULL,
  observacion   VARCHAR(200) NULL,
  fecha_entrega DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_entrega_pedido UNIQUE (id_pedido),
  CONSTRAINT fk_entrega_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT fk_entrega_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE anulacion (
  id_anulacion   INT AUTO_INCREMENT PRIMARY KEY,
  tipo           ENUM('PEDIDO','COMPROBANTE') NOT NULL,
  id_pedido      INT NULL,
  id_comprobante INT NULL,
  motivo         VARCHAR(255) NOT NULL,
  id_usuario     INT NOT NULL,
  fecha          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_anulacion_pedido UNIQUE (id_pedido),
  CONSTRAINT uq_anulacion_comprobante UNIQUE (id_comprobante),
  CONSTRAINT ck_anulacion_motivo CHECK (CHAR_LENGTH(TRIM(motivo)) >= 5),
  CONSTRAINT ck_anulacion_tipo CHECK ((tipo = 'PEDIDO' AND id_pedido IS NOT NULL AND id_comprobante IS NULL)
                                   OR (tipo = 'COMPROBANTE' AND id_comprobante IS NOT NULL AND id_pedido IS NULL)),
  CONSTRAINT fk_anul_pedido FOREIGN KEY (id_pedido) REFERENCES pedido(id_pedido)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_anul_comprobante FOREIGN KEY (id_comprobante) REFERENCES comprobante(id_comprobante)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT fk_anul_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  INDEX ix_anulacion_fecha (fecha)
) ENGINE=InnoDB;

CREATE TABLE auditoria_acceso (
  id_acceso          BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_usuario         INT NULL,
  username_intentado VARCHAR(40) NOT NULL,
  evento             ENUM('LOGIN_OK','LOGIN_FAIL','LOGOUT') NOT NULL,
  ip                 VARCHAR(45) NULL,
  user_agent         VARCHAR(255) NULL,
  fecha              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_acceso_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
    ON UPDATE CASCADE ON DELETE SET NULL,
  INDEX ix_acceso_usuario (id_usuario, fecha),
  INDEX ix_acceso_fecha (fecha)
) ENGINE=InnoDB;
DELIMITER $$

CREATE TRIGGER tr_producto_bu BEFORE UPDATE ON producto FOR EACH ROW
BEGIN
  IF NEW.stock_actual < 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Stock insuficiente: el stock no puede quedar negativo';
  END IF;
END$$

CREATE TRIGGER tr_detalle_bi BEFORE INSERT ON detalle_pedido FOR EACH ROW
BEGIN
  DECLARE v_estado VARCHAR(20);
  DECLARE v_disp, v_ctrl TINYINT;
  DECLARE v_stock INT;
  DECLARE v_precio DECIMAL(10,2);
  SELECT estado INTO v_estado FROM pedido WHERE id_pedido = NEW.id_pedido;
  IF v_estado IS NOT NULL AND v_estado <> 'REGISTRADO' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se pueden agregar productos a un pedido REGISTRADO';
  END IF;
  SELECT disponible, controla_stock, stock_actual, precio INTO v_disp, v_ctrl, v_stock, v_precio
    FROM producto WHERE id_producto = NEW.id_producto;
  IF v_disp = 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Producto no disponible';
  END IF;
  IF v_ctrl = 1 AND v_stock < NEW.cantidad THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Stock insuficiente para el producto solicitado';
  END IF;
  IF NEW.precio_unitario IS NULL THEN SET NEW.precio_unitario = v_precio; END IF;
END$$

CREATE TRIGGER tr_detalle_bu BEFORE UPDATE ON detalle_pedido FOR EACH ROW
BEGIN
  IF (SELECT estado FROM pedido WHERE id_pedido = OLD.id_pedido) <> 'REGISTRADO' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El detalle solo se modifica en pedidos REGISTRADOS';
  END IF;
END$$

CREATE TRIGGER tr_detalle_bd BEFORE DELETE ON detalle_pedido FOR EACH ROW
BEGIN
  IF (SELECT estado FROM pedido WHERE id_pedido = OLD.id_pedido) <> 'REGISTRADO' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El detalle solo se modifica en pedidos REGISTRADOS';
  END IF;
END$$

CREATE TRIGGER tr_pedido_ai AFTER INSERT ON pedido FOR EACH ROW
BEGIN
  INSERT INTO historial_estado_pedido (id_pedido, estado_anterior, estado_nuevo, id_usuario, observacion)
  VALUES (NEW.id_pedido, NULL, NEW.estado, NEW.id_usuario, 'Pedido registrado');
  IF NEW.id_mesa IS NOT NULL THEN
    UPDATE mesa SET estado = 'OCUPADA' WHERE id_mesa = NEW.id_mesa;
  END IF;
END$$

CREATE TRIGGER tr_pedido_bu BEFORE UPDATE ON pedido FOR EACH ROW
BEGIN
  IF NEW.estado <> OLD.estado THEN
    IF NOT ( (OLD.estado = 'REGISTRADO'     AND NEW.estado IN ('CONFIRMADO','CANCELADO'))
          OR (OLD.estado = 'CONFIRMADO'     AND NEW.estado IN ('EN_PREPARACION','CANCELADO'))
          OR (OLD.estado = 'EN_PREPARACION' AND NEW.estado IN ('LISTO','CANCELADO'))
          OR (OLD.estado = 'LISTO'          AND NEW.estado IN ('PAGADO','CANCELADO'))
          OR (OLD.estado = 'PAGADO'         AND NEW.estado = 'ENTREGADO') ) THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Transición de estado de pedido no permitida';
    END IF;

    IF NEW.estado = 'CONFIRMADO' THEN
      IF NOT EXISTS (SELECT 1 FROM detalle_pedido WHERE id_pedido = NEW.id_pedido) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede confirmar un pedido sin productos';
      END IF;
      IF EXISTS (SELECT 1 FROM detalle_pedido d JOIN producto p ON p.id_producto = d.id_producto
                 WHERE d.id_pedido = NEW.id_pedido AND p.disponible = 0) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El pedido contiene productos no disponibles';
      END IF;
      IF EXISTS (SELECT 1
                 FROM (SELECT id_producto, SUM(cantidad) AS c FROM detalle_pedido
                       WHERE id_pedido = NEW.id_pedido GROUP BY id_producto) x
                 JOIN producto p ON p.id_producto = x.id_producto
                 WHERE p.controla_stock = 1 AND p.stock_actual < x.c) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Stock insuficiente para confirmar el pedido';
      END IF;
    END IF;
    IF NEW.estado = 'CANCELADO'
       AND NOT EXISTS (SELECT 1 FROM anulacion WHERE id_pedido = NEW.id_pedido) THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Registre la anulación con motivo (sp_anular_pedido)';
    END IF;
    IF NEW.estado = 'PAGADO'
       AND (SELECT COALESCE(SUM(monto), 0) FROM pago WHERE id_pedido = NEW.id_pedido) < NEW.total THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El pedido no está pagado por completo';
    END IF;
    IF NEW.estado = 'ENTREGADO' THEN
      IF NOT EXISTS (SELECT 1 FROM comprobante WHERE id_pedido = NEW.id_pedido AND estado = 'EMITIDO') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Emita el comprobante antes de entregar';
      END IF;
      IF NOT EXISTS (SELECT 1 FROM entrega WHERE id_pedido = NEW.id_pedido) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Registre la entrega (sp_registrar_entrega)';
      END IF;
    END IF;
  END IF;
  IF OLD.estado <> 'REGISTRADO' AND (NEW.subtotal <> OLD.subtotal OR NEW.descuento <> OLD.descuento
       OR NEW.total <> OLD.total OR NEW.costo_envio <> OLD.costo_envio) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Los montos solo se modifican en pedidos REGISTRADOS';
  END IF;
END$$

CREATE TRIGGER tr_pedido_au AFTER UPDATE ON pedido FOR EACH ROW
BEGIN
  DECLARE v_user INT;
  IF NEW.estado <> OLD.estado THEN
    SET v_user = COALESCE(@id_usuario_actual, NEW.id_usuario);
    INSERT INTO historial_estado_pedido (id_pedido, estado_anterior, estado_nuevo, id_usuario, observacion)
    VALUES (NEW.id_pedido, OLD.estado, NEW.estado, v_user, @obs_estado);

    IF NEW.estado = 'CONFIRMADO' THEN                      -- descontar stock
      UPDATE producto p
        JOIN (SELECT id_producto, SUM(cantidad) AS c FROM detalle_pedido
              WHERE id_pedido = NEW.id_pedido GROUP BY id_producto) d ON d.id_producto = p.id_producto
        SET p.stock_actual = p.stock_actual - d.c
        WHERE p.controla_stock = 1;
      INSERT INTO movimiento_stock (id_producto, id_pedido, tipo, cantidad, stock_resultante, motivo, id_usuario)
      SELECT p.id_producto, NEW.id_pedido, 'VENTA', -d.c, p.stock_actual,
             CONCAT('Pedido #', NEW.id_pedido, ' confirmado'), v_user
        FROM producto p
        JOIN (SELECT id_producto, SUM(cantidad) AS c FROM detalle_pedido
              WHERE id_pedido = NEW.id_pedido GROUP BY id_producto) d ON d.id_producto = p.id_producto
        WHERE p.controla_stock = 1;
    END IF;

    IF NEW.estado = 'CANCELADO' AND OLD.estado IN ('CONFIRMADO','EN_PREPARACION','LISTO') THEN  -- devolver stock
      UPDATE producto p
        JOIN (SELECT id_producto, SUM(cantidad) AS c FROM detalle_pedido
              WHERE id_pedido = NEW.id_pedido GROUP BY id_producto) d ON d.id_producto = p.id_producto
        SET p.stock_actual = p.stock_actual + d.c
        WHERE p.controla_stock = 1;
      INSERT INTO movimiento_stock (id_producto, id_pedido, tipo, cantidad, stock_resultante, motivo, id_usuario)
      SELECT p.id_producto, NEW.id_pedido, 'ANULACION', d.c, p.stock_actual,
             CONCAT('Pedido #', NEW.id_pedido, ' anulado'), v_user
        FROM producto p
        JOIN (SELECT id_producto, SUM(cantidad) AS c FROM detalle_pedido
              WHERE id_pedido = NEW.id_pedido GROUP BY id_producto) d ON d.id_producto = p.id_producto
        WHERE p.controla_stock = 1;
    END IF;

    IF NEW.estado = 'PAGADO' AND NEW.id_cliente IS NOT NULL THEN
      UPDATE cliente SET total_visitas = total_visitas + 1, ultima_visita = NOW()
       WHERE id_cliente = NEW.id_cliente;
    END IF;

    IF NEW.estado IN ('ENTREGADO','CANCELADO') AND NEW.id_mesa IS NOT NULL THEN
      UPDATE mesa SET estado = 'LIBRE'
       WHERE id_mesa = NEW.id_mesa
         AND NOT EXISTS (SELECT 1 FROM pedido o WHERE o.id_mesa = NEW.id_mesa
                         AND o.id_pedido <> NEW.id_pedido AND o.estado NOT IN ('ENTREGADO','CANCELADO'));
    END IF;
  END IF;
END$$

CREATE TRIGGER tr_caja_bi BEFORE INSERT ON caja FOR EACH ROW
BEGIN
  IF (SELECT r.nombre FROM usuario u JOIN rol r ON r.id_rol = u.id_rol WHERE u.id_usuario = NEW.id_usuario)
       NOT IN ('CAJERO','ADMIN') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo un CAJERO o ADMIN puede abrir caja';
  END IF;
  IF EXISTS (SELECT 1 FROM caja WHERE id_usuario = NEW.id_usuario AND estado = 'ABIERTA') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El cajero ya tiene una caja abierta';
  END IF;
END$$

CREATE TRIGGER tr_pago_bi BEFORE INSERT ON pago FOR EACH ROW
BEGIN
  DECLARE v_total DECIMAL(10,2);
  DECLARE v_estado VARCHAR(20);
  IF (SELECT estado FROM caja WHERE id_caja = NEW.id_caja) <> 'ABIERTA' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Todo pago debe registrarse en una caja ABIERTA';
  END IF;
  SELECT total, estado INTO v_total, v_estado FROM pedido WHERE id_pedido = NEW.id_pedido;
  IF v_estado <> 'LISTO' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo se cobra un pedido en estado LISTO';
  END IF;
  IF (SELECT COALESCE(SUM(monto), 0) FROM pago WHERE id_pedido = NEW.id_pedido) + NEW.monto > v_total THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El pago excede el total del pedido';
  END IF;
END$$

-- Recalcula descuentos y totales (uso interno, sin result set). Solo pedidos REGISTRADOS.
CREATE PROCEDURE sp_recalcular_pedido(IN p_id_pedido INT)
BEGIN
  DECLARE v_estado VARCHAR(20);
  DECLARE v_fecha DATETIME;
  DECLARE v_promo INT;
  DECLARE v_tipo VARCHAR(20);
  DECLARE v_valor, v_bruto, v_elegible, v_desc_lineas, v_desc_global DECIMAL(10,2) DEFAULT 0;
  DECLARE v_aplica VARCHAR(20);

  SELECT estado, created_at INTO v_estado, v_fecha FROM pedido WHERE id_pedido = p_id_pedido;
  IF v_estado IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El pedido no existe';
  END IF;

  IF v_estado = 'REGISTRADO' THEN
    SELECT id_promocion, tipo, valor, aplica_a INTO v_promo, v_tipo, v_valor, v_aplica
      FROM promocion
     WHERE activa = 1 AND DATE(v_fecha) >= fecha_inicio
       AND (fecha_fin IS NULL OR DATE(v_fecha) <= fecha_fin)
       AND (dia_semana IS NULL OR dia_semana = WEEKDAY(v_fecha) + 1)
     ORDER BY prioridad, id_promocion LIMIT 1;

    UPDATE detalle_pedido d
      JOIN producto p ON p.id_producto = d.id_producto
      JOIN categoria_producto c ON c.id_categoria = p.id_categoria
       SET d.descuento = CASE
             WHEN v_promo IS NULL OR (v_aplica IS NOT NULL AND c.tipo <> v_aplica) THEN 0
             WHEN v_tipo = 'DOS_POR_UNO' THEN FLOOR(d.cantidad / 2) * d.precio_unitario
             WHEN v_tipo = 'PORCENTAJE'  THEN ROUND(d.cantidad * d.precio_unitario * v_valor / 100, 2)
             ELSE 0 END
     WHERE d.id_pedido = p_id_pedido;

    SELECT COALESCE(SUM(d.cantidad * d.precio_unitario), 0), COALESCE(SUM(d.descuento), 0)
      INTO v_bruto, v_desc_lineas FROM detalle_pedido d WHERE d.id_pedido = p_id_pedido;

    IF v_promo IS NOT NULL AND v_tipo = 'MONTO' THEN
      SELECT COALESCE(SUM(d.cantidad * d.precio_unitario), 0) INTO v_elegible
        FROM detalle_pedido d
        JOIN producto p ON p.id_producto = d.id_producto
        JOIN categoria_producto c ON c.id_categoria = p.id_categoria
       WHERE d.id_pedido = p_id_pedido AND (v_aplica IS NULL OR c.tipo = v_aplica);
      SET v_desc_global = LEAST(v_valor, v_elegible);
    END IF;

    UPDATE pedido
       SET id_promocion = v_promo,
           subtotal  = v_bruto,
           descuento = v_desc_lineas + v_desc_global,
           total     = v_bruto - v_desc_lineas - v_desc_global + costo_envio
     WHERE id_pedido = p_id_pedido;
  END IF;
END$$

-- Calcula (y guarda) el total con promoción e IGV 18 % (precios con IGV incluido)
CREATE PROCEDURE sp_calcular_total_pedido(IN p_id_pedido INT)
BEGIN
  CALL sp_recalcular_pedido(p_id_pedido);
  SELECT p.id_pedido, p.estado, pr.nombre AS promocion_aplicada,
         p.subtotal, p.descuento, p.costo_envio, p.total,
         ROUND(p.total / 1.18, 2)           AS base_imponible,
         p.total - ROUND(p.total / 1.18, 2) AS igv_18
    FROM pedido p LEFT JOIN promocion pr ON pr.id_promocion = p.id_promocion
   WHERE p.id_pedido = p_id_pedido;
END$$

CREATE PROCEDURE sp_cambiar_estado_pedido(IN p_id_pedido INT, IN p_estado VARCHAR(20),
                                          IN p_id_usuario INT, IN p_obs VARCHAR(200))
BEGIN
  IF p_estado = 'CONFIRMADO' THEN CALL sp_recalcular_pedido(p_id_pedido); END IF;
  SET @id_usuario_actual = p_id_usuario, @obs_estado = p_obs;
  UPDATE pedido SET estado = p_estado WHERE id_pedido = p_id_pedido;
  SET @id_usuario_actual = NULL, @obs_estado = NULL;
END$$

CREATE PROCEDURE sp_anular_pedido(IN p_id_pedido INT, IN p_motivo VARCHAR(255), IN p_id_usuario INT)
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;
  START TRANSACTION;
    INSERT INTO anulacion (tipo, id_pedido, motivo, id_usuario) VALUES ('PEDIDO', p_id_pedido, p_motivo, p_id_usuario);
    CALL sp_cambiar_estado_pedido(p_id_pedido, 'CANCELADO', p_id_usuario, p_motivo);
  COMMIT;
END$$

CREATE PROCEDURE sp_registrar_pago(IN p_id_pedido INT, IN p_id_caja INT, IN p_medio VARCHAR(10),
                                   IN p_monto DECIMAL(10,2), IN p_propina DECIMAL(10,2),
                                   IN p_referencia VARCHAR(40), IN p_id_usuario INT)
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;
  START TRANSACTION;
    INSERT INTO pago (id_pedido, id_caja, medio, monto, propina, referencia, id_usuario)
    VALUES (p_id_pedido, p_id_caja, p_medio, p_monto, COALESCE(p_propina, 0), p_referencia, p_id_usuario);
    IF (SELECT COALESCE(SUM(monto), 0) FROM pago WHERE id_pedido = p_id_pedido)
         >= (SELECT total FROM pedido WHERE id_pedido = p_id_pedido) THEN
      CALL sp_cambiar_estado_pedido(p_id_pedido, 'PAGADO', p_id_usuario, 'Pago completo');
    END IF;
  COMMIT;
END$$

CREATE PROCEDURE sp_emitir_comprobante(IN p_id_pedido INT, IN p_tipo VARCHAR(10), IN p_ruc CHAR(11),
                                       IN p_razon_social VARCHAR(150), IN p_id_usuario INT,
                                       OUT p_id_comprobante INT)
BEGIN
  DECLARE v_estado VARCHAR(20);
  DECLARE v_total, v_base DECIMAL(10,2);
  DECLARE v_cliente INT;
  DECLARE v_nombre, v_razon VARCHAR(150);
  DECLARE v_ruc CHAR(11);
  DECLARE v_serie CHAR(4);
  DECLARE v_num INT UNSIGNED;
  DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;

  SELECT p.estado, p.total, p.id_cliente, c.nombres, c.ruc, c.razon_social
    INTO v_estado, v_total, v_cliente, v_nombre, v_ruc, v_razon
    FROM pedido p LEFT JOIN cliente c ON c.id_cliente = p.id_cliente WHERE p.id_pedido = p_id_pedido;
  IF v_estado IS NULL OR v_estado <> 'PAGADO' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El comprobante se emite con el pedido PAGADO';
  END IF;
  IF EXISTS (SELECT 1 FROM comprobante WHERE id_pedido = p_id_pedido AND estado = 'EMITIDO') THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El pedido ya tiene un comprobante emitido';
  END IF;
  SET v_ruc = COALESCE(p_ruc, v_ruc);
  IF p_tipo = 'FACTURA' AND v_ruc IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La FACTURA exige el RUC del cliente';
  END IF;
  SET v_nombre = CASE WHEN p_tipo = 'FACTURA' THEN COALESCE(p_razon_social, v_razon, v_nombre, 'SIN RAZON SOCIAL')
                      ELSE COALESCE(v_nombre, 'CLIENTES VARIOS') END;
  SET v_base = ROUND(v_total / 1.18, 2);

  START TRANSACTION;
    SELECT serie, ultimo_numero + 1 INTO v_serie, v_num FROM serie_comprobante
     WHERE tipo = p_tipo AND activa = 1 ORDER BY id_serie LIMIT 1 FOR UPDATE;
    UPDATE serie_comprobante SET ultimo_numero = v_num WHERE serie = v_serie;
    INSERT INTO comprobante (id_pedido, id_cliente, tipo, serie, numero, cliente_nombre, ruc_cliente,
                             base_imponible, igv, total, id_usuario)
    VALUES (p_id_pedido, v_cliente, p_tipo, v_serie, v_num, v_nombre,
            CASE WHEN p_tipo = 'FACTURA' THEN v_ruc ELSE NULL END,
            v_base, v_total - v_base, v_total, p_id_usuario);
    SET p_id_comprobante = LAST_INSERT_ID();
  COMMIT;
END$$

CREATE PROCEDURE sp_anular_comprobante(IN p_id_comprobante INT, IN p_motivo VARCHAR(255), IN p_id_usuario INT)
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;
  START TRANSACTION;
    INSERT INTO anulacion (tipo, id_comprobante, motivo, id_usuario)
    VALUES ('COMPROBANTE', p_id_comprobante, p_motivo, p_id_usuario);
    UPDATE comprobante SET estado = 'ANULADO' WHERE id_comprobante = p_id_comprobante AND estado = 'EMITIDO';
    IF ROW_COUNT() = 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Comprobante inexistente o ya anulado';
    END IF;
  COMMIT;
END$$

CREATE PROCEDURE sp_registrar_entrega(IN p_id_pedido INT, IN p_id_usuario INT,
                                      IN p_recibido_por VARCHAR(100), IN p_obs VARCHAR(200))
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;
  START TRANSACTION;
    INSERT INTO entrega (id_pedido, id_usuario, recibido_por, observacion)
    VALUES (p_id_pedido, p_id_usuario, p_recibido_por, p_obs);
    CALL sp_cambiar_estado_pedido(p_id_pedido, 'ENTREGADO', p_id_usuario, 'Entrega registrada');
  COMMIT;
END$$

CREATE PROCEDURE sp_cerrar_caja(IN p_id_caja INT, IN p_monto_contado DECIMAL(10,2), IN p_obs VARCHAR(200))
BEGIN
  DECLARE v_esperado DECIMAL(10,2);
  SELECT c.monto_inicial + COALESCE(SUM(CASE WHEN p.medio = 'EFECTIVO' THEN p.monto + p.propina ELSE 0 END), 0)
    INTO v_esperado
    FROM caja c LEFT JOIN pago p ON p.id_caja = c.id_caja
   WHERE c.id_caja = p_id_caja AND c.estado = 'ABIERTA' GROUP BY c.id_caja, c.monto_inicial;
  IF v_esperado IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La caja no existe o ya está cerrada';
  END IF;
  UPDATE caja SET estado = 'CERRADA', fecha_cierre = NOW(), monto_esperado = v_esperado,
                  monto_contado = p_monto_contado, observacion = p_obs
   WHERE id_caja = p_id_caja;
END$$

CREATE PROCEDURE sp_ajustar_stock(IN p_id_producto INT, IN p_cantidad INT, IN p_motivo VARCHAR(200), IN p_id_usuario INT)
BEGIN
  DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;
  IF p_cantidad = 0 OR p_motivo IS NULL OR CHAR_LENGTH(TRIM(p_motivo)) < 5 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ajuste inválido: cantidad distinta de 0 y motivo obligatorio';
  END IF;
  START TRANSACTION;
    UPDATE producto SET stock_actual = stock_actual + p_cantidad
     WHERE id_producto = p_id_producto AND controla_stock = 1;
    IF ROW_COUNT() = 0 THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Producto inexistente o sin control de stock';
    END IF;
    INSERT INTO movimiento_stock (id_producto, tipo, cantidad, stock_resultante, motivo, id_usuario)
    SELECT id_producto, 'AJUSTE', p_cantidad, stock_actual, p_motivo, p_id_usuario
      FROM producto WHERE id_producto = p_id_producto;
  COMMIT;
END$$

CREATE PROCEDURE sp_ventas_por_rango(IN p_desde DATE, IN p_hasta DATE)
BEGIN
  SELECT DATE(created_at) AS fecha, COUNT(*) AS pedidos, SUM(subtotal) AS venta_bruta,
         SUM(descuento) AS descuentos, SUM(costo_envio) AS envios, SUM(total) AS total_vendido
    FROM pedido
   WHERE estado IN ('PAGADO','ENTREGADO') AND created_at >= p_desde AND created_at < DATE_ADD(p_hasta, INTERVAL 1 DAY)
   GROUP BY DATE(created_at) ORDER BY fecha;
END$$

DELIMITER ;
CREATE OR REPLACE VIEW vw_ventas_por_dia AS
SELECT DATE(p.created_at) AS fecha, COUNT(*) AS pedidos, SUM(p.subtotal) AS venta_bruta,
       SUM(p.descuento) AS descuentos, SUM(p.costo_envio) AS envios, SUM(p.total) AS total_vendido,
       ROUND(SUM(p.total) / 1.18, 2) AS base_imponible,
       SUM(p.total) - ROUND(SUM(p.total) / 1.18, 2) AS igv
  FROM pedido p
 WHERE p.estado IN ('PAGADO','ENTREGADO')
 GROUP BY DATE(p.created_at);

CREATE OR REPLACE VIEW vw_top_productos AS
SELECT pr.id_producto, pr.nombre AS producto, c.nombre AS categoria,
       SUM(d.cantidad) AS unidades_vendidas, SUM(d.subtotal) AS ingresos
  FROM detalle_pedido d
  JOIN pedido p ON p.id_pedido = d.id_pedido AND p.estado IN ('PAGADO','ENTREGADO')
  JOIN producto pr ON pr.id_producto = d.id_producto
  JOIN categoria_producto c ON c.id_categoria = pr.id_categoria
 GROUP BY pr.id_producto, pr.nombre, c.nombre
 ORDER BY unidades_vendidas DESC, ingresos DESC;

CREATE OR REPLACE VIEW vw_ventas_por_modalidad AS
SELECT p.modalidad, COUNT(*) AS pedidos, SUM(p.total) AS total_vendido,
       ROUND(AVG(p.total), 2) AS ticket_promedio
  FROM pedido p
 WHERE p.estado IN ('PAGADO','ENTREGADO')
 GROUP BY p.modalidad;

CREATE OR REPLACE VIEW vw_ventas_por_medio_pago AS
SELECT pg.medio, COUNT(*) AS pagos, SUM(pg.monto) AS monto, SUM(pg.propina) AS propinas,
       SUM(pg.monto + pg.propina) AS total_cobrado
  FROM pago pg
 GROUP BY pg.medio;

CREATE OR REPLACE VIEW vw_ventas_por_cajero_turno AS
SELECT u.id_usuario, CONCAT(u.nombres, ' ', u.apellidos) AS cajero, c.id_caja,
       DATE(c.fecha_apertura) AS fecha,
       CASE WHEN HOUR(c.fecha_apertura) < 13 THEN 'MAÑANA' ELSE 'TARDE' END AS turno,
       COUNT(pg.id_pago) AS pagos, COALESCE(SUM(pg.monto), 0) AS monto, COALESCE(SUM(pg.propina), 0) AS propinas
  FROM caja c
  JOIN usuario u ON u.id_usuario = c.id_usuario
  LEFT JOIN pago pg ON pg.id_caja = c.id_caja
 GROUP BY u.id_usuario, u.nombres, u.apellidos, c.id_caja, c.fecha_apertura;

CREATE OR REPLACE VIEW vw_productos_stock_bajo AS
SELECT id_producto, nombre, stock_actual, stock_minimo, stock_minimo - stock_actual AS faltante_para_minimo
  FROM producto
 WHERE controla_stock = 1 AND stock_actual <= stock_minimo
 ORDER BY stock_actual - stock_minimo;

CREATE OR REPLACE VIEW vw_clientes_frecuentes AS
SELECT c.id_cliente, c.nombres, c.telefono, c.total_visitas, c.ultima_visita,
       COALESCE(SUM(p.total), 0) AS monto_comprado,
       (c.total_visitas >= 3) AS es_frecuente              -- umbral: 3 o más visitas
  FROM cliente c
  LEFT JOIN pedido p ON p.id_cliente = c.id_cliente AND p.estado IN ('PAGADO','ENTREGADO')
 WHERE c.total_visitas > 0
 GROUP BY c.id_cliente, c.nombres, c.telefono, c.total_visitas, c.ultima_visita
 ORDER BY c.total_visitas DESC, monto_comprado DESC;

CREATE OR REPLACE VIEW vw_resumen_cierre_caja AS
SELECT c.id_caja, CONCAT(u.nombres, ' ', u.apellidos) AS cajero, c.estado,
       c.fecha_apertura, c.fecha_cierre, c.monto_inicial,
       COALESCE(SUM(CASE WHEN pg.medio = 'EFECTIVO' THEN pg.monto END), 0) AS ventas_efectivo,
       COALESCE(SUM(CASE WHEN pg.medio = 'YAPE'     THEN pg.monto END), 0) AS ventas_yape,
       COALESCE(SUM(CASE WHEN pg.medio = 'PLIN'     THEN pg.monto END), 0) AS ventas_plin,
       COALESCE(SUM(CASE WHEN pg.medio = 'TARJETA'  THEN pg.monto END), 0) AS ventas_tarjeta,
       COALESCE(SUM(pg.propina), 0) AS propinas,
       c.monto_inicial + COALESCE(SUM(CASE WHEN pg.medio = 'EFECTIVO' THEN pg.monto + pg.propina END), 0)
         AS efectivo_esperado,
       c.monto_contado, c.diferencia
  FROM caja c
  JOIN usuario u ON u.id_usuario = c.id_usuario
  LEFT JOIN pago pg ON pg.id_caja = c.id_caja
 GROUP BY c.id_caja, u.nombres, u.apellidos, c.estado, c.fecha_apertura, c.fecha_cierre,
          c.monto_inicial, c.monto_contado, c.diferencia;
INSERT INTO rol (id_rol, nombre, descripcion) VALUES
 (1,'ADMIN','Propietario: acceso total'),
 (2,'MESERO','Registra pedidos y entrega'),
 (3,'COCINA','Prepara pedidos'),
 (4,'CAJERO','Cobra, abre/cierra caja y emite comprobantes');

-- Hash BCrypt de ejemplo (clave de prueba "Caffeto2026!") -> REEMPLAZAR antes de producción
INSERT INTO usuario (id_usuario, id_rol, username, password_hash, nombres, apellidos) VALUES
 (1,1,'propietario','$2a$10$tHo4ruKcsmI4v2tRH5Bui.zPnrBhvsQzm3nFIWUlZ0UYppof1soRe','Propietario','Don Caffeto'),
 (2,2,'mesero1',    '$2a$10$tHo4ruKcsmI4v2tRH5Bui.zPnrBhvsQzm3nFIWUlZ0UYppof1soRe','Julio','Ramos Paucar'),
 (3,3,'cocina1',    '$2a$10$tHo4ruKcsmI4v2tRH5Bui.zPnrBhvsQzm3nFIWUlZ0UYppof1soRe','Marta','Gonzales Ríos'),
 (4,4,'cajero1',    '$2a$10$tHo4ruKcsmI4v2tRH5Bui.zPnrBhvsQzm3nFIWUlZ0UYppof1soRe','Sofía','Camayo Lazo'),
 (5,4,'cajero2',    '$2a$10$tHo4ruKcsmI4v2tRH5Bui.zPnrBhvsQzm3nFIWUlZ0UYppof1soRe','Diego','Huamán Soto');

INSERT INTO mesa (numero, capacidad) VALUES (1,2),(2,2),(3,4),(4,4),(5,4),(6,4),(7,6),(8,6),(9,8),(10,2);

INSERT INTO categoria_producto (id_categoria, nombre, tipo) VALUES
 (1,'Cafés calientes','BEBIDA'),(2,'Jugos y bebidas frías','BEBIDA'),(3,'Gaseosas y aguas','BEBIDA'),
 (4,'Sándwiches y snacks','COMIDA'),(5,'Desayunos y platos','COMIDA'),(6,'Postres y dulces','POSTRE'),
 (7,'Café de la zona y otros','OTRO');

INSERT INTO producto (id_producto, id_categoria, nombre, precio, controla_stock, stock_actual, stock_minimo) VALUES
 (1,1,'Espresso',5.00,0,0,0),
 (2,1,'Americano',6.00,0,0,0),
 (3,1,'Capuchino',9.00,0,0,0),
 (4,1,'Latte',10.00,0,0,0),
 (5,1,'Moka',11.00,0,0,0),
 (6,1,'Chocolate caliente',8.00,0,0,0),
 (7,1,'Infusión de hierbaluisa',4.00,0,0,0),
 (8,2,'Jugo de naranja',7.00,0,0,0),
 (9,2,'Jugo de papaya',7.00,0,0,0),
 (10,2,'Limonada',6.00,0,0,0),
 (11,2,'Frappé de café',12.00,0,0,0),
 (12,2,'Chicha morada (vaso)',5.00,0,0,0),
 (13,3,'Coca-Cola 500 ml',4.50,1,48,12),
 (14,3,'Inca Kola 500 ml',4.50,1,48,12),
 (15,3,'Agua mineral 500 ml',3.00,1,36,10),
 (16,4,'Sándwich de pollo',10.00,1,20,5),
 (17,4,'Sándwich de jamón y queso',9.00,1,20,5),
 (18,4,'Club sándwich',16.00,1,12,4),
 (19,4,'Pan con chicharrón',12.00,0,0,0),
 (20,4,'Empanada de carne',6.00,1,25,6),
 (21,4,'Tamal serrano',7.00,1,15,4),
 (22,5,'Desayuno continental',15.00,0,0,0),
 (23,5,'Hamburguesa clásica',14.00,0,0,0),
 (24,5,'Tostadas con palta',9.00,0,0,0),
 (25,6,'Torta de chocolate (porción)',8.00,1,15,4),
 (26,6,'Cheesecake de maracuyá (porción)',9.00,1,12,4),
 (27,6,'Pie de limón (porción)',8.00,1,5,4),
 (28,6,'Brownie',6.00,1,6,5),
 (29,7,'Café molido de Chanchamayo 250 g',22.00,1,30,8),
 (30,6,'Alfajor',3.50,1,30,8);

INSERT INTO promocion (id_promocion, nombre, tipo, valor, dia_semana, aplica_a, fecha_inicio, prioridad, activa) VALUES
 (1,'2x1 en bebidas los miércoles','DOS_POR_UNO',0,3,'BEBIDA','2024-01-01',1,1),
 (2,'10% en postres los viernes (ejemplo, inactiva)','PORCENTAJE',10,5,'POSTRE','2026-01-01',2,0);

INSERT INTO serie_comprobante (tipo, serie) VALUES ('BOLETA','B001'),('FACTURA','F001');

INSERT INTO cliente (id_cliente, nombres, dni, ruc, razon_social, telefono, direccion) VALUES
 (1,'Rosa Quispe Mendoza','45123678',NULL,NULL,'964111222',NULL),
 (2,'Luis Huamán Rojas','70234981',NULL,NULL,'964333444','Jr. Los Cafetales 120, Villa María, Perené'),
 (3,'Transportes Selva Central S.A.C.',NULL,'20601234567','TRANSPORTES SELVA CENTRAL S.A.C.','964555666','Av. Marginal 450, Perené'),
 (4,'Carmen Salas Vega','41876325',NULL,NULL,'964777888',NULL);

INSERT INTO auditoria_acceso (id_usuario, username_intentado, evento, ip, user_agent, fecha) VALUES
 (1,'propietario','LOGIN_OK','192.168.1.10','Mozilla/5.0','2026-10-14 07:20:00'),
 (4,'cajero1','LOGIN_OK','192.168.1.21','Mozilla/5.0','2026-10-14 07:28:00'),
 (NULL,'cajero3','LOGIN_FAIL','192.168.1.35','Mozilla/5.0','2026-10-14 08:02:00'),
 (2,'mesero1','LOGIN_OK','192.168.1.30','Mozilla/5.0 (Android)','2026-10-14 08:30:00'),
 (4,'cajero1','LOGOUT','192.168.1.21','Mozilla/5.0','2026-10-14 15:02:00'),
 (5,'cajero2','LOGIN_OK','192.168.1.22','Mozilla/5.0','2026-10-21 07:25:00');

-- ---------- 8 pedidos de ejemplo ----------
DELIMITER $$
CREATE PROCEDURE sp_seed_avanzar(IN p_id INT)               -- auxiliar solo para la semilla
BEGIN
  CALL sp_cambiar_estado_pedido(p_id, 'CONFIRMADO', 2, NULL);
  CALL sp_cambiar_estado_pedido(p_id, 'EN_PREPARACION', 3, NULL);
  CALL sp_cambiar_estado_pedido(p_id, 'LISTO', 3, NULL);
END$$
DELIMITER ;

-- Caja 1 (cajero1): pedidos 1 y 2 (miércoles 14/10, rige el 2x1)
INSERT INTO caja (id_caja, id_usuario, monto_inicial) VALUES (1, 4, 100.00);

INSERT INTO pedido (id_pedido, id_cliente, id_mesa, id_usuario, modalidad, created_at) VALUES
 (1, 1, 1, 2, 'SALON', '2026-10-14 10:15:00');
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES (1,3,2),(1,8,2),(1,16,1),(1,28,1);
CALL sp_seed_avanzar(1);
CALL sp_registrar_pago(1, 1, 'EFECTIVO', 32.00, 2.00, NULL, 4);
CALL sp_emitir_comprobante(1, 'BOLETA', NULL, NULL, 4, @c);
CALL sp_registrar_entrega(1, 2, 'Rosa Quispe', NULL);

INSERT INTO pedido (id_pedido, id_cliente, id_usuario, modalidad, created_at) VALUES
 (2, 1, 2, 'PARA_LLEVAR', '2026-10-14 12:40:00');
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, nota) VALUES (2,13,2,'Bien heladas'),(2,20,2,NULL);
CALL sp_seed_avanzar(2);
CALL sp_registrar_pago(2, 1, 'YAPE', 16.50, 0, '00123456', 4);
CALL sp_emitir_comprobante(2, 'BOLETA', NULL, NULL, 4, @c);
CALL sp_registrar_entrega(2, 2, 'Rosa Quispe', NULL);
CALL sp_cerrar_caja(1, 134.00, 'Cierre sin novedades');

-- Caja 2 (cajero2): pedidos 3 (factura) y 4 (delivery)
INSERT INTO caja (id_caja, id_usuario, monto_inicial) VALUES (2, 5, 80.00);

INSERT INTO pedido (id_pedido, id_cliente, id_mesa, id_usuario, modalidad, created_at) VALUES
 (3, 3, 4, 2, 'SALON', '2026-10-15 14:10:00');
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES (3,18,3),(3,23,2),(3,10,4);
CALL sp_seed_avanzar(3);
CALL sp_registrar_pago(3, 2, 'TARJETA', 100.00, 0, 'POS-778812', 5);
CALL sp_emitir_comprobante(3, 'FACTURA', NULL, NULL, 5, @c);
CALL sp_registrar_entrega(3, 2, 'Administrador de la empresa', NULL);

INSERT INTO pedido (id_pedido, id_cliente, id_usuario, modalidad, costo_envio, direccion_entrega, telefono_contacto, observacion, created_at) VALUES
 (4, 2, 2, 'DELIVERY', 3.00, 'Jr. Los Cafetales 120, Villa María, Perené', '964333444', 'Tocar el timbre', '2026-10-15 19:30:00');
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES (4,18,2),(4,4,2),(4,26,1);
CALL sp_seed_avanzar(4);
CALL sp_registrar_pago(4, 2, 'PLIN', 64.00, 0, 'PL-4471', 5);
CALL sp_emitir_comprobante(4, 'BOLETA', NULL, NULL, 5, @c);
CALL sp_registrar_entrega(4, 2, 'Luis Huamán', 'Entregado en puerta');
CALL sp_cerrar_caja(2, 79.50, 'Faltante de S/ 0.50 por vuelto');

-- Pedido 5: ANULADO tras confirmar (el stock vuelve al kardex)
INSERT INTO pedido (id_pedido, id_mesa, id_usuario, modalidad, created_at) VALUES
 (5, 6, 2, 'SALON', '2026-10-17 11:20:00');
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES (5,22,2),(5,3,2),(5,25,2);
CALL sp_cambiar_estado_pedido(5, 'CONFIRMADO', 2, NULL);
CALL sp_anular_pedido(5, 'Cliente se retiró antes de ser atendido', 2);

-- Caja 3 (cajero1): pedido 6
INSERT INTO caja (id_caja, id_usuario, monto_inicial) VALUES (3, 4, 100.00);
INSERT INTO pedido (id_pedido, id_cliente, id_usuario, modalidad, created_at) VALUES
 (6, 1, 2, 'PARA_LLEVAR', '2026-10-18 09:50:00');
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES (6,1,2),(6,19,2),(6,30,3);
CALL sp_seed_avanzar(6);
CALL sp_registrar_pago(6, 3, 'EFECTIVO', 44.50, 1.50, NULL, 4);
CALL sp_emitir_comprobante(6, 'BOLETA', NULL, NULL, 4, @c);
CALL sp_registrar_entrega(6, 2, 'Rosa Quispe', NULL);
CALL sp_cerrar_caja(3, 146.00, 'Cierre sin novedades');

-- Caja 4 (cajero2) queda ABIERTA: pedidos 7 y 8 (miércoles 21/10, rige el 2x1)
INSERT INTO caja (id_caja, id_usuario, monto_inicial) VALUES (4, 5, 100.00);
INSERT INTO pedido (id_pedido, id_mesa, id_usuario, modalidad, created_at) VALUES
 (7, 2, 2, 'SALON', '2026-10-21 08:45:00');
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES (7,2,2),(7,9,1),(7,24,2),(7,15,2);
CALL sp_seed_avanzar(7);
CALL sp_registrar_pago(7, 4, 'YAPE', 34.00, 0, 'YP-90231', 5);
CALL sp_emitir_comprobante(7, 'BOLETA', NULL, NULL, 5, @c);
CALL sp_registrar_entrega(7, 2, NULL, NULL);

INSERT INTO pedido (id_pedido, id_cliente, id_mesa, id_usuario, modalidad, created_at) VALUES
 (8, 2, 5, 2, 'SALON', '2026-10-21 12:30:00');
INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad) VALUES (8,23,2),(8,14,4),(8,27,2),(8,6,2);
CALL sp_seed_avanzar(8);
CALL sp_registrar_pago(8, 4, 'EFECTIVO', 30.00, 0, NULL, 5);       -- pago dividido
CALL sp_registrar_pago(8, 4, 'TARJETA', 31.00, 3.00, 'POS-778930', 5);
CALL sp_emitir_comprobante(8, 'BOLETA', NULL, NULL, 5, @c);
CALL sp_registrar_entrega(8, 2, 'Luis Huamán', NULL);

DROP PROCEDURE sp_seed_avanzar;

-- Ajuste manual de ejemplo (kardex): merma de 1 brownie
CALL sp_ajustar_stock(28, -1, 'Merma: porción caída en cocina', 1);

-- Reubicar las fechas de la semilla a la semana de ejemplo
CREATE TEMPORARY TABLE tmp_shift AS
  SELECT p.id_pedido, TIMESTAMPDIFF(SECOND, MIN(h.fecha), p.created_at) AS seg
    FROM pedido p JOIN historial_estado_pedido h ON h.id_pedido = p.id_pedido
   GROUP BY p.id_pedido, p.created_at;
UPDATE historial_estado_pedido h JOIN tmp_shift s ON s.id_pedido = h.id_pedido
   SET h.fecha = DATE_ADD(h.fecha, INTERVAL s.seg SECOND);
UPDATE pago x JOIN tmp_shift s ON s.id_pedido = x.id_pedido
   SET x.fecha = DATE_ADD(x.fecha, INTERVAL s.seg SECOND);
UPDATE comprobante x JOIN tmp_shift s ON s.id_pedido = x.id_pedido
   SET x.fecha_emision = DATE_ADD(x.fecha_emision, INTERVAL s.seg SECOND);
UPDATE entrega x JOIN tmp_shift s ON s.id_pedido = x.id_pedido
   SET x.fecha_entrega = DATE_ADD(x.fecha_entrega, INTERVAL s.seg SECOND);
UPDATE movimiento_stock x JOIN tmp_shift s ON s.id_pedido = x.id_pedido
   SET x.fecha = DATE_ADD(x.fecha, INTERVAL s.seg SECOND);
UPDATE anulacion x JOIN tmp_shift s ON s.id_pedido = x.id_pedido
   SET x.fecha = DATE_ADD(x.fecha, INTERVAL s.seg SECOND);
UPDATE pedido p JOIN tmp_shift s ON s.id_pedido = p.id_pedido
   SET p.updated_at = DATE_ADD(p.updated_at, INTERVAL s.seg SECOND);
DROP TEMPORARY TABLE tmp_shift;

UPDATE caja SET fecha_apertura = '2026-10-14 07:30:00', fecha_cierre = '2026-10-14 15:00:00' WHERE id_caja = 1;
UPDATE caja SET fecha_apertura = '2026-10-15 13:30:00', fecha_cierre = '2026-10-15 22:00:00' WHERE id_caja = 2;
UPDATE caja SET fecha_apertura = '2026-10-18 08:00:00', fecha_cierre = '2026-10-18 14:00:00' WHERE id_caja = 3;
UPDATE caja SET fecha_apertura = '2026-10-21 07:30:00' WHERE id_caja = 4;
UPDATE movimiento_stock SET fecha = '2026-10-21 16:00:00' WHERE tipo = 'AJUSTE';
UPDATE cliente c SET ultima_visita =
  (SELECT MAX(p.created_at) FROM pedido p WHERE p.id_cliente = c.id_cliente AND p.estado IN ('PAGADO','ENTREGADO'))
 WHERE c.total_visitas > 0;
SELECT * FROM vw_ventas_por_dia;            CALL sp_ventas_por_rango('2026-10-14','2026-10-21');
SELECT * FROM vw_top_productos;             SELECT * FROM vw_ventas_por_modalidad;
SELECT * FROM vw_ventas_por_medio_pago;     SELECT * FROM vw_ventas_por_cajero_turno;
SELECT * FROM vw_productos_stock_bajo;      SELECT * FROM vw_clientes_frecuentes;
SELECT * FROM vw_resumen_cierre_caja;
CALL sp_calcular_total_pedido(7);  -- subtotal, descuento 2x1, total, base imponible e IGV 18 %
