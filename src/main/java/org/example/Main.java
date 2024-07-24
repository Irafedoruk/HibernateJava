package org.example;

import com.github.javafaker.Faker;
import org.example.entities.*;
import org.example.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


import java.util.Date;
import java.util.List;
import java.util.Random;

public class Main {
    private static final Faker faker = new Faker(); // Ініціалізація об'єкта Faker
    private static final Random random = new Random();

    public static void main(String[] args) {
        //insertData();
        //selectList();
        //insertDataWithService();
        //var entity = getById(1);
        //System.out.println(entity.getFirstName()+ ' '+ entity.getLastName());
        insertServices();
        insertOrderStatuses();
        insertClientsAndOrders();
        selectOrders();
    }

    private static void insertData() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        ClientEntity entity = new ClientEntity();
        entity.setFirstName("Іван");
        entity.setLastName("Барабашка");
        entity.setPhone("+38 068 47 85 458");
        entity.setCar_model("Volkswagen Beetle A5");
        entity.setCar_year(2003);
        session.save(entity);

        transaction.commit();
        session.close();
    }

    private static void selectList() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<ClientEntity> results = session.createQuery("from ClientEntity", ClientEntity.class)
                .getResultList();

        //System.out.println("Count = "+ results.size());

        for (ClientEntity client : results) {
            System.out.println(client);
        }
        session.close();
    }

    private static ClientEntity getById(int id) {
        // Obtain a session from the SessionFactory
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        ClientEntity entity = session.get(ClientEntity.class, id);
        transaction.commit();
        session.close();
        return entity;
    }

    private static void insertOrderStatus() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String [] list = {
                "Нове замовлення",
                "В процесі виконання",
                "Виконано",
                "Скасовано клієнтом"
        };
        for (var item : list) {
            OrderStatusEntity entity = new OrderStatusEntity();
            entity.setName(item);
            session.save(entity);
        }
        transaction.commit();
        session.close();
    }

    private static void insertDataWithService() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        ClientEntity client = new ClientEntity();
        client.setFirstName("Іван");
        client.setLastName("Барабашка");
        client.setPhone("+38 068 47 85 458");
        client.setCar_model("Volkswagen Beetle A5");
        client.setCar_year(2003);
        session.save(client);

        OrderEntity order = new OrderEntity();
        order.setClient(client);
        order.setOrderDate(new Date());

        ServiceEntity service = session.get(ServiceEntity.class, 1); // assuming there is a service with ID 1
        OrderServiceEntity orderService = new OrderServiceEntity();
        orderService.setOrder(order);
        orderService.setService(service);

        order.getOrderServices().add(orderService);

        session.save(order);

        transaction.commit();
        session.close();
    }

//    private static void insertServices() {
//        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//
//        Session session = sessionFactory.openSession();
//        Transaction transaction = session.beginTransaction();
//
//        String[] serviceNames = {"Діагностика", "Ремонт двигуна", "Заміна масла"};
//        double[] servicePrices = {100.0, 500.0, 50.0};
//
//        for (int i = 0; i < serviceNames.length; i++) {
//            ServiceEntity service = new ServiceEntity();
//            service.setName(serviceNames[i]);
//            service.setPrice(servicePrices[i]);
//            session.save(service);
//        }
//
//        transaction.commit();
//        session.close();
//    }

    private static void insertServices() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        for (int i = 0; i < 10; i++) {
            ServiceEntity service = new ServiceEntity();
            service.setName(faker.commerce().productName());

            // Генеруємо випадкову ціну з плаваючою комою
            double price = faker.number().randomDouble(2, 10, 500); // 2 знаки після коми, від 10 до 500
            service.setPrice(price);

            session.save(service);
        }

        transaction.commit();
        session.close();
    }

    private static void selectOrders() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();

        List<OrderEntity> results = session.createQuery("from OrderEntity", OrderEntity.class)
                .getResultList();

        System.out.println("Список замовлень:");

        for (OrderEntity order : results) {
            System.out.println("Order ID: " + order.getId());
            System.out.println("Order Date: " + order.getOrderDate());
            System.out.println("Client: " + order.getClient().getFirstName() + " " + order.getClient().getLastName());
            System.out.println("Status: " + order.getStatus().getName());
            System.out.println("Services:");
            order.getOrderServices().forEach(orderService -> {
                System.out.println("\tService: " + orderService.getService().getName() +
                        ", Price: " + orderService.getService().getPrice());
            });
            System.out.println("----");
        }
        session.close();
    }

    private static void insertClientsAndOrders() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            for (int i = 0; i < 10; i++) {
                ClientEntity client = new ClientEntity();
                client.setFirstName(faker.name().firstName());
                client.setLastName(faker.name().lastName());
                client.setPhone(faker.phoneNumber().phoneNumber());
                client.setCar_model(faker.company().name());
                client.setCar_year(faker.number().numberBetween(2000, 2023));
                session.save(client);

                OrderEntity order = new OrderEntity();
                order.setClient(client);
                order.setOrderDate(faker.date().past(365, java.util.concurrent.TimeUnit.DAYS));

                OrderStatusEntity status = session.get(OrderStatusEntity.class, random.nextInt(4) + 1); // Assuming you have 4 statuses
                order.setStatus(status);

                ServiceEntity service = session.get(ServiceEntity.class, random.nextInt(10) + 1); // Assuming you have 10 services
                OrderServiceEntity orderService = new OrderServiceEntity();
                orderService.setOrder(order);
                orderService.setService(service);

                order.getOrderServices().add(orderService);

                session.save(order);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    private static void insertOrderStatuses() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        String[] statuses = {"Нове замовлення", "В процесі виконання", "Виконано", "Скасовано клієнтом"};
        for (String status : statuses) {
            OrderStatusEntity statusEntity = new OrderStatusEntity();
            statusEntity.setName(status);
            session.save(statusEntity);
        }

        transaction.commit();
        session.close();
    }

}
