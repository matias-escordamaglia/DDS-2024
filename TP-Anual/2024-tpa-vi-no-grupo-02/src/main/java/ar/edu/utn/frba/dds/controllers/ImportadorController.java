package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.colaboradores.RepoColaboradores;
import ar.edu.utn.frba.dds.model.importador.ImportadorColaboracion;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportadorController {
    public void show(Context ctx){
        Map<String, Object> model = new HashMap<>();
        model.put("usuario_id",ctx.sessionAttribute("usuario_id"));
        model.put("esAdmin",ctx.sessionAttribute("es_admin"));

        // Valida que el usuario esté logueado
        Integer usuarioId = ctx.sessionAttribute("usuario_id");
        if (usuarioId == null) {
            ctx.result("Usuario no autenticado");
            ctx.redirect("/login");
        }
        ctx.render("importacion.hbs", model);
}

public void create(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("usuario_id",ctx.sessionAttribute("usuario_id"));
    model.put("esAdmin",ctx.sessionAttribute("es_admin"));

    // Valida que el usuario esté logueado
    Integer usuarioId = ctx.sessionAttribute("usuario_id");
    if (usuarioId == null) {
        ctx.result("Usuario no autenticado");
        ctx.redirect("/login");
    }

    List<String> mensajesError;
    List<String> colaboraciones = new ArrayList<>();
    try {

        Map<String, List<String>> resultado = new ImportadorColaboracion().importarColaboracion(
                RepoColaboradores.getInstance(),
                ctx.uploadedFiles("csvFile").get(0).content(),
                true
        );
        // Obtener el número de registros importados y errores
        mensajesError = resultado.get("Errores");
        colaboraciones = resultado.get("Colaboraciones");
        if(!mensajesError.isEmpty()){
            model.put("mensajesError", mensajesError);
        }
    } catch (Exception e) {
        e.printStackTrace();
        model.put("mensajesError", "Error al importar el archivo");
    }
    finally {
        if(colaboraciones != null && !colaboraciones.isEmpty()){
            model.put("cantidadColaboraciones", colaboraciones.get(0));
        }
        ctx.render("importacion.hbs", model);
    }

    }


}
