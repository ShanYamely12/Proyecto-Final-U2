package pe.edu.upeu.sysdenuncias.components;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TableViewHelper<T> {

    public void addColumnsInOrderWithSize(TableView<T> tableView, LinkedHashMap<String, ColumnInfo> columns, Consumer<T> updateAction, Consumer<T> deleteAction) {
        for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
            TableColumn<T, Object> column = new TableColumn<>(entry.getKey());
            String field = entry.getValue().getField();

            if (field.contains(".")) {
                column.setCellValueFactory(cellData -> {
                    T item = cellData.getValue();
                    String[] fieldPath = field.split("\\.");
                    try {
                        Object value = item.getClass().getMethod("get" + capitalize(fieldPath[0])).invoke(item);
                        if (value != null) {
                            Object nestedValue = value.getClass().getMethod("get" + capitalize(fieldPath[1])).invoke(value);
                            return new SimpleObjectProperty<>(nestedValue != null ? nestedValue : "N/A");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new SimpleObjectProperty<>("N/A");
                });
            } else {
                column.setCellValueFactory(new PropertyValueFactory<>(field));
            }

            if (entry.getValue().getWidth() != null) {
                column.setPrefWidth(entry.getValue().getWidth());
            }

            tableView.getColumns().add(column);
        }

        addActionColumn(tableView, updateAction, deleteAction);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }

    private void addActionColumn(TableView<T> tableView, Consumer<T> updateAction, Consumer<T> deleteAction) {
        TableColumn<T, Void> actionColumn = new TableColumn<>("Acciones");

        Callback<TableColumn<T, Void>, TableCell<T, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<T, Void> call(final TableColumn<T, Void> param) {
                return new TableCell<>() {
                    private final Button btnUpdate = new Button("Editar");
                    private final Button btnDelete = new Button("Elim.");

                    {
                        btnUpdate.setOnAction(event -> {
                            T data = getTableView().getItems().get(getIndex());
                            updateAction.accept(data);
                        });
                        btnDelete.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                        btnDelete.setOnAction(event -> {
                            T data = getTableView().getItems().get(getIndex());
                            deleteAction.accept(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(10, btnUpdate, btnDelete);
                            btnUpdate.setPrefWidth(80);
                            btnDelete.setPrefWidth(80);
                            buttons.setSpacing(5);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };

        actionColumn.setCellFactory(cellFactory);
        actionColumn.setMinWidth(180);
        tableView.getColumns().add(actionColumn);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
