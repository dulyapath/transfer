<%@include file="../globalsub.jsp" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Model.Permission"%>
<%@page import="utils.PermissionUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="utils.RandomID"%>

<%    List css = request.getAttribute("css") == null ? new ArrayList<String>() : (List) request.getAttribute("css");
    String v = "?_=" + RandomID.rand();
    String active = request.getAttribute("active").toString();
    String user_code = session.getAttribute("user").toString();
    String is_direct = session.getAttribute("is_direct").toString();
    String is_del_history = session.getAttribute("is_del_history").toString();

%>
<!DOCTYPE html>
<html lang="en">
    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">

        <title>Transfer System</title>

        <!-- Custom fonts for this template-->
        <link href="${sublink}vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
        <link
            href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i"
            rel="stylesheet">

        <!-- Custom styles for this template-->
        <link href="${sublink}css/sb-admin-2.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${sublink}plugins/fontawesome-free/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/select2@4.0.13/dist/css/select2.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="${sublink}css/select2-bootstrap.css">
        <link rel="stylesheet" href="${sublink}css/sweetalert.css">
        <%            if (css.size() > 0) {
                for (int i = 0; i < css.size(); i++) {
                    out.print("<link rel=\"stylesheet\" href=\"" + css.get(i).toString() + v + "\">");
                }
            }

        %>
        <style>
            .text-header{
                font-size: 18px;
                font-weight: bold;
                color:#000000;
            }
            .text-body{
                font-size: 14.5px;
                font-weight: bold;
                color:#000000 !important;
            }

        </style>
    </head>
    <body id="page-top">

        <!-- Page Wrapper -->
        <div id="wrapper">

            <!-- Sidebar -->
            <ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

                <!-- Sidebar - Brand -->
                <a class="sidebar-brand d-flex align-items-center justify-content-center" href="index.jsp">
                    <div class="sidebar-brand-icon rotate-n-15">
                        <i class="fas fa-exchange-alt"></i>
                    </div>
                    <div class="sidebar-brand-text mx-3">Transfer System</div>
                </a>

                <!-- Divider -->
                <hr class="sidebar-divider">
                <div class="sidebar-heading">
                    <span style="font-size:12.5px">ปลายทาง(ขอเบิก/รับสินค้า)</span>
                </div>
                <% if (active == "request") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}transrequest/index.jsp">
                        <i class="fas fa-file"></i>
                        <span>ขอเบิก</span></a>
                </li>
                <% if (active == "receive") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}receive/index.jsp">
                        <i class="fas fa-file-import"></i>
                        <span>รับเข้า</span></a>
                </li>
                <% if (active == "history") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}history/index.jsp">
                        <i class="fas fa-file"></i>
                        <span>ประวัติการรับสินค้า</span></a>
                </li>
                <%
                    if (user_code.toUpperCase().equals("SUPERADMIN")) {
                        if (active == "reply") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}history/refund.jsp">
                        <i class="fas fa-file"></i>
                        <span>ยกเลิกใบรับ</span></a>
                </li>
                <% }%>
                <hr class="sidebar-divider">
                <div class="sidebar-heading">
                    <span style="font-size:12.5px">ต้นทาง(เบิกสินค้า)</span>
                </div>
                <% if (active == "send") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}transsend/index.jsp">
                        <i class="fas fa-file-export"></i>
                        <span>เบิกออก</span></a>
                </li>
                <% if (active == "refund") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}transsend/refund.jsp">
                        <i class="fas fa-file-export"></i>
                        <span>รับคืนจากการเบิก</span></a>
                </li>
                <%

                    if (is_direct != null || user_code.equals("SUPERADMIN")) {
                        if (is_direct.equals("1") || user_code.equals("SUPERADMIN")) {
                            if (active == "send2") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}transsend2/index.jsp">
                        <i class="fas fa-file-export"></i>
                        <span>เบิกออก(ไม่มีใบขอเบิก)</span></a>
                </li>
                <% }
                    }%>
                <% if (active == "cancelsend") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}transsend/cancel.jsp">
                        <i class="fas fa-file"></i>
                        <span>ยกเลิกใบเบิก</span></a>
                </li>
                <%   if (active == "report") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}report/index.jsp">
                        <i class="fas fa-file"></i>
                        <span>รายงานสถานะ</span></a>
                </li>
                <% if (active == "historysend") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}historysend/index.jsp">
                        <i class="fas fa-file"></i>
                        <span>ประวัติการเบิกสินค้า</span></a>
                </li>
                <% if (active == "historyrefund") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}historyrefund/index.jsp">
                        <i class="fas fa-file"></i>
                        <span>ประวัติการรับคืนสินค้า</span></a>
                </li>
                <!-- Divider -->
                <hr class="sidebar-divider">
                <% if (active == "reportrequestsend") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}reportrequestsend/index.jsp">
                        <i class="fas fa-file"></i>
                        <span>รายงานขอเบิก-เบิกสินค้า</span></a>
                </li>

                <%

                    if (is_del_history != null || user_code.equals("SUPERADMIN")) {
                        if (is_del_history.equals("1") || user_code.equals("SUPERADMIN")) {
                            if (active == "historydel") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}historydel/index.jsp">
                        <i class="fas fa-file-export"></i>
                        <span>ประวัติการลบ</span></a>
                </li>
                <% }
                    }

                    if (user_code.toUpperCase().equals("SUPERADMIN")) {
                        if (active == "store") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}storage/index.jsp">
                        <i class="fas fa-store"></i>
                        <span>กำหนดคลัง</span></a>
                </li>
                <%}
                    if (user_code.toUpperCase().equals("SUPERADMIN")) {
                        if (active == "setting") { %>
                <li class="nav-item active">
                    <% } else { %>
                <li class="nav-item ">
                    <% }%>
                    <a class="nav-link" href="${sublink}setting/index.jsp">
                        <i class="fas fa-store"></i>
                        <span>กำหนดค่าเริ่มต้น</span></a>
                </li>
                <%}%>
                <!-- Nav Item - Tables -->
                <li class="nav-item">
                    <a class="nav-link" href="${sublink}logout.jsp">
                        <i class="fas fa-sign-out-alt"></i>
                        <span>ออกจากระบบ</span></a>
                </li>

                <!-- Divider -->
                <hr class="sidebar-divider d-none d-md-block">
            </ul>
            <div id="content-wrapper" class="d-flex flex-column">

                <!-- Main Content -->
                <div id="content">
                    <nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">

                        <!-- Sidebar Toggle (Topbar) -->
                        <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
                            <i class="fa fa-bars"></i>
                        </button>

                        <div class="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search">
                            <h3>${title}</h3>
                        </div>
                        <!-- Topbar Navbar -->
                        <ul class="navbar-nav ml-auto">

                            <!-- Nav Item - User Information -->
                            <li class="nav-item dropdown no-arrow">
                                <a class="nav-link " id="userDropdown" role="button"
                                   aria-haspopup="true" aria-expanded="false">
                                    <span class="mr-2 d-none d-lg-inline text-gray-600 small">${user_name}</span>
                                    <img class="img-profile rounded-circle"
                                         src="${sublink}img/undraw_profile.svg">
                                </a>
                            </li>
                        </ul>
                    </nav>
                    <div class="container-fluid">