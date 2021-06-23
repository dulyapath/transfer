package receive;

import send.*;
import transfer.*;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.json.JSONArray;
import org.json.JSONObject;
import utils._global;
import utils._routine;

@WebServlet(name = "approverefund-save", urlPatterns = {"/approveRefund"})
public class approveRefund extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder __html = new StringBuilder();

        HttpSession _sess = request.getSession();
        String keyword = "", barcode = "";

        DecimalFormat decim = new DecimalFormat("#,###.##");

        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {

            return;
        }

        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();
        String __provider = _sess.getAttribute("provider").toString().toLowerCase();

        JSONArray jsarr = new JSONArray();

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String __queryExtend = "";
            String _code = "";
            String _name = "";

            String query1 = "select code,name_1  from ar_customer";
            //System.out.println("query1 "+query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();

            ResultSetMetaData _rsHeadMd = __rsHead.getMetaData();
            int _colHeadCount = _rsHeadMd.getColumnCount();

            int row = __rsHead.getRow();

            while (__rsHead.next()) {

                JSONObject obj = new JSONObject();
                obj.put("code", __rsHead.getString("code"));
                obj.put("name_1", __rsHead.getString("name_1"));
                jsarr.put(obj);
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

        response.getWriter().print(jsarr);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        HttpSession _sess = request.getSession();
        if (_sess.getAttribute("user") == null || _sess.getAttribute("user").toString().isEmpty()) {

            return;
        }

        String __user = _sess.getAttribute("user").toString().toUpperCase();
        String __dbname = _sess.getAttribute("dbname").toString().toLowerCase();
        String __provider = _sess.getAttribute("provider").toString().toLowerCase();

        String __doc_no = request.getParameter("doc_no");

        HttpSession session = request.getSession(true);

        StringBuilder _insert_trans_sale_temp = new StringBuilder();
        StringBuilder _update_price = new StringBuilder();
        StringBuilder _insert_trans_sale_details_temp = new StringBuilder();
        _routine __routine = new _routine();
        StringBuilder __result = new StringBuilder();

        try {
            Connection __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String query1 = "select doc_no  from ic_transfer_trans_temp where doc_no = '" + __doc_no + "'";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();
            __rsHead.next();
            int row = __rsHead.getRow();
            //System.out.println("row " + row);
            if (row > 0) {

                Double sum_amount = 0.0;
                _insert_trans_sale_details_temp.append("insert into ic_trans_detail (ref_doc_no,trans_type,trans_flag,doc_date,doc_time,doc_no,item_code,item_name,unit_code,line_number,qty,branch_code,wh_code,shelf_code,price,sum_amount,stand_value,divide_value,ratio,calc_flag)  "
                        + " select ref_doc_no,trans_type,trans_flag,doc_date,doc_time,doc_no,item_code,item_name,unit_code,line_number,qty,branch_code,wh_code,shelf_code,price,sum_amount,stand_value,divide_value,ratio,calc_flag from ic_transfer_trans_detail_temp where doc_no = '" + __doc_no + "'");

                _update_price.append("insert into ic_trans (trans_type,trans_flag,doc_date,doc_no,doc_time,branch_code,wh_from,location_from,doc_format_code,creator_code,last_editor_code,remark,total_amount) "
                        + " select trans_type,trans_flag,doc_date,doc_no,doc_time,branch_code,wh_from,location_from,doc_format_code,creator_code,last_editor_code,remark,total_amount from ic_transfer_trans_temp  where doc_no = '" + __doc_no + "'");
                _insert_trans_sale_temp.append("insert into ap_ar_trans_detail (trans_flag,trans_type,doc_date,doc_no,billing_no,billing_date) "
                        + " select trans_flag,trans_type,doc_date,doc_no,billing_no,billing_date from ap_ar_trans_detail_temp where doc_no = '" + __doc_no + "'");

                System.out.println(_insert_trans_sale_temp.toString());

                System.out.println(_insert_trans_sale_details_temp.toString());

                PreparedStatement __stmt_trans = __conn.prepareStatement(_insert_trans_sale_temp.toString());
                PreparedStatement __stmt_update = __conn.prepareStatement(_update_price.toString());
                PreparedStatement __stmt_detail = __conn.prepareStatement(_insert_trans_sale_details_temp.toString());
                __stmt_trans.executeUpdate();
                __stmt_trans.close();
                __stmt_update.executeUpdate();
                __stmt_update.close();
                __stmt_detail.executeUpdate();
                __stmt_detail.close();

                PreparedStatement __stmt_delete = __conn.prepareStatement("delete from ic_transfer_trans_temp  where doc_no = '" + __doc_no + "';"
                        + "delete from ic_transfer_trans_detail_temp  where doc_no = '" + __doc_no + "';"
                        + "delete from ap_ar_trans_detail_temp  where doc_no = '" + __doc_no + "';");
                __stmt_delete.executeUpdate();
                __stmt_delete.close();

            }
            __conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(e);
        }

        response.getWriter().print("success");
    }

}
