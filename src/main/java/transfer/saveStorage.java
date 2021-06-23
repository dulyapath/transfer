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

@WebServlet(name = "storage-save", urlPatterns = {"/userStorage"})
public class saveStorage extends HttpServlet {

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
        keyword = request.getParameter("empcode");
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

            String query1 = "select *  from erp_user_storage where emp_code = '" + keyword + "'";
            //System.out.println("query1 "+query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();

            ResultSetMetaData _rsHeadMd = __rsHead.getMetaData();
            int _colHeadCount = _rsHeadMd.getColumnCount();

            int row = __rsHead.getRow();

            while (__rsHead.next()) {

                JSONObject obj = new JSONObject();
                obj.put("emp_code", __rsHead.getString("emp_code"));
                obj.put("wh_code", __rsHead.getString("wh_code"));
                obj.put("shelf_code", __rsHead.getString("shelf_code"));
                obj.put("branch_code", __rsHead.getString("branch_code"));
                obj.put("to_branch_code", __rsHead.getString("to_branch_code"));
                obj.put("to_wh_code", __rsHead.getString("to_wh_code"));
                obj.put("to_shelf_code", __rsHead.getString("to_shelf_code"));
                obj.put("rev_branch_code", __rsHead.getString("rev_branch_code"));
                obj.put("rev_wh_code", __rsHead.getString("rev_wh_code"));
                obj.put("rev_shelf_code", __rsHead.getString("rev_shelf_code"));
                obj.put("is_direct", __rsHead.getString("is_direct"));
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

        String __emp_code = request.getParameter("emp_code");
        String __wh_code = request.getParameter("wh_code");
        String __sh_code = request.getParameter("sh_code");
        String __branch_code = request.getParameter("branch_code");
        String __to_wh_code = request.getParameter("to_whcode");
        String __to_sh_code = request.getParameter("to_shcode");
        String __to_branch_code = request.getParameter("to_branchcode");
        String __rev_wh_code = request.getParameter("rev_whcode");
        String __rev_sh_code = request.getParameter("rev_shcode");
        String __rev_branch_code = request.getParameter("rev_branchcode");
        String __is_direct = request.getParameter("is_direct");
        System.out.println("__wh_code " + __wh_code);

        HttpSession session = request.getSession(true);

        StringBuilder _insert_trans = new StringBuilder();

        _routine __routine = new _routine();
        StringBuilder __result = new StringBuilder();

        try {
            Connection __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String query1 = "select emp_code  from erp_user_storage where emp_code = '" + __emp_code + "'";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();
            __rsHead.next();
            int row = __rsHead.getRow();
            //System.out.println("row " + row);
            if (row > 0) {
                PreparedStatement __stmt_update = __conn.prepareStatement("update erp_user_storage set is_direct = '" + __is_direct + "',branch_code='" + __branch_code + "',wh_code='" + __wh_code + "',shelf_code='" + __sh_code + "' ,to_branch_code='" + __to_branch_code + "',to_wh_code = '" + __to_wh_code + "' ,to_shelf_code = '" + __to_sh_code + "',rev_branch_code='" + __rev_branch_code + "',rev_wh_code = '" + __rev_wh_code + "' ,rev_shelf_code = '" + __rev_sh_code + "' where emp_code = '" + __emp_code + "';");
                __stmt_update.executeUpdate();
                __stmt_update.close();
            } else {
                _insert_trans.append("insert into erp_user_storage (emp_code,branch_code,wh_code,shelf_code,to_branch_code,to_wh_code,to_shelf_code,rev_branch_code,rev_wh_code,rev_shelf_code,is_direct) "
                        + "values ('" + __emp_code + "','" + __branch_code + "','" + __wh_code + "','" + __sh_code + "','" + __to_branch_code + "','" + __to_wh_code + "','" + __to_sh_code + "','" + __rev_branch_code + "','" + __rev_wh_code + "','" + __rev_sh_code + "','" + __is_direct + "');");

            }

            PreparedStatement __stmt_trans = __conn.prepareStatement(_insert_trans.toString());

            __stmt_trans.executeUpdate();
            __stmt_trans.close();

            __conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(e);
        }

        response.getWriter().print("success");
    }

}
