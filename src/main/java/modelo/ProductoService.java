package modelo;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductoService {
    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;

    public ProductoService(ProductoDAO productoDAO, CategoriaDAO categoriaDAO) {
        this.productoDAO = productoDAO;
        this.categoriaDAO = categoriaDAO;
    }

    public List<Producto> carta() throws SQLException {
        return productoDAO.listar(true);
    }

    public List<Producto> todos(Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.ADMIN);
        return productoDAO.listar(false);
    }

    public List<Categoria> categorias() throws SQLException {
        return categoriaDAO.listar();
    }

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

    public void guardar(Producto p, Usuario usuario) throws SQLException {
        Autorizacion.exigir(usuario, Autorizacion.ADMIN);
        p.setNombre(p.getNombre() == null ? null : p.getNombre().trim());
        validar(p);
        productoDAO.guardar(p);
    }
}
