package ykk.cb.com.zcws.bean.k3Bean;

import java.io.Serializable;

import ykk.cb.com.zcws.bean.Department;
import ykk.cb.com.zcws.bean.Organization;

/**
 * @author 2019年4月28日 下午4:56:44
 * @Description:
 */
public class IcStockBill implements Serializable {
    /* 单据内码 */
    private Integer finterid;
    /* 单据类型 */
    private Short ftrantype;
    /* 单据日期 */
    private String fdate;
    /* 单据编号 */
    private String fbillno;
    /* 备注 */
    private String fnote;
    /* 收入库房id */
    private Integer fdcstockid;
    /* 发出库房id */
    private Integer fscstockid;
    /* 部门id */
    private Integer fdeptid;
    /* 业务员id */
    private Integer fempid;
    /* 供应商id */
    private Integer fsupplyid;
    /* 记账人id */
    private Integer fposterid;
    /* 审核人id */
    private Integer fcheckerid;
    /* 发货人id */
    private Integer ffmanagerid;
    /* 保管人id */
    private Integer fsmanagerid;
    /* 制单人id */
    private Integer fbillerid;
    /* 退货单号 */
    private Integer freturnbillinterid;
    /* 已废弃 */
    private String fscbillno;
    /* 状态 0-未审核，1-已审核 */
    private Short fstatus;
    /* 作废状态。0未作废，1已作废 */
    private Boolean fcancellation;
    /* 原单内码 */
    private Integer forgbillinterid;
    /* 单据类别 */
    private Integer fbilltypeid;
    /* 唯一标识 */
    private String fuuid;
    /* 关联发票号 */
    private Integer frelateinvoiceid;
    /* 摘要 */
    private String fexplanation;
    /* 交货地点 */
    private String ffetchadd;
    /* 交货日期 */
    private String ffetchdate;

    private Integer fmanagerid;
    /* 调拨类型 */
    private Integer freftype;
    /* 源单类型 */
    private Integer fseltrantype;
    /* 对方单据号 */
    private String fpoordbillno;
    /* 应收款日期 */
    private String fsettledate;
    /* 收货方 */
    private String fconsignee;
    /* 打印次数 */
    private Short fprintcount;
    /* 收件人 */
    private String freceiver;
    /* 收货地址 */
    private String fconsigneeadd;
    /* 收件人手机 */
    private String freceivermobile;

    /* 红蓝色 */
    private int frob;

    /* 在电商账套，代表源账套出库单号FbillNo。在生产账套代表：对方订单号 */
    private String fheadselfb0163;

    // -------------------start 以下字段在生产账套中存在-------------------//
    /* 生产账套表示对方客户id，电商账套表示来源单据内码 */
    private String fheadselfb0164;
    /* 对方客户代码 */
    private String fheadselfb0165;
    /* 对方客户名称 */
    private String fheadselfb0166;
    /* 对方制单人id */
    private String fheadselfb0167;
    /* 对方部门id */
    private String fheadselfb0168;
    /* 是否已下推至别的账套作为销售出库单 （A未下推，B已下推） */
    private String fheadselfb0169;

    /* 是否已下推至别的账套作为采购入库单（A未下推，B已下推） */
    private String fheadselfb0170;
    // -------------------end 以上字段在生产账套中存在-------------------//
    /* 销售出库单所属账套id */
    private int deptId;
    /* 部门名称 */
    private String deptName;
    /* 部门编码 */
    private String deptNumber;
    /* 客户代码 */
    private String fnumber;
    /* 客户名称 */
    private String fname;
    /* 发货人代码 不存库 */
    private String ffmanagercode;
    /* 保管人代码 不存库 */
    private String fsmanagercode;
    /* 供应商代码 不存库 */
    private String supplierCode;

    /* 部门 */
    private Department department;
    private Organization cust; // 客户对象
    /* 业务员 */
    private Emp emp;


    public Integer getFinterid() {
        return finterid;
    }
    public Short getFtrantype() {
        return ftrantype;
    }
    public String getFdate() {
        return fdate;
    }
    public String getFbillno() {
        return fbillno;
    }
    public String getFnote() {
        return fnote;
    }
    public Integer getFdcstockid() {
        return fdcstockid;
    }
    public Integer getFscstockid() {
        return fscstockid;
    }
    public Integer getFdeptid() {
        return fdeptid;
    }
    public Integer getFempid() {
        return fempid;
    }
    public Integer getFsupplyid() {
        return fsupplyid;
    }
    public Integer getFposterid() {
        return fposterid;
    }
    public Integer getFcheckerid() {
        return fcheckerid;
    }
    public Integer getFfmanagerid() {
        return ffmanagerid;
    }
    public Integer getFsmanagerid() {
        return fsmanagerid;
    }
    public Integer getFbillerid() {
        return fbillerid;
    }
    public Integer getFreturnbillinterid() {
        return freturnbillinterid;
    }
    public String getFscbillno() {
        return fscbillno;
    }
    public Short getFstatus() {
        return fstatus;
    }
    public Boolean getFcancellation() {
        return fcancellation;
    }
    public Integer getForgbillinterid() {
        return forgbillinterid;
    }
    public Integer getFbilltypeid() {
        return fbilltypeid;
    }
    public String getFuuid() {
        return fuuid;
    }
    public Integer getFrelateinvoiceid() {
        return frelateinvoiceid;
    }
    public String getFexplanation() {
        return fexplanation;
    }
    public String getFfetchadd() {
        return ffetchadd;
    }
    public String getFfetchdate() {
        return ffetchdate;
    }
    public Integer getFmanagerid() {
        return fmanagerid;
    }
    public Integer getFreftype() {
        return freftype;
    }
    public Integer getFseltrantype() {
        return fseltrantype;
    }
    public String getFpoordbillno() {
        return fpoordbillno;
    }
    public String getFsettledate() {
        return fsettledate;
    }
    public String getFconsignee() {
        return fconsignee;
    }
    public Short getFprintcount() {
        return fprintcount;
    }
    public String getFreceiver() {
        return freceiver;
    }
    public String getFconsigneeadd() {
        return fconsigneeadd;
    }
    public String getFreceivermobile() {
        return freceivermobile;
    }
    public int getFrob() {
        return frob;
    }
    public String getFheadselfb0163() {
        return fheadselfb0163;
    }
    public String getFheadselfb0164() {
        return fheadselfb0164;
    }
    public String getFheadselfb0165() {
        return fheadselfb0165;
    }
    public String getFheadselfb0166() {
        return fheadselfb0166;
    }
    public String getFheadselfb0167() {
        return fheadselfb0167;
    }
    public String getFheadselfb0168() {
        return fheadselfb0168;
    }
    public String getFheadselfb0169() {
        return fheadselfb0169;
    }
    public String getFheadselfb0170() {
        return fheadselfb0170;
    }
    public int getDeptId() {
        return deptId;
    }
    public String getDeptName() {
        return deptName;
    }
    public String getDeptNumber() {
        return deptNumber;
    }
    public String getFnumber() {
        return fnumber;
    }
    public String getFname() {
        return fname;
    }
    public String getFfmanagercode() {
        return ffmanagercode;
    }
    public String getFsmanagercode() {
        return fsmanagercode;
    }
    public String getSupplierCode() {
        return supplierCode;
    }
    public Department getDepartment() {
        return department;
    }
    public Organization getCust() {
        return cust;
    }
    public Emp getEmp() {
        return emp;
    }
    public void setFinterid(Integer finterid) {
        this.finterid = finterid;
    }
    public void setFtrantype(Short ftrantype) {
        this.ftrantype = ftrantype;
    }
    public void setFdate(String fdate) {
        this.fdate = fdate;
    }
    public void setFbillno(String fbillno) {
        this.fbillno = fbillno;
    }
    public void setFnote(String fnote) {
        this.fnote = fnote;
    }
    public void setFdcstockid(Integer fdcstockid) {
        this.fdcstockid = fdcstockid;
    }
    public void setFscstockid(Integer fscstockid) {
        this.fscstockid = fscstockid;
    }
    public void setFdeptid(Integer fdeptid) {
        this.fdeptid = fdeptid;
    }
    public void setFempid(Integer fempid) {
        this.fempid = fempid;
    }
    public void setFsupplyid(Integer fsupplyid) {
        this.fsupplyid = fsupplyid;
    }
    public void setFposterid(Integer fposterid) {
        this.fposterid = fposterid;
    }
    public void setFcheckerid(Integer fcheckerid) {
        this.fcheckerid = fcheckerid;
    }
    public void setFfmanagerid(Integer ffmanagerid) {
        this.ffmanagerid = ffmanagerid;
    }
    public void setFsmanagerid(Integer fsmanagerid) {
        this.fsmanagerid = fsmanagerid;
    }
    public void setFbillerid(Integer fbillerid) {
        this.fbillerid = fbillerid;
    }
    public void setFreturnbillinterid(Integer freturnbillinterid) {
        this.freturnbillinterid = freturnbillinterid;
    }
    public void setFscbillno(String fscbillno) {
        this.fscbillno = fscbillno;
    }
    public void setFstatus(Short fstatus) {
        this.fstatus = fstatus;
    }
    public void setFcancellation(Boolean fcancellation) {
        this.fcancellation = fcancellation;
    }
    public void setForgbillinterid(Integer forgbillinterid) {
        this.forgbillinterid = forgbillinterid;
    }
    public void setFbilltypeid(Integer fbilltypeid) {
        this.fbilltypeid = fbilltypeid;
    }
    public void setFuuid(String fuuid) {
        this.fuuid = fuuid;
    }
    public void setFrelateinvoiceid(Integer frelateinvoiceid) {
        this.frelateinvoiceid = frelateinvoiceid;
    }
    public void setFexplanation(String fexplanation) {
        this.fexplanation = fexplanation;
    }
    public void setFfetchadd(String ffetchadd) {
        this.ffetchadd = ffetchadd;
    }
    public void setFfetchdate(String ffetchdate) {
        this.ffetchdate = ffetchdate;
    }
    public void setFmanagerid(Integer fmanagerid) {
        this.fmanagerid = fmanagerid;
    }
    public void setFreftype(Integer freftype) {
        this.freftype = freftype;
    }
    public void setFseltrantype(Integer fseltrantype) {
        this.fseltrantype = fseltrantype;
    }
    public void setFpoordbillno(String fpoordbillno) {
        this.fpoordbillno = fpoordbillno;
    }
    public void setFsettledate(String fsettledate) {
        this.fsettledate = fsettledate;
    }
    public void setFconsignee(String fconsignee) {
        this.fconsignee = fconsignee;
    }
    public void setFprintcount(Short fprintcount) {
        this.fprintcount = fprintcount;
    }
    public void setFreceiver(String freceiver) {
        this.freceiver = freceiver;
    }
    public void setFconsigneeadd(String fconsigneeadd) {
        this.fconsigneeadd = fconsigneeadd;
    }
    public void setFreceivermobile(String freceivermobile) {
        this.freceivermobile = freceivermobile;
    }
    public void setFrob(int frob) {
        this.frob = frob;
    }
    public void setFheadselfb0163(String fheadselfb0163) {
        this.fheadselfb0163 = fheadselfb0163;
    }
    public void setFheadselfb0164(String fheadselfb0164) {
        this.fheadselfb0164 = fheadselfb0164;
    }
    public void setFheadselfb0165(String fheadselfb0165) {
        this.fheadselfb0165 = fheadselfb0165;
    }
    public void setFheadselfb0166(String fheadselfb0166) {
        this.fheadselfb0166 = fheadselfb0166;
    }
    public void setFheadselfb0167(String fheadselfb0167) {
        this.fheadselfb0167 = fheadselfb0167;
    }
    public void setFheadselfb0168(String fheadselfb0168) {
        this.fheadselfb0168 = fheadselfb0168;
    }
    public void setFheadselfb0169(String fheadselfb0169) {
        this.fheadselfb0169 = fheadselfb0169;
    }
    public void setFheadselfb0170(String fheadselfb0170) {
        this.fheadselfb0170 = fheadselfb0170;
    }
    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
    public void setDeptNumber(String deptNumber) {
        this.deptNumber = deptNumber;
    }
    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }
    public void setFname(String fname) {
        this.fname = fname;
    }
    public void setFfmanagercode(String ffmanagercode) {
        this.ffmanagercode = ffmanagercode;
    }
    public void setFsmanagercode(String fsmanagercode) {
        this.fsmanagercode = fsmanagercode;
    }
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }
    public void setCust(Organization cust) {
        this.cust = cust;
    }
    public void setEmp(Emp emp) {
        this.emp = emp;
    }

    @Override
    public String toString() {
        return "IcStockBill [finterid=" + finterid + ", ftrantype=" + ftrantype + ", fdate=" + fdate + ", fbillno="
                + fbillno + ", fnote=" + fnote + ", fdcstockid=" + fdcstockid + ", fscstockid=" + fscstockid
                + ", fdeptid=" + fdeptid + ", fempid=" + fempid + ", fsupplyid=" + fsupplyid + ", fposterid="
                + fposterid + ", fcheckerid=" + fcheckerid + ", ffmanagerid=" + ffmanagerid + ", fsmanagerid="
                + fsmanagerid + ", fbillerid=" + fbillerid + ", freturnbillinterid=" + freturnbillinterid
                + ", fscbillno=" + fscbillno + ", fstatus=" + fstatus + ", fcancellation=" + fcancellation
                + ", forgbillinterid=" + forgbillinterid + ", fbilltypeid=" + fbilltypeid + ", fuuid=" + fuuid
                + ", frelateinvoiceid=" + frelateinvoiceid + ", fexplanation=" + fexplanation + ", ffetchadd="
                + ffetchadd + ", ffetchdate=" + ffetchdate + ", fmanagerid=" + fmanagerid + ", freftype=" + freftype
                + ", fseltrantype=" + fseltrantype + ", fpoordbillno=" + fpoordbillno + ", fsettledate=" + fsettledate
                + ", fconsignee=" + fconsignee + ", fprintcount=" + fprintcount + ", freceiver=" + freceiver
                + ", fconsigneeadd=" + fconsigneeadd + ", freceivermobile=" + freceivermobile + ", frob=" + frob
                + ", fheadselfb0163=" + fheadselfb0163 + ", fheadselfb0164=" + fheadselfb0164 + ", fheadselfb0165="
                + fheadselfb0165 + ", fheadselfb0166=" + fheadselfb0166 + ", fheadselfb0167=" + fheadselfb0167
                + ", fheadselfb0168=" + fheadselfb0168 + ", fheadselfb0169=" + fheadselfb0169 + ", fheadselfb0170="
                + fheadselfb0170 + ", deptId=" + deptId + ", deptName=" + deptName + ", deptNumber=" + deptNumber
                + ", fnumber=" + fnumber + ", fname=" + fname + ", ffmanagercode=" + ffmanagercode + ", fsmanagercode="
                + fsmanagercode + ", supplierCode=" + supplierCode + ", department=" + department + ", cust=" + cust
                + ", emp=" + emp + "]";
    }


}