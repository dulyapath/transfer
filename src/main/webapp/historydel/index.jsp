<%@include file="../globalsub.jsp"  %>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<%    String pageName = "ประวัติการลบรายการ";
    request.setAttribute("title", pageName);
    request.setAttribute("sublink", "../");
    request.setAttribute("active", "historydel");
    // request.setAttribute("css", Arrays.asList("../css/sweetalert.css", "../css/bootstrap-datetimepicker.min.css"));
    request.setAttribute("js", Arrays.asList("../js/sweetalert.min.js", "../js/historydel/list.js"));
    HttpSession _sess = request.getSession();
%>

<jsp:include  page="../theme/header.jsp" flush="true" />

<input type="hidden" value="" id="r_status">
<input type="hidden" value="<%=_sess.getAttribute("user")%>" id="userlogin">
<input type="hidden" value="<%=session.getAttribute("user")%>" id="user_code">
<input type="hidden" value="<%=session.getAttribute("branch_code")%>" id="user_branch">
<input type="hidden" value="${user_name}" id="user_namex">
<input type="hidden" id="hSubLink" value="${sublink}">
<div class="card">
    <div class="card-body">
        <div class="row">
            <div class="col-md-6">
         
                <div class="input-group mb-3">
                    <input type="text" class="form-control" id="search_name" placeholder="เลขที่เอกสาร" >

                </div>
            </div>
            <div class="col-md-3">
                <button class="btn btn-primary" id="btn-search"><i class="fas fa-play"></i> ประมวลผล</button>
            </div>
            <!-- <div class="col-lg-4">
                 <div class="form-group">
                     <label>จากวันที่</label>
                     <input type="date" class="form-control" id="search_from_date" placeholder="จากวันที่">
                 </div>
             </div>
             <div class="col-lg-4">
                 <div class="form-group">
                     <label>ถึงวันที่</label>
                     <input type="date" class="form-control" id="search_to_date" placeholder="จากวันที่">
                 </div>
             </div>-->

        </div>


        <div class="table-responsive">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <td class="text-center">เลขที่เอกสาร</td>
                        <td class="text-center">ประเภท</td>
                        <td class="text-center">วันที่</td>
                        <td class="text-center">เวลา</td>
                        <td class="text-center">ผู้ลบ</td>
                    </tr>
                </thead>
                <tbody  id="show_list_detail">

                </tbody>
            </table>

        </div>
    </div>
</div>

<jsp:include  page="../theme/footer.jsp" flush="true" />
