package com.example.demo.controller;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
public class AppController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    // Vulnerable SQL Injection Login
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // This loads the login form
    }

    @PostMapping("/test-login")
    public String loginSubmit(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        String query = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(query);
        if (!users.isEmpty()) {
            model.addAttribute("message", "Login successful!");
        } else {
            model.addAttribute("message", "Invalid credentials.");
        }
        return "login";
    }

    // XSS Vulnerability
    @PostMapping("/submit")
    public String submit(@RequestParam("input") String input, Model model) {
        model.addAttribute("userInput", input);
        return "index"; // Ensure you have an index.html template
    }

    // Unrestricted File Upload Vulnerability - Added GET method
    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";  // Load the file upload form
    }

    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file, Model model) {
        try {
            String fileName = file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.write(path, file.getBytes());
            model.addAttribute("message", "File uploaded successfully.");
        } catch (Exception e) {
            model.addAttribute("message", "File upload failed.");
        }
        return "upload"; // Ensure you have an upload.html template
    }

    // Data Aggregation Attack
    @GetMapping("/data")
    public String aggregateData(Model model) {
        String userInfo = "User1: John, Order Amount: $100";
        String orderInfo = "Order ID: 1234, Credit Card: XXXX-XXXX-XXXX-1234";
        String aggregatedData = userInfo + " | " + orderInfo;
        model.addAttribute("aggregatedData", aggregatedData);
        return "data"; // Ensure you have a data.html template
    }

    // SSRF Vulnerability - Ensure 'url' param is provided
    @GetMapping("/ssrf")
    public String ssrfVulnerability(@RequestParam(value = "url", required = false) String url, Model model) {
        if (url == null || url.isEmpty()) {
            model.addAttribute("message", "URL parameter is missing.");
            return "ssrf";  // Return SSRF form if URL is not provided
        }
        try {
            URL targetUrl = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(targetUrl.openStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            model.addAttribute("response", response.toString());
        } catch (Exception e) {
            model.addAttribute("message", "Failed to fetch URL.");
        }
        return "ssrf"; // Ensure you have an ssrf.html template
    }

    // CSRF Vulnerable Profile Update
    @PostMapping("/profile")
    public String updateProfile(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        String query = "UPDATE users SET password='" + password + "' WHERE username='" + username + "'";
        jdbcTemplate.update(query);
        model.addAttribute("message", "Profile updated successfully for user: " + username);
        return "profile"; // profile.html template should display confirmation
    }

    // Serve the CSRF attacker page
    @GetMapping("/csrf-attack")
    public String csrfAttackPage() {
        return "csrf-attack";  // Load the attacker page
    }
}
