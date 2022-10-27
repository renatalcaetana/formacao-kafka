package br.com.alura.ecommerce;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.Source;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final  KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final  KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                try {
                    var email = Math.random() + "@email.com";
                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(req.getParameter("amount"));
                    var order = new Order(orderId, amount, email);

                    orderDispatcher.send("ECOMMERCE2_NEW_ORDER", email, order);
                    var emailCode = new Email("Teste", "Thank you for your order! We are processing your order!");
                    emailDispatcher.send("ECOMMERCE2_SEND_EMAIL", email, emailCode);
                    System.out.println("New order sent auccessfully");
                    resp.setStatus((HttpServletResponse.SC_OK));
                    resp.getWriter().println("New order sent auccessfully");

                } catch (ExecutionException e) {
                    throw new ServletException(e);
                } catch (InterruptedException e) {
                    throw new ServletException(e);
                }
            }
        }

