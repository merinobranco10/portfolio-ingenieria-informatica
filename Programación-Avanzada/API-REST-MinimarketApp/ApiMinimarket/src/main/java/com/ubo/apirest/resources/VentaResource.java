package com.ubo.apirest.resources;

import com.ubo.apirest.modelo.ConexionBD;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/ventas")
public class VentaResource {

//==============================
// POST /api/ventas
// Registrar venta completa
//==============================
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarVenta(String body) {

        JSONObject json = new JSONObject(body);

        double subtotal = json.getDouble("subtotal");
        double iva      = json.getDouble("iva");
        double total    = json.getDouble("total");
        int idUsuario   = json.getInt("idUsuario");
        JSONArray detalles = json.getJSONArray("detalles");

        String sqlVenta = "INSERT INTO ventas (subtotal, iva, total_final, id_usuario, fecha) " +
                          "VALUES (?, ?, ?, ?, NOW())";

        String sqlDetalle = "INSERT INTO detalle_venta " +
                            "(id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                            "VALUES (?, ?, ?, ?, ?)";

        String sqlActualizarStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";

        try (Connection con = ConexionBD.conectar()) {

            con.setAutoCommit(false);

            // 1) Insertar  venta
            PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setDouble(1, subtotal);
            psVenta.setDouble(2, iva);
            psVenta.setDouble(3, total);
            psVenta.setInt(4, idUsuario);
            psVenta.executeUpdate();

            int idVenta;
            try (ResultSet rs = psVenta.getGeneratedKeys()) {
                if (!rs.next()) {
                    con.rollback();
                    JSONObject error = new JSONObject();
                    error.put("ERROR", "No se pudo obtener el ID de la venta");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.toString()).build();
                }
                idVenta = rs.getInt(1);
            }

            // 2) Insertar detalle + actualizar stock
            PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
            PreparedStatement psStock   = con.prepareStatement(sqlActualizarStock);

            for (int i = 0; i < detalles.length(); i++) {
                JSONObject d = detalles.getJSONObject(i);

                int    idProducto     = d.getInt("idProducto");
                double cantidad       = d.getDouble("cantidad");
                double precioUnitario = d.getDouble("precioUnitario");
                double subDetalle     = precioUnitario * cantidad;

                // detalle_venta
                psDetalle.setInt(1, idVenta);
                psDetalle.setInt(2, idProducto);
                psDetalle.setDouble(3, cantidad);
                psDetalle.setDouble(4, precioUnitario);
                psDetalle.setDouble(5, subDetalle);
                psDetalle.addBatch();

                // actualizar stock
                psStock.setDouble(1, cantidad);
                psStock.setInt(2, idProducto);
                psStock.addBatch();
            }

            psDetalle.executeBatch();
            psStock.executeBatch();

            con.commit();

            JSONObject result = new JSONObject();
            result.put("mensaje", "Venta registrada correctamente");
            result.put("idVenta", idVenta);
            result.put("subtotal", subtotal);
            result.put("iva", iva);
            result.put("total", total);

            return Response.ok(result.toString()).build();

        } catch (SQLException e) {
            JSONObject error = new JSONObject();
            error.put("error", "Error al registrar venta");
            error.put("detalle", e.getMessage());
            return Response.serverError().entity(error.toString()).build();
        }
    }

// ==============================
//GET /api/ventas
//Listar ventas (vista_reporte_ventas)
//==============================
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarVentas() {

        String sql = "SELECT * FROM vista_reporte_ventas";
        JSONArray jsonLista = new JSONArray();

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("idVenta", rs.getInt("id_venta"));
                obj.put("fecha", rs.getString("fecha"));
                obj.put("usuario", rs.getString("usuario"));
                obj.put("subtotal", rs.getDouble("subtotal"));
                obj.put("iva", rs.getDouble("iva"));
                obj.put("totalFinal", rs.getDouble("total_final"));
                jsonLista.put(obj);
            }

            return Response.ok(jsonLista.toString()).build();

        } catch (SQLException e) {
            JSONObject error = new JSONObject();
            error.put("ERROR", "Error al listar ventas" + e.getMessage());
            return Response.serverError().entity(error.toString()).build();
        }
    }
}
