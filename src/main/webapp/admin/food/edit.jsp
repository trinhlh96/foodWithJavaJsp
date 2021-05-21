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
<%@ page import="entity.Food" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String status = (String) request.getAttribute("status");
    List<Category> categories = (ArrayList<Category>) request.getAttribute("categories");
    Food food = (Food) request.getAttribute("list");
    String linkImage = "https://res.cloudinary.com/hoadaica/image/upload/w_100,c_scale/";
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
        <form method="post" action="/admin/food/edit" id="product_form">
            <div class="form-group">
                <label for="name">Tên món ăn </label>
                <input value="<%= food.getName() %>" type="text" class="form-control" id="name" placeholder="Nhập tên món ăn" name="name">
            </div>
            <div class="form-group">
                <label for="categoryId">Danh mục</label>
                <select class="form-control" id="categoryId" name="categoryId">
                    <%for (Category category : categories) { %>
                    <option <%=category.getId() == food.getCategoryId() ? "selected" : ""%>  value=<%=category.getId()%>><%=category.getName()%>
                    </option>
                    <%}%>
                </select>
            </div>
            <div class="form-group">
                <label for="description">Mô tả </label>
                <textarea class="form-control" id="description" name="description" placeholder="Mô tả" rows="3"><%= food.getDescription() %></textarea>
            </div>
            <div class="form-group">
                <div class="col-sm-12 data-field-col">
                    <h4>Hình ảnh</h4>
                    <div class="form-group">
                        <button type="button" id="upload_widget" class="btn btn-primary">Thêm ảnh
                        </button>
                        <div class="thumbnails">
                            <ul class="cloudinary-thumbnails">
                                <li class="cloudinary-thumbnail active">
                                    <img src="<%=linkImage + food.getThumbnail() + ".jpg"%>" alt="">
                                    <a href="javascript:void(0)" class="cloudinary-delete">x</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <label for="price">Giá</label>
                <input value="<%= food.getPrice() %>" type="number" class="form-control" id="price" placeholder="Nhập giá tiền" name="price">
            </div>
            <div class="form-group">
                <label for="status">Trạng thái</label>
                <select class="form-control" id="status" name="status">
                    <%if(food.getStatus() == 1){
                        %>
                    <option selected value="1">Đang bán</option>
                    <option value="2">Dừng bán</option>
                    <option value="0">Xóa</option>
                    <%
                    }if (food.getStatus() == 2){
                    %>
                    <option value="1">Đang bán</option>
                    <option selected  value="2">Dừng bán</option>
                    <option value="0">Xóa</option>
                    <%
                    }
                    %>
                </select>
            </div>

            <button type="submit" class="btn btn-primary">Cập nhật món ăn</button>
            <input type="hidden" name="thumbnails" data-cloudinary-public-id="<%=food.getThumbnail()%>" value="<%=food.getThumbnail()%>">
            <input type="hidden" class="form-control" name="id" value="<%= food.getId() %>">
        </form>
    </div>
</c:set>
<mt:layoutAdmin title="Edit Food">
    <jsp:body>
        ${bodyContent}
    </jsp:body>
</mt:layoutAdmin>
