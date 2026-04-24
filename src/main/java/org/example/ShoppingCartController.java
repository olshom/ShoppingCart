package org.example;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ShoppingCartController {

    @FXML private Label languageLabel;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private Button confirmLanguageBtn;
    @FXML private Label numberOfItemsLabel;
    @FXML private TextField numberOfItemsField;
    @FXML private Button enterItemsBtn;
    @FXML private VBox itemsContainer;
    @FXML private Button calculateBtn;
    @FXML private Label totalLabel;

    private LocalizationService localizationService;
    private CartService cartService;
    private final List<TextField> priceFields = new ArrayList<>();
    private final List<TextField> quantityFields = new ArrayList<>();

    @FXML
    public void initialize() {
        languageComboBox.setItems(FXCollections.observableArrayList(
                "English", "Finnish", "Swedish", "Japanese", "Arabic"
        ));
        languageComboBox.getSelectionModel().selectFirst();
        try {
            localizationService = new LocalizationService("English");
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to initialize localization", e);
        }
        cartService = new CartService();
        updateUI();
    }

    @FXML
    private void onConfirmLanguage() {
        String language = languageComboBox.getSelectionModel().getSelectedItem();
        try {
            localizationService = new LocalizationService(language);
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to load language: " + language, e);
        }
        updateUI();
        rebuildItemLabels();

        // Handle Arabic RTL
        VBox root = (VBox) languageLabel.getScene().getRoot();
        if ("Arabic".equals(localizationService.getSelectedLanguage())) {
            root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        } else {
            root.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        }
    }

    @FXML
    private void onEnterItems() {
        String text = numberOfItemsField.getText().trim();
        int numberOfItems;
        try {
            numberOfItems = Integer.parseInt(text);
            if (numberOfItems <= 0) return;
        } catch (NumberFormatException e) {
            return;
        }

        itemsContainer.getChildren().clear();
        priceFields.clear();
        quantityFields.clear();

        for (int i = 0; i < numberOfItems; i++) {
            HBox row = new HBox(10);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label priceLabel = new Label(MessageFormat.format(localizationService.getTranslation("priceLabel"), i + 1));
            TextField priceField = new TextField();
            priceField.setPrefWidth(80);

            Label quantityLabel = new Label(MessageFormat.format(localizationService.getTranslation("quantityLabel"), i + 1));
            TextField quantityField = new TextField();
            quantityField.setPrefWidth(80);

            row.getChildren().addAll(priceLabel, priceField, quantityLabel, quantityField);
            itemsContainer.getChildren().add(row);

            priceFields.add(priceField);
            quantityFields.add(quantityField);
        }
    }

    @FXML
    private void onCalculateTotal() {
        if (priceFields.isEmpty()) return;

        try {
            int[] prices = new int[priceFields.size()];
            int[] quantities = new int[quantityFields.size()];

            for (int i = 0; i < priceFields.size(); i++) {
                prices[i] = Integer.parseInt(priceFields.get(i).getText().trim());
                quantities[i] = Integer.parseInt(quantityFields.get(i).getText().trim());
            }

            int total = ShoppingCart.calculateTotal(prices, quantities);
            totalLabel.setText(localizationService.getTranslation("totalLabel") + " " + total);

            cartService.saveFullCart(prices.length, total, localizationService.getSelectedLanguage(), prices, quantities);
        } catch (NumberFormatException | java.sql.SQLException e) {
            totalLabel.setText(localizationService.getTranslation("totalLabel") + " Error");
        }
    }

    private void updateUI() {
        languageLabel.setText(localizationService.getTranslation("selectLanguage"));
        confirmLanguageBtn.setText(localizationService.getTranslation("confirmLanguage"));
        numberOfItemsLabel.setText(localizationService.getTranslation("prompt1"));
        numberOfItemsField.setPromptText(localizationService.getTranslation("numberOfItemsPlaceholder"));
        enterItemsBtn.setText(localizationService.getTranslation("enterItems"));
        calculateBtn.setText(localizationService.getTranslation("calculateTotal"));
        totalLabel.setText(localizationService.getTranslation("totalLabel"));
    }

    private void rebuildItemLabels() {
        List<javafx.scene.Node> rows = itemsContainer.getChildren();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i) instanceof HBox row) {
                Label priceLabel = (Label) row.getChildren().get(0);
                Label quantityLabel = (Label) row.getChildren().get(2);
                priceLabel.setText(MessageFormat.format(localizationService.getTranslation("priceLabel"), i + 1));
                quantityLabel.setText(MessageFormat.format(localizationService.getTranslation("quantityLabel"), i + 1));
            }
        }
    }
}
