import entities.Address;
import entities.Employee;
import entities.Project;
import entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Engine implements Runnable {

    private final EntityManager entityManager;
    private final BufferedReader reader;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {

        //2. Remove Objects
        //this.removeObjectsEx();

        //3. Contains Employee
        //try {
        //    this.containsEmployee();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        //4. Employees with Salary Over 50 000
        //this.employeeWithSalaryOver50000();

        //5. Employees From Department
        //this.employeesFromDepartment();

        //6. Adding a New Address and Updating Employee
        //try {
        //    this.addingANewAddressAndUpdatingEmployee();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        //7. Addresses with Employee Count
        //this.addressesWithEmployeeCount();

        //8. Get Employee with Project
        //try {
        //    this.getEmployeeWithProject();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        //9. Find Latest 10 Projects
        //this.findLatestTestProjects();
        
        //10. Increase Salary
        //this.increaseSalary();
        
        //11. Remove Towns
        //try {
        //    this.removeTowns();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
        
        //12. Find Employees by First Name
        //try {
        //    this.findEmployeesByFirstName();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

    }

    private void findEmployeesByFirstName() throws IOException {
        System.out.println("Enter pattern:");
        String pattern = reader.readLine();

        List<Employee> employees = this.entityManager
                .createQuery("FROM Employee "
                        + "WHERE firstName LIKE CONCAT(:letters, '%')", Employee.class)
                .setParameter("letters", pattern)
                .getResultList();

        employees.forEach(empl -> System.out.printf("%s %s - %s - ($%.2f)%n",
                empl.getFirstName(),
                empl.getLastName(),
                empl.getJobTitle(),
                empl.getSalary()));
    }

    private void removeTowns() throws IOException {
        System.out.println("Enter town name:");
        String town = reader.readLine();

        this.entityManager.getTransaction().begin();

        Town townDelete = this.entityManager.createQuery("FROM Town "
                + "WHERE name = :town", Town.class)
                .setParameter("town", town)
                .getSingleResult();

        List<Address> addressesToDelete = this.entityManager
                .createQuery("FROM Address WHERE town.id= :id", Address.class)
                .setParameter("id", townDelete.getId())
                .getResultList();

        addressesToDelete
                .forEach(t -> t.getEmployees()
                        .forEach(em -> em.setAddress(null)));

        addressesToDelete.forEach(this.entityManager::remove);
        this.entityManager.remove(townDelete);

        int countDeletedAddresses = addressesToDelete.size();
        System.out.printf("%d address%s in %s deleted",
                countDeletedAddresses,
                countDeletedAddresses == 1 ? "" : "es",
                town);

        this.entityManager.getTransaction().commit();
    }

    private void increaseSalary() {
        List<String> departmentsToIncrease = Arrays.asList(
                "Engineering",
                "Tool Design",
                "Marketing",
                "Information Services");

        this.entityManager.getTransaction().begin();

        List<Employee> employees = this.entityManager
                .createQuery("FROM Employee WHERE department.name IN (:deps)", Employee.class)
                .setParameter("deps", departmentsToIncrease)
                .getResultList();

        employees
                .forEach(employee ->
                        employee.setSalary(employee.getSalary().multiply(BigDecimal.valueOf(1 + 0.12))));
        this.entityManager.flush();
        this.entityManager.getTransaction().commit();

        employees.forEach(employee ->
                System.out.printf("%s %s ($%.2f)%n",
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getSalary()));
    }

    private void findLatestTestProjects() {
        List<Project> projects = this.entityManager.createQuery("SELECT p FROM Project AS p " +
                "ORDER BY p.startDate DESC", Project.class).setMaxResults(10).getResultList();

        projects.stream().sorted((a, b) -> a.getName().compareTo(b.getName())).forEach(x -> {
            System.out.println(String.format("Project name: %s\n\tProject Description: %s\n\tProject Start Date:%s\n\tProjectEndDate: %s",
                    x.getName(), x.getDescription(), x.getStartDate(), x.getEndDate()));
        });
    }

    private void getEmployeeWithProject() throws IOException {
        System.out.println("Enter employee ID:");
        int id = Integer.parseInt(reader.readLine());
        Employee employee = this.entityManager.createQuery("SELECT e FROM Employee  AS e " +
                "WHERE e.id = :id", Employee.class).setParameter("id", id).getSingleResult();

        System.out.printf("%s %s - %s%n\t%s",
                employee.getFirstName(),
                employee.getLastName(),
                employee.getJobTitle(),
                employee.getProjects().stream()
                        .map(Project::getName)
                        .sorted()
                        .collect(Collectors.joining(System.lineSeparator() + "\t")));
    }

    private void addressesWithEmployeeCount() {
        List<Address> addresses =
                this.entityManager.createQuery("SELECT a FROM Address AS a " +
                        "ORDER BY size(employees) DESC", Address.class)
                .setMaxResults(10)
                .getResultList();

        addresses.forEach(x -> {
            System.out.println(x.getText() + ", " + x.getTown().getName() + " - " + x.getEmployees().size());
        });
    }

    private void addingANewAddressAndUpdatingEmployee() throws IOException {


        Employee employee = null;

        while (employee == null) {
            System.out.println("Enter last name of employee:");
            String lastName = this.reader.readLine();
            try {
                employee =
                        this.entityManager.createQuery("SELECT e FROM Employee AS e " +
                                "WHERE e.lastName = :name", Employee.class)
                                .setParameter("name", lastName)
                                .getSingleResult();
            } catch (NoResultException e) {
                System.out.println("No employee with such last name found.");
            }
        }
        Address address = this.createNewAddress("Vitoshka 15");
        this.entityManager.getTransaction().begin();
        this.entityManager.detach(employee);
        employee.setAddress(address);
        this.entityManager.merge(employee);
        this.entityManager.flush();
        this.entityManager.getTransaction().commit();
    }

    private Address createNewAddress(String addressName) {
        Address address = new Address();
        address.setText(addressName);
        System.out.println();
        this.entityManager.getTransaction().begin();
        this.entityManager.persist(address);
        this.entityManager.getTransaction().commit();
        System.out.println();
        return address;
    }

    private void employeesFromDepartment() {
        List<Employee> employees =
                this.entityManager.createQuery("SELECT e FROM Employee AS e " +
                        "WHERE e.department.name = 'Research and Development' " +
                        "ORDER BY e.salary ASC, e.id ASC", Employee.class)
                .getResultList();

        employees.forEach(x -> {
            System.out.println(String.format("%s %s from %s - $%.2f",
                    x.getFirstName(), x.getLastName(), x.getDepartment().getName(), x.getSalary()));
        });
    }

    private void employeeWithSalaryOver50000() {
        List<Employee> employees =
                this.entityManager.createQuery("SELECT e FROM Employee AS e " +
                        "WHERE e.salary > 50000", Employee.class)
                .getResultList();

        employees.forEach(x -> {
            System.out.println(x.getFirstName());
        });
    }

    private void containsEmployee() throws IOException {
        System.out.println("Enter full name of the employee:");
        String fullname = this.reader.readLine();

        try {
            Employee employee =
                    this.entityManager.createQuery("SELECT e FROM Employee AS e " +
                            "WHERE concat(e.firstName, ' ', e.lastName) =  :name", Employee.class)
                            .setParameter("name", fullname)
                            .getSingleResult();
            System.out.println("Yes");
        } catch (NoResultException e) {
            System.out.println("No");
        }

        System.out.println();
    }

    private void removeObjectsEx() {
        List<Town> towns = this.entityManager.createQuery("SELECT t FROM Town AS t " +
                "WHERE length(t.name) > 5 ", Town.class)
                .getResultList();

        this.entityManager.getTransaction().begin();
        towns.forEach(this.entityManager::detach);
        for (Town town : towns) {
            town.setName(town.getName().toLowerCase());
        }
        towns.forEach( this.entityManager::merge);
        this.entityManager.flush();
        this.entityManager.getTransaction().commit();

    }
}
