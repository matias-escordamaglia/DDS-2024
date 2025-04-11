package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.colaboraciones.RegistracionPersonaVulnerable;
import ar.edu.utn.frba.dds.model.colaboraciones.RegistracionPersonaVulnerableBuilder;
import ar.edu.utn.frba.dds.model.colaboradores.Colaborador;
import ar.edu.utn.frba.dds.model.colaboradores.ColaboradorFisico;
import ar.edu.utn.frba.dds.model.colaboradores.Credencial;
import ar.edu.utn.frba.dds.model.colaboradores.RepoColaboradores;
import ar.edu.utn.frba.dds.model.colaboradores.datoscolaborador.*;
import ar.edu.utn.frba.dds.repositories.RepoCredenciales;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrarPersonaVulnerableController implements WithSimplePersistenceUnit, TransactionalOps {

  public static Map<String, Object> index(@NotNull Context ctx) {
    Map<String, Object> model = new HashMap<>();

    // Add user session attributes to the model
    model.put("usuario_id", ctx.sessionAttribute("usuario_id"));
    model.put("esAdmin", ctx.sessionAttribute("es_admin"));

    // Form title
    model.put("formTitle", "Registrar Persona Vulnerable");

    // Populate document types for the dropdown
    model.put("tipoDocumentos", Arrays.stream(Documento.values())
        .map(Documento::name)
        .toList());

    return model;
  }

  public void saveForm(Context ctx) {
    try {
      ColaboradorFisico usuarioColaborador;

      // Verify authentication
      Integer usuarioId = ctx.sessionAttribute("usuario_id");
      if (usuarioId == null) {
        ctx.redirect("/login");
        return; // Stop execution if not authenticated
      }

      // Check if the user is a collaborator
      Credencial credencial = RepoCredenciales.INSTANCE.buscar(usuarioId);
      usuarioColaborador = RepoColaboradores.getInstance().buscarColaboradorFisicoPorCredencialID(credencial);
      if (usuarioColaborador == null) {
        ctx.status(403).result("El usuario no es un colaborador autorizado.");
        return; // Stop execution if not authorized
      }

      // Parse form data
      String nombre = ctx.formParam("nombre");
      String apellido = ctx.formParam("apellido");
      int edad = Integer.parseInt(Objects.requireNonNull(ctx.formParam("edad")));
      String direccionParam = ctx.formParam("direccion");
      String tipoDocumentoParam = ctx.formParam("tipoDocumento");
      String numeroDocumentoParam = ctx.formParam("numeroDocumento");
      boolean situacionCalle = ctx.formParam("situacionCalle") != null;
      boolean tieneHijos = ctx.formParam("tieneHijos") != null;

      // Address parsing
      assert direccionParam != null;
      String[] partesDireccion = direccionParam.split(",\\s*");
      String calle = partesDireccion.length > 0 ? partesDireccion[0] : "";
      Integer altura = partesDireccion.length > 1 ? Integer.parseInt(partesDireccion[1].trim()) : 0;
      String ciudad = partesDireccion.length > 2 ? partesDireccion[2] : "";
      String localidad = partesDireccion.length > 3 ? partesDireccion[3] : "";

      // Build registration object
      RegistracionPersonaVulnerableBuilder builder = RegistracionPersonaVulnerableBuilder.crear(usuarioColaborador);
      builder.setNombre(nombre + " " + apellido);
      builder.setFechaNacimiento(LocalDate.now().minusYears(edad));
      builder.setSituacionDeCalle(situacionCalle);

      if (!situacionCalle) {
        builder.setDireccion(new Direccion(ciudad, localidad, calle, altura));
      }

      Documento tipoDocumento = Documento.valueOf(tipoDocumentoParam.toUpperCase());
      builder.setTipoDocumento(tipoDocumento);
      builder.setNumeroDocumento(Integer.parseInt(numeroDocumentoParam));

      builder.setTieneMenoresBajoTutela(tieneHijos);
      if (tieneHijos) {
        int cantidadHijos = Integer.parseInt(Objects.requireNonNull(ctx.formParam("cantidadHijos")));
        builder.setCantMenoresBajoTutela(cantidadHijos);
      }

      // Perform registration within a transaction
      withTransaction(() -> {
        RegistracionPersonaVulnerable registracion = builder.generarLaRegistracion();
        // Save logic, if applicable
      });

      ctx.status(201).result("Información de la persona vulnerable guardada correctamente.");
    } catch (Exception e) {
      e.printStackTrace();
      ctx.status(400).result("Error al registrar la persona vulnerable: " + e.getMessage());
    }
  }
}