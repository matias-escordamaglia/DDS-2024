package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.Middleware.MiddlewareAdmin;
import ar.edu.utn.frba.dds.controllers.AboutController;
import ar.edu.utn.frba.dds.controllers.DonacionController;
import ar.edu.utn.frba.dds.controllers.DonacionDineroController;
import ar.edu.utn.frba.dds.controllers.DonacionDistribucionController;
import ar.edu.utn.frba.dds.controllers.DonacionHeladeraController;
import ar.edu.utn.frba.dds.controllers.DonacionViandaController;
import ar.edu.utn.frba.dds.controllers.HomeController;
import ar.edu.utn.frba.dds.controllers.SessionController;
import ar.edu.utn.frba.dds.controllers.*;
import io.github.flbulgarelli.jpa.extras.test.SimplePersistenceTest;
import io.javalin.Javalin;

public class Router implements SimplePersistenceTest {
    public void configure(Javalin app) {
        HomeController homeController = new HomeController();
        AboutController aboutController = new AboutController();
        SessionController sessionController = new SessionController();
        DonacionController donacionController = new DonacionController();
        AdminController adminController = new AdminController();
        DonacionHeladeraController donacionheladeraController = new DonacionHeladeraController();
        DonacionDineroController donaciondineroController = new DonacionDineroController();
        DonacionViandaController donacionviandaController = new DonacionViandaController();
        DonacionDistribucionController donaciondistribucionController = new DonacionDistribucionController();


        AltaFallaController altaFallaController = new AltaFallaController();
        GestionFallaTecnicaController gestionFallaTecnicaController = new GestionFallaTecnicaController();

        GestionHeladerasController gestionHeladerasController = new GestionHeladerasController();

        ImportadorController importadorController = new ImportadorController();

        RegistrarPersonaVulnerableController personaVulnerableController = new RegistrarPersonaVulnerableController();

        app.before(ctx -> {
            entityManager().clear();
            MiddlewareAdmin.verificarAdmin(ctx);
        });

        //TODO limpiar clases de pokemon y código extra; las dejé de guía
        app.get("/", context -> context.redirect("/home"));
        app.get("/home", ctx -> ctx.render("home.hbs", homeController.index(ctx)));
        app.get("/about", ctx -> ctx.render("about.hbs", aboutController.index(ctx)));
        app.get("/donacion", donacionController:: show);
        app.post("/donacion", donacionController::redirect);
        app.get("/donacion/heladera", ctx -> ctx.render("donacionheladera.hbs", donacionheladeraController.index(ctx)));
        app.get("/donacion/dinero", ctx -> ctx.render("donaciondinero.hbs", donaciondineroController.index(ctx)));
        app.get("/donacion/vianda", ctx -> ctx.render("donacionvianda.hbs", donacionviandaController.index(ctx)));
        app.get("/donacion/distribucion-viandas", ctx -> ctx.render("donaciondistribucion.hbs", donaciondistribucionController.index(ctx)));

        app.get("/login", sessionController::show);
        app.post("/login", sessionController::create);
        app.get("/logout", sessionController::logout);
        app.get("/admin", adminController::show);
        app.post("/admin", adminController::redirect);


        app.post("/donacion/heladera", donacionheladeraController::saveForm);
        app.post("/donacion/distribucion-viandas", donaciondistribucionController::saveForm);
        app.post("/donacion/dinero", donaciondineroController::saveForm);
        app.post("/donacion/vianda", donacionviandaController::saveForm);

        app.get("/heladeras/search", new AltaFallaController()::searchHeladeras);
        app.get("/gestionheladeras", ctx -> ctx.render("gestion-heladeras.hbs", gestionHeladerasController.index(ctx)));

        app.get("/fallas", ctx -> ctx.render("gestion-reportes-index.hbs", gestionFallaTecnicaController.index(ctx)));
        app.get("/fallas/new", ctx -> ctx.render("reportefalla.hbs", altaFallaController.showForm(ctx)));
        app.post("/altaFalla", altaFallaController::processForm);

        app.post("/fallas", altaFallaController::create);
        app.get("/fallas/{id}", ctx -> ctx.render("falla-detalles.hbs", gestionFallaTecnicaController.show(ctx)));
        app.post("/fallas/{id}/{action}", gestionFallaTecnicaController::toggleFallaState);

        app.get("/personavulnerable/new",ctx -> ctx.render("registrarpv.hbs", RegistrarPersonaVulnerableController.index(ctx)));
        app.post("/personavulnerable", personaVulnerableController::saveForm);

        app.get("/colaboradorfisico/new",ctx -> ctx.render("colafisico.hbs", RegistrarColaboradorFisicoController.index(ctx)));
        app.get("/colaboradorjuridico/new",ctx -> ctx.render("colajuridico.hbs", RegistrarColaboradorJuridicoController.index(ctx)));

        app.get("/importacion/masiva", importadorController::show);
        app.post("/importacion/masiva", importadorController::create);

        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace(); // Log the full stack trace for debugging
            ctx.status(500);
            ctx.result("Server encountered an error: " + e.getMessage());
        });

    }
}