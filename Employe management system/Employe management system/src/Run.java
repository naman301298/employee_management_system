import java.util.Scanner;

public class Run {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Repository repo = new Repository();
        repo.createConnection();
        int button = 0;

        while (button != 9) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Employee");
            System.out.println("2. Add Department");
            System.out.println("3. Add Salary");
            System.out.println("4. View All Employees");
            System.out.println("5. View All Departments");
            System.out.println("6. Show Employee Details by ID");
            System.out.println("7. Show Employees by Department ID");
            System.out.println("8. Remove Employee or Department");
            System.out.println("9. Exit");
            System.out.print("Enter choice: ");
            button = sc.nextInt();
            sc.nextLine(); // Clear newline

            try {
                switch (button) {
                    case 1 -> repo.addEmployee();
                    case 2 -> repo.addDepartment();
                    case 3 -> repo.addSalary();
                    case 4 -> repo.showEmployee();
                    case 5 -> repo.showDepartments();
                    case 6 -> {
                        System.out.print("Enter Employee ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        repo.showEmployeeById(id);
                    }
                    case 7 -> {
                        System.out.print("Enter Department ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        repo.showEmployeesByDepartment(id);
                    }
                    case 8 -> {
                        System.out.println("1. Remove Employee\n2. Remove Department");
                        int type = sc.nextInt();
                        System.out.print("Enter ID to remove: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        if (type == 1)
                            repo.removeEmployee(id);
                        else if (type == 2)
                            repo.removeDepartment(id);
                        else
                            System.out.println("Invalid choice.");
                    }
                    case 9 -> System.out.println("Exiting...");
                    default -> System.out.println("Wrong choice, try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sc.close();
    }
}
