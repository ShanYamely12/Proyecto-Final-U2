package pe.edu.upeu.sysdenuncias.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import pe.edu.upeu.sysdenuncias.config.AppContext;
import javafx.scene.control.Label;
import pe.edu.upeu.sysdenuncias.dto.SessionManager;
import pe.edu.upeu.sysdenuncias.model.Funcionario;

import java.io.IOException;

public class MainGuiController {

    @FXML private TabPane tabPaneFx;

    @FXML
    public void menuAction(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        String id = menuItem.getId();
        
        switch (id) {
            case "miDashboard" -> abrirTabConFXML("/view/dashboard.fxml", "Dashboard Interactivo");
            case "miCiudadanos" -> abrirTabConFXML("/view/ciudadano.fxml", "Gestión de Ciudadanos");
            case "miFuncionarios" -> abrirTabConFXML("/view/funcionario.fxml", "Gestión de Funcionarios");
            case "miTiposDenuncia" -> abrirTabConFXML("/view/tipodenuncia.fxml", "Tipos de Denuncia");
            case "miDenuncias" -> abrirTabConFXML("/view/denuncia.fxml", "Gestión de Denuncias");
            case "miSalir" -> javafx.application.Platform.exit();
        }
    }
    @FXML
    private Label lblUsuario;

    private void abrirTabConFXML(String fxmlPath, String tituloTab) {
            try {

                AppContext ctx = AppContext.getInstance();

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                loader.setControllerFactory(ctx::getBean);

                Parent root = loader.load();

                ScrollPane scrollPane = new ScrollPane(root);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);

                for (Tab tab : tabPaneFx.getTabs()) {
                    if (tab.getText().equals(tituloTab)) {
                        tabPaneFx.getSelectionModel().select(tab);
                        return;
                    }
                }

                Tab newTab = new Tab(tituloTab, scrollPane);
                tabPaneFx.getTabs().add(newTab);
                tabPaneFx.getSelectionModel().select(newTab);
            } catch (Exception e) {

                System.out.println("ERROR AL CARGAR:");
                e.printStackTrace();

            }
        }

    @FXML
    public void initialize() {

        Funcionario func =
                SessionManager.getInstance()
                        .getFuncionarioLogueado();

        if(func != null) {

            lblUsuario.setText(
                    "Bienvenido: "
                            + func.getNombre()
                            + " | Cargo: "
                            + func.getCargo()
            );
        }
    }

}
