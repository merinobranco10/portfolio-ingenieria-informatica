package com.ubo.apirest.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class RootResource {

    @GET
    @Produces("text/plain; charset=UTF-8")
    public Response info() {

        String texto =
                "==========================================================\n" +
                "      API Minimarket – Universidad Bernardo O'Higgins\n" +
                "==========================================================\n" +
                "\n" +
                "Estado: OK (API funcionando correctamente)\n" +
                "\n" +
                "Endpoints disponibles:\n" +
                "  • /api/productos   -> Gestión de productos\n" +
                "  • /api/usuarios    -> Gestión de usuarios\n" +
                "  • /api/ventas      -> Gestión de ventas\n" +
                "\n" +
                "Formato de datos: JSON\n" +
                "Métodos soportados: GET, POST, PUT, DELETE\n" +
                "\n" +
                "Integrantes del proyecto:\n" +
                "  - Branco Merino\n" +
                "  - Matías Díaz\n" +
                "  - Gerardo González\n" +
                "  - Matías Sepúlveda\n" +
                "\n" +
                "Descripción:\n" +
                "Esta API REST es parte de la extensión del proyecto\n"
                + "Minimarket,permitiendo la integración de la aplicación\n" +
                "con servicios web usando Java, MySQL y arquitectura MVC.\n" +
                "\n" +
                "==========================================================\n";

        return Response
                .ok(texto)
                .type("text/plain; charset=UTF-8")
                .build();
    }
}
