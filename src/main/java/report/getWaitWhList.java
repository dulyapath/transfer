package report;

import history.*;
import receive.*;
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

@WebServlet(name = "getWaitWhList-sale", urlPatterns = {"/getWaitWhList"})
public class getWaitWhList extends HttpServlet {

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

        String __whcode = _sess.getAttribute("to_wh_code").toString();
        String __shcode = _sess.getAttribute("to_shelf_code").toString();
        String __bhcode = _sess.getAttribute("to_branch_code").toString();

        String search = "";

        String[] whList = __whcode.split(",");
        String _whList = "";
        System.out.println("whList.length " + whList.length);
        if (whList.length > 1) {
            for (int i = 0; i < whList.length; i++) {
                if (i == 0) {
                    _whList += "'" + whList[i] + "'";
                } else {
                    _whList += ",'" + whList[i] + "'";
                }

            }
        } else {
            _whList += "'" + __whcode + "'";
        }

        String[] shList = __shcode.split(",");
        String _shList = "";
        System.out.println("shList.length " + shList.length);
        if (shList.length > 1) {
            for (int i = 0; i < shList.length; i++) {
                if (i == 0) {
                    _shList += "'" + shList[i] + "'";
                } else {
                    _shList += ",'" + shList[i] + "'";
                }

            }
        } else {
            _shList += "'" + __shcode + "'";
        }

        String[] bhList = __bhcode.split(",");
        String _bhList = "";
        System.out.println("bhList.length " + bhList.length);
        if (bhList.length > 1) {
            for (int i = 0; i < bhList.length; i++) {
                if (i == 0) {
                    _bhList += "'" + bhList[i] + "'";
                } else {
                    _bhList += ",'" + bhList[i] + "'";
                }

            }
        } else {
            _bhList += "'" + __bhcode + "'";
        }

        if (!request.getParameter("search").equals("")) {
            search = " and doc_no like '%" + request.getParameter("search") + "%' or user_code  like '%" + request.getParameter("search") + "%'  ";
        }
        JSONArray jsarr = new JSONArray();

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String __queryExtend = "";
            String _code = "";
            String _name = "";

            String query1 = "select distinct branch_code,COALESCE((select name_1 from erp_branch_list where erp_branch_list.code = branch_code),'')as branch_name,COALESCE((select name_1 from ic_warehouse where code = wh_code),'')as wh_name,wh_code,COALESCE((select name_1 from ic_shelf where ic_shelf.code = shelf_code and ic_shelf.whcode = wh_code),'')as shelf_name,shelf_code from ic_transfer_doc_temp where to_branch_code in (" + _bhList + ") and to_wh_code in (" + _whList + ") and to_shelf_code in (" + _shList + ") and status in (1) ";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();

            ResultSetMetaData _rsHeadMd = __rsHead.getMetaData();
            int _colHeadCount = _rsHeadMd.getColumnCount();

            int row = __rsHead.getRow();

            while (__rsHead.next()) {
                JSONObject obj = new JSONObject();
                obj.put("branch_code", __rsHead.getString("branch_code"));
                obj.put("wh_code", __rsHead.getString("wh_code"));
                obj.put("shelf_code", __rsHead.getString("shelf_code"));
                obj.put("branch_name", __rsHead.getString("branch_name"));
                obj.put("wh_name", __rsHead.getString("wh_name"));
                obj.put("shelf_name", __rsHead.getString("shelf_name"));
                JSONArray jsarrz = new JSONArray();
                String query2 = "select item_code,item_name,unit_code,qty,\n"
                        + "COALESCE((select balance_qty from (   \n"
                        + "                               select   \n"
                        + "                               ic_inventory.code as ic_code,   \n"
                        + "                               ic_inventory.name_1 as ic_name ,   \n"
                        + "                               unit_standard as ic_unit_code,   \n"
                        + "                               (unit_standard_stand_value/unit_standard_divide_value) as unit_ratio,   \n"
                        + "                               coalesce(temp1.balance_qty, 0)  as balance_qty,   \n"
                        + "                               coalesce(temp1.balance_amount, 0) as balance_amount,   \n"
                        + "                               coalesce(temp1.qty_in, 0) as qty_in,   \n"
                        + "                               coalesce(temp1.amount_in, 0) as amount_in,   \n"
                        + "                               coalesce(temp1.qty_out, 0) as qty_out,   \n"
                        + "                               coalesce(temp1.amount_out, 0) as amount_out,   \n"
                        + "                               temp1.wh_code   \n"
                        + "                          \n"
                        + "                               from ic_inventory   \n"
                        + "                          \n"
                        + "                               left join (select   \n"
                        + "                               item_code as ic_code,   \n"
                        + "                               (select name_1 from ic_inventory where ic_inventory.code=item_code) as ic_name,   \n"
                        + "                               (select unit_standard from ic_inventory where ic_inventory.code=item_code) as ic_unit_code,   \n"
                        + "                               coalesce(sum(calc_flag*(case when (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) then qty*(stand_value / divide_value) else 0 end)),0) as balance_qty,   \n"
                        + "                               coalesce(sum(calc_flag*(case when (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and (qty>0 or sum_of_cost > 0)) or (trans_flag=14) or (trans_flag=48 and inquiry_type < 2)) or (trans_flag in (56,68,72,44) or (trans_flag=66 and (qty<0 or sum_of_cost<0)) or (trans_flag=46)  or (trans_flag=16) or (trans_flag=311 and inquiry_type=0)) then sum_of_cost else 0 end)),0) as balance_amount,   \n"
                        + "                               sum(case when doc_date_calc>='now()' and (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and qty>0) or (trans_flag=14 and inquiry_type=0) or (trans_flag=48 and inquiry_type < 2)) then calc_flag*(qty*(stand_value/divide_value)) else 0 end) as qty_in,   \n"
                        + "                               sum(case when doc_date_calc>='now()' and (trans_flag in (70,54,60,58,310,12) or (trans_flag=66 and (qty>0 or sum_of_cost > 0)) or (trans_flag=14) or (trans_flag=48 and inquiry_type < 2))  then calc_flag*sum_of_cost else 0 end) as amount_in,   \n"
                        + "                               -1*sum(case when doc_date_calc>='now()' and (trans_flag in (56,68,72,44) or (trans_flag=66 and qty<0) or (trans_flag=46 and inquiry_type in (0,2))  or (trans_flag=16 and inquiry_type in (0,2)) or (trans_flag=311 and inquiry_type=0)) then calc_flag*qty*(stand_value/divide_value) else 0 end) as qty_out,   \n"
                        + "                               -1*sum(case when doc_date_calc>='now()' and (trans_flag in (56,68,72,44) or (trans_flag=66 and (qty<0 or sum_of_cost<0)) or (trans_flag=46)  or (trans_flag=16) or (trans_flag=311 and inquiry_type=0)) then calc_flag*sum_of_cost else 0 end) as amount_out   \n"
                        + "                               ,wh_code from ic_trans_detail   \n"
                        + "                               where ic_trans_detail.wh_code = '" + __rsHead.getString("wh_code") + "' and ic_trans_detail.last_status=0 and ic_trans_detail.item_type<>5 and (select item_type from ic_inventory where ic_inventory.code = ic_trans_detail.item_code)<>1 and doc_date_calc<='now()' group by item_code,wh_code ) as temp1   \n"
                        + "                               on temp1.ic_code = ic_inventory.code where temp1.ic_code = ic_transfer_detail_temp.item_code  and ic_unit_code = ic_transfer_detail_temp.unit_code\n"
                        + "                               ) as temp2),'0') as balance  \n"
                        + "                               from ic_transfer_detail_temp where doc_no in (select doc_no from ic_transfer_doc_temp where branch_code = '" + __rsHead.getString("branch_code") + "' and wh_code = '" + __rsHead.getString("wh_code") + "' and shelf_code = '" + __rsHead.getString("shelf_code") + "' and status = 1)\n";
                System.out.println("query2 " + query2);
                PreparedStatement __stmt2 = __conn.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet __rsHead2 = __stmt2.executeQuery();
                while (__rsHead2.next()) {
                    JSONObject objz = new JSONObject();
                    objz.put("item_code", __rsHead2.getString("item_code"));
                    objz.put("item_name", __rsHead2.getString("item_name"));
                    objz.put("unit_code", __rsHead2.getString("unit_code"));
                    objz.put("balance", __rsHead2.getString("balance"));
                    objz.put("qty", __rsHead2.getString("qty"));
                    jsarrz.put(objz);
                }
                obj.put("detail", jsarrz);
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

}
