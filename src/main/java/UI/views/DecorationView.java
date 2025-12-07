package UI.views;

import Model.Decoration;
import database.dao.DecorationDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DecorationView {

    private DecorationDAO decorationDAO = new DecorationDAO();
    private FlowPane flowPane;
    private BorderPane root;

    public Parent getView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        HBox topBox = new HBox(20);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        Label title = new Label("Декор для букета");
        title.setFont(new Font("Arial", 24));
        title.setStyle("-fx-font-weight: bold;");

        Button addButton = new Button("+ Добавить декор");
        addButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;");
        addButton.setOnAction(e -> showAddDecorationDialog());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBox.getChildren().addAll(title, spacer, addButton);

        flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(10));

        loadDecorations();

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white;");

        root.setTop(topBox);
        root.setCenter(scrollPane);

        return root;
    }

    private void loadDecorations() {
        flowPane.getChildren().clear();
        for (Decoration deco : decorationDAO.getAllDecorations()) {
            VBox decoCard = createDecorationCard(deco);
            flowPane.getChildren().add(decoCard);
        }
    }

    private VBox createDecorationCard(Decoration deco) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(200);
        card.setMinHeight(280);

        Node imageRegion;
        try {
            if (deco.getImagePath() != null && !deco.getImagePath().isEmpty()) {
                File imgFile = new File(System.getProperty("user.dir"), deco.getImagePath());
                if (imgFile.exists()) {
                    Image img = new Image(imgFile.toURI().toString(), 150, 150, true, true);
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(150);
                    iv.setFitHeight(150);
                    imageRegion = iv;
                } else {
                    StackPane placeholder = new StackPane();
                    placeholder.setPrefSize(150, 150);
                    placeholder.setStyle("-fx-background-color: #fff9e6; -fx-border-color: #ddd;");
                    Label imageLabel = new Label("✨");
                    imageLabel.setStyle("-fx-font-size: 48px;");
                    placeholder.getChildren().add(imageLabel);
                    imageRegion = placeholder;
                }
            } else {
                StackPane placeholder = new StackPane();
                placeholder.setPrefSize(150, 150);
                placeholder.setStyle("-fx-background-color: #fff9e6; -fx-border-color: #ddd;");
                Label imageLabel = new Label("✨");
                imageLabel.setStyle("-fx-font-size: 48px;");
                placeholder.getChildren().add(imageLabel);
                imageRegion = placeholder;
            }
        } catch (Exception ex) {
            StackPane placeholder = new StackPane();
            placeholder.setPrefSize(150, 150);
            placeholder.setStyle("-fx-background-color: #fff9e6; -fx-border-color: #ddd;");
            Label imageLabel = new Label("✨");
            imageLabel.setStyle("-fx-font-size: 48px;");
            placeholder.getChildren().add(imageLabel);
            imageRegion = placeholder;
        }

        Label nameLabel = new Label(deco.getName());
        nameLabel.setFont(new Font("Arial", 16));
        nameLabel.setStyle("-fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(180);

        Label priceLabel = new Label(deco.getPrice() + " ₸");
        priceLabel.setFont(new Font("Arial", 14));
        priceLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button editBtn = new Button("✏️");
        editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-cursor: hand;");
        editBtn.setOnAction(e -> showEditDecorationDialog(deco));

        Button deleteBtn = new Button("🗑️");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> deleteDecoration(deco));

        buttonBox.getChildren().addAll(editBtn, deleteBtn);

        card.getChildren().addAll(imageRegion, nameLabel, priceLabel, buttonBox);

        return card;
    }

    private void showAddDecorationDialog() {
        Dialog<Decoration> dialog = new Dialog<>();
        dialog.setTitle("Добавить декор");
        dialog.setHeaderText("Введите данные нового декора");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Название");
        TextField priceField = new TextField();
        priceField.setPromptText("Цена");
        TextField imageField = new TextField();
        imageField.setPromptText("Путь к изображению");
        imageField.setText("images/default.png");

        Button browseBtn = new Button("Обзор...");
        browseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Выберите изображение");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selected = chooser.showOpenDialog(root.getScene() != null ? root.getScene().getWindow() : null);
            if (selected != null) {
                try {
                    File imagesDir = new File(System.getProperty("user.dir"), "images");
                    if (!imagesDir.exists()) imagesDir.mkdirs();
                    File dest = new File(imagesDir, selected.getName());
                    Files.copy(selected.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    imageField.setText("images/" + dest.getName());
                } catch (Exception ex) {
                    showAlert("Ошибка", "Не удалось скопировать файл изображения: " + ex.getMessage());
                }
            }
        });

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Цена (₸):"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Изображение:"), 0, 2);
        HBox imgRow = new HBox(10, imageField, browseBtn);
        grid.add(imgRow, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText().trim();
                    int price = Integer.parseInt(priceField.getText().trim());
                    String imagePath = imageField.getText().trim();

                    if (name.isEmpty()) {
                        showAlert("Ошибка", "Название не может быть пустым");
                        return null;
                    }

                    return new Decoration(name, price, imagePath);
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Цена должна быть числом");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(decoration -> {
            if (decorationDAO.addDecoration(decoration)) {
                showAlert("Успех", "Декор успешно добавлен!");
                loadDecorations();
            } else {
                String err = decorationDAO.getLastError();
                showAlert("Ошибка", "Не удалось добавить декор" + (err != null ? ":\n" + err : ""));
            }
        });
    }

    private void showEditDecorationDialog(Decoration deco) {
        Dialog<Decoration> dialog = new Dialog<>();
        dialog.setTitle("Редактировать декор");
        dialog.setHeaderText("Измените данные декора");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(deco.getName());
        TextField priceField = new TextField(String.valueOf(deco.getPrice()));
        TextField imageField = new TextField(deco.getImagePath());

        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Цена (₸):"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Изображение:"), 0, 2);
        grid.add(imageField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    deco.setName(nameField.getText().trim());
                    deco.setPrice(Integer.parseInt(priceField.getText().trim()));
                    deco.setImagePath(imageField.getText().trim());
                    return deco;
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Цена должна быть числом");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedDecoration -> {
            if (decorationDAO.updateDecoration(updatedDecoration)) {
                showAlert("Успех", "Декор успешно обновлен!");
                loadDecorations();
            } else {
                showAlert("Ошибка", "Не удалось обновить декор");
            }
        });
    }

    private void deleteDecoration(Decoration deco) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Удалить декор?");
        alert.setContentText("Вы действительно хотите удалить " + deco.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (decorationDAO.deleteDecoration(deco.getId())) {
                    showAlert("Успех", "Декор успешно удален!");
                    loadDecorations();
                } else {
                    showAlert("Ошибка", "Не удалось удалить декор");
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}