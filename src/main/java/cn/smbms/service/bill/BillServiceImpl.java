package cn.smbms.service.bill;

import java.util.List;

import cn.smbms.dao.bill.BillMapper;
import cn.smbms.pojo.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 */
@Service
public class BillServiceImpl implements BillService {

	@Autowired
	private BillMapper billMapper;


    @Override
    public List<Bill> getBillList(String productName, Integer providerId, Integer isPayment, Integer currentPageNo, Integer pageSize)  {
        currentPageNo = (currentPageNo-1)*pageSize;
        return billMapper.getBillList(productName,providerId,isPayment,currentPageNo,pageSize);
    }

    @Override
    public int getBillCount(String productName, Integer providerId, Integer isPayment)  {
        return billMapper.getBillCount(productName,providerId,isPayment);
    }

    @Override
    public boolean add(Bill bill)  {
        return billMapper.add(bill);
    }

    @Override
    public Bill getBillById(Integer id)  {
        return billMapper.getBillById(id);
    }

    @Override
    public boolean modify(Bill bill)  {
        return billMapper.modify(bill);
    }

    @Override
    public boolean deleteBillById(Integer delId)  {
        return billMapper.deleteBillById(delId);
    }

    @Override
    public int getBillCountByProviderId(Integer id) {
        return billMapper.getBillCountByProviderId(id);
    }
}
