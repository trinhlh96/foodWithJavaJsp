package controller;

import entity.Food;
import service.FoodService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet(name = "DeleteFood", urlPatterns = "/admin/food/delete")
public class DeleteFood extends HttpServlet {
    private FoodService foodService = new FoodService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // lỗi Utf8 ngoài view
        String sid = req.getParameter("id");
        Food exist = foodService.getById(sid);
        if (exist == null){
            System.out.println("không tìm thấy id");
            resp.sendRedirect("/admin/food/list");
            return;
        }
        foodService.delete(sid,exist);
        resp.sendRedirect("/admin/food/list");
    }
}
