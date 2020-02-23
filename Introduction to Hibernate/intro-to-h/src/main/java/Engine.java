import entities.Address;
import entities.Employee;
import entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
        this.addressesWithEmployeeCount();

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
