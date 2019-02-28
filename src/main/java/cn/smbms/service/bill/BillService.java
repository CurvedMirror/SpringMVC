package cn.smbms.service.bill;

import java.util.List;

import cn.smbms.pojo.Bill;
import org.apache.ibatis.annotations.Param;

public interface BillService {

	public List<Bill> getBillList(String productName,Integer providerId,
								  Integer isPayment,Integer currentPageNo,Integer pageSize) ;


	/**
	 * 通过条件查询-订单表记录数
	 * @param productName
	 * @param providerId
	 * @param isPayment
	 * @return
	 * @
	 */
	public int getBillCount(String productName,Integer providerId,Integer isPayment) ;

	/**
	 * 增加订单
	 * @param bill
	 * @return
	 * @
	 */
	public boolean add(Bill bill) ;

	/**
	 * 通过id获取Bill
	 * @param id
	 * @return
	 * @
	 */
	public Bill getBillById(Integer id) ;

	/**
	 * 修改订单信息
	 * @param bill
	 * @return
	 * @
	 */
	public boolean modify(Bill bill) ;

	/**
	 * 通过id删除订单信息
	 * @param delId
	 * @return
	 * @
	 */
	public boolean deleteBillById(Integer delId) ;

	int getBillCountByProviderId(Integer id);
}
