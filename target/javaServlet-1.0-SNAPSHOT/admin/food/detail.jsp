<%--
  Created by IntelliJ IDEA.
  User: Trình
  Date: 4/23/2021
  Time: 7:57 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="java.util.ArrayList" %>
<%@ page import="entity.Category" %>
<%@ page import="entity.Food" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="mt" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<%    List<Category> categories = (ArrayList<Category>) request.getAttribute("categories");
    String linkImage = "https://res.cloudinary.com/hoadaica/image/upload/w_200,c_scale/";

%>
<% Food food = (Food) request.getAttribute("list"); %>


<c:set var="bodyContent">
    <!-- Nội dung cần thêm -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800">Detail Food</h1>
    </div>
    <hr>
    <h4 class="text-center"><a href="/admin/food/create">Create new</a></h4>
    <table class="table">
        <thead class="table-info">
        <tr class="text-center">
            <th scope="col">Mã món ăn</th>
            <th scope="col">Tên món ăn</th>
            <th scope="col">Danh mục món ăn</th>
            <th scope="col">Mô tả</th>
            <th scope="col">Ảnh đại diện</th>
            <th scope="col">Giá</th>
            <th scope="col">Ngày bắt đầu bán</th>
            <th scope="col">Ngày sửa thông tin</th>
            <th scope="col">Trạng thái</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td scope="col" style="text-align: center"><%=food.getId()%>
            </td>
            <td scope="col" style="text-align: center"><%=food.getName()%>
            </td>
            <td scope="col" style="text-align: center">
                <%for (Category category : categories) {
                    if (food.getCategoryId() == category.getId()){%>
                <%=category.getName()%>
                <%}
                }%>
            </td>
            <td scope="col" style="text-align: center"><%=food.getDescription()%>
            </td>
            <td scope="col" style="text-align: center">
                <div style="width: 100px; height: 100px; background-repeat: no-repeat; background-position: center; background-size: cover; background-image:
                        url('<%=linkImage + food.getThumbnail() + ".jpg"%>')"></div>
            </td>
            <c:set var = "balance" value = "<%=food.getPrice()%>" />
            <td scope="col" style="text-align: center">
                <fmt:formatNumber type = "number" maxFractionDigits = "3" value = "${balance}" />VNĐ
            </td>
            <td scope="col"
                style="text-align: center"><%=food.getSaleStartDate()%>
            </td>
            <td scope="col"
                style="text-align: center"><%=food.getUpdatedAt()%>
            </td>
            <td scope="col" style="text-align: center"><%=food.toStatus(food.getStatus())%>
            </td>
        </tr>
        </tbody>
    </table>
    <div>
        <a href="/admin/food/list" class="badge badge-primary">Back</a>
    </div>
</c:set>

<mt:layoutAdmin title="Detail Food">
    <jsp:body>
        ${bodyContent}
    </jsp:body>
</mt:layoutAdmin>


