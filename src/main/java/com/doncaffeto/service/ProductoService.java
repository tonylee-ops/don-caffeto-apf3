package com.doncaffeto.service;

import com.doncaffeto.dao.CategoriaDAO;
import com.doncaffeto.dao.ProductoDAO;
import com.doncaffeto.model.Categoria;
import com.doncaffeto.model.Producto;
import com.doncaffeto.model.Usuario;
import com.doncaffeto.util.Validador;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Carta de productos. Solo el administrador puede crear o modificar productos.
 *
 * @author Equipo Don Caffeto
 */
public class ProductoService {

    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;

    /**
     * @param productoDAO  acceso a productos
     * @param categoriaDAO acceso a categorías
     */
    public ProductoService(ProductoDAO productoDAO, CategoriaDAO categoriaDAO) {
        this.productoDAO = productoDAO;
        this.categoriaDAO = categoriaDAO;
    }

    /**
     * @return productos disponibles para vender
     * @throws SQLException si falla la base de datos
     */
    public List<Producto> carta() throws SQLException {
        return productoDAO.listar(true);
    }

    /**
     * @param usuario administrador
     * @return todos los productos, también los no disponibles
     * @throws SQLException si falla la base de datos
     */
    public List<Producto> todos(Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.ADMIN);
        return productoDAO.listar(false);
    }

    /**
     * @return categorías de la carta
     * @throws SQLException si falla la base de datos
     */
    public List<Categoria> categorias() throws SQLException {
        return categoriaDAO.listar();
    }

    /**
     * Revisa los datos del producto.
     *
     * @param p producto
     * @throws IllegalArgumentException con el dato incorrecto
     */
    public void validar(Producto p) {
        if (Validador.estaVacio(p.getNombre())) {
            throw new IllegalArgumentException("Escribe el nombre del producto.");
        }
        if (p.getIdCategoria() <= 0) {
            throw new IllegalArgumentException("Elige una categoría.");
        }
        if (p.getPrecio() == null || p.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que 0.");
        }
    }

    /**
     * @param p       producto nuevo (código 0) o existente
     * @param usuario administrador
     * @throws SQLException si falla la base de datos (por ejemplo, nombre repetido)
     */
    public void guardar(Producto p, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.ADMIN);
        p.setNombre(p.getNombre() == null ? null : p.getNombre().trim());
        validar(p);
        productoDAO.guardar(p);
    }
}
