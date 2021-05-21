package controller;

import entity.Category;
import entity.Food;
import service.CategoryService;
import service.FoodService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "DetailFood",urlPatterns = "/admin/food/detail")
public class DetailFoodServlet extends HttpServlet {
    private CategoryService categoryService = new CategoryService();
    private FoodService foodService = new FoodService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sid = req.getParameter("id");
        Food existFood = foodService.getById(sid);
        if (existFood == null){
            System.out.println("không tìm thấy id");
            resp.sendRedirect("/admin/category/list");
            return;
        }
        req.setAttribute("list", existFood);
        req.setAttribute("categories",  categoryService.findByCondition());
        req.getRequestDispatcher("/admin/food/detail.jsp").forward(req,resp);
    }
}
