<%--
  Created by IntelliJ IDEA.
  User: Trình
  Date: 4/23/2021
  Time: 7:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String status = (String) request.getAttribute("status");
%>
<%@ taglib prefix="mt" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="bodyContent">
    <!-- Nội dung cần thêm -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800">Create Category</h1>
    </div>
    <div style="width: 80vw; margin: 0 auto">
        <p class="text-danger"><%=status%>
        </p>
        <form method="post" action="/admin/category/create">
            <div class="form-group">
                <label for="name">Tên Danh mục </label>
                <input type="text" class="form-control" id="name" placeholder="Nhập tên danh mục" name="name" required>
            </div>
            <button type="submit" class="btn btn-primary">Tạo Danh mục</button>
        </form>
    </div>
</c:set>
<mt:layoutAdmin title="Create Category">
    <jsp:body>
        ${bodyContent}
    </jsp:body>
</mt:layoutAdmin>

