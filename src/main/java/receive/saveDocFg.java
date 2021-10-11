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

@WebServlet(name = "docfg-save", urlPatterns = {"/saveDocFg"})
public class saveDocFg extends HttpServlet {

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
        String __doc_date = request.getParameter("doc_date");
        String __user_code = request.getParameter("user_code");
        String __remark = request.getParameter("remark");
        String __from_bh = request.getParameter("from_bh");
        String __from_wh = request.getParameter("from_wh");
        String __from_sh = request.getParameter("from_sh");
        String __to_bh = request.getParameter("to_bh");
        String __to_wh = request.getParameter("to_wh");
        String __to_sh = request.getParameter("to_sh");

        String wid_doc = request.getParameter("wid_doc");
        String __wid_doc_no = request.getParameter("wid_docno");
        String __wid_doc_date = request.getParameter("wid_docdate");
        String __wid_remark = request.getParameter("wid_remark");
        String __wid_doc_time = request.getParameter("wid_doctime");

        String data = request.getParameter("data");

        HttpSession session = request.getSession(true);

        JSONArray jsonArray = new JSONArray(data);

        StringBuilder _insert_trans_sale_temp = new StringBuilder();
        StringBuilder _update_price = new StringBuilder();
        StringBuilder _insert_trans_sale_details_temp = new StringBuilder();
        _routine __routine = new _routine();
        StringBuilder __result = new StringBuilder();

        try {
            Connection __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String query1 = "select doc_no  from ic_transfer_doc_temp where doc_no = '" + __doc_no + "'";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();
            __rsHead.next();
            int row = __rsHead.getRow();
            //System.out.println("row " + row);
            if (row > 0) {
                PreparedStatement __stmt_delete = __conn.prepareStatement("update ic_transfer_doc_temp set status=4 , fg_doc = '" + __wid_doc_no + "' where doc_no = '" + __doc_no + "';");
                __stmt_delete.executeUpdate();
                __stmt_delete.close();
            }
            Double sum_amount = 0.0;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                _update_price.append("update ic_transfer_detail_temp set receive_qty = " + obj.get("receive_qty") + " where doc_no='" + __doc_no + "' and item_code='" + obj.get("item_code") + "' and unit_code='" + obj.get("unit_code") + "';");
                if (Double.parseDouble(obj.get("receive_qty").toString()) > 0) {
                    sum_amount += Double.parseDouble(obj.get("sum_of_cost").toString());
                    _insert_trans_sale_details_temp.append("insert into ic_trans_detail (trans_type,trans_flag,doc_date,doc_time,doc_time_calc,doc_no,item_code,item_name,unit_code,line_number,qty,branch_code,wh_code,shelf_code,price,sum_amount,sum_amount_exclude_vat,stand_value,divide_value,ratio,calc_flag) values "
                            + "(3,60,'" + __wid_doc_date + "','" + __wid_doc_time + "','" + __wid_doc_time + "','" + __wid_doc_no + "','" + obj.get("item_code") + "','" + obj.get("item_name") + "','" + obj.get("unit_code") + "','" + obj.get("line_number") + "','" + obj.get("receive_qty") + "','" + __to_bh + "','" + __to_wh + "','" + __to_sh + "','" + obj.get("average_cost") + "','" + obj.get("sum_of_cost") + "','" + obj.get("sum_of_cost") + "',"
                            + "(select stand_value from ic_unit_use where ic_code='" + obj.get("item_code") + "' and code='" + obj.get("unit_code") + "'),(select divide_value from ic_unit_use where ic_code='" + obj.get("item_code") + "' and code='" + obj.get("unit_code") + "'),(select ratio from ic_unit_use where ic_code='" + obj.get("item_code") + "' and code='" + obj.get("unit_code") + "'),1);");
                }
            }
            _insert_trans_sale_temp.append("insert into ic_trans (doc_ref,doc_ref_date,trans_type,trans_flag,doc_date,doc_no,doc_time,branch_code,wh_from,location_from,doc_format_code,creator_code,last_editor_code,remark,total_amount) "
                    + "values ('" + wid_doc + "',(select doc_date from ic_trans where trans_flag = 56 and doc_no = 'MWID20219121539435F64'),3,60,'" + __wid_doc_date + "','" + __wid_doc_no + "','" + __wid_doc_time + "','" + __to_bh + "','" + __to_wh + "','" + __to_sh + "','MFG','" + __user + "','" + __user + "','" + __wid_remark + "'," + sum_amount + ")");

            System.out.println(_update_price.toString());

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
            __conn.close();
            response.getWriter().print("success");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(e);
        }

    }

}
