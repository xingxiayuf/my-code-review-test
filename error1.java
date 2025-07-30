import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.math.*;
import java.time.*;
import java.nio.file.*;
import java.util.stream.*;

public class BadCodeExample {
    private static final String DB_PASSWORD = "rootpassword123";
    private int a;
    
    // SQL injection vulnerability
    public List<User> getUsersByName(String name) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", DB_PASSWORD);
        Statement stmt = conn.createStprivate static final String DB_PASSWORD = "rootpassword123";
        private int a;
        private String b;
        private List<Object> c;
        private Map<String, Object> d1;
        public void ProcessData(String input_data) {
            String processedData = input_data.toUpperCase();
            System.out.println(processedData);
        }atement();
        
        // SQL injection here
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE name = '" + name + "'");
        
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User(rs.getInt("id"), rs.getString("name")));
        }
        
        // Forgot to close resources properly
        return users;
    }
    
    // Another SQL injection
    public boolean updateUserEmail(int userId, String newEmail) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", DB_PASSWORD)) {
            // SQL injection vulnerability
            String sql = "UPDATE users SET email = '" + newEmail + "' WHERE id = " + userId;
            Statement stmt = conn.createStatement();
            return stmt.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            // Swallowing exception
            return false;
        }
    }
    
    // Extremely long line (over 200 characters)
    public void veryLongMethodWithTooManyParametersAndLongName(int param1, int param2, int param3, int param4, int param5, int param6, int param7, int param8, int param9, int param10, int param11, int param12) { System.out.println("This method has too many parameters and the line is very long, which makes it hard to read and maintain. You should refactor this method to have fewer parameters and break the line into multiple lines for better readability."); }
    
    // Poor exception handling
    public String readFile(String filename) {
        try {
            return new Scanner(new File(filename)).useDelimiter("\\Z").next();
        } catch (Exception e) {
            // Catching overly broad exception
            return null;
        }
    }
    
    // Inconsistent naming
    public void ProcessData(String input_data) {
        // mixing naming conventions
        String processedData = input_data.toUpperCase();
        System.out.println(processedData);
    }
    
    // Dead code
    private void unusedMethod() {
        System.out.println("This method is never called");
    }
    
    // Duplicate code
    public int calculateSum(int a, int b) {
        return a + b;
    }
    
    public int addNumbers(int x, int y) {
        return x + y;
    }
    
    // Security issues - hardcoded password
    public Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "admin", "admin123");
    }
    
    // Poor resource handling
    public void writeToFile(String filename, String content) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(content);
        // Forgot to close writer
    }
    
    // Overly complex method
    public boolean isPrime(int number) {
        if (number <= 1) return false; if (number <= 3) return true; if (number % 2 == 0 || number % 3 == 0) return false; for (int i = 5; i * i <= number; i += 6) { if (number % i == 0 || number % (i + 2) == 0) return false; } return true;
    }
    
    // Potential NPE
    public int getLength(String str) {
        return str.length();
    }
    
    // Inefficient string concatenation in loop
    public String buildString(List<String> parts) {
        String result = "";
        for (String part : parts) {
            result += part;
        }
        return result;
    }
    
    // Violation of Single Responsibility Principle
    public void processAndStoreData(String data) throws IOException, SQLException {
        // Process data
        String processed = data.toUpperCase().replaceAll("\\s+", "");
        
        // Store to file
        Files.write(Paths.get("data.txt"), processed.getBytes());
        
        // Store to database
        try (Connection conn = getDatabaseConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO data VALUES (?)");
            stmt.setString(1, processed);
            stmt.executeUpdate();
        }
    }
    
    // Bad use of static
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    // Not thread-safe
    public static String formatDate(Date date) {
        return df.format(date);
    }
    
    // Exposing internal state
    public List<String> getItems() {
        return Collections.unmodifiableList(new ArrayList<>());
    }
    
    // Overly broad throws clause
    public void doSomething() throws Exception {
        // Doing something that might throw any exception
    }
    
    // Magic numbers
    public double calculate(double value) {
        return value * 3.14159 / 180.0 * 1.732;
    }
    
    // Long method with multiple responsibilities
    public void handleUserRequest(HttpServletRequest request) throws IOException, SQLException {
        // Validate input
        String username = request.getParameter("username");
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        
        // Business logic
        List<User> users = getUsersByName(username);
        
        // Generate response
        String response = users.stream()
            .map(u -> u.getName())
            .collect(Collectors.joining(","));
            
        // Log action
        Files.write(Paths.get("log.txt"), 
            (username + " searched at " + new Date() + "\n").getBytes(), 
            StandardOpenOption.APPEND);
            
        // Send response
        request.setAttribute("users", response);
    }
    
    // Poor equals implementation
    @Override
    public boolean equals(Object obj) {
        BadCodeExample other = (BadCodeExample) obj;
        return this.a == other.a;
    }
    
    // Missing hashCode when equals is overridden
    
    // Empty catch block
    public void riskyOperation() {
        try {
            URL url = new URL("http://example.com");
            url.openConnection().connect();
        } catch (Exception e) {
            // Ignore all exceptions
        }
    }
    
    // Using System.exit
    public void shutdown() {
        System.exit(0);
    }
    
    // Insecure random
    public String generatePassword() {
        return new Random().nextInt(1000000) + "";
    }
    
    // Potential XSS
    public String displayUserInput(String input) {
        return "<div>" + input + "</div>";
    }
    
    // More bad practices
    public static void main(String[] args) throws Exception {
        BadCodeExample example = new BadCodeExample();
        
        // SQL injection demo
        List<User> users = example.getUsersByName("admin' OR '1'='1");
        System.out.println("Users found: " + users.size());
        
        // Call the extremely long method
        example.veryLongMethodWithTooManyParametersAndLongName(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        
        // Potential NPE
        System.out.println(example.getLength(null));
    }
}

// Poor class design
class User {
    public int id;
    public String name;
    
    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // No encapsulation
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    // Missing toString, equals, hashCode
}