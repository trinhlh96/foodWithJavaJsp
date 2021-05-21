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
<% ArrayList<Category> categories = (ArrayList<Category>) request.getAttribute("categories"); %>
<% int total = (int) request.getAttribute("total"); %>
<% int pages = (int) request.getAttribute("pages"); %>
<%@ taglib prefix="mt" tagdir="/WEB-INF/tags" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="bodyContent">
    <!-- Nội dung cần thêm -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800">List Category</h1>
    </div>
    <hr>
    <h4 class="text-center"><a href="/admin/category/create">Create new</a></h4>
    <table class="table">
        <thead class="table-info">
        <tr  class="text-center">
            <th scope="col">Mã danh mục</th>
            <th scope="col">Tên danh mục</th>
            <th scope="col">Ngày tạo</th>
            <th scope="col">Ngày sửa thông tin</th>
            <th scope="col">Trạng thái</th>
            <th scope="col">Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <% if (categories != null && categories.size() > 0) {
            for (int i = 0; i < categories.size(); i++) {
        %>
        <tr>
            <td scope="col" style="text-align: center"><%=categories.get(i).getId()%>
            </td>
            <td scope="col" style="text-align: center"><%=categories.get(i).getName()%>
            </td>
            <td scope="col" style="text-align: center"><%=categories.get(i).getCreatedAt()%>
            </td>
            <td scope="col" style="text-align: center"><%=categories.get(i).getUpdatedAt()%>
            </td>
            <td scope="col" style="text-align: center"><%=categories.get(i).toStatus(categories.get(i).getStatus())%>
            </td>
            <td scope="col" style="text-align: center">
                <div class="btn-group">
                    <a href="/admin/category/detail?id=<%=categories.get(i).getId()%>" class="btn btn-primary">Xem</a>
                    <a href="/admin/category/edit?id=<%=categories.get(i).getId()%>" class="btn btn-success">Sửa</a>
                    <a href="/admin/category/delete?id=<%=categories.get(i).getId()%>" class="btn btn-danger">Xóa</a>
                </div>
            </td>
        </tr>
        <%
                }
            }
        %>
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

<mt:layoutAdmin title="List Category">
    <jsp:body>
        ${bodyContent}
    </jsp:body>
</mt:layoutAdmin>

