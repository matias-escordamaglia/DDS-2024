package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.Captura;
import ar.edu.utn.frba.dds.model.Usuario;
import ar.edu.utn.frba.dds.repositories.RepoCredenciales;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AboutController implements WithSimplePersistenceUnit, TransactionalOps {
    public Map<String, Object> index(@NotNull Context ctx) {
        Map model = new HashMap<>();
        model.put("usuario_id",ctx.sessionAttribute("usuario_id"));
        model.put("esAdmin",ctx.sessionAttribute("es_admin"));

        return model;
    }
}
