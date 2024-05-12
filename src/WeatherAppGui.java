import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    // Constructor for the WeatherAppGui class
    public WeatherAppGui() {

        // Set the title of the window
        super("Weather App");

        // Set the default close operation
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set the size of the window
        setSize(450, 650);

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Disable layout manager to use absolute positioning
        setLayout(null);

        // Disable window resizing
        setResizable(false);

        // Add GUI components to the window
        addGuiComponents();
    }

    // Method to add GUI components to the window
    private void addGuiComponents() {

        // Create a text field for searching
        JTextField searchTextField = new JTextField();

        // Set the position and size of the text field
        searchTextField.setBounds(15, 15, 351, 45);

        // Set the font of the text field
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        // Add the text field to the window
        add(searchTextField);

        // Create a label for displaying weather condition image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // Create a label for displaying temperature
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // Create a label for displaying weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // Create a label for displaying humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // Create a label for displaying humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // Create a label for displaying wind speed image
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);

        // Create a label for displaying wind speed text
        JLabel windSpeedText = new JLabel("<html><b>Wind Speed</b> 15km</html>");
        windSpeedText.setBounds(310, 500, 85, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        // Create a search button with an image
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        // Set the cursor to a hand cursor when hovering over the button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String userInput = searchTextField.getText();

                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }
                weatherData = WeatherApp.getWeatherData(userInput);

                if (weatherData == null) {
                    return;
                }

                String weatherCondition = (String) weatherData.get("weather_condition");

                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                weatherConditionDesc.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed</b> " + windspeed + "kn</html>");
            }
        });
        add(searchButton);
    }

    // Method to load an image from a file
    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // If image loading fails, print an error message
        System.out.println("Could not find resource");
        return null;
    }
}
