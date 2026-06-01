package pe.edu.upeu.sysdenuncias.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import pe.edu.upeu.sysdenuncias.components.ColumnInfo;
import pe.edu.upeu.sysdenuncias.components.TableViewHelper;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.dto.SessionManager;
import pe.edu.upeu.sysdenuncias.enums.EstadoDenuncia;
import pe.edu.upeu.sysdenuncias.model.Ciudadano;
import pe.edu.upeu.sysdenuncias.model.Denuncia;
import pe.edu.upeu.sysdenuncias.model.Funcionario;
import pe.edu.upeu.sysdenuncias.model.TipoDenuncia;
import pe.edu.upeu.sysdenuncias.service.ICiudadanoService;
import pe.edu.upeu.sysdenuncias.service.IDenunciaService;
import pe.edu.upeu.sysdenuncias.service.IFuncionarioService;
import pe.edu.upeu.sysdenuncias.service.ITipoDenunciaService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class DenunciaController {

    private final IDenunciaService denunciaService;
    private final ICiudadanoService ciudadanoService;
    private final ITipoDenunciaService tipoDenunciaService;
    private final IFuncionarioService funcionarioService;

    private ObservableList<Denuncia> listarDenuncias;

    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextField txtUbicacion;
    @FXML
    private ComboBox<Ciudadano> cbxCiudadano;
    @FXML
    private ComboBox<TipoDenuncia> cbxTipoDenuncia;
    @FXML
    private ComboBox<EstadoDenuncia> cbxEstado;

    @FXML
    private TableView<Denuncia> tableView;
    @FXML
    private Button btnGuardar;

    private Long idDenunciaEdit = 0L;

    public DenunciaController(IDenunciaService denunciaService, ICiudadanoService ciudadanoService,
                              ITipoDenunciaService tipoDenunciaService, IFuncionarioService funcionarioService) {
        this.denunciaService = denunciaService;
        this.ciudadanoService = ciudadanoService;
        this.tipoDenunciaService = tipoDenunciaService;
        this.funcionarioService = funcionarioService;
    }

    @FXML
    public void initialize() {
        cbxEstado.setItems(FXCollections.observableArrayList(EstadoDenuncia.values()));
        cbxCiudadano.setItems(FXCollections.observableArrayList(ciudadanoService.findAll()));
        cbxTipoDenuncia.setItems(FXCollections.observableArrayList(tipoDenunciaService.findAll()));

        // Cargar nombres correctos en ComboBox
        cbxCiudadano.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Ciudadano> call(ListView<Ciudadano> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Ciudadano item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item == null ? "" : item.getNombre() + " (" + item.getDni() + ")");
                    }
                };
            }
        });
        cbxCiudadano.setButtonCell(cbxCiudadano.getCellFactory().call(null));

        cbxTipoDenuncia.setCellFactory(new Callback<>() {
            @Override
            public ListCell<TipoDenuncia> call(ListView<TipoDenuncia> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(TipoDenuncia item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(item == null ? "" : item.getNombre());
                    }
                };
            }
        });
        cbxTipoDenuncia.setButtonCell(cbxTipoDenuncia.getCellFactory().call(null));

        TableViewHelper<Denuncia> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("id", 40.0));
        columns.put("Fecha", new ColumnInfo("fecha", 120.0));
        columns.put("Ciudadano", new ColumnInfo("ciudadano.nombre", 120.0));
        columns.put("Tipo", new ColumnInfo("tipoDenuncia.nombre", 100.0));
        columns.put("Estado", new ColumnInfo("estado", 80.0));
        columns.put("Funcionario", new ColumnInfo("funcionario.nombre", 120.0));

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, this::editDenuncia, this::deleteDenuncia);
        addReportColumn();
        listar();
    }




    private void addReportColumn() {
        TableColumn<Denuncia, Void> actionColumn = new TableColumn<>("Notificar");
        Callback<TableColumn<Denuncia, Void>, TableCell<Denuncia, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Denuncia, Void> call(final TableColumn<Denuncia, Void> param) {
                return new TableCell<>() {
                    private final MenuButton btnNotificar = new MenuButton("...");
                    {
                        btnNotificar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");

                        // 🖨️ Creamos las TRES opciones del menú desplegable ahora mismo
                        MenuItem opcionWhatsapp = new MenuItem("Enviar por WhatsApp");
                        MenuItem opcionCorreo = new MenuItem("Enviar por Correo");
                        MenuItem opcionImpreso = new MenuItem("Entregar Impreso");

                        btnNotificar.getItems().addAll(opcionWhatsapp, opcionCorreo, opcionImpreso);

                        opcionWhatsapp.setOnAction(event -> {
                            Denuncia data = getTableView().getItems().get(getIndex());
                            procesarNotificacion(data, "WhatsApp");
                        });

                        opcionCorreo.setOnAction(event -> {
                            Denuncia data = getTableView().getItems().get(getIndex());
                            procesarNotificacion(data, "Correo");
                        });

                        opcionImpreso.setOnAction(event -> {
                            Denuncia data = getTableView().getItems().get(getIndex());
                            procesarNotificacion(data, "Impreso");
                        });
                    }

                    private void procesarNotificacion(Denuncia data, String medio) {
                        denunciaService.generarConstanciaPdf(data.getId());

                        String telefono = data.getCiudadano().getTelefono();
                        String nombre = data.getCiudadano().getNombre();
                        String tipoDenuncia = data.getTipoDenuncia() != null ? data.getTipoDenuncia().getNombre() : "Denuncia";


                        if (medio.equals("WhatsApp")) {
                            if (telefono != null && !telefono.isEmpty()) {
                                enviarNotificacionWhatsApp(telefono, nombre, tipoDenuncia, data.getId());
                            }
                        } else if (medio.equals("Correo")) {
                            String correo = data.getCiudadano().getCorreo();
                            System.out.println("✉️ [Terminal] Enviando constancia formal en PDF al correo: " + correo);
                        } else if (medio.equals("Impreso")) {

                            System.out.println("🖨️ [Impresora] Mandando orden de impresión física para la denuncia N° " + data.getId());
                        }

                        // 2. Modificamos el estado a NOTIFICADO para cerrar el ciclo
                        data.setEstado(EstadoDenuncia.NOTIFICADO);
                        denunciaService.update(data.getId(), data);

                        // 3. Limpieza total e instantánea de la interfaz
                        listar();
                        limpiar();
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(btnNotificar));
                        }
                    }
                };
            }
        };
        actionColumn.setCellFactory(cellFactory);
        actionColumn.setPrefWidth(95);
        tableView.getColumns().add(actionColumn);
    }

    private void enviarNotificacionWhatsApp(String telefono, String nombre, String tipoDenuncia, Long idDenuncia) {
        try {
            String mensaje = "🏛️ *MUNICIPALIDAD - SISTEMA DE DENUNCIAS*\n\n"
                    + "Estimado(a) *" + nombre + "*,\n"
                    + "Se ha procesado formalmente su denuncia por '" + tipoDenuncia + "'.\n\n"
                    + "📄 *Nro. de Trámite:* 000" + idDenuncia + "-2026\n"
                    + "📎 _Su Constancia Oficial en formato PDF ha sido generada con éxito en el sistema._\n\n"
                    + "_Por favor, conserve este mensaje como cargo oficial de presentación._";

            String mensajeCodificado = java.net.URLEncoder.encode(mensaje, "UTF-8");
            String numeroCompleto = telefono.startsWith("51") ? telefono : "51" + telefono;
            String urlWhatsApp = "https://wa.me/" + numeroCompleto + "?text=" + mensajeCodificado;

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                desktop.browse(new java.net.URI(urlWhatsApp));

                String carpetaUsuario = System.getProperty("user.home");
                String rutaPdf = carpetaUsuario + "\\Documents\\constancia_" + idDenuncia + ".pdf";
                java.io.File archivoPdf = new java.io.File(rutaPdf);

                if (archivoPdf.exists()) {
                    desktop.open(archivoPdf);
                    System.out.println("🟩 [Sistema] PDF abierto automáticamente desde Documentos.");
                } else {
                    Runtime.getRuntime().exec("explorer.exe " + carpetaUsuario + "\\Documents");
                    System.out.println("⚠️ Archivo específico no encontrado, abriendo la carpeta Documentos.");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error al buscar el PDF de forma universal: " + e.getMessage());
        }
    }

    private void listar() {
        listarDenuncias = FXCollections.observableArrayList(denunciaService.findAll());
        tableView.setItems(listarDenuncias);
    }

    @FXML
    public void guardar() {
        try {
            Funcionario fLogueado = SessionManager.getInstance().getFuncionarioLogueado();

            Denuncia denuncia = Denuncia.builder()
                    .descripcion(txtDescripcion.getText())
                    .ubicacion(txtUbicacion.getText())
                    .observacion(txtObservacion.getText())
                    .estado(cbxEstado.getValue() != null ? cbxEstado.getValue() : EstadoDenuncia.PENDIENTE)
                    .ciudadano(cbxCiudadano.getValue())
                    .tipoDenuncia(cbxTipoDenuncia.getValue())
                    .funcionario(fLogueado) 
                    .fecha(LocalDateTime.now())
                    .build();

            if (idDenunciaEdit != 0L) {
                denuncia.setId(idDenunciaEdit);
                // Aquí el Service interceptará si el estado es NOTIFICADO e imprimirá en consola
                denunciaService.update(idDenunciaEdit, denuncia);
                Toast.showToast(null, "Actualizado correctamente", 2000, 500, 300);
            } else {
                denunciaService.save(denuncia);
                Toast.showToast(null, "Guardado correctamente", 2000, 500, 300);
            }
            limpiar();
            listar();
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    private void editDenuncia(Denuncia d) {
        idDenunciaEdit = d.getId();
        txtDescripcion.setText(d.getDescripcion());
        txtUbicacion.setText(d.getUbicacion());
        txtObservacion.setText(d.getObservacion());
        // Find exact objects to select in combo
        cbxEstado.setValue(d.getEstado());
        cbxCiudadano.getItems().stream().filter(c -> c.getId().equals(d.getCiudadano().getId())).findFirst().ifPresent(cbxCiudadano::setValue);
        cbxTipoDenuncia.getItems().stream().filter(t -> t.getId().equals(d.getTipoDenuncia().getId())).findFirst().ifPresent(cbxTipoDenuncia::setValue);
        
        btnGuardar.setText("Actualizar");
    }

    private void deleteDenuncia(Denuncia d) {
        denunciaService.delete(d.getId());
        listar();
        Toast.showToast(null, "Eliminado correctamente", 2000, 500, 300);
    }

    @FXML
    public void limpiar() {
        txtDescripcion.clear();
        txtUbicacion.clear();
        txtObservacion.clear();
        cbxCiudadano.setValue(null);
        cbxTipoDenuncia.setValue(null);
        cbxEstado.setValue(EstadoDenuncia.PENDIENTE);
        idDenunciaEdit = 0L;
        btnGuardar.setText("Guardar");
    }
    @FXML
    private TextArea txtObservacion;
}
