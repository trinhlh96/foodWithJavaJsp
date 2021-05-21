package controller;

import entity.Category;
import repository.GenericRepository;
import service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ListCategory", urlPatterns = "/admin/category/list")
public class ListCategoryServlet extends HttpServlet {
    private CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int first = 0, last = 0, pages = 1;
        if (req.getParameter("pages") != null) {
            pages = (int) Integer.parseInt(req.getParameter("pages"));
        }
        //Lấy tổng sản phẩm trong data bằng query select count(id) from name_table với JDBC Connect
        int total = new GenericRepository<>(Category.class).getCountCategory();
        if (total <= 5) {
            first = 0;
            last = total;
        } else {
            first = (pages - 1) * 5;
            last = 5;
        }
        //Lấy ra danh sách sản phẩm
        req.setAttribute("categories", categoryService.getList(first, last));
        req.setAttribute("total", total);
        req.setAttribute("pages", pages);
        req.getRequestDispatcher("/admin/category/list.jsp").forward(req, resp);
    }
}
