package pe.edu.upeu.sysdenuncias.service.impl;

import net.sf.jasperreports.engine.*;
import pe.edu.upeu.sysdenuncias.enums.EstadoDenuncia;
import pe.edu.upeu.sysdenuncias.model.Denuncia;
import pe.edu.upeu.sysdenuncias.repository.DenunciaRepository;
import pe.edu.upeu.sysdenuncias.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysdenuncias.service.IDenunciaService;
import pe.edu.upeu.sysdenuncias.service.INotificacionService;
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
        EstadoDenuncia estadoAnterior = repo.findById(id)
                .map(Denuncia::getEstado)
                .orElse(null);

        Denuncia actualizada = super.update(id, entity);
        
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
            String path = "src/main/resources/reports/constancia_denuncia.jrxml";
            java.sql.Connection con = pe.edu.upeu.sysdenuncias.config.DatabaseConnection.getConnection();
            java.util.Map<String, Object> params = new java.util.HashMap<>();
            params.put("ID_DENUNCIA", idDenuncia);

            net.sf.jasperreports.engine.JasperReport jr = net.sf.jasperreports.engine.JasperCompileManager.compileReport(path);
            net.sf.jasperreports.engine.JasperPrint jp = net.sf.jasperreports.engine.JasperFillManager.fillReport(jr, params, con);

            String ruta = "Constancia_Denuncia_" + idDenuncia + ".pdf";
            net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile(jp, ruta);
            System.out.println("Archivo guardado en: " + ruta);

            net.sf.jasperreports.view.JasperViewer.viewReport(jp, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Integer> obtenerEstadisticasPorTipo() {
        return repo.getCountByTipo();
    }

    @Override
    public Map<String, Integer> obtenerEstadisticasPorEstado() {
        return repo.getCountByEstado();
    }
}
