package com.ubo.apirest.resources;

import com.ubo.apirest.modelo.ConexionBD;
import com.ubo.apirest.modelo.Usuario;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.*;
import org.json.JSONObject;

/**
 *
 * @author branc
 */

@Path("/usuarios")
public class UsuarioResource {

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Usuario usuario) {

        String username = usuario.getUsername();
        String password = usuario.getPassword();

        String sql = "SELECT * FROM usuarios WHERE username=? AND password=? AND activo=1";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    JSONObject error = new JSONObject();
                    error.put("error", "Credenciales incorrectas");
                    return Response.status(Response.Status.UNAUTHORIZED).entity(error.toString()).build();
                }

                JSONObject result = new JSONObject();
                result.put("idUsuario", rs.getInt("id_usuario"));
                result.put("username", rs.getString("username"));
                result.put("rol", rs.getString("rol"));
                result.put("activo", rs.getBoolean("activo"));

                return Response.ok(result.toString()).build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", "Error interno");
            error.put("detalle", e.toString());
            return Response.serverError().entity(error.toString()).build();
        }
    }
}