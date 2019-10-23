package ykk.cb.com.zcws.bean.k3Bean;

import java.io.Serializable;

/**
 * 盘点备份表
 */
public class ICInvBackup implements Serializable {
	private int id; // id
	private int finterId; // 方案id
	private int stockId; // 仓库id
	private int mtlId; // 物料id
	private double fauxQty; // 账存数
	private double fauxQtyAct; // 实存数
	private double fauxCheckQty; // 盘点数
	private String createDate; // 创建日期
	private int createUserId; // 创建人
	private int toK3; // 是否提交到K3  1: 未提交	3:已提交

	// 临时字段，不存表
	private String stockName; // 仓库名称
	private String mtlNumber; // 物料代码
	private String mtlName; // 物料名称
	private String unitName; // 单位名称
	private String fmodel; // 物料规格
	private String fbatchNo; // 物料批次
	private double realQty; // 当时盘点的输入的数
	private boolean check; // 是否选中
	
	public ICInvBackup() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFinterId() {
		return finterId;
	}

	public void setFinterId(int finterId) {
		this.finterId = finterId;
	}

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public int getMtlId() {
		return mtlId;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public double getFauxQty() {
		return fauxQty;
	}

	public void setFauxQty(double fauxQty) {
		this.fauxQty = fauxQty;
	}

	public double getFauxQtyAct() {
		return fauxQtyAct;
	}

	public void setFauxQtyAct(double fauxQtyAct) {
		this.fauxQtyAct = fauxQtyAct;
	}

	public double getFauxCheckQty() {
		return fauxCheckQty;
	}

	public void setFauxCheckQty(double fauxCheckQty) {
		this.fauxCheckQty = fauxCheckQty;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getMtlNumber() {
		return mtlNumber;
	}

	public void setMtlNumber(String mtlNumber) {
		this.mtlNumber = mtlNumber;
	}

	public String getMtlName() {
		return mtlName;
	}

	public void setMtlName(String mtlName) {
		this.mtlName = mtlName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getFmodel() {
		return fmodel;
	}

	public void setFmodel(String fmodel) {
		this.fmodel = fmodel;
	}

	public String getFbatchNo() {
		return fbatchNo;
	}

	public void setFbatchNo(String fbatchNo) {
		this.fbatchNo = fbatchNo;
	}

	public double getRealQty() {
		return realQty;
	}

	public void setRealQty(double realQty) {
		this.realQty = realQty;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public int getToK3() {
		return toK3;
	}

	public void setToK3(int toK3) {
		this.toK3 = toK3;
	}
	
	
}
