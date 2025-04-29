import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherApp extends Application {

    private TextField locationField;
    private Label weatherLabel;
    private ImageView weatherIcon;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather Information App");

        locationField = new TextField();
        locationField.setPromptText("Enter city name");

        Button getWeatherButton = new Button("Get Weather");
        weatherLabel = new Label();
        weatherIcon = new ImageView();

        getWeatherButton.setOnAction(event -> {
            String location = locationField.getText().trim();
            if (!location.isEmpty()) {
                try {
                    fetchWeatherData(location);
                } catch (IOException e) {
                    weatherLabel.setText("Error fetching weather data. Please check the location.");
                }
            } else {
                weatherLabel.setText("Please enter a location.");
            }
        });

        // Create a VBox layout and set padding
        VBox layout = new VBox(10, locationField, getWeatherButton, weatherLabel, weatherIcon);
        layout.setPadding(new Insets(10));

        // Set the background image
        Image backgroundImage = new Image(getClass().getResourceAsStream("/images/background.jpg"));
        BackgroundImage myBI = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true));
        layout.setBackground(new Background(myBI));

        // Create the scene and set it on the primary stage
        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchWeatherData(String location) throws IOException {
        String apiKey = ""; // Replace with your API key
        String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey + "&units=metric";

        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            weatherLabel.setText("Error fetching weather data.");
            return;
        }

        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        // Process the response manually to extract data
        String responseString = response.toString();
        String description = extractValue(responseString, "main");
        String temp = extractValue(responseString, "temp");
        String humidity = extractValue(responseString, "humidity"); // Get humidity value

        // Set the text with styling
        String weatherInfo = "Temperature: " + temp + "°C\nCondition: " + description + "\nHumidity: " + humidity + "%";
        weatherLabel.setText(weatherInfo);
        weatherLabel.setStyle("-fx-text-fill: Orange; -fx-font-weight: bold; -fx-font-size: 32px;"); // Change color, weight, and size

        displayWeatherIcon(description);
    }

    // Helper method to extract values from the JSON response string
    private String extractValue(String response, String key) {
        String keyWithQuotes = "\"" + key + "\":";
        int index = response.indexOf(keyWithQuotes);
        if (index == -1) return null;

        int startIndex = index + keyWithQuotes.length();
        int endIndex = response.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = response.indexOf("}", startIndex);
        }

        return response.substring(startIndex, endIndex).replaceAll("[\" }]", "").trim(); // Clean up the value
    }

    private void displayWeatherIcon(String weatherDescription) {
        String imageName;

        switch (weatherDescription.toLowerCase()) {
            case "clear":
                imageName = "sun.jpeg";
                break;
            case "clouds":
                imageName = "cloud.jpeg";
                break;
            case "rain":
                imageName = "rain.png";
                break;
            case "snow":
                imageName = "snow.jpeg";
                break;
            case "thunderstorm":
                imageName = "thunder.jpeg";
                break;
            default:
                imageName = "default.jpg"; // A default image if no specific icon matches
                break;
        }

        Image icon = new Image(getClass().getResourceAsStream("/images/" + imageName));
        weatherIcon.setImage(icon);
        weatherIcon.setFitWidth(50);  // Adjust as needed
        weatherIcon.setFitHeight(50); // Adjust as needed
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
