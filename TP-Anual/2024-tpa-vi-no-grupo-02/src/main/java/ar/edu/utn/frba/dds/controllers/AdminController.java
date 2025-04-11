package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.colaboradores.ColaboradorFisico;
import ar.edu.utn.frba.dds.model.colaboradores.RepoColaboradores;
import io.javalin.http.Context;

import javax.persistence.EntityTransaction;
import java.util.*;

public class AdminController {
    public void show(Context ctx){
        Map<String, Object> model = new HashMap<>();
        model.put("usuario_id", ctx.sessionAttribute("usuario_id"));
        model.put("esAdmin", ctx.sessionAttribute("es_admin"));

        // Valida que el usuario esté logueado
        Integer usuarioId = ctx.sessionAttribute("usuario_id");
        if (usuarioId == null) {
            ctx.result("Usuario no autenticado");
            ctx.redirect("/login");
        }

        int offset = 0;
        int limite = 4;

        String offsetString = ctx.queryParam("offset");
        if(offsetString != null){
            offset = Integer.parseInt(offsetString);
        }

        List<ColaboradorFisico> colaboradorFisicos = RepoColaboradores.getInstance().todosLosFisicos();
        List<ColaboradorFisico> colaboradoresParaMostrar = new ArrayList<>();

        if (colaboradorFisicos != null && !colaboradorFisicos.isEmpty()) {
            for (int i = offset; i < offset + limite && i < colaboradorFisicos.size(); i++) {
                colaboradoresParaMostrar.add(colaboradorFisicos.get(i));
            }
            model.put("colaboradores", colaboradoresParaMostrar);
        }

        int offsetSiguiente = offset + limite;
        int offsetAnterior = offset - limite;

        // Lógica para el botón "Siguiente"
        if (offsetSiguiente < colaboradorFisicos.size()) {
            model.put("siguiente", "/admin?offset=" + offsetSiguiente);
        } else {
            model.put("siguiente", null);  // Si no hay página siguiente, se establece null
        }

        // Lógica para el botón "Anterior"
        if (offsetAnterior >= 0) {
            model.put("anterior", "/admin?offset=" + offsetAnterior);
        } else {
            model.put("anterior", null);  // Si el offset es 0 o negativo, no hay página anterior
        }

        ctx.render("admin-rol.hbs", model);
    }


    public void redirect(Context ctx) {
        try {
            // Itera sobre los parámetros enviados
            for (String key : ctx.formParamMap().keySet()) {
                if (key.startsWith("colaborador_")) {
                    // Obtiene el ID del colaborador desde el nombre del parámetro
                    int colaboradorId = Integer.parseInt(key.replace("colaborador_", ""));
                    boolean esAdmin = Boolean.parseBoolean(ctx.formParam(key));

                    ColaboradorFisico colaborador = RepoColaboradores.getInstance()
                            .buscarColaboradorFisicoPorID(colaboradorId);

                    if (colaborador != null) {
                        colaborador.setEsAdmin(esAdmin);
                        EntityTransaction tx = RepoColaboradores.getInstance().getTransaction();
                        tx.begin();
                        RepoColaboradores.getInstance().persist(colaborador);
                        tx.commit();
                    }
                }
            }

            ctx.redirect("/admin");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.redirect("/");
        }
    }

public void create(Context ctx) {
    try {
        ColaboradorFisico colaboradorFisico = RepoColaboradores.getInstance()
                .buscarColaboradorFisicoPorID(Integer.parseInt(ctx.formParams("colaborador").get(0)));

        if (colaboradorFisico != null) {
            colaboradorFisico.setEsAdmin(true);
            ctx.sessionAttribute("es_admin", true);
            EntityTransaction tx = RepoColaboradores.getInstance().getTransaction();
            tx.begin();

            RepoColaboradores.getInstance().persist(colaboradorFisico);
            tx.commit();
        }

        Map<String, Object> model = new HashMap<>();

        ctx.redirect("/admin");

    } catch (Exception e) {
        Map<String, Object> model = new HashMap<>();
        e.printStackTrace();
        ctx.redirect("/");

    }
}
    public void delete(Context ctx) {
        try {
            ColaboradorFisico colaboradorFisico = RepoColaboradores.getInstance()
                    .buscarColaboradorFisicoPorID(Integer.parseInt(ctx.formParams("colaborador").get(0)));

            if (colaboradorFisico != null && colaboradorFisico.esAdmin()) {
                colaboradorFisico.setEsAdmin(false);
                ctx.sessionAttribute("es_admin", false);
                EntityTransaction tx = RepoColaboradores.getInstance().getTransaction();
                tx.begin();

                RepoColaboradores.getInstance().persist(colaboradorFisico);
                tx.commit();
            }

            Map<String, Object> model = new HashMap<>();

            ctx.redirect("/admin");

        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            e.printStackTrace();
            ctx.redirect("/");

        }
    }
}
