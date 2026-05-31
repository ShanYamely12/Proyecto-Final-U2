package pe.edu.upeu.sysdenuncias.service.impl;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import pe.edu.upeu.sysdenuncias.config.DatabaseConnection;
import pe.edu.upeu.sysdenuncias.enums.EstadoDenuncia;
import pe.edu.upeu.sysdenuncias.model.Denuncia;
import pe.edu.upeu.sysdenuncias.repository.DenunciaRepository;
import pe.edu.upeu.sysdenuncias.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysdenuncias.service.IDenunciaService;
import pe.edu.upeu.sysdenuncias.service.INotificacionService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DenunciaServiceImp extends CrudGenericoServiceImp<Denuncia, Long> implements IDenunciaService {

    private final DenunciaRepository repo;
    private final INotificacionService notificacionService;

    public DenunciaServiceImp(DenunciaRepository repo, INotificacionService notificacionService) {
        this.repo = repo;
        this.notificacionService = notificacionService;
    }

    @Override
    protected ICrudGenericoRepository<Denuncia, Long> getRepo() {
        return repo;
    }

    @Override
    public Denuncia update(Long id, Denuncia entity) {
        // Obtener el estado anterior antes de actualizar
        EstadoDenuncia estadoAnterior = repo.findById(id)
                .map(Denuncia::getEstado)
                .orElse(null);

        Denuncia actualizada = super.update(id, entity);
        
        // Regla de negocio: Si el estado ha cambiado, notificar al ciudadano
        if (estadoAnterior != null && estadoAnterior != actualizada.getEstado()) {
            String mensaje = "Estimado/a " + actualizada.getCiudadano().getNombre() + ",\n" +
                    "Le informamos que el estado de su denuncia (Código: " + actualizada.getId() + ") " +
                    "ha cambiado de \"" + estadoAnterior.name() + "\" a \"" + actualizada.getEstado().name() + "\".\n" +
                    "Funcionario a cargo: " + (actualizada.getFuncionario() != null ? actualizada.getFuncionario().getNombre() : "No asignado") + ".";
            
            notificacionService.notificarCiudadano(actualizada.getCiudadano(), mensaje);
        }
        
        return actualizada;
    }

    @Override
    public void generarConstanciaPdf(Long idDenuncia) {
        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/constancia_denuncia.jrxml");
            if (reportStream == null) {
                System.out.println("No se encontró el archivo JRXML.");
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ID_DENUNCIA", idDenuncia);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, 
                    parameters, 
                    DatabaseConnection.getInstance().getConnection()
            );

            // Mostrar el reporte
            JasperViewer.viewReport(jasperPrint, false);

        } catch (JRException e) {
            e.printStackTrace();
            System.out.println("Error al generar el reporte: " + e.getMessage());
        }
    }
}
