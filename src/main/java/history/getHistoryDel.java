package history;

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

@WebServlet(name = "historydel-sale", urlPatterns = {"/getHistoryDel"})
public class getHistoryDel extends HttpServlet {

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

        String from_date = "";
        if (!request.getParameter("fd").equals("")) {
            from_date = " and create_datetime between '" + request.getParameter("fd") + "' and '" + request.getParameter("td") + "' ";
        }

        if (!request.getParameter("search").equals("")) {
            from_date = "";
            search = " and doc_no like '%" + request.getParameter("search").trim() + "%' "
                    + "or wid_doc like '%" + request.getParameter("search").trim() + "%' ";
        }
        JSONArray jsarr = new JSONArray();

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String __queryExtend = "";
            String _code = "";
            String _name = "";

            String query1 = "select *,to_char(create_datetime,'DD/MM/YYYY') as doc_datex,to_char(create_datetime,'HH24:MI') as doc_timex,(select name_1 from erp_user where code = approve_code)as approve_name from ic_transfer_log where 1=1 " + search + " limit 100";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();

            ResultSetMetaData _rsHeadMd = __rsHead.getMetaData();
            int _colHeadCount = _rsHeadMd.getColumnCount();

            int row = __rsHead.getRow();

            while (__rsHead.next()) {

                JSONObject obj = new JSONObject();

                obj.put("doc_no", __rsHead.getString("doc_no"));
                obj.put("wid_doc", __rsHead.getString("wid_doc"));
                obj.put("doc_date", __rsHead.getString("doc_datex"));
                obj.put("doc_time", __rsHead.getString("doc_timex"));
                obj.put("screen", __rsHead.getString("screen"));
                obj.put("approve_name", __rsHead.getString("approve_name"));
                obj.put("approve_code", __rsHead.getString("approve_code"));
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
