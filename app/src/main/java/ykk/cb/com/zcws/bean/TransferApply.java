package ykk.cb.com.zcws.bean;

import java.io.Serializable;

/**
 * 调拨申请单主表
 * @author Administrator
 *
 */
public class TransferApply implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String billNo;			// 唯一单号
	private int stockId;			//
	private int status;				// 状态
	private int createUserId;
	private String createDate;

	private Stock stock;	// 对应的仓库

	public TransferApply() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}
	
	
	
}
