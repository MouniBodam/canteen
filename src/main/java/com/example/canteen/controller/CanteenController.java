package com.example.canteen.controller;

import com.example.canteen.model.Canteen;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CanteenController {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // Method to get a connection to the database
    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Method to list all tokens in the canteen
    @GetMapping("/")
    public String home(Model model) {
        List<Canteen> tokens = getAllTokens();
        model.addAttribute("tokens", tokens);
        return "index";  // Corresponds to index.html
    }

    // Method to show the form for adding a new token
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("canteen", new Canteen());
        return "add-token";  // Corresponds to add-token.html
    }

    // Method to handle adding a new token
    @PostMapping("/add")
    public String addToken(@ModelAttribute Canteen canteen, Model model) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO canteen (tokenId, itemName, price, issue, createdAt) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            Timestamp currentTimestamp = Timestamp.valueOf(java.time.LocalDateTime.now());

            statement.setLong(1, canteen.getTokenId());
            statement.setString(2, canteen.getItemName());
            statement.setLong(3, canteen.getPrice());
            statement.setString(4, canteen.getIssue());
            statement.setString(5, currentTimestamp.toString());

            statement.executeUpdate();
            model.addAttribute("success", "Successfully added token for item: " + canteen.getItemName());
            return "add-token";
        } catch (SQLException e) {
            model.addAttribute("error", "Error adding token: " + e.getMessage());
            e.printStackTrace();
            return "add-token";
        }
    }

    // Method to search tokens by item name
    @GetMapping("/search")
    public String searchTokens(@RequestParam String name, Model model) {
        List<Canteen> tokens = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM canteen WHERE itemName LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Canteen canteen = new Canteen();
                canteen.setTokenId(resultSet.getLong("tokenId"));
                canteen.setItemName(resultSet.getString("itemName"));
                canteen.setPrice(resultSet.getLong("price"));
                canteen.setIssue(resultSet.getString("issue"));
                canteen.setCreatedAt(resultSet.getString("createdAt"));
                tokens.add(canteen);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("tokens", tokens);
        return "index";  // Corresponds to index.html
    }

    // Method to show the form for editing a token
    @GetMapping("/edit/{tokenId}")
    public String showEditForm(@PathVariable Long tokenId, Model model) {
        try {
            Canteen canteen = getTokenById(tokenId);
            model.addAttribute("canteen", canteen);
            return "edit-token";  // Corresponds to edit-token.html
        } catch (SQLException e) {
            model.addAttribute("error", "Error retrieving token: " + e.getMessage());
            e.printStackTrace();
            return "index";
        }
    }

    // Method to handle editing a token
    @PostMapping("/edit/{tokenId}")
    public String editToken(@PathVariable long tokenId, @ModelAttribute Canteen canteen, Model model) {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE canteen SET itemName = ?, price = ?, issue = ? WHERE tokenId = ?";
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1, canteen.getItemName());
            statement.setLong(2, canteen.getPrice());
            statement.setString(3, canteen.getIssue());
            statement.setLong(4, tokenId);

            statement.executeUpdate();

            model.addAttribute("success", "Successfully updated token for item: " + canteen.getItemName());
            return "edit-token";  // Corresponds to edit-token.html
        } catch (SQLException e) {
            model.addAttribute("error", "Error updating token: " + e.getMessage());
            e.printStackTrace();
            return "edit-token";  // Return to the same page in case of an error
        }
    }

    // Method to show the form for deleting a token
    @GetMapping("/delete/{tokenId}")
    public String showDeleteForm(@PathVariable Long tokenId, Model model) {
        try {
            Canteen canteen = getTokenById(tokenId);
            model.addAttribute("canteen", canteen);
            return "delete-token";  // Corresponds to delete-token.html
        } catch (SQLException e) {
            model.addAttribute("error", "Error retrieving token: " + e.getMessage());
            e.printStackTrace();
            return "index";
        }
    }

    // Method to handle deleting a token
    @PostMapping("/delete/{tokenId}")
    public String deleteToken(@PathVariable long tokenId, Model model, RedirectAttributes redirectAttributes) {
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM canteen WHERE tokenId = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setLong(1, tokenId);
            statement.executeUpdate();

            redirectAttributes.addFlashAttribute("success", "Token with ID " + tokenId + " has been deleted!");
            return "redirect:/";  // Redirect back to the home page
        } catch (SQLException e) {
            model.addAttribute("error", "Error deleting token: " + e.getMessage());
            e.printStackTrace();
            return "delete-token";  // Return to the same page in case of an error
        }
    }

    // Helper method to get all tokens from the database
    private List<Canteen> getAllTokens() {
        List<Canteen> tokens = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM canteen";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Canteen canteen = new Canteen();
                canteen.setTokenId(resultSet.getLong("tokenId"));
                canteen.setItemName(resultSet.getString("itemName"));
                canteen.setPrice(resultSet.getLong("price"));
                canteen.setIssue(resultSet.getString("issue"));
                canteen.setCreatedAt(resultSet.getString("createdAt"));
                tokens.add(canteen);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    // Helper method to get a token by its tokenId
    private Canteen getTokenById(Long tokenId) throws SQLException {
        Canteen canteen = new Canteen();
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM canteen WHERE tokenId = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setLong(1, tokenId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                canteen.setTokenId(resultSet.getLong("tokenId"));
                canteen.setItemName(resultSet.getString("itemName"));
                canteen.setPrice(resultSet.getLong("price"));
                canteen.setIssue(resultSet.getString("issue"));
                canteen.setCreatedAt(resultSet.getString("createdAt"));
            }
        }
        return canteen;
    }
}
