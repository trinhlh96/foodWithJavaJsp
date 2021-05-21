package controller;

import entity.Category;
import entity.Food;
import repository.GenericRepository;
import service.FoodService;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

@WebServlet(name = "DashBoard", urlPatterns = "/index")
public class Dashboard extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int totalFood = new GenericRepository<>(Food.class).getCountFood();
        int totalCategory = new GenericRepository<>(Category.class).getCountCategory();
        req.setAttribute("totalFood",totalFood);
        req.setAttribute("totalCategory",totalCategory);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
