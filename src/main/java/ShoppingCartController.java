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

    private ResourceBundle bundle;
    private final List<TextField> priceFields = new ArrayList<>();
    private final List<TextField> quantityFields = new ArrayList<>();

    @FXML
    public void initialize() {
        languageComboBox.setItems(FXCollections.observableArrayList(
                "English", "Finnish", "Swedish", "Japanese", "Arabic"
        ));
        languageComboBox.getSelectionModel().selectFirst();
        bundle = ResourceBundle.getBundle("MessagesBundle", new Locale("en", "US"));
        updateUI();
    }

    @FXML
    private void onConfirmLanguage() {
        int selectedIndex = languageComboBox.getSelectionModel().getSelectedIndex();
        Locale locale = ShoppingCart.getLocale(selectedIndex + 1);
        bundle = ResourceBundle.getBundle("MessagesBundle", locale);
        updateUI();
        rebuildItemLabels();

        // Handle Arabic RTL
        VBox root = (VBox) languageLabel.getScene().getRoot();
        if ("ar".equals(locale.getLanguage())) {
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

            Label priceLabel = new Label(MessageFormat.format(bundle.getString("priceLabel"), i + 1));
            TextField priceField = new TextField();
            priceField.setPrefWidth(80);

            Label quantityLabel = new Label(MessageFormat.format(bundle.getString("quantityLabel"), i + 1));
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
            totalLabel.setText(bundle.getString("totalLabel") + " " + total);
        } catch (NumberFormatException e) {
            totalLabel.setText(bundle.getString("totalLabel") + " Error");
        }
    }

    private void updateUI() {
        languageLabel.setText(bundle.getString("selectLanguage"));
        confirmLanguageBtn.setText(bundle.getString("confirmLanguage"));
        numberOfItemsLabel.setText(bundle.getString("prompt1"));
        numberOfItemsField.setPromptText(bundle.getString("numberOfItemsPlaceholder"));
        enterItemsBtn.setText(bundle.getString("enterItems"));
        calculateBtn.setText(bundle.getString("calculateTotal"));
        totalLabel.setText(bundle.getString("totalLabel"));
    }

    private void rebuildItemLabels() {
        List<javafx.scene.Node> rows = itemsContainer.getChildren();
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i) instanceof HBox row) {
                Label priceLabel = (Label) row.getChildren().get(0);
                Label quantityLabel = (Label) row.getChildren().get(2);
                priceLabel.setText(MessageFormat.format(bundle.getString("priceLabel"), i + 1));
                quantityLabel.setText(MessageFormat.format(bundle.getString("quantityLabel"), i + 1));
            }
        }
    }
}
