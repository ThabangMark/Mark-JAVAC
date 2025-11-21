package banking;

import banking.util.DatabaseUtil;
import banking.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class BankingApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseUtil.initializeDatabase();

        LoginView loginView = new LoginView(primaryStage);
        loginView.show();
    }

    @Override
    public void stop() {
        DatabaseUtil.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}