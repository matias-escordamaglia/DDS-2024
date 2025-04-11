package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.db.dt.HeladeraDTO;
import ar.edu.utn.frba.dds.db.dt.ComidaDTO;
import ar.edu.utn.frba.dds.model.colaboradores.ColaboradorFisico;
import ar.edu.utn.frba.dds.model.colaboradores.Credencial;
import ar.edu.utn.frba.dds.model.colaboradores.RepoColaboradores;
import ar.edu.utn.frba.dds.model.heladeras.Comida;
import ar.edu.utn.frba.dds.model.heladeras.Heladera;
import ar.edu.utn.frba.dds.model.heladeras.MapaHeladeras;
import ar.edu.utn.frba.dds.model.heladeras.Vianda;
import ar.edu.utn.frba.dds.repositories.RepoCredenciales;
import ar.edu.utn.frba.dds.repositories.RepositorioComidas;
import ar.edu.utn.frba.dds.repositories.RepositorioHeladeras;
import io.github.flbulgarelli.jpa.extras.TransactionalOps;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DonacionViandaController implements WithSimplePersistenceUnit, TransactionalOps {

  //solo fisico
  ColaboradorFisico usuarioColaborador;
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

  public Map<String, Object> showForm(Context ctx) {
    Map<String, Object> model = new HashMap<>();

    // Fetch all heladeras
    List<Heladera> heladeras = RepositorioHeladeras.INSTANCE.findAll();

    // Sort heladeras alphabetically by 'nombreHeladera' (A-Z)
    heladeras = heladeras.stream()
        .sorted((h1, h2) -> h1.getNombreHeladera().compareToIgnoreCase(h2.getNombreHeladera()))
        .toList();

    // Limit to the first 10 heladeras
    List<Heladera> limitedHeladeras = heladeras.stream().limit(10).collect(Collectors.toList());

    // Add the sorted and limited list to the model
    model.put("heladeras", limitedHeladeras);
    return model;
  }


  public void searchHeladeras(Context ctx) {
    String searchTerm = ctx.queryParam("search") != null ? ctx.queryParam("search") : "";

    // Fetch all heladeras
    List<Heladera> heladeras = RepositorioHeladeras.INSTANCE.findAll();

    List<HeladeraDTO> filteredHeladeras = heladeras.stream()
        .filter(h -> h.getNombreHeladera().toLowerCase().contains(searchTerm.toLowerCase()))
        .sorted((h1, h2) -> h1.getNombreHeladera().compareToIgnoreCase(h2.getNombreHeladera()))
        .limit(10)
        .map(h -> new HeladeraDTO((long) h.getId(), h.getNombreHeladera())) // Map to DTO
        .collect(Collectors.toList());

    ctx.json(filteredHeladeras);
  }

  public Map<String, Object> showComidasForm(Context ctx) {
    Map<String, Object> model = new HashMap<>();

    // Fetch all comidas
    List<Comida> comidas = RepositorioComidas.INSTANCE.findAll();

    // Sort comidas alphabetically by 'nombre' (A-Z)
    comidas = comidas.stream()
        .sorted((c1, c2) -> c1.getNombre().compareToIgnoreCase(c2.getNombre()))
        .toList();

    // Limit to the first 10 comidas
    List<Comida> limitedComidas = comidas.stream().limit(10).collect(Collectors.toList());

    // Add the sorted and limited list to the model
    model.put("comidas", limitedComidas);
    return model;
  }

  public void searchComidas(Context ctx) {
    String searchTerm = ctx.queryParam("search") != null ? ctx.queryParam("search") : "";

    // Fetch all comidas
    List<Comida> comidas = RepositorioComidas.INSTANCE.findAll();

    List<ComidaDTO> filteredComidas = comidas.stream()
        .filter(c -> c.getNombre().toLowerCase().contains(searchTerm.toLowerCase()))
        .sorted((c1, c2) -> c1.getNombre().compareToIgnoreCase(c2.getNombre()))
        .limit(10)
        .map(c -> new ComidaDTO((long) c.getId(), c.getNombre())) // Map to DTO
        .collect(Collectors.toList());

    ctx.json(filteredComidas);
  }


  public void saveForm(Context ctx) {

    Integer usuarioId = ctx.sessionAttribute("usuario_id");

    if (usuarioId != null) {
      Credencial credencial = RepoCredenciales.INSTANCE.buscarPorId(usuarioId);
      usuarioColaborador =
          RepoColaboradores.getInstance().buscarColaboradorFisicoPorCredencialID(credencial);
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
    String nombreHeladera = ctx.formParam("nombre-heladera-origen");
    String fechaDonacion = ctx.formParam("fecha-donacion");
    String comida = ctx.formParam("comida");
    String fechaCaducacion = ctx.formParam("fecha-caducacion");
    String peso = ctx.formParam("peso");
    String calorias = ctx.formParam("calorias");

    Integer pesoInt = Integer.parseInt(peso);
    Integer caloriasInt = Integer.parseInt(calorias);
    LocalDate fechaCaducacionDate = LocalDate.parse(fechaCaducacion);
    LocalDate fechaDonacionDate = LocalDate.parse(fechaDonacion);

    withTransaction(() -> {
      Heladera heladera = mapaHeladeras.buscarHeladera(nombreHeladera);

      Comida comidaVianda = RepositorioComidas.INSTANCE.findByName(comida);
      if(comidaVianda == null) {
        comidaVianda = new Comida(comida);
      }
      Vianda vianda = new Vianda(comidaVianda, fechaCaducacionDate, pesoInt, caloriasInt);
      List viandas = new ArrayList<>();
      viandas.add(vianda);

      usuarioColaborador.donarViandas(fechaDonacionDate, viandas, heladera);

    });
  }

}
