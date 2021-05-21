<%--
  Created by IntelliJ IDEA.
  User: Trình
  Date: 4/23/2021
  Time: 7:57 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="java.util.ArrayList" %>
<%@ page import="entity.Category" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="mt" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% Category category = (Category) request.getAttribute("list"); %>


<c:set var="bodyContent">
    <!-- Nội dung cần thêm -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800">Detail Category</h1>
    </div>
    <hr>
    <h4 class="text-center"><a href="/admin/category/create">Create new</a></h4>
    <table class="table">
        <thead class="table-info">
        <tr class="text-center">
            <th scope="col">Mã danh mục</th>
            <th scope="col">Tên danh mục</th>
            <th scope="col">Ngày tạo</th>
            <th scope="col">Ngày sửa thông tin</th>
            <th scope="col">Trạng thái</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td scope="col" style="text-align: center"><%=category.getId()%>
            </td>
            <td scope="col" style="text-align: center"><%=category.getName()%>
            </td>
            <td scope="col" style="text-align: center"><%=category.getCreatedAt()%>
            </td>
            <td scope="col" style="text-align: center"><%=category.getUpdatedAt()%>
            </td>
            <td scope="col" style="text-align: center"><%=category.toStatus(category.getStatus())%>
            </td>
        </tr>
        </tbody>
    </table>
    <div>
        <a href="/admin/category/list" class="badge badge-primary">Back</a>
    </div>
</c:set>

<mt:layoutAdmin title="Detail Category">
    <jsp:body>
        ${bodyContent}
    </jsp:body>
</mt:layoutAdmin>


