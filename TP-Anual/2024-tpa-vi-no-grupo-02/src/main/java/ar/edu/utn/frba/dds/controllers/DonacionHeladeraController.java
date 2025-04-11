package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.colaboradores.Colaborador;
import ar.edu.utn.frba.dds.model.colaboradores.ColaboradorFisico;
import ar.edu.utn.frba.dds.model.colaboradores.ColaboradorJuridico;
import ar.edu.utn.frba.dds.model.colaboradores.Credencial;
import ar.edu.utn.frba.dds.model.colaboradores.RepoColaboradores;
import ar.edu.utn.frba.dds.model.colaboradores.datoscolaborador.Direccion;
import ar.edu.utn.frba.dds.model.heladeras.ConfiguracionHeladera;
import ar.edu.utn.frba.dds.model.heladeras.EstadoHeladera;
import ar.edu.utn.frba.dds.model.heladeras.Heladera;
import ar.edu.utn.frba.dds.model.heladeras.MapaHeladeras;
import ar.edu.utn.frba.dds.model.heladeras.UbicacionPrecisa;
import ar.edu.utn.frba.dds.repositories.RepoCredenciales;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class DonacionHeladeraController implements WithSimplePersistenceUnit, TransactionalOps {
  ColaboradorJuridico usuarioColaborador;
  MapaHeladeras mapaHeladeras = MapaHeladeras.getInstance();

  public Map<String, Object> index(@NotNull Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("usuario_id",ctx.sessionAttribute("usuario_id"));
    model.put("esAdmin",ctx.sessionAttribute("es_admin"));

    // Valida que el usuario esté logueado
    Integer usuarioId = ctx.sessionAttribute("usuario_id");
    if (usuarioId == null) {
      ctx.result("Usuario no autenticado");
      ctx.redirect("/login");
    }
    return model;
  }

  public void saveForm(Context ctx) {

    Integer usuarioId = ctx.sessionAttribute("usuario_id");

    //solo juridico
    if (usuarioId != null) {
      Credencial credencial = RepoCredenciales.INSTANCE.buscarPorId(usuarioId);
      usuarioColaborador =
          RepoColaboradores.getInstance().buscarColaboradorJuridicoPorCredencialID(credencial);
    } else {
      Map<String, Object> model = new HashMap<>();
      model.put("error", "Debes iniciar sesión para realizar esta accion");
      ctx.render("login.hbs", model);
      return;
    }

    if (usuarioColaborador == null) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", "El usuario no esta registrado como colaborador");
      ctx.render("login.hbs", model);
      return;
    }

    // Obtiene los valores de los campos del formulario
    String nombreHeladera = ctx.formParam("nombre-heladera");
    String calle = ctx.formParam("calle");
    String ciudad = ctx.formParam("ciudad");
    String localidad = ctx.formParam("localidad");
    String altura = ctx.formParam("altura");
    String capacidadHeladera = ctx.formParam("capacidad-heladera");
    String fechaHeladera = ctx.formParam("fecha-colocacion");

    Integer alturaDireccion = Integer.parseInt(altura);
    double capacidadHeladeraDouble = Double.parseDouble(capacidadHeladera);

    withTransaction(() -> {
      // Realiza las asignaciones de los valores del formulario
      UbicacionPrecisa ubicacion = new UbicacionPrecisa(ciudad,
          new Direccion(ciudad,
              localidad, calle, alturaDireccion), 0.0, 0.0);

      //generico
      ConfiguracionHeladera config = new ConfiguracionHeladera(5.0,
          25.0, 3);

      Heladera heladera = new Heladera(nombreHeladera, ubicacion,
          EstadoHeladera.ACTIVA, capacidadHeladeraDouble, config);

      mapaHeladeras.agregarHeladera(heladera);

      // Guarda o actualiza el usuario en la base de datos
      usuarioColaborador.hacerseCargoDeHeladera(heladera);
    });
  }
}
