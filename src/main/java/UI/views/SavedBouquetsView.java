package UI.views;

import database.dao.BouquetDAO;
import database.dao.BouquetDAO.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.time.format.DateTimeFormatter;

public class SavedBouquetsView {

    private BouquetDAO bouquetDAO = new BouquetDAO();
    private VBox bouquetsContainer;
    private BorderPane root;

    public Parent getView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        // Верхняя панель
        HBox topBox = new HBox(20);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(0, 0, 20, 0));

        Label title = new Label("Сохраненные букеты");
        title.setFont(new Font("Arial", 24));
        title.setStyle("-fx-font-weight: bold;");

        Button refreshButton = new Button("🔄 Обновить");
        refreshButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;");
        refreshButton.setOnAction(e -> loadBouquets());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBox.getChildren().addAll(title, spacer, refreshButton);

        // Контейнер для букетов
        bouquetsContainer = new VBox(15);
        bouquetsContainer.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(bouquetsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white;");

        root.setTop(topBox);
        root.setCenter(scrollPane);

        loadBouquets();

        return root;
    }

    private void loadBouquets() {
        bouquetsContainer.getChildren().clear();

        for (BouquetInfo info : bouquetDAO.getAllBouquets()) {
            VBox bouquetCard = createBouquetCard(info);
            bouquetsContainer.getChildren().add(bouquetCard);
        }

        if (bouquetsContainer.getChildren().isEmpty()) {
            Label emptyLabel = new Label("Нет сохраненных букетов");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
            bouquetsContainer.getChildren().add(emptyLabel);
        }
    }

    private VBox createBouquetCard(BouquetInfo info) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Заголовок букета
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(info.getName() != null ? info.getName() : "Букет #" + info.getId());
        nameLabel.setFont(new Font("Arial", 18));
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label priceLabel = new Label(info.getTotalPrice() + " ₸");
        priceLabel.setFont(new Font("Arial", 20));
        priceLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button detailsButton = new Button("Подробнее");
        detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        detailsButton.setOnAction(e -> showBouquetDetails(info));

        header.getChildren().addAll(nameLabel, spacer, priceLabel, detailsButton);

        // Информация
        VBox infoBox = new VBox(5);

        if (info.getPackagingName() != null) {
            Label packagingLabel = new Label("📦 Упаковка: " + info.getPackagingName());
            packagingLabel.setStyle("-fx-font-size: 14px;");
            infoBox.getChildren().add(packagingLabel);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Label dateLabel = new Label("🕒 Создан: " + info.getCreatedAt().toLocalDateTime().format(formatter));
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        infoBox.getChildren().add(dateLabel);

        card.getChildren().addAll(header, infoBox);

        return card;
    }

    private void showBouquetDetails(BouquetInfo info) {
        BouquetDetails details = bouquetDAO.getBouquetDetails(info.getId());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Детали букета");
        alert.setHeaderText(info.getName() != null ? info.getName() : "Букет #" + info.getId());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Цветы
        Label flowersTitle = new Label("Цветы:");
        flowersTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(flowersTitle);

        for (FlowerDetail flower : details.getFlowers()) {
            Label flowerLabel = new Label(String.format("  • %s - %d шт. по %d ₸ = %d ₸",
                    flower.getName(), flower.getQuantity(), flower.getPrice(),
                    flower.getQuantity() * flower.getPrice()));
            content.getChildren().add(flowerLabel);
        }

        // Декорации
        if (!details.getDecorations().isEmpty()) {
            Label decoTitle = new Label("\nДекорации:");
            decoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            content.getChildren().add(decoTitle);

            for (DecorationDetail deco : details.getDecorations()) {
                Label decoLabel = new Label(String.format("  • %s - %d ₸",
                        deco.getName(), deco.getPrice()));
                content.getChildren().add(decoLabel);
            }
        }

        // Упаковка
        if (info.getPackagingName() != null) {
            Label packagingLabel = new Label("\nУпаковка: " + info.getPackagingName());
            packagingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            content.getChildren().add(packagingLabel);
        }

        // Итого
        Label totalLabel = new Label("\nИтого: " + info.getTotalPrice() + " ₸");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #4CAF50;");
        content.getChildren().add(totalLabel);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);

        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }
}