package UI.views;

import Model.Flower;
import Model.BouquetItem;
import Model.Packaging;
import Model.Decoration;
import database.dao.*;
import service.BouquetCalculator;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class CreateBouquetView {

    private FlowerDAO flowerDAO = new FlowerDAO();
    private PackagingDAO packagingDAO = new PackagingDAO();
    private DecorationDAO decorationDAO = new DecorationDAO();
    private BouquetDAO bouquetDAO = new BouquetDAO();

    private ComboBox<Flower> flowerComboBox;
    private Spinner<Integer> quantitySpinner;
    private TableView<BouquetItemRow> bouquetTable;
    private ObservableList<BouquetItemRow> bouquetItems;
    private Label totalPriceLabel;
    private ComboBox<Packaging> packagingComboBox;
    private ListView<Decoration> decorationListView;
    private List<BouquetItem> currentBouquet;
    private TextField bouquetNameField;

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        VBox topPanel = createTopPanel();
        VBox centerPanel = createCenterPanel();
        VBox bottomPanel = createBottomPanel();

        root.setTop(topPanel);
        root.setCenter(centerPanel);
        root.setBottom(bottomPanel);

        loadData();

        return root;
    }

    private VBox createTopPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(0, 0, 20, 0));

        Label title = new Label("Создание букета");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Название букета
        HBox nameRow = new HBox(10);
        nameRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label nameLabel = new Label("Название букета:");
        bouquetNameField = new TextField();
        bouquetNameField.setPromptText("Введите название (необязательно)");
        bouquetNameField.setPrefWidth(300);
        nameRow.getChildren().addAll(nameLabel, bouquetNameField);

        // Выбор цветов
        HBox inputRow = new HBox(10);
        inputRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label flowerLabel = new Label("Выберите цветок:");
        flowerComboBox = new ComboBox<>();
        flowerComboBox.setPrefWidth(200);

        Label quantityLabel = new Label("Количество:");
        quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setPrefWidth(80);
        quantitySpinner.setEditable(true);

        Button addButton = new Button("Добавить в букет");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> addFlowerToBouquet());

        Button refreshButton = new Button("🔄");
        refreshButton.setTooltip(new Tooltip("Обновить списки"));
        refreshButton.setOnAction(e -> loadData());

        inputRow.getChildren().addAll(flowerLabel, flowerComboBox, quantityLabel, quantitySpinner, addButton, refreshButton);

        panel.getChildren().addAll(title, nameRow, inputRow);
        return panel;
    }

    private VBox createCenterPanel() {
        VBox panel = new VBox(10);

        Label tableTitle = new Label("Состав букета:");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        bouquetItems = FXCollections.observableArrayList();
        currentBouquet = new ArrayList<>();

        bouquetTable = new TableView<>();
        bouquetTable.setItems(bouquetItems);
        bouquetTable.setPrefHeight(250);

        TableColumn<BouquetItemRow, String> nameCol = new TableColumn<>("Название цветка");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);

        TableColumn<BouquetItemRow, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);

        TableColumn<BouquetItemRow, Integer> priceCol = new TableColumn<>("Цена за шт.");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);

        TableColumn<BouquetItemRow, Integer> totalCol = new TableColumn<>("Сумма");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(100);

        TableColumn<BouquetItemRow, Void> actionCol = new TableColumn<>("Действие");
        actionCol.setPrefWidth(100);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Удалить");
            {
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteBtn.setOnAction(event -> {
                    BouquetItemRow item = getTableView().getItems().get(getIndex());
                    removeFlowerFromBouquet(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        bouquetTable.getColumns().addAll(nameCol, quantityCol, priceCol, totalCol, actionCol);

        panel.getChildren().addAll(tableTitle, bouquetTable);
        return panel;
    }

    private VBox createBottomPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20, 0, 0, 0));

        // Упаковка
        HBox packagingRow = new HBox(10);
        packagingRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label packagingLabel = new Label("Выберите упаковку:");
        packagingComboBox = new ComboBox<>();
        packagingComboBox.setPrefWidth(250);
        packagingComboBox.setOnAction(e -> updateTotalPrice());

        packagingRow.getChildren().addAll(packagingLabel, packagingComboBox);

        // Декорации
        HBox decoRow = new HBox(10);
        decoRow.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        Label decoLabel = new Label("Выберите декорации:");
        decorationListView = new ListView<>();
        decorationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        decorationListView.setPrefHeight(100);
        decorationListView.setPrefWidth(400);
        decorationListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());

        VBox decoBox = new VBox(5);
        decoBox.getChildren().addAll(decoLabel, decorationListView);

        // Итого
        HBox totalRow = new HBox(10);
        totalRow.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        totalPriceLabel = new Label("ИТОГО: 0 ₸");
        totalPriceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        Button clearButton = new Button("Очистить букет");
        clearButton.setOnAction(e -> clearBouquet());

        Button saveButton = new Button("Сохранить букет");
        saveButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;");
        saveButton.setOnAction(e -> saveBouquet());

        totalRow.getChildren().addAll(clearButton, saveButton, totalPriceLabel);

        panel.getChildren().addAll(packagingRow, decoBox, new Separator(), totalRow);
        return panel;
    }

    private void loadData() {
        // Загрузка цветов
        flowerComboBox.getItems().clear();
        flowerComboBox.getItems().addAll(flowerDAO.getAllFlowers());

        // Загрузка упаковки
        packagingComboBox.getItems().clear();
        packagingComboBox.getItems().add(null); // Опция "без упаковки"
        packagingComboBox.getItems().addAll(packagingDAO.getAllPackaging());
        packagingComboBox.setValue(null);

        // Загрузка декораций
        decorationListView.getItems().clear();
        decorationListView.getItems().addAll(decorationDAO.getAllDecorations());
    }

    private void addFlowerToBouquet() {
        Flower selectedFlower = flowerComboBox.getValue();
        if (selectedFlower == null) {
            showAlert("Ошибка", "Пожалуйста, выберите цветок");
            return;
        }

        int quantity = quantitySpinner.getValue();

        boolean found = false;
        for (int i = 0; i < currentBouquet.size(); i++) {
            BouquetItem item = currentBouquet.get(i);
            if (item.getFlower().getId() == selectedFlower.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                bouquetItems.get(i).setQuantity(item.getQuantity());
                bouquetItems.get(i).setTotal(item.getFlower().getPrice() * item.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            BouquetItem newItem = new BouquetItem(selectedFlower, quantity);
            currentBouquet.add(newItem);
            bouquetItems.add(new BouquetItemRow(
                    selectedFlower.getName(),
                    quantity,
                    selectedFlower.getPrice(),
                    selectedFlower.getPrice() * quantity
            ));
        }

        bouquetTable.refresh();
        updateTotalPrice();
    }

    private void removeFlowerFromBouquet(BouquetItemRow row) {
        bouquetItems.remove(row);
        currentBouquet.removeIf(item -> item.getFlower().getName().equals(row.getName()));
        updateTotalPrice();
    }

    private void clearBouquet() {
        bouquetItems.clear();
        currentBouquet.clear();
        packagingComboBox.setValue(null);
        decorationListView.getSelectionModel().clearSelection();
        bouquetNameField.clear();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        Packaging selectedPackaging = packagingComboBox.getValue();
        int total = BouquetCalculator.calculateTotal(currentBouquet, selectedPackaging);

        // Добавляем стоимость декораций
        for (Decoration deco : decorationListView.getSelectionModel().getSelectedItems()) {
            total += deco.getPrice();
        }

        totalPriceLabel.setText("ИТОГО: " + total + " ₸");
    }

    private void saveBouquet() {
        if (currentBouquet.isEmpty()) {
            showAlert("Ошибка", "Букет пустой! Добавьте цветы.");
            return;
        }

        String bouquetName = bouquetNameField.getText().trim();
        if (bouquetName.isEmpty()) {
            bouquetName = "Букет от " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        }

        Packaging selectedPackaging = packagingComboBox.getValue();
        List<Decoration> selectedDecorations = new ArrayList<>(decorationListView.getSelectionModel().getSelectedItems());

        int totalPrice = BouquetCalculator.calculateTotal(currentBouquet, selectedPackaging);
        for (Decoration deco : selectedDecorations) {
            totalPrice += deco.getPrice();
        }

        boolean success = bouquetDAO.saveBouquet(bouquetName, currentBouquet, selectedPackaging, selectedDecorations, totalPrice);

        if (success) {
            showAlert("Успех", "Букет успешно сохранен в базу данных!\n" +
                    "Название: " + bouquetName + "\n" +
                    "Общая стоимость: " + totalPrice + " ₸");
            clearBouquet();
        } else {
            showAlert("Ошибка", "Не удалось сохранить букет в базу данных");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class BouquetItemRow {
        private String name;
        private int quantity;
        private int price;
        private int total;

        public BouquetItemRow(String name, int quantity, int price, int total) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.total = this.price * quantity;
        }

        public int getPrice() { return price; }
        public void setPrice(int price) { this.price = price; }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
    }
}