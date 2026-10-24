
package com.example;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class Main {

    public static void main(String[] args) {

        try (SessionFactory factory = new Configuration()
                .configure()
                .buildSessionFactory();
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("\n===== PRODUCT MANAGEMENT =====");
                System.out.println("1. Add Product");
                System.out.println("2. View All Products");
                System.out.println("3. Update Product");
                System.out.println("4. Delete Product");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> addProduct(factory, scanner);
                    case 2 -> viewProducts(factory);
                    case 3 -> updateProduct(factory, scanner);
                    case 4 -> deleteProduct(factory, scanner);
                    case 5 -> {
                        System.out.println("Thank you!!");
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addProduct(SessionFactory factory,
                                   Scanner scanner) {
        System.out.print("Enter product name: ");
        String name = scanner.next();

        System.out.print("Enter price: ");
        double price = scanner.nextDouble();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Product product = new Product(name, price, quantity);
            session.persist(product);

            tx.commit();

            System.out.println("Product added! ID: " + product.getId());

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    private static void viewProducts(SessionFactory factory) {
        try (Session session = factory.openSession()) {

            List<Product> products = session
                    .createQuery("FROM Product", Product.class)
                    .getResultList();

            if (products.isEmpty()) {
                System.out.println("No products found.");
                return;
            }

            System.out.println("\nID | Name | Price | Quantity");

            for (Product p : products) {
                System.out.println(
                        p.getId() + " | "
                        + p.getName() + " | "
                        + p.getPrice() + " | "
                        + p.getQuantity());
            }
        }
    }

    private static void updateProduct(SessionFactory factory,
                                      Scanner scanner) {
        System.out.print("Enter product ID to update: ");
        int id = scanner.nextInt();

        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Product product = session.get(Product.class, id);

            if (product == null) {
                System.out.println("Product not found.");
                tx.rollback();
                return;
            }

            System.out.print("Enter new name: ");
            String name = scanner.next();

            System.out.print("Enter new price: ");
            double price = scanner.nextDouble();

            System.out.print("Enter new quantity: ");
            int quantity = scanner.nextInt();

            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);

            tx.commit();

            System.out.println("Product updated successfully!");

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    private static void deleteProduct(SessionFactory factory,
                                      Scanner scanner) {
        System.out.print("Enter product ID to delete: ");
        int id = scanner.nextInt();

        Transaction tx = null;

        try (Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Product product = session.get(Product.class, id);

            if (product == null) {
                System.out.println("Product not found.");
                tx.rollback();
                return;
            }

            session.remove(product);
            tx.commit();

            System.out.println("Product deleted successfully!");

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }
}
