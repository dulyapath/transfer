package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Permission;

public class PermissionUtil {

    private final int WEB_FLAG = 1;
    private String provider;
    public Permission pmx;

    public PermissionUtil(String dbname) {
        this.provider = dbname.toLowerCase();
    }

    public PermissionUtil(String provider, String user, String menuCode) {
        this.provider = provider.toLowerCase();
        this.pmx = this.getPermissMenu(user, menuCode);
    }

    public Permission getPermissMenu(String user, String menuCode) {
        _routine routine = new _routine();
        Connection __conn = null;
        Permission pmx = new Permission();
        try {
            __conn = routine._connect("smlerpmain" + this.provider, "SMLConfig" + this.provider.toUpperCase() + ".xml");
            String _query = "SELECT is_create, is_read, is_update, is_delete FROM sml_user_web WHERE web_flag = " + WEB_FLAG + " AND lower(user_access) = lower('" + menuCode + "') AND upper(user_code) = upper('" + user + "')";

            Statement __stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet __rs = __stmt.executeQuery(_query);

            pmx.setMenuCode(menuCode);
            while (__rs.next()) {
                pmx.setCreate(num2bool(__rs.getInt("is_create")));
                pmx.setRead(num2bool(__rs.getInt("is_read")));
                pmx.setUpdate(num2bool(__rs.getInt("is_update")));
                pmx.setDelete(num2bool(__rs.getInt("is_delete")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
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

        return pmx;
    }

    public List<Permission> getPermissUser(String user) throws SQLException {
        _routine routine = new _routine();
        Connection __conn = null;

        List<Permission> pmList = new ArrayList<>();
//        try {
//            __conn = routine._connect("smlerpmain" + this.provider, _global.FILE_CONFIG(provider));
//            String _query = "SELECT * FROM sml_user_web WHERE  web_flag = " + WEB_FLAG + " AND upper(user_code) = upper('" + user + "')";
//            Statement __stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//            ResultSet __rs = __stmt.executeQuery(_query);
//
//            while (__rs.next()) {
//                Permission pmx = new Permission();
//                pmx.setMenuCode(__rs.getString("user_access"));
//                pmx.setCreate(num2bool(__rs.getInt("is_create")));
//                pmx.setRead(num2bool(__rs.getInt("is_read")));
//                pmx.setUpdate(num2bool(__rs.getInt("is_update")));
//                pmx.setDelete(num2bool(__rs.getInt("is_delete")));
//                pmList.add(pmx);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (__conn != null) {
//                try {
//                    __conn.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }

        return pmList;
    }

    public Permission getPermissByList(List<Permission> pmList, String menuCode) {
        Permission pmx = new Permission();

        for (Permission pm : pmList) {
            if (pm.getMenuCode().equals(menuCode)) {
                return pm;
            }
        }
        return pmx;
    }

    public boolean checkPermissAdmin(String user) throws SQLException {
        _routine routine = new _routine();
        Connection __conn = routine._connect("smlerpmain" + this.provider, "SMLConfig" + this.provider.toUpperCase() + ".xml");

        String _query = "SELECT user_level FROM sml_user_list WHERE upper(user_code) = upper('" + user + "') AND user_level = 2 ";
        Statement __stmt = __conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet __rs = __stmt.executeQuery(_query);

        while (__rs.next()) {
            return true;
        }

        return false;
    }

    public boolean checkByList(List<Integer> pmx, List<Integer> pmList) {
        for (int _chk : pmList) {
            if (pmx.contains(_chk)) {
                return true;
            }
        }
        return false;
    }

    private boolean num2bool(int num) {
        return num == 1 ? true : false;
    }
}
