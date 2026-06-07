package com.ubo.apirest.resources;

import com.ubo.apirest.modelo.ConexionBD;     
import com.ubo.apirest.modelo.Producto;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author branc
 */

//==============================
// GET /api/productos
//Listar los Productos
//==============================
@Path("/productos")
public class ProductoResource {
    
@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarProductos() {

        JSONArray jsonLista = new JSONArray();
        String consultaSQL = "SELECT * FROM productos";

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement consulta = conexion.prepareStatement(consultaSQL);
             ResultSet resultado = consulta.executeQuery()) {

            while (resultado.next()) {
                JSONObject obj = new JSONObject();

                obj.put("idProducto", resultado.getInt("id_producto"));
                obj.put("nombre", resultado.getString("nombre"));
                obj.put("idCategoria", resultado.getInt("id_categoria"));
                obj.put("tipoVenta", resultado.getString("tipo_venta"));
                obj.put("precioBase", resultado.getDouble("precio_base"));
                obj.put("stock", resultado.getDouble("stock"));
                obj.put("extra", resultado.getString("extra"));

                jsonLista.put(obj);
            }

        } catch (SQLException e) {
            JSONObject error = new JSONObject();
            error.put("ERROR", "Error al obtener los productos" + e.getMessage());

            return Response.serverError().entity(error.toString()).build();
        }

        return Response.ok(jsonLista.toString()).build();
    }
    
//==============================
// GET /api/productos
// Consultar producto por id
//==============================
 @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerProductoPorId(@PathParam("id") int id) {

        String consultaSQL = "SELECT * FROM productos WHERE id_producto = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(consultaSQL)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JSONObject error = new JSONObject();
                    error.put("error", "Producto no encontrado");
                    return Response.status(Response.Status.NOT_FOUND).entity(error.toString()).build();
                }

                JSONObject obj = new JSONObject();
                obj.put("idProducto", rs.getInt("id_producto"));
                obj.put("nombre", rs.getString("nombre"));
                obj.put("idCategoria", rs.getInt("id_categoria"));
                obj.put("tipoVenta", rs.getString("tipo_venta"));
                obj.put("precioBase", rs.getDouble("precio_base"));
                obj.put("stock", rs.getDouble("stock"));
                obj.put("extra", rs.getString("extra"));

                return Response.ok(obj.toString()).build();
            }

        } catch (SQLException e) {
            JSONObject error = new JSONObject();
            error.put("ERROR", "Error al obtener producto" + e.getMessage());
            return Response.serverError().entity(error.toString()).build();
        }
    }
    
//==============================
// POST /api/productos/{id}
// Insertar nuevo producto
//==============================
 @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearProducto(Producto producto) {

        String consultaSQL = "INSERT INTO productos " +
                     "(nombre, id_categoria, tipo_venta, precio_base, stock, extra) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(consultaSQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, producto.getNombre());
            ps.setInt(2, producto.getIdCategoria());
            ps.setString(3, producto.getTipoVenta());
            ps.setDouble(4, producto.getPrecioBase());
            ps.setDouble(5, producto.getStock());
            ps.setString(6, producto.getExtra());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                JSONObject error = new JSONObject();
                error.put("Error", "No se pudo insertar el producto");
                return Response.status(Response.Status.BAD_REQUEST).entity(error.toString()).build();
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setIdProducto(rs.getInt(1));
                }
            }

            JSONObject result = new JSONObject();
            result.put("mensaje", "Producto creado correctamente");
            result.put("idProducto", producto.getIdProducto());
            result.put("nombre", producto.getNombre());
            result.put("idCategoria", producto.getIdCategoria());
            result.put("tipoVenta", producto.getTipoVenta());
            result.put("precioBase", producto.getPrecioBase());
            result.put("stock", producto.getStock());
            result.put("extra", producto.getExtra());

            return Response.status(Response.Status.CREATED).entity(result.toString()).build();

        } catch (SQLException e) {
            JSONObject error = new JSONObject();
            error.put("ERROR", "Error al crear producto" + e.getMessage());
            return Response.serverError().entity(error.toString()).build();
        }
    }
    
//==============================
// PUT /api/productos/{id}
// Actualizar producto
//==============================
 @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarProducto(@PathParam("id") int id, Producto producto) {

        String consultaSQL = "UPDATE productos SET nombre=?, id_categoria=?, tipo_venta=?, " +
                     "precio_base=?, stock=?, extra=? WHERE id_producto=?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(consultaSQL)) {

            ps.setString(1, producto.getNombre());
            ps.setInt(2, producto.getIdCategoria());
            ps.setString(3, producto.getTipoVenta());
            ps.setDouble(4, producto.getPrecioBase());
            ps.setDouble(5, producto.getStock());
            ps.setString(6, producto.getExtra());
            ps.setInt(7, id);

            int filas = ps.executeUpdate();
            if (filas == 0) {
                JSONObject error = new JSONObject();
                error.put("error", "Producto no encontrado");
                return Response.status(Response.Status.NOT_FOUND).entity(error.toString()).build();
            }

            producto.setIdProducto(id);

            JSONObject result = new JSONObject();
            result.put("mensaje", "Producto actualizado correctamente");
            result.put("idProducto", producto.getIdProducto());
            result.put("nombre", producto.getNombre());
            result.put("idCategoria", producto.getIdCategoria());
            result.put("tipoVenta", producto.getTipoVenta());
            result.put("precioBase", producto.getPrecioBase());
            result.put("stock", producto.getStock());
            result.put("extra", producto.getExtra());

            return Response.ok(result.toString()).build();

        } catch (SQLException e) {
            JSONObject error = new JSONObject();
            error.put("error", "Error al actualizar producto");
            error.put("detalle", e.getMessage());
            return Response.serverError().entity(error.toString()).build();
        }
    }

//==============================
// DELETE /api/productos/{id}
// Eliminar producto
//==============================
@DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarProducto(@PathParam("id") int id) {

        String consultaSQL = "DELETE FROM productos WHERE id_producto=?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(consultaSQL)) {

            ps.setInt(1, id);
            int filas = ps.executeUpdate();

            if (filas == 0) {
                JSONObject error = new JSONObject();
                error.put("ERROR", "Producto no encontrado");
                return Response.status(Response.Status.NOT_FOUND).entity(error.toString()).build();
            }

            JSONObject result = new JSONObject();
            result.put("mensaje", "Producto eliminado correctamente");
            result.put("idProducto", id);

            return Response.ok(result.toString()).build();

        } catch (SQLException e) {
            JSONObject error = new JSONObject();
            error.put("error", "Error al eliminar producto" + e.getMessage());
            return Response.serverError().entity(error.toString()).build();
        }
    }    
  
    
    //final clase
}

