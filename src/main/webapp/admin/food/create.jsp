<%--
  Created by IntelliJ IDEA.
  User: Trình
  Date: 4/23/2021
  Time: 7:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="entity.Category" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String status = (String) request.getAttribute("status");
    List<Category> categories = (ArrayList<Category>) request.getAttribute("categories");
%>
<%@ taglib prefix="mt" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://cloudinary.com/jsp/taglib" prefix="cl" %>


<c:set var="bodyContent">
    <!-- Nội dung cần thêm -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800">Create Food</h1>
    </div>
    <div style="width: 80vw; margin: 0 auto">
        <p class="text-danger"><%=status%>
        </p>
        <form method="post" action="/admin/food/create" id="product_form">
            <div class="form-group">
                <label for="name">Tên món ăn </label>
                <input type="text" class="form-control" id="name" placeholder="Nhập tên món ăn" name="name" required>
            </div>
            <div class="form-group">
                <label for="categoryId">Danh mục</label>
                <select class="form-control" id="categoryId" name="categoryId" required>
                    <%for (Category category : categories) { %>
                    <option value=<%=category.getId()%>><%=category.getName()%>
                    </option>
                    <%}%>
                </select>
            </div>
            <div class="form-group">
                <label for="exampleFormControlTextarea1">Mô tả </label>
                <textarea class="form-control" id="exampleFormControlTextarea1" name="description" placeholder="Mô tả" rows="3" required></textarea>
            </div>
            <div class="form-group">
                <div class="col-sm-12 data-field-col">
                    <h4>Hình ảnh</h4>
                    <div class="form-group">
                        <button type="button" id="upload_widget" class="btn btn-primary">Thêm ảnh
                        </button>
                        <div class="thumbnails"></div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label for="price">Giá</label>
                <input type="number" class="form-control" id="price" placeholder="Nhập giá tiền" name="price" required>
            </div>
            <button type="submit" class="btn btn-primary">Tạo món ăn</button>
        </form>
    </div>
</c:set>
<mt:layoutAdmin title="Create Food">
    <jsp:body>
        ${bodyContent}
    </jsp:body>
</mt:layoutAdmin>
