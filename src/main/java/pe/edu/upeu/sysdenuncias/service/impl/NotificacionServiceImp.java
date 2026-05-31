package pe.edu.upeu.sysdenuncias.service.impl;
 
import pe.edu.upeu.sysdenuncias.model.Ciudadano;
import pe.edu.upeu.sysdenuncias.service.INotificacionService;
 
public class NotificacionServiceImp implements INotificacionService {
    @Override
    public void notificarCiudadano(Ciudadano ciudadano, String mensaje) {
        System.out.println("==================================================");
        System.out.println(" SIMULANDO ENVÍO DE CORREO ELECTRÓNICO ");
        System.out.println("Para: " + ciudadano.getNombre());
        System.out.println("Correo destino: " + ciudadano.getCorreo());
        System.out.println("Teléfono de contacto: " + ciudadano.getTelefono());
        System.out.println("Mensaje enviado:\n" + mensaje);
        System.out.println("==================================================");
    }
}
