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

    /* 部门id */
    private Integer deptId;

    /* 创建时间 */
    private String createTime;

    /* 创建者id */
    private Integer createrId;

    /* 创建者名字 */
    private String createrName;

    /* 对应金蝶用户名 */
    private String kdUserName;

    /* 对应金蝶用户编码 */
    private String kdUserNumber;

    /* k3员工id */
    private Integer staffId;
    /* 状态：1.启用，2.禁用 */
    private Integer status;
    /* k3员工 */
    private Staff staff;
    private List<Role> roles;

    /* 部门 */
    private Department department;
    /* k3账号id */
    private Integer kdAccountId;
    /* k3账号名称 */
    private String kdAccountName;
    /* k3账号 */
    private String kdAccount;
    /* k3账号密码 */
    private String kdAccountPassword;
    /* 用户类型。1内部用户，2供应商 */
    private int accountType;
    /* 供应商id */
    private Integer supplierId;
    private Supplier supplier;

    /*所属账套名称*/
    private String kdAcctName;
    /*所属数据库实例名称*/
    private String kdDBName;
    /*所属职员id*/
    private int empId;
    /*所属职员姓名*/
    private String empName;
    /*所属职员编码*/
    private String empNumber;

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

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getKdUserName() {
        return kdUserName;
    }

    public void setKdUserName(String kdUserName) {
        this.kdUserName = kdUserName;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public String getKdUserNumber() {
        return kdUserNumber;
    }

    public void setKdUserNumber(String kdUserNumber) {
        this.kdUserNumber = kdUserNumber;
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

    public Integer getKdAccountId() {
        return kdAccountId;
    }

    public void setKdAccountId(Integer kdAccountId) {
        this.kdAccountId = kdAccountId;
    }

    public String getKdAccountName() {
        return kdAccountName;
    }

    public void setKdAccountName(String kdAccountName) {
        this.kdAccountName = kdAccountName;
    }

    public String getKdAccount() {
        return kdAccount;
    }

    public void setKdAccount(String kdAccount) {
        this.kdAccount = kdAccount;
    }

    public String getKdAccountPassword() {
        return kdAccountPassword;
    }

    public void setKdAccountPassword(String kdAccountPassword) {
        this.kdAccountPassword = kdAccountPassword;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

}
