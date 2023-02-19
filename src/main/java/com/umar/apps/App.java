package com.umar.apps;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.RequestLine;
import feign.Retryer;
import feign.Retryer.Default;
import feign.gson.GsonDecoder;
import feign.slf4j.Slf4jLogger;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;

public class App {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(App.class);

    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try {
            Info info = Feign
                    .builder()
                    .logger(new MyLogger())
                    .logLevel(Logger.Level.FULL)
                    .decoder(new GsonDecoder())
                    .options(new Request.Options(5, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS, true))
                    .retryer(new Default())
                    //.retryer(Retryer.NEVER_RETRY)
                    .target(Info.class, "http://localhost:9090");
            var employees = info.employees();
            logger.info("Time taken {} ms to get employees", System.currentTimeMillis() - startTime);
            employees.forEach(e -> logger.info("{}", e));
        } catch (Exception e) {
            logger.error("Exception occurred. Time taken {} ms.", System.currentTimeMillis() - startTime);    
        }
        System.out.println(new App().getGreeting());
    }
    
    private static class MyLogger extends Logger {
    @Override
    protected void log(String s, String s1, Object... objects) {
        System.out.println(String.format(s + s1, objects)); // Change me!
    }
}
}

interface Info {
    @RequestLine("GET /employees")
    List<Employee> employees();
    
}

class Employee {

    private Long id;
    private String name;
    private String role;

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getRole() {
        return this.role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        Employee employee = (Employee) o;
        return Objects.equals(this.id, employee.id) && Objects.equals(this.name, employee.name)
                && Objects.equals(this.role, employee.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.name, this.role);
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + this.id + ", name='" + this.name + '\'' + ", role='" + this.role + '\'' + '}';
    }
}
