package pe.edu.upeu.sysdenuncias.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.sysdenuncias.components.ColumnInfo;
import pe.edu.upeu.sysdenuncias.components.TableViewHelper;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.enums.Genero;
import pe.edu.upeu.sysdenuncias.model.Ciudadano;
import pe.edu.upeu.sysdenuncias.service.ICiudadanoService;

import java.util.LinkedHashMap;

public class CiudadanoController {

    private final ICiudadanoService ciudadanoService;
    private ObservableList<Ciudadano> listarCiudadanos;

    @FXML private TextField txtNombre;
    @FXML private TextField txtDni;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtDireccion;
    @FXML private ComboBox<Genero> cbxGenero;
    
    @FXML private TableView<Ciudadano> tableView;
    @FXML private Button btnGuardar;

    private Long idCiudadanoEdit = 0L;

    public CiudadanoController(ICiudadanoService ciudadanoService) {
        this.ciudadanoService = ciudadanoService;
    }

    @FXML
    public void initialize() {
        cbxGenero.setItems(FXCollections.observableArrayList(Genero.values()));
        
        TableViewHelper<Ciudadano> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("id", 50.0));
        columns.put("Nombre", new ColumnInfo("nombre", 150.0));
        columns.put("DNI", new ColumnInfo("dni", 80.0));
        columns.put("Teléfono", new ColumnInfo("telefono", 100.0));
        columns.put("Correo", new ColumnInfo("correo", 180.0));
        columns.put("Dirección", new ColumnInfo("direccion", 150.0));
        columns.put("Género", new ColumnInfo("genero", 80.0));

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, this::editCiudadano, this::deleteCiudadano);
        listar();
    }

    private void listar() {
        listarCiudadanos = FXCollections.observableArrayList(ciudadanoService.findAll());
        tableView.setItems(listarCiudadanos);
    }

    @FXML
    public void guardar() {
        try {
            Ciudadano ciudadano = Ciudadano.builder()
                    .nombre(txtNombre.getText())
                    .dni(txtDni.getText())
                    .telefono(txtTelefono.getText())
                    .correo(txtCorreo.getText())
                    .direccion(txtDireccion.getText())
                    .genero(cbxGenero.getValue())
                    .build();

            if (idCiudadanoEdit != 0L) {
                ciudadano.setId(idCiudadanoEdit);
                ciudadanoService.update(idCiudadanoEdit, ciudadano);
                Toast.showToast(null, "Actualizado correctamente", 2000, 500, 300);
            } else {
                ciudadanoService.save(ciudadano);
                Toast.showToast(null, "Guardado correctamente", 2000, 500, 300);
            }
            limpiar();
            listar();
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    private void editCiudadano(Ciudadano c) {
        idCiudadanoEdit = c.getId();
        txtNombre.setText(c.getNombre());
        txtDni.setText(c.getDni());
        txtTelefono.setText(c.getTelefono());
        txtCorreo.setText(c.getCorreo());
        txtDireccion.setText(c.getDireccion());
        cbxGenero.setValue(c.getGenero());
        btnGuardar.setText("Actualizar");
    }

    private void deleteCiudadano(Ciudadano c) {
        ciudadanoService.delete(c.getId());
        listar();
        Toast.showToast(null, "Eliminado correctamente", 2000, 500, 300);
    }

    @FXML
    public void limpiar() {
        txtNombre.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtDireccion.clear();
        cbxGenero.setValue(null);
        idCiudadanoEdit = 0L;
        btnGuardar.setText("Guardar");
    }
}
