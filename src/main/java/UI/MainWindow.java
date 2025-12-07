package UI;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import UI.views.*;

public class MainWindow {

    private final BorderPane root = new BorderPane();

    private final CreateBouquetView createBouquetView = new CreateBouquetView();
    private final FlowersCatalogView flowersCatalogView = new FlowersCatalogView();
    private final PackagingView packagingView = new PackagingView();
    private final DecorationView decorationView = new DecorationView();
    private final SavedBouquetsView savedBouquetsView = new SavedBouquetsView();

    public Parent createContent() {

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #f5f5f5;");
        sidebar.setPrefWidth(200);

        Button btnCreateBouquet = new Button("Создать букет");
        Button btnSavedBouquets = new Button("Мои букеты");
        Button btnFlowers = new Button("Каталог цветов");
        Button btnPackages = new Button("Упаковка");
        Button btnDecor = new Button("Декор");

        // Стилизация кнопок
        String buttonStyle = "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-font-size: 14px; -fx-cursor: hand;";

        btnCreateBouquet.setStyle(buttonStyle);
        btnSavedBouquets.setStyle(buttonStyle);
        btnFlowers.setStyle(buttonStyle);
        btnPackages.setStyle(buttonStyle);
        btnDecor.setStyle(buttonStyle);

        btnCreateBouquet.setPrefWidth(160);
        btnSavedBouquets.setPrefWidth(160);
        btnFlowers.setPrefWidth(160);
        btnPackages.setPrefWidth(160);
        btnDecor.setPrefWidth(160);

        // События
        btnCreateBouquet.setOnAction(e -> {
            root.setCenter(createBouquetView.getView());
            highlightButton(btnCreateBouquet);
        });

        btnSavedBouquets.setOnAction(e -> {
            root.setCenter(savedBouquetsView.getView());
            highlightButton(btnSavedBouquets);
        });

        btnFlowers.setOnAction(e -> {
            root.setCenter(flowersCatalogView.getView());
            highlightButton(btnFlowers);
        });

        btnPackages.setOnAction(e -> {
            root.setCenter(packagingView.getView());
            highlightButton(btnPackages);
        });

        btnDecor.setOnAction(e -> {
            root.setCenter(decorationView.getView());
            highlightButton(btnDecor);
        });

        sidebar.getChildren().addAll(
                btnCreateBouquet,
                btnSavedBouquets,
                btnFlowers,
                btnPackages,
                btnDecor
        );

        root.setLeft(sidebar);

        // По умолчанию открываем экран создания букета
        root.setCenter(createBouquetView.getView());
        highlightButton(btnCreateBouquet);

        return root;
    }

    private void highlightButton(Button activeButton) {
        VBox sidebar = (VBox) root.getLeft();
        String normalStyle = "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10; -fx-font-size: 14px; -fx-cursor: hand;";
        String activeStyle = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-color: #1976D2; -fx-border-width: 1; -fx-padding: 10; -fx-font-size: 14px; -fx-cursor: hand;";

        sidebar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                ((Button) node).setStyle(normalStyle);
            }
        });

        activeButton.setStyle(activeStyle);
    }
}