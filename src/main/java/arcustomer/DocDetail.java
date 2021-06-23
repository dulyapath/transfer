package arcustomer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils._routine;

@WebServlet(name = "Arcustomer-detail", urlPatterns = {"/arcustomer-detail"})
public class DocDetail extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        StringBuilder __html = new StringBuilder();
        StringBuilder html2 = new StringBuilder();
        HttpSession _sess = request.getSession();
        Integer count = 0;
        DecimalFormat decim = new DecimalFormat("#,###.##");
        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {

            return;
        }
        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();

        String __providerCode = _sess.getAttribute("provider").toString();
        String fileConfig = "SMLConfig" + __providerCode.toUpperCase() + ".xml";

        String cust = "";
        if (request.getParameter("cust") == null || request.getParameter("cust").toString().isEmpty()) {
            response.getWriter().write("No code");
            return;
        }

        cust = request.getParameter("cust");
        String mode = request.getParameter("mode");

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, fileConfig);

            String query = "select code,name_1 from ar_customer where code='" + cust + "' order by name_1 limit 10";
            PreparedStatement __stmt = __conn.prepareStatement(query);
            ResultSet __rs1 = __stmt.executeQuery();
            ResultSetMetaData _rsBodyMd = __rs1.getMetaData();
            int _colBodyCount = _rsBodyMd.getColumnCount();

            while (__rs1.next()) {
                __html.append("<font size=+1><b>" + __rs1.getString("code") + " : " + __rs1.getString("name_1") + "</b></font><br/>");

                Integer count1 = 0;
                Integer total_total_amount = 0;
                Integer total_balance_amount = 0;

                String query2 = "select * from (select branch_code,(select name_1 from erp_branch_list where code=branch_code) as branch_name,doc_date,doc_time,doc_no,total_amount,total_amount-(coalesce((select sum(total_amount) from ic_trans where ic_trans.doc_ref=q1.doc_no and last_status=0),0)+coalesce((select sum(amount) from cb_trans_detail where cb_trans_detail.doc_type=5 and cb_trans_detail.trans_number=q1.doc_no and last_status=0),0)) as balance from ic_trans as q1 where cust_code='" + __rs1.getString("code") + "' and trans_flag=40 and last_status=0) as q1 where balance<>0 order by doc_date,doc_time";
                PreparedStatement __stmt1 = __conn.prepareStatement(query2);
                ResultSet __rs2 = __stmt1.executeQuery();
                while (__rs2.next()) {
                    if (count1 == 0) {
                        __html.append("<table width='100%' border=1>");
                        __html.append("<tr bgcolor='#EFF2FB'>");
                        __html.append("<td colspan=6 align='right'><font size=+1><b>รับเงินล่วงหน้า</b></font></td>");
                        __html.append("</tr>");
                        __html.append("<tr bgcolor='#BCF5A9'>");
                        __html.append("<td align='center'><b>วันที่เอกสาร</b></td>");
                        __html.append("<td align='center'><b>เวลา</b></td>");
                        __html.append("<td align='center'><b>สาขา</b></td>");
                        __html.append("<td align='center'><b>เลขที่เอกสาร</b></td>");
                        __html.append("<td align='center'><b>ยอดรับเงินล่วงหน้า</b></td>");
                        __html.append("<td align='center'><b>เงินรับล่วงหน้าคงเหลือ</b></td>");
                        __html.append("</tr>");
                    }
                    __html.append("<tr bgcolor='#ACF5A9'>");
                    __html.append("<td align='center'>" + _routine.dateThai(__rs2.getString("doc_date")) + "</td>");
                    __html.append("<td align='center'>" + __rs2.getString("doc_time") + "</td>");
                    __html.append("<td>" + __rs2.getString("branch_code") + "&nbsp;(" + __rs2.getString("branch_name") + ")</td>");
                    __html.append("<td>" + __rs2.getString("doc_no") + "</td>");
                    __html.append("<td align='right'>" + decim.format(__rs2.getInt("total_amount")) + "</td>");
                    __html.append("<td align='right'>" + decim.format(__rs2.getInt("balance")) + "</td>");
                    __html.append("</tr>");
                    count1++;
                    total_total_amount += __rs2.getInt("total_amount");
                    total_balance_amount += __rs2.getInt("balance");
                }

                if (count1 > 0) {
                    __html.append("<tr bgcolor='#cce6ff'>");
                    __html.append("<td align='right' colspan=4><b>รวม</b></td>");
                    __html.append("<td align='right'><b>" + decim.format(total_total_amount) + "</b></td>");
                    __html.append("<td align='right'><b>" + decim.format(total_balance_amount) + "</b></td>");
                    __html.append("</tr>");
                    __html.append("</table>");
                }

                String query3 = "select branch_code,credit_date,(select name_1 from erp_branch_list where code=branch_code) as branch_name,trans_flag(doc_type) as trans_flag,cust_code as ar_code , (select name_1 from ar_customer where ar_customer.code = xx.cust_code) as ar_name, doc_no, doc_date, ref_doc_no, ref_doc_date, amount as total_amount, balance_amount from (select cust_code, doc_date , credit_date as due_date , doc_no , trans_flag as doc_type , used_status , doc_ref as ref_doc_no , doc_ref_date as ref_doc_date ,credit_date, coalesce(total_amount,0) as amount , coalesce(total_amount,0)-(select coalesce(sum(coalesce(sum_pay_money,0)),0) from ap_ar_trans_detail where coalesce(last_status, 0)=0 and trans_flag in (239) and ic_trans.doc_no=ap_ar_trans_detail.billing_no and ic_trans.doc_date=ap_ar_trans_detail.billing_date) as balance_amount,branch_code  from ic_trans  where  coalesce(last_status, 0)=0  and trans_flag=44 and (inquiry_type=0  or inquiry_type=2)  and cust_code='" + __rs1.getString("code") + "' "
                        + "union all select cust_code , doc_date , credit_date as due_date , doc_no , trans_flag as doc_type , used_status , '' as ref_doc_no , null as ref_doc_date ,null as credit_date, coalesce(total_amount,0) as amount , coalesce(total_amount,0)-(select coalesce(sum(coalesce(sum_pay_money,0)),0) from ap_ar_trans_detail where coalesce(last_status, 0)=0 and trans_flag in (239) and ic_trans.doc_no=ap_ar_trans_detail.billing_no and ic_trans.doc_date=ap_ar_trans_detail.billing_date ) as balance_amount,branch_code  from ic_trans  where  coalesce(last_status, 0)=0   and (trans_flag=46 or trans_flag=93 or trans_flag=99 or trans_flag=95 or trans_flag=101)  and cust_code='" + __rs1.getString("code") + "' "
                        + "union all select cust_code , doc_date , credit_date as due_date , doc_no , trans_flag as doc_type , used_status , '' as ref_doc_no , null as ref_doc_date ,null as credit_date, -1*coalesce(total_amount,0) as amount , -1*(coalesce(total_amount,0)+(select coalesce(sum(coalesce(sum_pay_money,0)),0) from ap_ar_trans_detail where coalesce(last_status, 0)=0 and trans_flag in (239) and ic_trans.doc_no=ap_ar_trans_detail.billing_no and ic_trans.doc_date=ap_ar_trans_detail.billing_date)) as balance_amount,branch_code from ic_trans  where  coalesce(last_status, 0)=0 and ((trans_flag=48 and inquiry_type in (0,2,4) ) or trans_flag=97 or trans_flag=103)  and cust_code='" + __rs1.getString("code") + "' ) as xx where balance_amount  <> 0 order by cust_code, doc_date, doc_no";

//                System.out.println(query3);
                String bgcolor = "";
                String bgcolor1 = "#F0FFFF";
                String bgcolor2 = "#F5F5DC";
                count1 = 0;
                total_total_amount = 0;
                total_balance_amount = 0;
                PreparedStatement __stmt2 = __conn.prepareStatement(query3);
                ResultSet __rs3 = __stmt2.executeQuery();

                while (__rs3.next()) {

                    if (count1 == 0) {
                        __html.append("<table width='100%' border=1> ");
                        __html.append("<tr bgcolor='#EFF2FB'> ");
                        __html.append("<td colspan=7 align='right'><font size=+1><b>รายละเอียดบิลค้างชำระ/ส่วนลด</b></font></td> ");
                        __html.append("</tr> ");
                        __html.append("<tr bgcolor='#BCF5A9'> ");
                        __html.append("<td align='center'><b>วันที่เอกสาร</b></td> ");
                        __html.append("<td align='center'><b>สาขา</b></td> ");
                        __html.append("<td align='center'><b>เลขที่เอกสาร</b></td> ");
                        /* 07/06/2561 - เพิ่ม วันครบกำหนด ของ อินทรี */
                        __html.append("<td align='center'><b>วันครบกำหนด</b></td> ");
                        __html.append("<td align='center'><b>ประเภท</b></td> ");
                        __html.append("<td align='center'><b>ยอดตั้งหนี้</b></td> ");
                        __html.append("<td align='center'><b>ยอดเงินคงเหลือ</b></td> ");
                        __html.append("</tr> ");
                    }
                    if (bgcolor.equals(bgcolor1) == false) {
                        bgcolor = bgcolor1;
                    } else {
                        bgcolor = bgcolor2;
                    }
                    String bgcoloruse = bgcolor;
                    if (__rs3.getInt("balance_amount") < 0) {
                        bgcoloruse = "#ffcccc";
                    }
                    __html.append("<tr bgcolor='" + bgcoloruse + "'> ");
                    __html.append("<td align='center'>" + __routine.dateThai(__rs3.getString("doc_date")) + "</td> ");
                    __html.append("<td>" + __rs3.getString("branch_code") + "&nbsp;(" + __rs3.getString("branch_name") + ")</td> ");
                    __html.append("<td>" + __rs3.getString("doc_no") + "</td> ");
                    /* 07/06/2561 - เพิ่ม วันครบกำหนด ของ อินทรี */
                    __html.append("<td align='center'>" + (__rs3.getString("credit_date") != null ? __routine.dateThai(__rs3.getString("credit_date")) : "null") + "</td>");
                    __html.append("<td>" + __rs3.getString("trans_flag") + "</td> ");
                    __html.append("<td align='right'>" + decim.format(__rs3.getInt("total_amount")) + "</td> ");
                    __html.append("<td align='right'>" + decim.format(__rs3.getInt("balance_amount")) + "</td> ");
                    __html.append("</tr> ");
                    total_total_amount += __rs3.getInt("total_amount");
                    total_balance_amount += __rs3.getInt("balance_amount");
                    count1++;

                }

                if (count1 > 0) {
                    __html.append("<tr bgcolor='#cce6ff'> ");
                    __html.append("<td align='right' colspan=5><b>รวม</b></td> ");
                    __html.append("<td align='right'><b>" + decim.format(total_total_amount) + "</b></td> ");
                    __html.append("<td align='right'><b>" + decim.format(total_balance_amount) + "</b></td> ");
                    __html.append("</tr> ");
                    __html.append("</table> ");
                }
                __html.append("</table> ");

                String query4 = "select * from cb_chq_list where ap_ar_code='" + __rs1.getString("code") + "' and ap_ar_type=1 and chq_type=1 and status not in (2, 7) order by chq_get_date,chq_number";
                // echo $query3 . "<br/>";
                bgcolor1 = "#F0FFFF";
                bgcolor2 = "#F5F5DC";
                Integer total_chq_amount = 0;
                Integer count2 = 0;
                PreparedStatement __stmt3 = __conn.prepareStatement(query4);
                ResultSet __rs4 = __stmt3.executeQuery();

                while (__rs4.next()) {

                    if (count2 == 0) {
                        __html.append("<table width='100%' border=1>");
                        __html.append("<tr bgcolor='#EFF2FB'>");
                        __html.append("<td colspan=5 align='right'><font size=+1><b>รายละเอียดเช็คที่ยังไม่ผ่าน</b></font></td>");
                        __html.append("<tr bgcolor='#BCF5A9'>");
                        __html.append("<td align='center'><b>วันที่จ่ายเช็ค</b></td>");
                        __html.append("<td align='center'><b>วันครบกำหนด</b></td>");
                        __html.append("<td align='center'><b>เลขที่เช็ค</b></td>");
                        __html.append("<td align='center'><b>เลขที่เอกสารอ้างอิง</b></td>");
                        __html.append("<td align='center'><b>จำนวนเงิน</b></td>");
                        __html.append("</tr>");
                    }
                    if (bgcolor.equals(bgcolor1) == false) {
                        bgcolor = bgcolor1;
                    } else {
                        bgcolor = bgcolor2;
                    }
                    __html.append("<tr bgcolor='" + bgcolor + "'>");
                    __html.append("<td align='center'>" + __routine.dateThai(__rs4.getString("chq_get_date")) + "</td>");
                    __html.append("<td align='center'>" + __routine.dateThai(__rs4.getString("chq_due_date")) + "</td>");
                    __html.append("<td>" + __rs4.getString("chq_number") + "</td>");
                    __html.append("<td>" + __rs4.getString("doc_ref") + "</td>");
                    __html.append("<td align='right'>" + decim.format(__rs4.getInt("amount")) + "</td>");
                    __html.append("</tr>");
                    total_chq_amount += __rs4.getInt("amount");
                    count2++;

                }
                if (count2 > 0) {
                    __html.append("<tr bgcolor='#cce6ff'>");
                    __html.append("<td align='right' colspan=4><b>รวม</b></td>");
                    __html.append("<td align='right'><b>" + decim.format(total_chq_amount) + "</b></td>");
                    __html.append("</tr>");
                    __html.append("</table>");

                }

                // ใบสั่งจองค้างออกบิล
                String query5 = "select *,total_amount-total_pay_amount as balance_amount from (select branch_code,(select name_1 from erp_branch_list where code=branch_code) as branch_name,doc_date,doc_no,total_amount,(select coalesce((select sum(sum_amount) from ic_trans_detail where ic_trans_detail.ref_doc_no=ic_trans.doc_no and ic_trans_detail.trans_flag=44 and ic_trans_detail.last_status=0),0)) as total_pay_amount from ic_trans where cust_code='" + __rs1.getString("code") + "' and trans_flag=34 and last_status=0 and inquiry_type in(0,2)) as q1 where total_amount-total_pay_amount<>0 order by doc_date,doc_no";
                bgcolor1 = "#F0FFFF";
                bgcolor2 = "#F5F5DC";
                Integer total4_amount = 0;
                Integer total4_pay_amount = 0;
                Integer total4_balance_amount = 0;
                Integer count4 = 0;

                PreparedStatement __stmt4 = __conn.prepareStatement(query5);
                ResultSet __rs5 = __stmt4.executeQuery();

                while (__rs5.next()) {
                    if (count4 == 0) {
                        __html.append("<table width='100%' border=1>");
                        __html.append("<tr bgcolor='#EFF2FB'>");
                        __html.append("<td colspan=6 align='right'><font size=+1><b>ใบสั่งจองค้างออกบิล</b></font></td>");
                        __html.append("<tr bgcolor='#BCF5A9'>");
                        __html.append("<td align='center'><b>วันที่ใบสั่งจอง</b></td>");
                        __html.append("<td align='center'><b>สาขา</b></td>");
                        __html.append("<td align='center'><b>เลขที่ใบสั่งจอง</b></td>");
                        __html.append("<td align='center'><b>จำนวนเงิน</b></td>");
                        __html.append("<td align='center'><b>ยอดหัก</b></td>");
                        __html.append("<td align='center'><b>ยอดคงเหลือ</b></td>");
                        __html.append("</tr>");
                    }
                    if (bgcolor.equals(bgcolor1) == false) {
                        bgcolor = bgcolor1;
                    } else {
                        bgcolor = bgcolor2;
                    }
                    __html.append("<tr bgcolor='" + bgcolor + "'>");
                    __html.append("<td align='center'>" + __routine.dateThai(__rs5.getString("doc_date")) + "</td>");
                    __html.append("<td>" + __rs5.getString("branch_code") + "&nbsp;(" + __rs5.getString("branch_name") + ")</td>");
                    __html.append("<td>" + __rs5.getString("doc_no") + "</td>");
                    __html.append("<td align='right'>" + decim.format(__rs5.getInt("total_amount")) + "</td>");
                    __html.append("<td align='right'>" + decim.format(__rs5.getInt("total_pay_amount")) + "</td>");
                    __html.append("<td align='right'>" + decim.format(__rs5.getInt("balance_amount")) + "</td>");
                    __html.append("</tr>");
                    total4_amount += __rs5.getInt("total_amount");
                    total4_pay_amount += __rs5.getInt("total_pay_amount");
                    total4_balance_amount += __rs5.getInt("balance_amount");
                    count4++;
                }
                if (count4 > 0) {
                    __html.append("<tr bgcolor='#cce6ff'>");
                    __html.append("<td align='right' colspan=3><b>รวม</b></td>");
                    __html.append("<td align='right'><b>" + decim.format(total4_amount) + "</b></td>");
                    __html.append("<td align='right'><b>" + decim.format(total4_pay_amount) + "</b></td>");
                    __html.append("<td align='right'><b>" + decim.format(total4_balance_amount) + "</b></td>");
                    __html.append("</tr>");
                    __html.append("</table>");
                }

                // ใบสั่งขายค้างออกบิล
                String query6 = "select *,total_amount-total_pay_amount as balance_amount from (select branch_code,(select name_1 from erp_branch_list where code=branch_code) as branch_name,doc_date,doc_no,total_amount,(select coalesce((select sum(sum_amount) from ic_trans_detail where ic_trans_detail.ref_doc_no=ic_trans.doc_no and ic_trans_detail.trans_flag=44 and ic_trans_detail.last_status=0),0)) as total_pay_amount from ic_trans where cust_code='" + __rs1.getString("code") + "' and trans_flag=36 and last_status=0 and doc_success = 0) as q1 where total_amount-total_pay_amount<>0 order by doc_date,doc_no";
                // echo $query5 . "<br/>";

                bgcolor1 = "#F0FFFF";
                bgcolor2 = "#F5F5DC";
                Integer total5_amount = 0;
                Integer total5_pay_amount = 0;
                Integer total5_balance_amount = 0;
                Integer count5 = 0;

                PreparedStatement __stmt5 = __conn.prepareStatement(query6);
                ResultSet __rs6 = __stmt5.executeQuery();

                while (__rs6.next()) {

                    if (count5 == 0) {
                        __html.append("<table width='100%' border=1>");
                        __html.append("<tr bgcolor='#EFF2FB'>");
                        __html.append("<td colspan=6 align='right'><font size=+1><b>ใบสั่งขายค้างออกบิล</b></font></td>");
                        __html.append("<tr bgcolor='#BCF5A9'>");
                        __html.append("<td align='center'><b>วันที่ใบสั่งขาย</b></td>");
                        __html.append("<td align='center'><b>สาขา</b></td>");
                        __html.append("<td align='center'><b>เลขที่ใบสั่งขาย</b></td>");
                        __html.append("<td align='center'><b>จำนวนเงิน</b></td>");
                        __html.append("<td align='center'><b>ยอดหัก</b></td>");
                        __html.append("<td align='center'><b>ยอดคงเหลือ</b></td>");
                        __html.append("</tr>");
                    }
                    if (bgcolor.equals(bgcolor1) == false) {
                        bgcolor = bgcolor1;
                    } else {
                        bgcolor = bgcolor2;
                    }
                    __html.append("<tr bgcolor='" + bgcolor + "'>");
                    __html.append("<td align='center'>" + __routine.dateThai(__rs6.getString("doc_date")) + "</td>");
                    __html.append("<td>" + __rs6.getString("branch_code") + "&nbsp;(" + __rs6.getString("branch_name") + ")</td>");
                    __html.append("<td>" + __rs6.getString("doc_no") + "</td>");
                    __html.append("<td align='right'>" + decim.format(__rs6.getInt("total_amount")) + "</td>");
                    __html.append("<td align='right'>" + decim.format(__rs6.getInt("total_pay_amount")) + "</td>");
                    __html.append("<td align='right'>" + decim.format(__rs6.getInt("balance_amount")) + "</td>");
                    __html.append("</tr>");
                    total5_amount += __rs6.getInt("total_amount");
                    total5_pay_amount += __rs6.getInt("total_pay_amount");
                    total5_balance_amount += __rs6.getInt("balance_amount");
                    count5++;

                }
                if (count5 > 0) {
                    __html.append("<tr bgcolor='#cce6ff'>");
                    __html.append("<td align='right' colspan=3><b>รวม</b></td>");
                    __html.append("<td align='right'><b>" + decim.format(total5_amount) + "</b></td>");
                    __html.append("<td align='right'><b>" + decim.format(total5_pay_amount) + "</b></td>");
                    __html.append("<td align='right'><b>" + decim.format(total5_balance_amount) + "</b></td>");
                    __html.append("</tr>");
                    __html.append("</table>");
                }

            }

            __stmt.close();
            __rs1.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("error 1 :" + e.getMessage());
            return;

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("error 2 :" + e.getMessage());
            return;
        } finally {
            if (__conn != null) {
                try {
                    __conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        response.getWriter().write(__html.toString());
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
