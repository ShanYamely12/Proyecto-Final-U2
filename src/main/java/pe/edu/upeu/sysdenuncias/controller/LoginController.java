package pe.edu.upeu.sysdenuncias.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.sysdenuncias.components.StageManager;
import pe.edu.upeu.sysdenuncias.components.Toast;
import pe.edu.upeu.sysdenuncias.config.AppContext;
import pe.edu.upeu.sysdenuncias.dto.SessionManager;
import pe.edu.upeu.sysdenuncias.model.Funcionario;
import pe.edu.upeu.sysdenuncias.service.IFuncionarioService;

import java.io.IOException;

public class LoginController {

    private final IFuncionarioService funcionarioService;

    public LoginController(IFuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @FXML TextField txtUsuario;
    @FXML PasswordField txtClave;
    @FXML Button btnIngresar;

    @FXML
    public void login(ActionEvent event) {
        try {
            funcionarioService.loginFuncionario(txtUsuario.getText(), txtClave.getText())
                    .ifPresentOrElse(
                            func -> abrirMain(event, func),
                            () -> mostrarError(event)
                    );
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
        }
    }

    private void abrirMain(ActionEvent event, Funcionario func) {
        try {
            SessionManager.getInstance().setFuncionarioLogueado(func);

            AppContext ctx = AppContext.getInstance();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/maingui.fxml"));
            loader.setControllerFactory(ctx::getBean);
            Parent mainRoot = loader.load();
            
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getBounds();
            Scene mainScene = new Scene(mainRoot, 1100, 650);;

            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            mainScene.getStylesheets().add(cssPath);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.setTitle("Sistema de Denuncias Ciudadanas");
            stage.setResizable(true);

            StageManager.setPrimaryStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        double w = stage.getWidth() * 1.5;
        double h = stage.getHeight() / 2;
        Toast.showToast(stage, "Credenciales inválidas, intente nuevamente", 2000, w, h);
    }

    @FXML
    public void cerrar(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }
}
