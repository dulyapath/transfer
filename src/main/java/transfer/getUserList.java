package transfer;

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

@WebServlet(name = "user-list", urlPatterns = {"/getUserList"})
public class getUserList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder __html = new StringBuilder();

        HttpSession _sess = request.getSession();
        String keyword = "", barcode = "";

        if (!request.getParameter("search").equals("")) {
            keyword = " where code like '%" + request.getParameter("search") + "%' or name_1  like '%" + request.getParameter("search") + "%' ";
        }

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

            String query1 = "select code,name_1,coalesce((select branch_code from erp_user_storage where emp_code = code ),'') as branch_code,coalesce((select wh_code from erp_user_storage where emp_code = code ),'') as wh_code,coalesce((select shelf_code from erp_user_storage where emp_code = code ),'') as shelf_code "
                    + ", coalesce((select to_branch_code from erp_user_storage where emp_code = code ),'') as to_branch_code,coalesce((select to_wh_code from erp_user_storage where emp_code = code ),'') as to_wh_code,coalesce((select to_shelf_code from erp_user_storage where emp_code = code ),'') as to_shelf_code "
                    + ", coalesce((select rev_branch_code from erp_user_storage where emp_code = code ),'') as rev_branch_code,coalesce((select rev_wh_code from erp_user_storage where emp_code = code ),'') as rev_wh_code,coalesce((select rev_shelf_code from erp_user_storage where emp_code = code ),'') as rev_shelf_code "
                    + "from erp_user " + keyword + " ";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();

            ResultSetMetaData _rsHeadMd = __rsHead.getMetaData();
            int _colHeadCount = _rsHeadMd.getColumnCount();

            int row = __rsHead.getRow();

            while (__rsHead.next()) {

                JSONObject obj = new JSONObject();
                obj.put("code", __rsHead.getString("code"));
                obj.put("name_1", __rsHead.getString("name_1"));
                obj.put("branch_code", __rsHead.getString("branch_code"));
                obj.put("wh_code", __rsHead.getString("wh_code"));
                obj.put("shelf_code", __rsHead.getString("shelf_code"));
                obj.put("to_branch_code", __rsHead.getString("to_branch_code"));
                obj.put("to_wh_code", __rsHead.getString("to_wh_code"));
                obj.put("to_shelf_code", __rsHead.getString("to_shelf_code"));
                obj.put("rev_branch_code", __rsHead.getString("rev_branch_code"));
                obj.put("rev_wh_code", __rsHead.getString("rev_wh_code"));
                obj.put("rev_shelf_code", __rsHead.getString("rev_shelf_code"));
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
