package cn.smbms.service.provider;

import java.io.File;
import java.util.List;

import cn.smbms.dao.bill.BillMapper;
import cn.smbms.dao.provider.ProviderMapper;
import cn.smbms.pojo.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 */
@Service
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    private ProviderMapper providerMapper;

    @Autowired
    private BillMapper billMapper;

    @Override
    public boolean add(Provider provider) {
        return providerMapper.add(provider);
    }

    @Override
    public List<Provider> getProviderList(String proName, String proCode, Integer currentPageNo, Integer pageSize) {
        currentPageNo = (currentPageNo - 1) * pageSize;
        return providerMapper.getProviderList(proName, proCode, currentPageNo, pageSize);
    }

    @Override
    public int getProviderCount(String proName, String proCode) {
        return providerMapper.getProviderCount(proName, proCode);
    }

    @Override
    public boolean smbmsDeleteProviderById(Integer delId) {
        boolean flag = true;
       /* int billCountByProviderId = billMapper.getBillCountByProviderId(delId);
        if (billCountByProviderId > 0) {
            billMapper.deleteBillByProviderId(delId);
        }*/
        String path = "F:\\SMBMS\\SMBMS\\target\\SMBMS\\statics\\uploadfiles\\";
        Provider provider = providerMapper.getProviderById(delId);
        if (provider.getOrgCodePicPath() != null && !"".equals(provider.getOrgCodePicPath())) {
            File file = new File(path + provider.getOrgCodePicPath());
            if (file.exists()) {
                flag = file.delete();
            }
        }
        if (provider.getCompanyLicPicPath() != null && !"".equals(provider.getCompanyLicPicPath())) {
            File file = new File(path + provider.getCompanyLicPicPath());
            if (file.exists()) {
                flag = file.delete();
            }
        }
        if (flag) {
            if (providerMapper.deleteProviderById(delId) < 1) {
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public Provider getProviderById(Integer id) {
        return providerMapper.getProviderById(id);
    }

    @Override
    public boolean modify(Provider provider) {
        return providerMapper.modify(provider);
    }
}
