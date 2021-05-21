<%@ page import="java.util.ArrayList" %>
<%@ page import="entity.Food" %>
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
<%@ taglib prefix="mt" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<%
    ArrayList<Food> foods = (ArrayList<Food>) request.getAttribute("foods");
    List<Category> categories = (ArrayList<Category>) request.getAttribute("categories");
    String linkImage = "https://res.cloudinary.com/hoadaica/image/upload/w_100,c_scale/";
%>
<% int total = (int) request.getAttribute("total"); %>
<% int pages = (int) request.getAttribute("pages"); %>

<c:set var="bodyContent">
    <!-- Nội dung cần thêm -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800">List Food</h1>
    </div>
    <hr>
    <h4 class="text-center"><a href="/admin/food/create">Create new</a></h4>
    <table class="table">
        <thead class="table-info">
        <tr  class="text-center">
            <th scope="col">Mã món ăn</th>
            <th scope="col">Tên món ăn</th>
            <th scope="col">Danh mục món ăn</th>
            <th scope="col">Mô tả</th>
            <th scope="col">Ảnh đại diện</th>
            <th scope="col">Giá</th>
            <th scope="col">Ngày bắt đầu bán</th>
            <th scope="col">Ngày sửa thông tin</th>
            <th scope="col">Trạng thái</th>
            <th scope="col">Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <% if (foods != null && foods.size() > 0) {
            for (int i = 0; i < foods.size(); i++) {
        if (foods.get(i).getStatus()==1){
            %>
        <tr>
            <td scope="col" style="text-align: center"><%=foods.get(i).getId()%>
            </td>
            <td scope="col" style="text-align: center"><%=foods.get(i).getName()%>
            </td>
            <td scope="col" style="text-align: center">
                <%for (Category category : categories) {
                    if (foods.get(i).getCategoryId() == category.getId()){%>
                <%=category.getName()%>
                <%}
                }%>
            </td>
            <td scope="col" style="text-align: center"><%=foods.get(i).getDescription()%>
            </td>
            <td scope="col" style="text-align: center">
                <div style="width: 100px; height: 100px; background-repeat: no-repeat; background-position: center; background-size: cover; background-image:
                        url('<%=linkImage + foods.get(i).getThumbnail() + ".jpg"%>')"></div>
            </td>
            <c:set var = "balance" value = "<%=foods.get(i).getPrice()%>" />
            <td scope="col" style="text-align: center">
                <fmt:formatNumber type = "number" maxFractionDigits = "3" value = "${balance}" />VNĐ
            </td>
            <td scope="col"
                style="text-align: center"><%= foods.get(i).getSaleStartDate()%>
            </td>
            <td scope="col"
                style="text-align: center"><%=foods.get(i).getUpdatedAt()%>
            </td>
            <td scope="col" style="text-align: center"><%=foods.get(i).toStatus(foods.get(i).getStatus())%>
            </td>
            <td scope="col" style="text-align: center">
                <div class="btn-group">
                    <a href="/admin/food/detail?id=<%=foods.get(i).getId()%>" class="btn btn-primary">Xem</a>
                    <a href="/admin/food/edit?id=<%=foods.get(i).getId()%>" class="btn btn-success">Sửa</a>
                    <a href="/admin/food/delete?id=<%=foods.get(i).getId()%>" class="btn btn-danger">Xóa</a>
                </div>
            </td>
        </tr>
        <%
        }
       }
       }%>
        </tbody>
    </table>
    <nav aria-label="Page navigation example">
        <ul class="pagination" style="align-content: center">
            <%                //Button Previous
                int back = 0;
                if (pages == 0 || pages == 1) {
                    back = 1;//Luon la page 1
                } else {
                    back = pages - 1;//Neu pages tu 2 tro len thi back tru 1
                }
            %>
            <li class="page-item"><a class="page-link" href="list?pages=<%=back%>">Previous</a></li>
            <%
                //Button Number pages
                int loop = 0, num = 0;
                if ((total / 4) % 2 == 0) {
                    num = total / 4;
                } else {
                    num = (total + 1) / 4;
                }
                //Nếu total lẻ thêm 1
                if (total % 2 != 0) {
                    loop = (total / 4) + 1;

                } else {
                    //Nếu total chẵn nhỏ hơn fullpage và # fullPage thì thêm 1
                    if (total < (num * 4) + 4 && total != num * 4) {
                        loop = (total / 4) + 1;
                    } else {
                        //Nếu bằng fullPage thì không thêm
                        loop = (total / 4);
                    }
                }
                //Lap so pages
                for (int i = 1; i <= loop; i++) {%>
            <% if (pages == i) {%>
            <li class="page-item active">
                <a class="page-link" href="list?pages=<%=i%>"><%=i%> <span class="sr-only">(current)</span></a>
            </li>
            <%} else {%>
            <li class="page-item"><a class="page-link" href="list?pages=<%=i%>"><%=i%></a></li>

            <%}
            }%>
            <%
                //Button Next
                int next = 0;
                //Nếu total lẻ
                if (total % 2 != 0) {
                    if (pages == (total / 4) + 1) {
                        next = pages;//Khong next
                    } else {
                        next = pages + 1;//Co next
                    }
                } else {
                    //Nếu total chẵn nhỏ hơn fullpage
                    //Và không fullPage thì thêm 1
                    if (total < (num * 4) + 4 && total != num * 4) {
                        if (pages == (total / 4) + 1) {
                            next = pages;//Khong next
                        } else {
                            next = pages + 1;//Co next
                        }
                    } else {
                        //Nếu fullPage đến trang cuối dừng
                        //Chưa tới trang cuối thì được next
                        if (pages == (total / 4)) {
                            next = pages;//Khong next
                        } else {
                            next = pages + 1;//Co next
                        }
                    }
                }
            %>
            <li class="page-item"><a class="page-link" href="list?pages=<%=next%>">Next</a></li>
        </ul>
    </nav>
</c:set>

<mt:layoutAdmin title="List Food">
    <jsp:body>
        ${bodyContent}
    </jsp:body>
</mt:layoutAdmin>