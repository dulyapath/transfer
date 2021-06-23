package com.mis2.permission;

import java.io.IOException;
import java.sql.Connection;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import utils._global;
import utils._routine;

@WebServlet(name = "userlist-list", urlPatterns = {"/userlist-list"})
public class UserList extends HttpServlet {

    private JSONObject objResult;
    private String __DBNAME;
    private String __PROVIDER;
    private String __USERCODE;
    private Integer _totalPerPages;
    private Integer _pageNO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.objResult = new JSONObject();
        this.objResult.put("success", false);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession __SESSION = request.getSession();

        if (__SESSION.getAttribute("dbname") != null) {
            this.__DBNAME = "smlerpmain" + __SESSION.getAttribute("provider").toString().toLowerCase();
        }

        if (__SESSION.getAttribute("provider") != null) {
            this.__PROVIDER = __SESSION.getAttribute("provider").toString();
        }

        if (__SESSION.getAttribute("user") != null) {
            this.__USERCODE = __SESSION.getAttribute("user").toString().toUpperCase();
        }

        String _actionName = "";
        if (request.getParameter("action_name") != null) {
            _actionName = request.getParameter("action_name");
        }

        String _keyID = "";
        if (request.getParameter("key_id") != null) {
            _keyID = request.getParameter("key_id");
        }

        String _perm = "";
        if (request.getParameter("perm") != null) {
            _perm = request.getParameter("perm");
        }

        String _queryExtend = "";
        if (request.getParameter("page_no") != null && request.getParameter("total_page") != null) {
            this._pageNO = Integer.parseInt(request.getParameter("page_no"));
            this._totalPerPages = Integer.parseInt(request.getParameter("total_page"));

            int _startPostion;
            if (this._pageNO > 0) {
                _startPostion = (this._pageNO - 1) * this._totalPerPages;

                _queryExtend += " LIMIT " + this._totalPerPages + " OFFSET " + _startPostion;
            }
        }

        Connection __CONN = null;
        String strQUERY;
        try {
            _routine __ROUTINE = new _routine();
            __CONN = __ROUTINE._connect(this.__DBNAME, _global.FILE_CONFIG(__PROVIDER));

            switch (_actionName) {
                case "get_userlist":
                    displayUserList(__CONN, this.__USERCODE, _queryExtend);
                    break;
                case "find_userlist":
                    displayUserPerm(__CONN, _keyID);
                    break;
                case "get_web_pages":
                    strQUERY = "SELECT page_code FROM sml_web_page WHERE web_flag = '" + _global._WEB_FLAG + "' ORDER BY page_code";
                    findData(__CONN, strQUERY);
                    break;
                case "update_permission":
                    updateUserPermission(__CONN, _keyID, _perm);
                    break;
                case "find_permission_user":
                    strQUERY = "SELECT is_read, is_create, is_update, is_delete FROM sml_permission_user_list WHERE UPPER(user_code)='" + this.__USERCODE + "' AND page_code='" + _keyID + "' AND web_flag='" + _global._WEB_FLAG + "'";
                    findData(__CONN, strQUERY);
                    break;
            }
        } catch (JSONException JSONex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", JSONex.getMessage());
        } finally {
            if (__CONN != null) {
                try {
                    __CONN.close();
                } catch (SQLException SQLex) {
                    objResult.put("err_title", "ข้อความระบบ");
                    objResult.put("err_msg", SQLex.getMessage());
                }
            }
        }

        response.getWriter().print(objResult);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void displayUserList(Connection connection, String userCode, String strQueryExtend) {
        String __strQUERY = "SELECT user_code, user_name,COALESCE((select p_code from sa_permission where upper(user_code)=upper(sml_user_list.user_code)),'0')as permission FROM sml_user_list WHERE UPPER(user_code) <> upper('SUPERADMIN') ";
        String __rsHTML = "";

        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData = __stmt.executeQuery(__strQUERY + strQueryExtend);
            __rsData.next();
            Integer __rowCOUNT = __rsData.getRow();
            System.out.println(__strQUERY);
            if (__rowCOUNT > 0) {
                __rsData.previous();
                Integer __rowNumber = 1;
                while (__rsData.next()) {
                    __rsHTML += "<tr>";
                    __rsHTML += "<td>" + __rowNumber + "</td>";
                    __rsHTML += "<td>" + __rsData.getString("user_code") + " ~ " + __rsData.getString("user_name") + "</td>";
                    if (__rsData.getString("permission").equals("")) {
                        __rsHTML += "<td>ผู้จัดทำ</td>";
                    } else if (__rsData.getString("permission").equals("0")) {
                        __rsHTML += "<td>ผู้จัดทำ</td>";
                    } else if (__rsData.getString("permission").equals("1")) {
                        __rsHTML += "<td>ผู้อนุมัติ</td>";
                    } else {
                        __rsHTML += "<td>ผู้จัดทำ</td>";
                    }
                    __rsHTML += "<td><button class='btn btn-primary btn-block' id='btn-manage' key_id='" + __rsData.getString("user_code") + "' data-role='" + __rsData.getString("permission") + "' >จัดการสิทธิ์</button></td>";
                    __rsHTML += "</tr>";

                    __rowNumber++;
                }
            } else {
                __rsHTML = "<tr><td colspan='3'>ไม่พบข้อมูล.</td></tr>";
            }

            objResult.put("success", true);
            objResult.put("data", __rsHTML);

            displayPagination(connection, __strQUERY);

        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
        }

    }

    private void displayPagination(Connection connection, String __strQUERY) {
        String __rsHTML;

        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData = __stmt.executeQuery(__strQUERY);
            __rsData.last();
            int _totalRecords = __rsData.getRow();
            int _currentPages = 1;
            int _totalPages = 0;

            if (_totalRecords > 0) {
                _totalPages = (int) Math.ceil(_totalRecords / this._totalPerPages);
            }

            if (_totalPages == 0) {
                _totalPages = 1;
            } else {
                Double _totalPages2 = Double.parseDouble(String.valueOf(_totalRecords)) / Double.parseDouble(String.valueOf(this._totalPerPages));
                DecimalFormat df = new DecimalFormat();
                df.applyPattern("0.00");
                String[] arrTotalPages2 = String.valueOf(df.format(_totalPages2)).split("\\.");

                if (Integer.parseInt(arrTotalPages2[1]) > 0) {
                    _totalPages += 1;
                }
            }

            if (_pageNO > 1) {
                _currentPages = _pageNO;
            }

            if (_currentPages != 1) {
                __rsHTML = "<li><a href='#' page_no ='" + (_currentPages - 1) + "' class='pagination_link'><span>&laquo;</span></></li>";
            } else {
                __rsHTML = "<li class='disabled pagination_link'><a href='#'><span>&laquo;</span></></li>";
            }

            for (int i = 1; i <= _totalPages; i++) {
                if (i == _currentPages) {
                    __rsHTML += "<li><a href='#' page_no='" + i + "' style='color: red;' class='pagination_link'><span>" + i + "</span></a></li>";
                } else {
                    __rsHTML += "<li><a href='#' page_no='" + i + "' class='pagination_link'><span>" + i + "</span></a></li>";
                }
            }

            if (_currentPages != _totalPages) {
                __rsHTML += "<li><a href='#' page_no='" + (_currentPages + 1) + "' class='pagination_link'><span>&raquo;</span></a></li>";
            } else {
                __rsHTML += "<li class='disabled pagination_link'><a href='#'><span>&raquo;</span></a></li>";
            }

            objResult.put("success", true);
            objResult.put("data_pagination", __rsHTML);

        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
        }
    }

    private void displayUserPerm(Connection connection, String userCode) {
        String __strQUERY = "select * from sa_permission where upper(user_code)=upper('" + userCode + "')";
        String __rsHTML = "";

        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData = __stmt.executeQuery(__strQUERY);
            __rsData.next();
            Integer __rowCOUNT = __rsData.getRow();

            if (__rowCOUNT > 0) {
                __rsData.previous();
                Integer __rowNumber = 1;
                while (__rsData.next()) {

                    String p_code = __rsData.getString("p_code");

                    __rsHTML = p_code;

                    __rowNumber++;
                }
            } else {
                __rsHTML = "";
            }

            __stmt.close();
            __rsData.close();

            objResult.put("success", true);
            objResult.put("data", __rsHTML);

        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
        }
    }

    private void findData(Connection connection, String strQUERY) {
        try {
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData = __stmt.executeQuery(strQUERY);
            ResultSetMetaData __rsMData = __rsData.getMetaData();
            int __colCOUNT = __rsMData.getColumnCount();

            JSONArray arrJSList = new JSONArray();

            while (__rsData.next()) {
                JSONObject objData = new JSONObject();
                for (int i = 1; i <= __colCOUNT; i++) {
                    String __colNAME = __rsMData.getColumnName(i);
                    objData.put(__colNAME, __rsData.getObject(i));
                }
                arrJSList.put(objData);
            }

            __rsData.close();
            __stmt.close();

            this.objResult.put("success", true);
            this.objResult.put("data", arrJSList);
        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
        }
    }

    private void updateUserPermission(Connection connection, String strUserCode, String strPerm) {
        String __strQUERY = "UPDATE sa_permission SET p_code='" + strPerm + "' WHERE UPPER(user_code)='" + strUserCode.toUpperCase() + "' ";
        try {
            Statement __stmt0 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rsData0 = __stmt0.executeQuery("SELECT user_code FROM sa_permission WHERE UPPER(user_code)='" + strUserCode.toUpperCase() + "'");
            __rsData0.next();
            int __rowCOUNT = __rsData0.getRow();

            if (__rowCOUNT <= 0) {
                Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                __stmt.executeUpdate("INSERT INTO sa_permission (user_code, p_code) VALUES ('" + strUserCode.toUpperCase() + "','" + strPerm + "')");
                __stmt.close();
            }
            Statement __stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            __stmt.executeUpdate(__strQUERY);
            __stmt.close();
            this.objResult.put("success", true);
        } catch (SQLException SQLex) {
            objResult.put("err_title", "ข้อความระบบ");
            objResult.put("err_msg", SQLex.getMessage());
            objResult.put("err_sql", __strQUERY);
        }
    }

    private void showLog(String value) {
        System.out.println(value);
    }

}
