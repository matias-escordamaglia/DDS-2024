        package ar.edu.utn.frba.dds.Middleware;

        import ar.edu.utn.frba.dds.model.colaboradores.ColaboradorFisico;
        import ar.edu.utn.frba.dds.model.colaboradores.Credencial;
        import ar.edu.utn.frba.dds.model.colaboradores.RepoColaboradores;
        import ar.edu.utn.frba.dds.repositories.RepoCredenciales;
        import io.javalin.http.Context;

        public class MiddlewareAdmin {
            // Middleware para verificar el atributo esAdmin
            public static void verificarAdmin(Context ctx) {
                // Obtener el ID del usuario desde la sesión
                Integer usuarioId = ctx.sessionAttribute("usuario_id");

                if (usuarioId != null) {
                    Credencial credencial = RepoCredenciales.INSTANCE.buscar(usuarioId);
                    // Buscar el colaborador físico en el repositorio
                    ColaboradorFisico colaborador = RepoColaboradores.getInstance()
                        .buscarColaboradorFisicoPorCredencialID(credencial);

                // Configurar el atributo de sesión esAdmin según el estado del colaborador
                if (colaborador != null){
                    if(colaborador.esAdmin()) {
                    ctx.sessionAttribute("es_admin", true);
                    } else {
                    ctx.sessionAttribute("es_admin", false);
                    }
                }
            }
        }

        }
