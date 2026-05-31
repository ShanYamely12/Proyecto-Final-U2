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

    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtUbicacion;
    @FXML private ComboBox<Ciudadano> cbxCiudadano;
    @FXML private ComboBox<TipoDenuncia> cbxTipoDenuncia;
    @FXML private ComboBox<EstadoDenuncia> cbxEstado;
    
    @FXML private TableView<Denuncia> tableView;
    @FXML private Button btnGuardar;

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
        TableColumn<Denuncia, Void> actionColumn = new TableColumn<>("PDF");
        Callback<TableColumn<Denuncia, Void>, TableCell<Denuncia, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Denuncia, Void> call(final TableColumn<Denuncia, Void> param) {
                return new TableCell<>() {
                    private final Button btnPdf = new Button("PDF");
                    {
                        btnPdf.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                        btnPdf.setOnAction(event -> {
                            Denuncia data = getTableView().getItems().get(getIndex());
                            denunciaService.generarConstanciaPdf(data.getId());
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(btnPdf));
                        }
                    }
                };
            }
        };
        actionColumn.setCellFactory(cellFactory);
        actionColumn.setPrefWidth(60);
        tableView.getColumns().add(actionColumn);
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
