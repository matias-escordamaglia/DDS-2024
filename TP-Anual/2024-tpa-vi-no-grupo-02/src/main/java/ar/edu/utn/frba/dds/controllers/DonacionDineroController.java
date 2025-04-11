package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.colaboraciones.Frecuencia;
import ar.edu.utn.frba.dds.model.colaboraciones.MotivoDistribuicion;
import ar.edu.utn.frba.dds.model.colaboradores.Colaborador;
import ar.edu.utn.frba.dds.model.colaboradores.ColaboradorFisico;
import ar.edu.utn.frba.dds.model.colaboradores.ColaboradorJuridico;
import ar.edu.utn.frba.dds.model.colaboradores.Credencial;
import ar.edu.utn.frba.dds.model.colaboradores.RepoColaboradores;
import ar.edu.utn.frba.dds.model.colaboradores.datoscolaborador.Direccion;
import ar.edu.utn.frba.dds.model.heladeras.ConfiguracionHeladera;
import ar.edu.utn.frba.dds.model.heladeras.EstadoHeladera;
import ar.edu.utn.frba.dds.model.heladeras.Heladera;
import ar.edu.utn.frba.dds.model.heladeras.UbicacionPrecisa;
import ar.edu.utn.frba.dds.repositories.RepoCredenciales;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
* 41710110 - !nL@Sn10mez
123456789 - !nelL@Sn10ssi*/

public class DonacionDineroController implements WithSimplePersistenceUnit, TransactionalOps {

  //ambos
  ColaboradorFisico usuarioColaboradorFisico;
  ColaboradorJuridico usuarioColaboradorJuridico;

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

    if (usuarioId != null) {
      Credencial credencial = RepoCredenciales.INSTANCE.buscarPorId(usuarioId);
      usuarioColaboradorFisico =
          RepoColaboradores.getInstance().buscarColaboradorFisicoPorCredencialID(credencial);

      if(usuarioColaboradorFisico == null){
        usuarioColaboradorJuridico = RepoColaboradores.getInstance().buscarColaboradorJuridicoPorCredencialID(credencial);
      }
    } else {
      Map<String, Object> model = new HashMap<>();
      model.put("error", "Debes iniciar sesión para realizar esta accion");
      ctx.render("login.hbs", model);
      return;
    }

    if (usuarioColaboradorFisico == null && usuarioColaboradorJuridico == null) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", "El usuario no esta registrado como colaborador");
      ctx.render("login.hbs", model);
      return;
    }

    // Obtiene los valores de los campos del formulario
    String monto = ctx.formParam("monto");
    String frecuencia = ctx.formParam("frecuencia");
    String fechaDonacionInicio = ctx.formParam("fecha-donacion-inicio");
    String fechaDonacionFin = ctx.formParam("fecha-donacion-fin");
    String nroTarjeta = ctx.formParam("nro-tarjeta");
    String vencimiento = ctx.formParam("vencimiento");
    String cvc = ctx.formParam("cvc");

    withTransaction(() -> {
      try {
        Double montoDouble = Double.parseDouble(monto); // Convertir a Double
        Integer montoDinero = montoDouble.intValue();
        Frecuencia frecuenciaDonacion = Frecuencia.valueOf(frecuencia);

        // Guarda o actualiza el usuario en la base de datos
        if(usuarioColaboradorFisico == null){
          usuarioColaboradorJuridico.donarDinero(montoDinero, frecuenciaDonacion);
        } else {
          usuarioColaboradorFisico.donarDinero(montoDinero, frecuenciaDonacion);
        }

        ctx.status(201).json(Map.of("message", "Donación realizada exitosamente."));
      } catch (Exception e) {
        e.printStackTrace();
        ctx.status(400).json(Map.of("message", "Error al procesar la donación: " + e.getMessage()));
      }
    });

  }
}
