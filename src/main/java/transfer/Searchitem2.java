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
import utils._global;
import utils._routine;

@WebServlet(name = "Search-item2", urlPatterns = {"/search_item2"})
public class Searchitem2 extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
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

        int limit = 30;

        Connection __conn = null;
        try {
            _routine __routine = new _routine();
            __conn = __routine._connect(__dbname, _global.FILE_CONFIG(__provider));

            String __queryExtend = "";
            String _code = "";
            String _name = "";
            String _barcode = "";
            if (!request.getParameter("name").equals("")) {
                _name = request.getParameter("name");
            }
            if (!request.getParameter("barcode").equals("")) {
                _barcode = " and upper(temp.barcodes) LIKE upper('%" + request.getParameter("barcode") + "%')";
            }
            String[] __keywordx = _name.split(" ");

            StringBuilder __wherelike1 = new StringBuilder();
            StringBuilder __wherelike2 = new StringBuilder();
            StringBuilder __wherelike3 = new StringBuilder();
            StringBuilder __wherelike = new StringBuilder();

            if (__keywordx.length > 1) {
                __wherelike.append(" ");
                for (int i = 0; i < __keywordx.length; i++) {
                    if (i == 0) {
                        __wherelike1.append(" (upper(temp.code) LIKE upper('%" + __keywordx[i] + "%') ");
                        __wherelike2.append(" (upper(temp.name_1) LIKE upper('%" + __keywordx[i] + "%') ");
                        __wherelike3.append(" (upper(temp.barcodes) LIKE upper('%" + __keywordx[i] + "%') ");
                    } else {
                        __wherelike1.append(" and upper(temp.code) LIKE upper('%" + __keywordx[i] + "%') ");
                        __wherelike2.append(" and upper(temp.name_1) LIKE upper('%" + __keywordx[i] + "%') ");
                        __wherelike3.append(" and upper(temp.barcodes) LIKE upper('%" + __keywordx[i] + "%') ");

                    }

                }
                __wherelike1.append(" ) or ");
                __wherelike2.append(" ) or ");
                __wherelike3.append(" ) ");

                __wherelike.append(" and " + __wherelike1 + __wherelike2 + __wherelike3);
            } else {
                if (_barcode.equals("")) {

                    __wherelike.append(" and  upper(temp.code) LIKE upper('%" + _name + "%') or upper(temp.name_1) LIKE upper('%" + _name + "%') or upper(temp.barcodes) LIKE upper('%" + _name + "%')");
                }
            }

            String query1 = "select code,name_1,barcodes from (select code,name_1,array_to_string(array(select barcode from ic_inventory_barcode where ic_inventory_barcode.ic_code=ic_inventory.code),',')as barcodes from ic_inventory ) as temp where 1=1 " + __wherelike + _barcode +  " limit 100";
            System.out.println("query1 " + query1);
            PreparedStatement __stmt = __conn.prepareStatement(query1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsHead = __stmt.executeQuery();

            ResultSetMetaData _rsHeadMd = __rsHead.getMetaData();
            int _colHeadCount = _rsHeadMd.getColumnCount();

            int row = __rsHead.getRow();

            while (__rsHead.next()) {

                __html.append(" <li class = 'list-group-item list-group-item-action select-items' data-code='" + __rsHead.getString("code") + "' data-name='" + __rsHead.getString("name_1") + "'> " + __rsHead.getString("code") + '~' + __rsHead.getString("name_1") + " | " + __rsHead.getString("barcodes") + " </li>");

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

        response.getWriter().write(__html.toString());
    }

}
