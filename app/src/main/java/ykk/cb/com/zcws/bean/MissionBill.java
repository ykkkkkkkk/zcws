package ykk.cb.com.zcws.bean;

import java.io.Serializable;

/**
 * 任务单实体类
 * @author hongmoon
 *
 */
public class MissionBill implements Serializable {
	private static final long serialVersionUID = 1L;

	//ID
	private int id;
	//任务单编号
	private String billNo;
	//任务类型 	1:调拨拣货任务
	private int missionType;
	//创建时间
	private String createTime;
	//审核时间
	private String checkTime;
	//审核人
	private String checkerName;
	//任务推送人
	private String missionCreater;
	//执行人id
	private int receiveUserId;
	//执行人名称
	private String missionExecuter;
	//任务需要完成的时间
	private String missionFinishTime;
	//任务开始时间
	private String missionStartTime;
	//任务结束时间
	private String missionEndTime;
	//任务内容
	private String missionContent;
	//源单类型	1:调拨申请单
	private int sourceBillType;
	//源单ID
	private int sourceBillId;
	//源单号
	private String sourceBillNo;
	// 任务状态 ( A：创建、B：审核、C：业务关闭、D：进行中，E：手工关闭，F：待关闭 )
	private String missionStatus;
	//备注
	private String remark;
	//关闭人
	private String closerName;
	//关闭时间
	private String closeTime;
	private int icstockBillId;	// 出入库主表id( ICStockBill--》id )
	//来源单据的分录数
	private int sourceBillEntryCount;
	private int stockId;	// 对应仓库id

	private Stock stock;

	// 临时字段，不存吧
	// 关联对象( 把来源对象转成Json字符串  )
	private String sourceObj;
	private boolean check; // 前端选中标识，或是否超时判断


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
	public int getMissionType() {
		return missionType;
	}
	public void setMissionType(int missionType) {
		this.missionType = missionType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}
	public String getCheckerName() {
		return checkerName;
	}
	public void setCheckerName(String checkerName) {
		this.checkerName = checkerName;
	}
	public String getMissionCreater() {
		return missionCreater;
	}
	public void setMissionCreater(String missionCreater) {
		this.missionCreater = missionCreater;
	}
	public String getMissionExecuter() {
		return missionExecuter;
	}
	public void setMissionExecuter(String missionExecuter) {
		this.missionExecuter = missionExecuter;
	}
	public String getMissionFinishTime() {
		return missionFinishTime;
	}
	public void setMissionFinishTime(String missionFinishTime) {
		this.missionFinishTime = missionFinishTime;
	}
	public String getMissionStartTime() {
		return missionStartTime;
	}
	public void setMissionStartTime(String missionStartTime) {
		this.missionStartTime = missionStartTime;
	}
	public String getMissionEndTime() {
		return missionEndTime;
	}
	public void setMissionEndTime(String missionEndTime) {
		this.missionEndTime = missionEndTime;
	}
	public String getMissionContent() {
		return missionContent;
	}
	public void setMissionContent(String missionContent) {
		this.missionContent = missionContent;
	}
	public int getSourceBillType() {
		return sourceBillType;
	}
	public void setSourceBillType(int sourceBillType) {
		this.sourceBillType = sourceBillType;
	}
	public int getSourceBillId() {
		return sourceBillId;
	}
	public void setSourceBillId(int sourceBillId) {
		this.sourceBillId = sourceBillId;
	}
	public String getSourceBillNo() {
		return sourceBillNo;
	}
	public void setSourceBillNo(String sourceBillNo) {
		this.sourceBillNo = sourceBillNo;
	}
	public String getMissionStatus() {
		return missionStatus;
	}
	public void setMissionStatus(String missionStatus) {
		this.missionStatus = missionStatus;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCloserName() {
		return closerName;
	}
	public void setCloserName(String closerName) {
		this.closerName = closerName;
	}
	public String getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}
	public String getSourceObj() {
		return sourceObj;
	}
	public void setSourceObj(String sourceObj) {
		this.sourceObj = sourceObj;
	}
	public int getReceiveUserId() {
		return receiveUserId;
	}
	public void setReceiveUserId(int receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}

	public int getIcstockBillId() {
		return icstockBillId;
	}
	public void setIcstockBillId(int icstockBillId) {
		this.icstockBillId = icstockBillId;
	}

	public int getSourceBillEntryCount() {
		return sourceBillEntryCount;
	}

	public void setSourceBillEntryCount(int sourceBillEntryCount) {
		this.sourceBillEntryCount = sourceBillEntryCount;
	}

	public int getStockId() {
		return stockId;
	}
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}
	public Stock getStock() {
		return stock;
	}
	public void setStock(Stock stock) {
		this.stock = stock;
	}

	@Override
	public String toString() {
		return "MissionBill [id=" + id + ", billNo=" + billNo + ", missionType=" + missionType + ", createTime="
				+ createTime + ", checkTime=" + checkTime + ", checkerName=" + checkerName + ", missionCreater="
				+ missionCreater + ", receiveUserId=" + receiveUserId + ", missionExecuter=" + missionExecuter
				+ ", missionFinishTime=" + missionFinishTime + ", missionStartTime=" + missionStartTime
				+ ", missionEndTime=" + missionEndTime + ", missionContent=" + missionContent + ", sourceBillType="
				+ sourceBillType + ", sourceBillId=" + sourceBillId + ", sourceBillNo=" + sourceBillNo
				+ ", missionStatus=" + missionStatus + ", remark=" + remark + ", closerName=" + closerName
				+ ", closeTime=" + closeTime + ", icstockBillId=" + icstockBillId + ", sourceBillEntryCount="
				+ sourceBillEntryCount + ", stockId=" + stockId + ", stock=" + stock + ", sourceObj=" + sourceObj
				+ ", check=" + check + "]";
	}

}
