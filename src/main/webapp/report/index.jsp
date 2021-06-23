<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<%    String pageName = "รายงานสถานะค้างเบิก";
    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
    request.setAttribute("active", "report");
    // request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/sweetalert.min.js", "../js/report/list.js"));
    HttpSession _sess = request.getSession();
%>

<jsp:include  page="../theme/header.jsp" flush="true" />

<input type="hidden" value="" id="r_status">
<input type="hidden" value="<%=_sess.getAttribute("user")%>" id="userlogin">
<input type="hidden" value="<%=session.getAttribute("user")%>" id="user_code">
<input type="hidden" value="<%=session.getAttribute("branch_code")%>" id="user_branch">
<input type="hidden" value="${user_name}" id="user_namex">
<input type="hidden" id="hSubLink" value="${sublink}">


<div id="show_list_detail">
   
</div>
<jsp:include  page="../theme/footer.jsp" flush="true" />
