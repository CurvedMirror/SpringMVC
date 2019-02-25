package cn.smbms.service.provider;

import java.util.List;

import cn.smbms.pojo.Provider;
import org.apache.ibatis.annotations.Param;

public interface ProviderService {
	/**
	 * 增加供应商信息
	 * @param provider
	 * @return
	 * @
	 */
	public boolean add(Provider provider);

	/**
	 * 通过条件查询-providerList
	 * @param proName
	 * @param proCode
	 * @param currentPageNo
	 * @param pageSize
	 * @return
	 * @
	 */
	public List<Provider> getProviderList(@Param("proName") String proName, @Param("proCode") String proCode,
										  @Param("from") Integer currentPageNo, @Param("pageSize") Integer pageSize);

	/**
	 * 通过条件查询-供应商表记录数
	 * @param proName
	 * @param proCode
	 * @return
	 * @
	 */
	public int getProviderCount(@Param("proName") String proName, @Param("proCode") String proCode);

	/**
	 * 通过供应商id删除供应商信息
	 * @param delId
	 * @return
	 * @
	 */
	public boolean smbmsDeleteProviderById(@Param("id") Integer delId);

	/**
	 * 根据provider id 获取供应商信息
	 * @param id
	 * @return
	 * @
	 */
	public Provider getProviderById(@Param("id") Integer id);

	/**
	 * 修改供应商
	 * @param provider
	 * @return
	 * @
	 */
	public boolean modify(Provider provider);
	
}
