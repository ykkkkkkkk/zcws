package ykk.cb.com.zcws.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 用户   (t_user)
 */
public class User implements Serializable {
    /* 用户id */
    private Integer id;

    /* 用户名 */
    private String username;

    /* 密码 */
    private String password;

    /* 性别 */
    private Integer sex;

    /* 真实姓名 */
    private String truename;

    /* 创建时间 */
    private String createTime;

    /* 创建者id */
    private Integer createrId;

    /* 创建者名字 */
    private String createrName;

    /* 状态：1.启用，2.禁用 */
    private Integer status;
    private List<Role> roles;

    /*所属账套名称*/
    private String kdAcctName;
    /*所属数据库实例名称*/
    private String kdDBName;
    /*所属职员id 对应职员表t_emp 中的FItemID*/
    private int empId;
    /*所属职员姓名*/
    private String empName;
    /*所属职员编码*/
    private String empNumber;
    /*对应ERP里用户t_User表中的FUserID*/
    private int erpUserId;
    /*ERP用户名*/
    private String erpUserName;
    private int deptId;				// 生产部门id
    private int managerStockId;  	// 管理的仓库

    private Stock managerStock;		// 管理的对象
    private Department dept;		// 生产部门对象

    public User() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Integer createrId) {
        this.createrId = createrId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getKdAcctName() {
        return kdAcctName;
    }

    public void setKdAcctName(String kdAcctName) {
        this.kdAcctName = kdAcctName;
    }

    public String getKdDBName() {
        return kdDBName;
    }

    public void setKdDBName(String kdDBName) {
        this.kdDBName = kdDBName;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpNumber() {
        return empNumber;
    }

    public void setEmpNumber(String empNumber) {
        this.empNumber = empNumber;
    }

    public int getManagerStockId() {
        return managerStockId;
    }

    public void setManagerStockId(int managerStockId) {
        this.managerStockId = managerStockId;
    }

    public Stock getManagerStock() {
        return managerStock;
    }

    public void setManagerStock(Stock managerStock) {
        this.managerStock = managerStock;
    }

    public int getErpUserId() {
        return erpUserId;
    }

    public void setErpUserId(int erpUserId) {
        this.erpUserId = erpUserId;
    }

    public String getErpUserName() {
        return erpUserName;
    }

    public void setErpUserName(String erpUserName) {
        this.erpUserName = erpUserName;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public Department getDept() {
        return dept;
    }

    public void setDept(Department dept) {
        this.dept = dept;
    }

}
