DROP DATABASE IF EXISTS don_caffeto_apf3;
CREATE DATABASE don_caffeto_apf3 CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE don_caffeto_apf3;

CREATE TABLE roles (
  id_rol      INT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(20) NOT NULL,
  descripcion VARCHAR(100) NOT NULL,
  CONSTRAINT uq_roles_nombre UNIQUE (nombre)
);

CREATE TABLE usuarios (
  id_usuario    INT AUTO_INCREMENT PRIMARY KEY,
  id_rol        INT NOT NULL,
  username      VARCHAR(30) NOT NULL,
  password_hash CHAR(60) NOT NULL,
  nombres       VARCHAR(80) NOT NULL,
  activo        TINYINT(1) NOT NULL DEFAULT 1,
  creado_en     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_usuarios_username UNIQUE (username),
  CONSTRAINT ck_usuarios_hash CHECK (password_hash LIKE '$2%'),
  CONSTRAINT fk_usuarios_rol FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
);

CREATE TABLE mesas (
  id_mesa       INT AUTO_INCREMENT PRIMARY KEY,
  numero        INT NOT NULL,
  capacidad     INT NOT NULL,
  estado        ENUM('LIBRE', 'OCUPADA', 'CUENTA') NOT NULL DEFAULT 'LIBRE',
  ocupada_desde DATETIME NULL,
  CONSTRAINT uq_mesas_numero UNIQUE (numero),
  CONSTRAINT ck_mesas_capacidad CHECK (capacidad > 0),
  CONSTRAINT ck_mesas_hora CHECK ((estado = 'LIBRE' AND ocupada_desde IS NULL)
                               OR (estado <> 'LIBRE' AND ocupada_desde IS NOT NULL))
);

CREATE TABLE categorias (
  id_categoria INT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(50) NOT NULL,
  CONSTRAINT uq_categorias_nombre UNIQUE (nombre)
);

CREATE TABLE productos (
  id_producto  INT AUTO_INCREMENT PRIMARY KEY,
  id_categoria INT NOT NULL,
  nombre       VARCHAR(80) NOT NULL,
  precio       DECIMAL(8,2) NOT NULL,
  disponible   TINYINT(1) NOT NULL DEFAULT 1,
  CONSTRAINT uq_productos_nombre UNIQUE (nombre),
  CONSTRAINT ck_productos_precio CHECK (precio > 0),
  CONSTRAINT fk_productos_categoria FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria)
);

CREATE TABLE pedidos (
  id_pedido  INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  id_mesa    INT NULL,
  modalidad  ENUM('SALON', 'PARA_LLEVAR', 'DELIVERY') NOT NULL,
  estado     ENUM('PENDIENTE', 'EN_PREPARACION', 'LISTO', 'PAGADO', 'CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
  total      DECIMAL(10,2) NOT NULL DEFAULT 0,
  direccion  VARCHAR(150) NULL,
  telefono   CHAR(9) NULL,
  creado_en  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_pedidos_total CHECK (total >= 0),
  CONSTRAINT ck_pedidos_mesa CHECK ((modalidad = 'SALON' AND id_mesa IS NOT NULL)
                                 OR (modalidad <> 'SALON' AND id_mesa IS NULL)),
  CONSTRAINT ck_pedidos_delivery CHECK (modalidad <> 'DELIVERY'
                                 OR (direccion IS NOT NULL AND telefono IS NOT NULL)),
  CONSTRAINT fk_pedidos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario),
  CONSTRAINT fk_pedidos_mesa FOREIGN KEY (id_mesa) REFERENCES mesas (id_mesa)
);

CREATE TABLE detalle_pedido (
  id_detalle      INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido       INT NOT NULL,
  id_producto     INT NOT NULL,
  cantidad        INT NOT NULL,
  precio_unitario DECIMAL(8,2) NOT NULL,
  subtotal        DECIMAL(10,2) AS (cantidad * precio_unitario) STORED,
  CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),
  CONSTRAINT ck_detalle_precio CHECK (precio_unitario > 0),
  CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido) ON DELETE CASCADE,
  CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES productos (id_producto)
);

CREATE TABLE observaciones_pedido (
  id_observacion INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido      INT NOT NULL,
  id_usuario     INT NOT NULL,
  texto          VARCHAR(200) NOT NULL,
  creado_en      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_obs_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido) ON DELETE CASCADE,
  CONSTRAINT fk_obs_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
);

CREATE TABLE observaciones_detalle (
  id_observacion INT AUTO_INCREMENT PRIMARY KEY,
  id_detalle     INT NOT NULL,
  texto          VARCHAR(150) NOT NULL,
  CONSTRAINT fk_obs_detalle FOREIGN KEY (id_detalle) REFERENCES detalle_pedido (id_detalle) ON DELETE CASCADE
);

CREATE TABLE pagos (
  id_pago    INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido  INT NOT NULL,
  id_usuario INT NOT NULL,
  metodo     ENUM('EFECTIVO', 'YAPE', 'PLIN', 'TARJETA') NOT NULL,
  monto      DECIMAL(10,2) NOT NULL,
  recibido   DECIMAL(10,2) NOT NULL,
  vuelto     DECIMAL(10,2) NOT NULL DEFAULT 0,
  creado_en  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_pagos_pedido UNIQUE (id_pedido),
  CONSTRAINT ck_pagos_montos CHECK (monto > 0 AND recibido >= monto AND vuelto = recibido - monto),
  CONSTRAINT fk_pagos_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido),
  CONSTRAINT fk_pagos_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
);

CREATE TABLE ventas (
  id_venta  INT AUTO_INCREMENT PRIMARY KEY,
  id_pedido INT NOT NULL,
  id_pago   INT NOT NULL,
  subtotal  DECIMAL(10,2) NOT NULL,
  igv       DECIMAL(10,2) NOT NULL,
  total     DECIMAL(10,2) NOT NULL,
  fecha     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_ventas_pedido UNIQUE (id_pedido),
  CONSTRAINT ck_ventas_total CHECK (total = subtotal + igv),
  CONSTRAINT fk_ventas_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido),
  CONSTRAINT fk_ventas_pago FOREIGN KEY (id_pago) REFERENCES pagos (id_pago)
);

CREATE INDEX ix_pedidos_estado ON pedidos (estado);
CREATE INDEX ix_pedidos_fecha ON pedidos (creado_en);
CREATE INDEX ix_ventas_fecha ON ventas (fecha);

INSERT INTO roles (id_rol, nombre, descripcion) VALUES
 (1, 'ADMIN',  'Administrador: productos, usuarios y reportes'),
 (2, 'MESERO', 'Registra pedidos'),
 (3, 'COCINA', 'Prepara pedidos y actualiza su estado'),
 (4, 'CAJERO', 'Cobra los pedidos');

INSERT INTO usuarios (id_usuario, id_rol, username, password_hash, nombres) VALUES
 (1, 1, 'admin',   '$2a$10$4.qdqEW7Bd.fJh8qpj4XFOaAChlyQf00uPo1E32MrS5sdbf5did3u', 'Rosa Quispe'),
 (2, 2, 'mesero1', '$2a$10$.itMnI.RAXZd7hZrx13i.e3u1TCDiLr2oFtrM990JgKD5WK73EvqO', 'Julio Ramos'),
 (3, 3, 'cocina1', '$2a$10$v/O89ndTPgM0rHc2EmqYAe2ZWIsWVYRFOAT9yyryPhM/tI5RkzsBy', 'Marta Gonzales'),
 (4, 4, 'cajero1', '$2a$10$OkTFwtkCg2aIBzwpBwVAru3vJrpXFRIWuLKXB/rz1x.3w6Hxe/h4q', 'Sofía Camayo');

INSERT INTO mesas (numero, capacidad) VALUES
 (1, 2), (2, 2), (3, 4), (4, 4), (5, 4), (6, 6), (7, 6);

INSERT INTO categorias (id_categoria, nombre) VALUES
 (1, 'Cafés calientes'), (2, 'Bebidas frías'), (3, 'Sándwiches'), (4, 'Desayunos'), (5, 'Postres');

INSERT INTO productos (id_producto, id_categoria, nombre, precio) VALUES
 (1, 1, 'Espresso', 5.00), (2, 1, 'Americano', 6.00), (3, 1, 'Capuchino', 9.00),
 (4, 1, 'Latte', 10.00), (5, 1, 'Mocaccino', 11.00),
 (6, 2, 'Frappé de café', 12.00), (7, 2, 'Jugo de papaya', 7.00), (8, 2, 'Limonada', 6.00),
 (9, 2, 'Agua mineral', 3.00),
 (10, 3, 'Sándwich de pollo', 10.00), (11, 3, 'Sándwich mixto', 8.00), (12, 3, 'Empanada de carne', 6.00),
 (13, 4, 'Tostadas con palta', 9.00), (14, 4, 'Desayuno americano', 16.00), (15, 4, 'Tamal con café', 12.00),
 (16, 5, 'Brownie', 6.00), (17, 5, 'Pie de limón', 8.00), (18, 5, 'Torta de chocolate', 9.00);

INSERT INTO pedidos (id_pedido, id_usuario, id_mesa, modalidad, estado, total, direccion, telefono, creado_en) VALUES
 (1, 2, 3, 'SALON', 'PAGADO', 22.00, NULL, NULL, NOW() - INTERVAL 90 MINUTE),
 (2, 2, NULL, 'DELIVERY', 'LISTO', 28.00, 'Jr. Los Cafetales 120', '964333444', NOW() - INTERVAL 25 MINUTE),
 (3, 2, 5, 'SALON', 'EN_PREPARACION', 21.00, NULL, NULL, NOW() - INTERVAL 12 MINUTE),
 (4, 2, NULL, 'PARA_LLEVAR', 'PENDIENTE', 17.00, NULL, NULL, NOW() - INTERVAL 3 MINUTE),
 (5, 2, 2, 'SALON', 'LISTO', 16.00, NULL, NULL, NOW() - INTERVAL 35 MINUTE);
UPDATE mesas SET estado = 'OCUPADA', ocupada_desde = NOW() - INTERVAL 12 MINUTE WHERE id_mesa = 5;
UPDATE mesas SET estado = 'CUENTA',  ocupada_desde = NOW() - INTERVAL 35 MINUTE WHERE id_mesa = 2;

INSERT INTO detalle_pedido (id_detalle, id_pedido, id_producto, cantidad, precio_unitario) VALUES
 (1, 1, 2, 2, 6.00), (2, 1, 10, 1, 10.00),
 (3, 2, 14, 1, 16.00), (4, 2, 6, 1, 12.00),
 (5, 3, 3, 1, 9.00), (6, 3, 13, 1, 9.00), (7, 3, 9, 1, 3.00),
 (8, 4, 4, 1, 10.00), (9, 4, 7, 1, 7.00),
 (10, 5, 4, 1, 10.00), (11, 5, 16, 1, 6.00);

INSERT INTO observaciones_pedido (id_pedido, id_usuario, texto) VALUES
 (2, 2, 'Tocar el timbre dos veces'),
 (3, 2, 'Cliente con apuro');

INSERT INTO observaciones_detalle (id_detalle, texto) VALUES
 (5, 'Sin azúcar'),
 (8, 'Con leche deslactosada');

INSERT INTO pagos (id_pago, id_pedido, id_usuario, metodo, monto, recibido, vuelto) VALUES
 (1, 1, 4, 'EFECTIVO', 22.00, 50.00, 28.00);

INSERT INTO ventas (id_pedido, id_pago, subtotal, igv, total, fecha) VALUES
 (1, 1, 18.64, 3.36, 22.00, NOW() - INTERVAL 60 MINUTE);

DELIMITER $$

CREATE FUNCTION fn_igv(p_total DECIMAL(10,2))
RETURNS DECIMAL(10,2)
DETERMINISTIC NO SQL
BEGIN
  RETURN p_total - ROUND(p_total / 1.18, 2);
END$$

CREATE FUNCTION fn_valor_venta(p_total DECIMAL(10,2))
RETURNS DECIMAL(10,2)
DETERMINISTIC NO SQL
BEGIN
  RETURN ROUND(p_total / 1.18, 2);
END$$

CREATE FUNCTION fn_total_pedido(p_id_pedido INT)
RETURNS DECIMAL(10,2)
NOT DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE v_total DECIMAL(10,2);
  SELECT COALESCE(SUM(subtotal), 0) INTO v_total FROM detalle_pedido WHERE id_pedido = p_id_pedido;
  RETURN v_total;
END$$

CREATE FUNCTION fn_minutos_ocupada(p_id_mesa INT)
RETURNS INT
NOT DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE v_desde DATETIME;
  SELECT ocupada_desde INTO v_desde FROM mesas WHERE id_mesa = p_id_mesa;
  RETURN COALESCE(TIMESTAMPDIFF(MINUTE, v_desde, NOW()), 0);
END$$

CREATE FUNCTION fn_ventas_del_dia(p_fecha DATE)
RETURNS DECIMAL(10,2)
NOT DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE v_total DECIMAL(10,2);
  SELECT COALESCE(SUM(total), 0) INTO v_total FROM ventas WHERE DATE(fecha) = p_fecha;
  RETURN v_total;
END$$

CREATE FUNCTION fn_unidades_vendidas(p_id_producto INT, p_desde DATE, p_hasta DATE)
RETURNS INT
NOT DETERMINISTIC READS SQL DATA
BEGIN
  DECLARE v_unidades INT;
  SELECT COALESCE(SUM(d.cantidad), 0) INTO v_unidades
    FROM detalle_pedido d JOIN ventas v ON v.id_pedido = d.id_pedido
   WHERE d.id_producto = p_id_producto AND DATE(v.fecha) BETWEEN p_desde AND p_hasta;
  RETURN v_unidades;
END$$

CREATE FUNCTION fn_estado_texto(p_estado VARCHAR(20))
RETURNS VARCHAR(20)
DETERMINISTIC NO SQL
BEGIN
  RETURN CASE p_estado
    WHEN 'PENDIENTE' THEN 'Pendiente'
    WHEN 'EN_PREPARACION' THEN 'En preparación'
    WHEN 'LISTO' THEN 'Listo'
    WHEN 'PAGADO' THEN 'Pagado'
    WHEN 'CANCELADO' THEN 'Cancelado'
    ELSE p_estado
  END;
END$$

DELIMITER ;

CREATE VIEW vw_mesas AS
SELECT m.numero, m.capacidad, m.estado, fn_minutos_ocupada(m.id_mesa) AS minutos_ocupada,
       p.id_pedido, p.total
  FROM mesas m
  LEFT JOIN pedidos p ON p.id_mesa = m.id_mesa AND p.estado NOT IN ('PAGADO', 'CANCELADO');

CREATE VIEW vw_pedidos AS
SELECT p.id_pedido, p.creado_en, p.modalidad, fn_estado_texto(p.estado) AS estado,
       u.nombres AS mesero, fn_valor_venta(p.total) AS valor_venta, fn_igv(p.total) AS igv, p.total
  FROM pedidos p JOIN usuarios u ON u.id_usuario = p.id_usuario;

CREATE USER IF NOT EXISTS 'app_apf3'@'localhost' IDENTIFIED BY '1234';
ALTER USER 'app_apf3'@'localhost' IDENTIFIED BY '1234';
GRANT SELECT, INSERT, UPDATE, EXECUTE ON don_caffeto_apf3.* TO 'app_apf3'@'localhost';
FLUSH PRIVILEGES;

SELECT 'Base don_caffeto_apf3 creada' AS resultado, COUNT(*) AS usuarios FROM usuarios;
