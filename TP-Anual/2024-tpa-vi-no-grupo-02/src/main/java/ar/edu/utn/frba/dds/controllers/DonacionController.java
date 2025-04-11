package ar.edu.utn.frba.dds.controllers;

import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DonacionController implements WithSimplePersistenceUnit, TransactionalOps {
    public void show(@NotNull Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("usuario_id",ctx.sessionAttribute("usuario_id"));
        model.put("esAdmin",ctx.sessionAttribute("es_admin"));

        // Valida que el usuario esté logueado
        Integer usuarioId = ctx.sessionAttribute("usuario_id");
        if (usuarioId == null) {
            ctx.result("Usuario no autenticado");
            ctx.redirect("/login");
        }

        ctx.render("donacion.hbs", model);
    }

    public void redirect(Context ctx) {
        String tipoDonacion = ctx.formParam("donacionTipo");

        switch (tipoDonacion) {
            case "heladera":
                ctx.redirect("/donacion/heladera");
                break;
            case "dinero":
                ctx.redirect("/donacion/dinero");
                break;
            case "vianda":
                ctx.redirect("/donacion/vianda");
                break;
            case "distribucionViandas":
                ctx.redirect("/donacion/distribucion-viandas");
                break;
            default:
                ctx.redirect("/donacion");
        }
    }

}
