package ar.edu.utn.frba.dds.model.colaboradores.medioscontacto;

import ar.edu.utn.frba.dds.model.excepciones.TwilioNotificacionException;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;

public class AdapterTwilio implements IAdapterWhatsapp {

  @Override
  public void enviarWhatsapp(String numeroTelefono, String mensaje) {
    // Get your Twilio Account SID and Auth Token from environment variables
    String username = System.getenv("TWILIO_USERNAME"); // e.g., "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    String password = System.getenv("TWILIO_PASSWORD"); // e.g., "your-auth-token"

    if (username == null || password == null) {
      throw new TwilioNotificacionException("El username y password de Twilio están vacíos");
    }

    try {
      Twilio.init(username, password);
      Message message = Message.creator(
              new com.twilio.type.PhoneNumber("whatsapp:" + numeroTelefono),
              new com.twilio.type.PhoneNumber("whatsapp:+14155238886"), // Twilio sandbox number
              mensaje)
          .create();
    }
    catch (ApiException e) {
      throw new TwilioNotificacionException("El mensaje de whatsapp no ha podido ser enviado");
    }
  }
}
