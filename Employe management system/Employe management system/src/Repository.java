import java.sql.*;
import java.util.*;
import Model.*;

public class Repository {

    String url = "jdbc:h2:./data/testdb";
    String user = "sa";
    String password = "";

    Connection conn = null;
    Scanner sc = new Scanner(System.in);

    // Connect to H2 database and create tables
    public void createConnection() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to H2 database.");
            Statement stmt = conn.createStatement();

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS departments (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255)
                        );
                    """);
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS employees (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255),
                            departmentid INT,
                            FOREIGN KEY (departmentid) REFERENCES departments(id)
                        );
                    """);
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS salaries (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            employeeid INT,
                            amount DOUBLE,
                            FOREIGN KEY (employeeid) REFERENCES employees(id)
                        );
                    """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addEmployee() throws Exception {
        Employee emp = new Employee();
        System.out.print("Enter Name: ");
        emp.Name = sc.nextLine();
        showDepartments();

        System.out.print("Enter DepartmentId: ");
        emp.DepartementId = sc.nextInt();
        sc.nextLine();

        PreparedStatement psmt = conn.prepareStatement("INSERT INTO employees (name, departmentid) VALUES (?, ?)");
        psmt.setString(1, emp.Name);
        psmt.setInt(2, emp.DepartementId);
        psmt.executeUpdate();

        System.out.println("Employee added successfully.");
    }

    public void addDepartment() throws Exception {
        Department dep = new Department();
        System.out.print("Enter Department Name: ");
        dep.Name = sc.nextLine();

        PreparedStatement psmt = conn.prepareStatement("INSERT INTO departments (name) VALUES (?)");
        psmt.setString(1, dep.Name);
        psmt.executeUpdate();

        System.out.println("Department added successfully.");
    }

    public void addSalary() throws Exception {
        Salary sal = new Salary();
        showAllEmployees();

        System.out.print("Enter Employee Id: ");
        sal.EmployeeId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Amount: ");
        sal.Amount = sc.nextDouble();
        sc.nextLine();

        PreparedStatement psmt = conn.prepareStatement("INSERT INTO salaries (employeeid, amount) VALUES (?, ?)");
        psmt.setInt(1, sal.EmployeeId);
        psmt.setDouble(2, sal.Amount);
        psmt.executeUpdate();

        System.out.println("Salary added successfully.");
    }

    public void showEmployee() throws Exception {
        String query = """
                    SELECT employees.name AS emp_name, salaries.amount, departments.name AS dept_name
                    FROM employees
                    JOIN salaries ON employees.Id = salaries.EmployeeId
                    JOIN departments ON employees.DepartmentId = departments.id
                """;

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("\n+------------------+----------+-----------------+");
        System.out.println("| Employee Name    | Salary   | Department      |");
        System.out.println("+------------------+----------+-----------------+");

        while (rs.next()) {
            String empName = rs.getString("emp_name");
            double salary = rs.getDouble("amount");
            String deptName = rs.getString("dept_name");

            System.out.printf("| %-16s | %-8.2f | %-15s |\n", empName, salary, deptName);
        }
        System.out.println("+------------------+----------+-----------------+");
    }

    public void showDepartments() throws Exception {
        String query = "SELECT * FROM departments";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("\n+----+----------------------+");
        System.out.println("| ID | Department Name      |");
        System.out.println("+----+----------------------+");

        while (rs.next()) {
            System.out.printf("| %-2d | %-20s |\n", rs.getInt("id"), rs.getString("name"));
        }
        System.out.println("+----+----------------------+");
    }

    public void showEmployeeById(int empId) throws Exception {
        String query = """
                    SELECT e.name AS emp_name, s.amount, d.name AS dept_name
                    FROM employees e
                    JOIN salaries s ON e.id = s.employeeid
                    JOIN departments d ON e.departmentid = d.id
                    WHERE e.id = ?
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, empId);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("\n+------------------+----------+-----------------+");
            System.out.println("| Employee Name    | Salary   | Department      |");
            System.out.println("+------------------+----------+-----------------+");

            System.out.printf("| %-16s | %-8.2f | %-15s |\n",
                    rs.getString("emp_name"),
                    rs.getDouble("amount"),
                    rs.getString("dept_name"));

            System.out.println("+------------------+----------+-----------------+");
        } else {
            System.out.println("Employee not found.");
        }
    }

    public void showEmployeesByDepartment(int deptId) throws Exception {
        String query = """
                    SELECT e.name AS emp_name, s.amount, d.name AS dept_name
                    FROM employees e
                    LEFT JOIN salaries s ON e.id = s.employeeid
                    LEFT JOIN departments d ON e.departmentid = d.id
                    WHERE e.departmentid = ?
                """;

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, deptId);
        ResultSet rs = ps.executeQuery();

        boolean found = false;

        System.out.println("\n+------------------+----------+");
        System.out.println("| Employee Name    | Salary   |");
        System.out.println("+------------------+----------+");

        while (rs.next()) {
            found = true;
            System.out.printf("| %-16s | %-8.2f |\n",
                    rs.getString("emp_name"),
                    rs.getDouble("amount"));
        }

        System.out.println("+------------------+----------+");

        if (!found) {
            System.out.println("No employees found in this department.");
        }
    }

    public void removeEmployee(int empId) throws Exception {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM employees WHERE id = ?");
        ps.setInt(1, empId);
        int rows = ps.executeUpdate();
        System.out.println(rows > 0 ? "Employee removed." : "Employee not found.");
    }

    public void removeDepartment(int deptId) throws Exception {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM departments WHERE id = ?");
        ps.setInt(1, deptId);
        int rows = ps.executeUpdate();
        System.out.println(rows > 0 ? "Department removed." : "Department not found.");
    }

    public void showAllEmployees() throws Exception {
        String query = "SELECT id, name FROM employees";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("\n+----+----------------------+");
        System.out.println("| ID | Employee Name        |");
        System.out.println("+----+----------------------+");

        while (rs.next()) {
            System.out.printf("| %-2d | %-20s |\n", rs.getInt("id"), rs.getString("name"));
        }

        System.out.println("+----+----------------------+");
    }
}
