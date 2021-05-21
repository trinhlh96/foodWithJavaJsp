package controller;

import entity.Category;
import service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "DetailCategory",urlPatterns = "/admin/category/detail")
public class DetailCategoryServlet extends HttpServlet {
    private CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sid = req.getParameter("id");
        Category existCategory = categoryService.getById(sid);
        if (existCategory == null){
            System.out.println("không tìm thấy id");
            resp.sendRedirect("/admin/category/list");
            return;
        }
        req.setAttribute("list", existCategory);
        req.getRequestDispatcher("/admin/category/detail.jsp").forward(req,resp);
    }
}
