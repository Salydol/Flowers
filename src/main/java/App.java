import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import UI.MainWindow;
import database.DatabaseInitializer;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Инициализируем базу данных перед запуском приложения
        DatabaseInitializer.initializeDatabase();
        
        MainWindow mainWindow = new MainWindow();
        Scene scene = new Scene(mainWindow.createContent(), 1100, 700);

        stage.setTitle("Flower Shop - Создание букета");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
