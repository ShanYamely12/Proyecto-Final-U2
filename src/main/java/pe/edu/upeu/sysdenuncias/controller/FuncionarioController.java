package pe.edu.upeu.sysdenuncias.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import pe.edu.upeu.sysdenuncias.components.ColumnInfo;
import pe.edu.upeu.sysdenuncias.components.TableViewHelper;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.dto.SessionManager;
import pe.edu.upeu.sysdenuncias.enums.Cargo;
import pe.edu.upeu.sysdenuncias.model.Funcionario;
import pe.edu.upeu.sysdenuncias.service.IFuncionarioService;

import java.util.LinkedHashMap;

public class FuncionarioController {

    private final IFuncionarioService funcionarioService;
    private ObservableList<Funcionario> listarFuncionarios;

    @FXML private TextField txtNombre;
    @FXML
    private ComboBox<Cargo> cbxCargo;
    @FXML private TextField txtCredenciales;

    @FXML private TableView<Funcionario> tableView;
    @FXML private Button btnGuardar;

    private Long idFuncionarioEdit = 0L;

    public FuncionarioController(IFuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @FXML
    public void initialize() {
        TableViewHelper<Funcionario> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("id", 50.0));
        columns.put("Nombre", new ColumnInfo("nombre", 150.0));
        columns.put("Cargo", new ColumnInfo("cargo", 150.0));

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, this::editFuncionario, this::deleteFuncionario);
        listar();
        cbxCargo.setItems(
                FXCollections.observableArrayList(Cargo.values())
        );
        Funcionario logueado =
                SessionManager.getInstance()
                        .getFuncionarioLogueado();

        if(logueado.getCargo() != Cargo.ADMINISTRADOR){
            btnGuardar.setDisable(true);
        }

    }

    private void listar() {
        listarFuncionarios = FXCollections.observableArrayList(funcionarioService.findAll());
        tableView.setItems(listarFuncionarios);
    }

    @FXML
    public void guardar() {
        try {

            Funcionario funcionario = Funcionario.builder()
                    .nombre(txtNombre.getText())
                    .cargo(cbxCargo.getValue())
                    .credenciales(txtCredenciales.getText())
                    .build();

            if (idFuncionarioEdit != 0L) {
                funcionario.setId(idFuncionarioEdit);
                funcionarioService.update(idFuncionarioEdit, funcionario);
                Toast.showToast(null, "Actualizado correctamente", 2000, 500, 300);
            } else {
                funcionarioService.save(funcionario);
                Toast.showToast(null, "Guardado correctamente", 2000, 500, 300);
            }
            limpiar();
            listar();
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    private void editFuncionario(Funcionario f) {
        idFuncionarioEdit = f.getId();
        txtNombre.setText(f.getNombre());
        cbxCargo.setValue(f.getCargo());        txtCredenciales.setText(f.getCredenciales());
        btnGuardar.setText("Actualizar");
    }

    private void deleteFuncionario(Funcionario f) {
        funcionarioService.delete(f.getId());
        listar();
        Toast.showToast(null, "Eliminado correctamente", 2000, 500, 300);
    }

    @FXML
    public void limpiar() {
        txtNombre.clear();
        cbxCargo.setValue(null);
        txtCredenciales.clear();
        idFuncionarioEdit = 0L;
        btnGuardar.setText("Guardar");
    }
}
