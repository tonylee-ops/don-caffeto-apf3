USE don_caffeto_apf3;

-- 1. EMPLEADOS CON SU CARGO
SELECT u.id_usuario AS id, u.nombres AS empleado, u.username AS usuario,
       r.nombre AS cargo, r.descripcion AS funciones,
       IF(u.activo = 1, 'Activo', 'Inactivo') AS estado
  FROM usuarios u
  JOIN roles r ON r.id_rol = u.id_rol
 ORDER BY r.id_rol;

-- 2. ESTADO DE LAS MESAS
SELECT m.numero AS mesa, m.capacidad AS personas,
       CASE m.estado WHEN 'LIBRE' THEN 'Libre (gris)'
                     WHEN 'OCUPADA' THEN 'En cocina (naranja)'
                     WHEN 'CUENTA' THEN 'Cuenta pedida (verde)' END AS estado,
       fn_minutos_ocupada(m.id_mesa) AS minutos_ocupada,
       p.id_pedido AS pedido, p.total
  FROM mesas m
  LEFT JOIN pedidos p ON p.id_mesa = m.id_mesa AND p.estado NOT IN ('PAGADO', 'CANCELADO')
 ORDER BY m.numero;

-- 3. RESUMEN DE MESAS
SELECT estado, COUNT(*) AS cantidad FROM mesas GROUP BY estado;

-- 4. PEDIDOS DEL DÍA
SELECT p.id_pedido AS pedido, TIME(p.creado_en) AS hora, p.modalidad,
       IFNULL(CONCAT('Mesa ', m.numero), '-') AS mesa,
       fn_estado_texto(p.estado) AS estado, u.nombres AS mesero, p.total
  FROM pedidos p
  JOIN usuarios u ON u.id_usuario = p.id_usuario
  LEFT JOIN mesas m ON m.id_mesa = p.id_mesa
 WHERE DATE(p.creado_en) = CURDATE()
 ORDER BY p.id_pedido;

-- 5. PEDIDOS POR ESTADO
SELECT fn_estado_texto(estado) AS estado, COUNT(*) AS pedidos, SUM(total) AS importe
  FROM pedidos GROUP BY estado;

-- 6. DETALLE DE UN PEDIDO (cambia el 6 por el número de pedido)
SELECT d.id_pedido AS pedido, pr.nombre AS producto, c.nombre AS categoria,
       d.cantidad, d.precio_unitario, d.subtotal,
       IFNULL(GROUP_CONCAT(o.texto SEPARATOR ', '), '') AS notas
  FROM detalle_pedido d
  JOIN productos pr ON pr.id_producto = d.id_producto
  JOIN categorias c ON c.id_categoria = pr.id_categoria
  LEFT JOIN observaciones_detalle o ON o.id_detalle = d.id_detalle
 WHERE d.id_pedido = 6
 GROUP BY d.id_detalle, pr.nombre, c.nombre, d.cantidad, d.precio_unitario, d.subtotal;

-- 7. PEDIDOS EN COCINA
SELECT p.id_pedido AS pedido, fn_estado_texto(p.estado) AS estado,
       IFNULL(CONCAT('Mesa ', m.numero), p.modalidad) AS destino,
       TIMESTAMPDIFF(MINUTE, p.creado_en, NOW()) AS minutos_esperando
  FROM pedidos p
  LEFT JOIN mesas m ON m.id_mesa = p.id_mesa
 WHERE p.estado IN ('PENDIENTE', 'EN_PREPARACION')
 ORDER BY p.creado_en;

-- 8. COBROS REALIZADOS
SELECT pg.id_pago AS cobro, pg.id_pedido AS pedido, pg.creado_en AS fecha,
       pg.metodo, pg.monto, pg.recibido, pg.vuelto, u.nombres AS cajero
  FROM pagos pg
  JOIN usuarios u ON u.id_usuario = pg.id_usuario
 ORDER BY pg.creado_en DESC;

-- 9. VENTAS CON IGV
SELECT v.id_venta AS venta, v.id_pedido AS pedido, v.fecha,
       v.subtotal AS valor_venta, v.igv, v.total
  FROM ventas v
 ORDER BY v.fecha DESC;

-- 10. TOTAL VENDIDO HOY Y POR MÉTODO DE PAGO
SELECT fn_ventas_del_dia(CURDATE()) AS vendido_hoy;

SELECT pg.metodo, COUNT(*) AS cobros, SUM(pg.monto) AS total
  FROM pagos pg
 WHERE DATE(pg.creado_en) = CURDATE()
 GROUP BY pg.metodo;

-- 11. PRODUCTOS MÁS VENDIDOS
SELECT pr.nombre AS producto, SUM(d.cantidad) AS unidades, SUM(d.subtotal) AS importe
  FROM detalle_pedido d
  JOIN ventas v ON v.id_pedido = d.id_pedido
  JOIN productos pr ON pr.id_producto = d.id_producto
 GROUP BY pr.nombre
 ORDER BY unidades DESC;

-- 12. CARTA POR CATEGORÍA
SELECT c.nombre AS categoria, pr.nombre AS producto, pr.precio,
       IF(pr.disponible = 1, 'Sí', 'No') AS disponible
  FROM productos pr
  JOIN categorias c ON c.id_categoria = pr.id_categoria
 ORDER BY c.nombre, pr.nombre;

-- 13. CONTRASEÑAS CIFRADAS (seguridad: no se guardan en texto plano)
SELECT username, password_hash FROM usuarios;
