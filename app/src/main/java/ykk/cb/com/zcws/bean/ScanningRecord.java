package ykk.cb.com.zcws.bean;

import java.io.Serializable;

/**
 * 出入库扫描记录表
 *
 * @author Administrator
 */
public class ScanningRecord implements Serializable {
    private int id; // 自增长id
    private int type; // 1：电商销售出库，10：生产产品入库，11：发货通知单销售出库，12：电商销售退货
    private int sourceId; // 来源id
    private String sourceNumber; // 来源单号
    private int sourceEntryId; // 来源分录id
    private String expressNo; // 快递单号
    private int icItemId; // 物料id
    private String icItemNumber; // 物料代码
    private String icItemName; // 物料名称
    private String stockNumber; // 仓库代码
    private String stockName; // 仓库名称
    private String stockPositionNumber; // 库位代码
    private String stockPositionName; // 库位名称
    private String custNumber; // 客户代码
    private String custName; // 客户名称
    private String deliveryWay; // 发货方式
    private String deptNumber; // 部门代码
    private String deptName; // 部门名称
    private double sourceQty; // 来源数量
    private double realQty; // 实收数量
    private String pdaNo; // pda产生的流水号
    private String k3BillNo; // k3保存成功返回的单号
    private int createUserId; // 创建人id
    private String createUserName; // 创建人名称
    private String createDate; // 创建日期

    // 临时字段，不存表
    private Stock stock; // 仓库
    private StockPosition stockPos; // 库位
    private char isUniqueness; // 条码是否唯一：Y是，N否
    private String strBarcodes; // 用逗号拼接的条码号
    private String sourceObj; // 来源对象
    private double useableQty; // 可用数
    private int empId; // 所属职员id


    public ScanningRecord() {
        super();
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getSourceId() {
        return sourceId;
    }

    public String getSourceNumber() {
        return sourceNumber;
    }

    public int getSourceEntryId() {
        return sourceEntryId;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public String getIcItemNumber() {
        return icItemNumber;
    }

    public String getIcItemName() {
        return icItemName;
    }

    public String getStockNumber() {
        return stockNumber;
    }

    public String getStockName() {
        return stockName;
    }

    public String getStockPositionNumber() {
        return stockPositionNumber;
    }

    public String getStockPositionName() {
        return stockPositionName;
    }

    public String getCustNumber() {
        return custNumber;
    }

    public String getCustName() {
        return custName;
    }

    public String getDeliveryWay() {
        return deliveryWay;
    }

    public String getDeptNumber() {
        return deptNumber;
    }

    public String getDeptName() {
        return deptName;
    }

    public double getSourceQty() {
        return sourceQty;
    }

    public double getRealQty() {
        return realQty;
    }

    public String getPdaNo() {
        return pdaNo;
    }

    public String getK3BillNo() {
        return k3BillNo;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public void setSourceNumber(String sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    public void setSourceEntryId(int sourceEntryId) {
        this.sourceEntryId = sourceEntryId;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public void setIcItemNumber(String icItemNumber) {
        this.icItemNumber = icItemNumber;
    }

    public void setIcItemName(String icItemName) {
        this.icItemName = icItemName;
    }

    public void setStockNumber(String stockNumber) {
        this.stockNumber = stockNumber;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public void setStockPositionNumber(String stockPositionNumber) {
        this.stockPositionNumber = stockPositionNumber;
    }

    public void setStockPositionName(String stockPositionName) {
        this.stockPositionName = stockPositionName;
    }

    public void setCustNumber(String custNumber) {
        this.custNumber = custNumber;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public void setDeliveryWay(String deliveryWay) {
        this.deliveryWay = deliveryWay;
    }

    public void setDeptNumber(String deptNumber) {
        this.deptNumber = deptNumber;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setSourceQty(double sourceQty) {
        this.sourceQty = sourceQty;
    }

    public void setRealQty(double realQty) {
        this.realQty = realQty;
    }

    public void setPdaNo(String pdaNo) {
        this.pdaNo = pdaNo;
    }

    public void setK3BillNo(String k3BillNo) {
        this.k3BillNo = k3BillNo;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getStrBarcodes() {
        return strBarcodes;
    }

    public String getSourceObj() {
        return sourceObj;
    }

    public void setStrBarcodes(String strBarcodes) {
        this.strBarcodes = strBarcodes;
    }

    public void setSourceObj(String sourceObj) {
        this.sourceObj = sourceObj;
    }

    public char getIsUniqueness() {
        return isUniqueness;
    }

    public void setIsUniqueness(char isUniqueness) {
        this.isUniqueness = isUniqueness;
    }

    public Stock getStock() {
        return stock;
    }

    public StockPosition getStockPos() {
        return stockPos;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setStockPos(StockPosition stockPos) {
        this.stockPos = stockPos;
    }

    public double getUseableQty() {
        return useableQty;
    }

    public void setUseableQty(double useableQty) {
        this.useableQty = useableQty;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getIcItemId() {
        return icItemId;
    }

    public void setIcItemId(int icItemId) {
        this.icItemId = icItemId;
    }


}
