package arcustomer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils._global;
import utils._routine;

@WebServlet(name = "Brcustomer-list", urlPatterns = {"/arcustomer-list"})
public class List extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        StringBuilder __html = new StringBuilder();

        HttpSession _sess = request.getSession();
        String keyword = "", barcode = "";

        DecimalFormat decim = new DecimalFormat("#,###.##");

        if (request.getParameter("value") == null || request.getParameter("value").toString().isEmpty()) {

        } else {
            keyword = request.getParameter("value");
        }

        String[] __keywordx = keyword.split(" ");
        StringBuilder __wherelike1 = new StringBuilder();
        StringBuilder __wherelike2 = new StringBuilder();
        StringBuilder __wherelike3 = new StringBuilder();
        StringBuilder __wherelike = new StringBuilder();

        if (__keywordx.length > 1) {
            __wherelike.append(" ");
            for (int i = 0; i < __keywordx.length; i++) {
                if (i == 0) {
                    __wherelike1.append(" upper(name_1) LIKE upper('%" + __keywordx[i] + "%') ");
                    __wherelike2.append(" upper(code) LIKE upper('%" + __keywordx[i] + "%') ");
                    __wherelike3.append(" replace(telephone,'-','') LIKE '%" + __keywordx[i].replace("-", "") + "%' ");

                } else {
                    __wherelike1.append(" and upper(name_1) LIKE upper('%" + __keywordx[i] + "%') ");
                    __wherelike2.append(" and upper(code) LIKE upper('%" + __keywordx[i] + "%') ");
                    __wherelike3.append(" and replace(telephone,'-','') LIKE '%" + __keywordx[i].replace("-", "") + "%' ");

                }

            }

            __wherelike.append(" where  (" + __wherelike1 + ") or (" + __wherelike2 + ") or (" + __wherelike3 + ")");
        } else {

            __wherelike.append(" where  upper(code) LIKE upper('%" + keyword + "%') or upper(name_1) LIKE upper('%" + keyword + "%') or replace(telephone,'-','') LIKE '%" + keyword.replace("-", "") + "%' ");

        }

        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {

            return;
        }

        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();
        String __provider = _sess.getAttribute("provider").toString().toLowerCase();
        PreparedStatement __stmt = null;
        int page = 1;
        int limit = 30;
        int offset = 0;

        Connection __conn = null;
        String __strHTML = "";
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String __queryExtend = "";


            /* if (request.getParameter("from_total") != null && request.getParameter("to_total") != null) {
                __queryExtend += " AND total_amount BETWEEN " + Double.parseDouble(request.getParameter("from_total")) + " AND " + Double.parseDouble(request.getParameter("to_total")) + " ";
            }
             */
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }

            if (page < 1) {
                page = 1;
            }

            offset = limit * (page - 1);
            Integer branch = 0;
            Statement __stmtHead3 = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __branchcheck = __stmtHead3.executeQuery("select count(*) as xx from sync_branch_list");
            while (__branchcheck.next()) {
                branch = __branchcheck.getInt("xx");

            }
            String branch_sync = "";
            if (branch > 0) {
                branch_sync = " and x1.branch_sync = ic_trans_detail.branch_sync ";
            }

            // ยอดค้างชำระ
            String query2 = " select sum(balance_amount) from (select cust_code, doc_date , credit_date as due_date , doc_no , trans_flag as doc_type , used_status , doc_ref as ref_doc_no , doc_ref_date as ref_doc_date , coalesce(total_amount,0) as amount , coalesce(total_amount,0)-(select coalesce(sum(coalesce(sum_pay_money,0)),0) from ap_ar_trans_detail where coalesce(last_status, 0)=0 and trans_flag in (239) and ic_trans.doc_no=ap_ar_trans_detail.billing_no and ic_trans.doc_date=ap_ar_trans_detail.billing_date) as balance_amount  from ic_trans  where  coalesce(last_status, 0)=0  and trans_flag=44 and (inquiry_type=0  or inquiry_type=2)  and cust_code=ar_customer.code "
                    + "union all select cust_code , doc_date , credit_date as due_date , doc_no , trans_flag as doc_type , used_status , '' as ref_doc_no , null as ref_doc_date , coalesce(total_amount,0) as amount , coalesce(total_amount,0)-(select coalesce(sum(coalesce(sum_pay_money,0)),0) from ap_ar_trans_detail where coalesce(last_status, 0)=0 and trans_flag in (239) and ic_trans.doc_no=ap_ar_trans_detail.billing_no and ic_trans.doc_date=ap_ar_trans_detail.billing_date ) as balance_amount  from ic_trans  where  coalesce(last_status, 0)=0   and (trans_flag=46 or trans_flag=93 or trans_flag=99 or trans_flag=95 or trans_flag=101)  and cust_code=ar_customer.code "
                    + "union all select cust_code , doc_date , credit_date as due_date , doc_no , trans_flag as doc_type , used_status , '' as ref_doc_no , null as ref_doc_date , -1*coalesce(total_amount,0) as amount , -1*(coalesce(total_amount,0)+(select coalesce(sum(coalesce(sum_pay_money,0)),0) from ap_ar_trans_detail where coalesce(last_status, 0)=0 and trans_flag in (239) and ic_trans.doc_no=ap_ar_trans_detail.billing_no and ic_trans.doc_date=ap_ar_trans_detail.billing_date)) as balance_amount  from ic_trans  where  coalesce(last_status, 0)=0   and ((trans_flag=48 and inquiry_type in (0,2,4) ) or trans_flag=97 or trans_flag=103)  and cust_code=ar_customer.code) as xx";
            // เช็คค้างชำระ
            String query3 = " select sum(amount) from cb_chq_list where ap_ar_code=ar_customer.code and ap_ar_type=1 and chq_type=1 and status<>2";
            // ใบสั่งจองค้างออกบิล
            String query4 = " select sum(total1-total2) from (select doc_no,coalesce(sum(total_amount),0) as total1,(select sum(coalesce((select sum(sum_amount) from ic_trans_detail where ic_trans_detail.ref_doc_no=ic_trans.doc_no and trans_flag=44 and last_status=0),0))) as total2 from ic_trans where cust_code=ar_customer.code and trans_flag=34 and inquiry_type in(0,2) and last_status=0 group by doc_no) as q1";
            // ใบสั่งขายค้างออกบิล
            String query5 = " select sum(total1-total2) from (select doc_no,coalesce(sum(total_amount),0) as total1,(select sum(coalesce((select sum(sum_amount) from ic_trans_detail where ic_trans_detail.ref_doc_no=ic_trans.doc_no and trans_flag=44 and last_status=0),0))) as total2 from ic_trans where cust_code=ar_customer.code and trans_flag=36 and last_status=0 and doc_success = 0 group by doc_no) as q2";
            // เงินรับล่วงหน้า
            // 40=รับล่วงหน้า(ลูกหนี้)
            // 42=คืนเงินรับล่วงหน้า(ลูกหนี้)
            String query6 = " select sum(total_amount-(coalesce((select sum(total_amount) from ic_trans where ic_trans.doc_ref=q11.doc_no and last_status=0),0)+coalesce((select sum(amount) from cb_trans_detail where cb_trans_detail.trans_number=q11.doc_no and last_status=0),0))) from ic_trans as q11 where cust_code=ar_customer.code and trans_flag=40 and last_status=0";
            //
            String query7 = ",(select sum(case when status in  (0,1,3) then amount else 0 end) as onhand_amount from cb_chq_list where chq_type = 1 and ap_ar_code = ar_customer.code) as chq_on_hand";
            String query = " select *,coalesce((select name_1 from ar_group where ar_group.code=q1.ar_group),'') as ar_group_name,coalesce((select name_1 from ar_type where ar_type.code=q1.ar_type),'') as ar_type_name from (select (select credit_money from ar_customer_detail where ar_customer_detail.ar_code=code) as credit_money,coalesce((select group_main from ar_customer_detail where ar_customer_detail.ar_code=code),'') as ar_group,coalesce(ar_type,'')as ar_type,code,name_1,address,coalesce(telephone,'')as telephone,(" + query2 + ") as balance_amount,(" + query3 + ") as chq_amount,(" + query4 + ") as s1_amount,(" + query5 + ") as s2_amount,(" + query6 + ") as deposit_amount " + query7 + " from ar_customer " + __wherelike + ") as q1 order by name_1";
//            System.out.println(query);
            // echo $query;
            // response.getWriter().print(query);

            //response.getWriter().write(__queryHead.toString());
            __stmt = __conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();
            ResultSetMetaData _rsHeadMd = __rsHead.getMetaData();
            int _colHeadCount = _rsHeadMd.getColumnCount();
            __rsHead.next();
            int row = __rsHead.getRow();

            __rsHead.previous();

            if (row > 0) {
                __strHTML += "<table width='100%' border=1>";
                __strHTML += "<tr>"
                        + "<td align='center'><b>รหัสลูกค้า</b></td>"
                        + "<td align='center'><b>ชื่อลูกค้า</b></td>"
                        + "<td align='center'><b>กลุ่มลูกค้า</b></td>"
                        + "<td align='center'><b>ประเภทลูกค้า</b></td>"
                        + "<td align='center'><b>วงเงิน</b></td>"
                        + "<td align='center'><b>วงเงินคงเหลือ</b></td>"
                        + "<td align='center'><b>รับล่วงหน้า</b></td>"
                        + "<td align='center'><b>ยอดค้างชำระ</b></td>"
                        + "<td align='center'><b>เช็คในมือ</b></td>"
                        + "<td align='center'><b>ใบสั่งจองค้างออกบิล(เงินเชื่อ)</b></td>"
                        + "<td align='center'><b>ใบสั่งขายค้างออกบิล(เงินเชื่อ)</b></td>"
                        + "<td align='center'><b>รายละเอียด</b></td>";
                __strHTML += "</tr>";

                while (__rsHead.next()) {

                    String strCode = __rsHead.getString("code") != null && !__rsHead.getString("code").isEmpty() ? __rsHead.getString("code") : "";
                    String strName = __rsHead.getString("name_1") != null && !__rsHead.getString("name_1").isEmpty() ? __rsHead.getString("name_1") : "";
                    String strTelephone = __rsHead.getString("telephone") != null && !__rsHead.getString("telephone").isEmpty() ? __rsHead.getString("telephone") : "";
                    String strArGroup = __rsHead.getString("ar_group") != null && !__rsHead.getString("ar_group").isEmpty() ? __rsHead.getString("ar_group") : "";
                    String strArGroupName = __rsHead.getString("ar_group_name") != null && !__rsHead.getString("ar_group_name").isEmpty() ? __rsHead.getString("ar_group_name") : "";
                    String strArType = __rsHead.getString("ar_type") != null && !__rsHead.getString("ar_type").isEmpty() ? __rsHead.getString("ar_type") : "";
                    String strArTypeName = __rsHead.getString("ar_type_name") != null && !__rsHead.getString("ar_type_name").isEmpty() ? __rsHead.getString("ar_type_name") : "";

                    String strCreditMoney = __rsHead.getString("credit_money") != null && !__rsHead.getString("credit_money").isEmpty() ? String.valueOf(decim.format(__rsHead.getInt("credit_money"))) : "";
                    String strDepositAmount = __rsHead.getString("deposit_amount") != null && !__rsHead.getString("deposit_amount").isEmpty() ? String.valueOf(decim.format(__rsHead.getInt("deposit_amount"))) : "";
                    String strBalanceAmount = __rsHead.getString("balance_amount") != null && !__rsHead.getString("balance_amount").isEmpty() ? String.valueOf(decim.format(__rsHead.getInt("balance_amount"))) : "";
                    String strCHQonHead = __rsHead.getString("chq_on_hand") != null && !__rsHead.getString("chq_on_hand").isEmpty() ? String.valueOf(decim.format(__rsHead.getInt("chq_on_hand"))) : "";
                    String strS1Amount = __rsHead.getString("s1_amount") != null && !__rsHead.getString("s1_amount").isEmpty() ? String.valueOf(decim.format(__rsHead.getInt("s1_amount"))) : "";
                    String strS2Amount = __rsHead.getString("s2_amount") != null && !__rsHead.getString("s2_amount").isEmpty() ? String.valueOf(decim.format(__rsHead.getInt("s2_amount"))) : "";

                    double credit_money = __rsHead.getString("credit_money") != null && !__rsHead.getString("credit_money").isEmpty() ? __rsHead.getInt("credit_money") : 0;
                    double deposit_amount = __rsHead.getString("deposit_amount") != null && !__rsHead.getString("deposit_amount").isEmpty() ? __rsHead.getInt("deposit_amount") : 0;
                    double balance_amount = __rsHead.getString("balance_amount") != null && !__rsHead.getString("balance_amount").isEmpty() ? __rsHead.getInt("balance_amount") : 0;
                    double chq_on_hand = __rsHead.getString("chq_on_hand") != null && !__rsHead.getString("chq_on_hand").isEmpty() ? __rsHead.getInt("chq_on_hand") : 0;

                    double totol_amount = (((credit_money + deposit_amount) - balance_amount) - chq_on_hand);

                    __strHTML += "<tr>";
                    __strHTML += "<td>" + strCode + "</td>";
                    __strHTML += "<td>" + strName + "(" + strTelephone + ")" + "</td>";
                    __strHTML += "<td>" + strArGroup + " " + strArGroupName + "</td>";
                    __strHTML += "<td>" + strArType + " " + strArTypeName + "</td>";
                    __strHTML += "<td><b>" + strCreditMoney + "</b></td>";
                    __strHTML += "<td><b>" + decim.format(totol_amount) + "</b></td>";
                    __strHTML += "<td><b>" + strDepositAmount + "</b></td>";
                    __strHTML += "<td><b>" + strBalanceAmount + "</b></td>";
                    __strHTML += "<td><b>" + strCHQonHead + "</b></td>";
                    __strHTML += "<td><b>" + strS1Amount + "</b></td>";
                    __strHTML += "<td><b>" + strS2Amount + "</b></td>";
                    __strHTML += "<td align='center'><button id='C" + strCode + "' onclick='credit(this)' class='btn btn-warning btn-sm'>รายละเอียด</button></td>";
                    __strHTML += "</tr>";
                    __strHTML += "<tr><td colspan=12 bgcolor='orange'><div style='display: inline' id='R" + strCode + "'></div></td></tr>";
                }
                __strHTML += "</table>";
            } else {
                response.getWriter().print("no item");
                return;
            }

            __rsHead.close();
            __stmt.close();

        } catch (SQLException e) {
            __html.append(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            __html.append(e.getMessage());
            e.printStackTrace();
        } finally {
            if (__conn != null) {
                try {
                    __conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        response.getWriter().write(__strHTML);
    }

}
