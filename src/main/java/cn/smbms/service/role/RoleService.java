package cn.smbms.service.role;

import java.util.List;

import cn.smbms.pojo.Role;
import org.apache.ibatis.annotations.Param;

public interface RoleService {
    /**
     * 获取角色列表
     * @return
     */
	public List<Role> getRoleList();
	/**
	 * 增加角色信息
	 * @param role
	 * @return
	 * @
	 */
	public int add(Role role);

	/**
	 * 通过Id删除role
	 * @param delId
	 * @return
	 * @
	 */
	public int deleteRoleById(@Param("id")Integer delId);

	/**
	 * 修改角色信息
	 * @param role
	 * @return
	 * @
	 */
	public int modify(Role role);


	/**
	 * 通过id获取role
	 * @param id
	 * @return
	 * @
	 */
	public Role getRoleById(@Param("id") Integer id);

	/**
	 * 根据roleCode ，进行角色编码的唯一性验证
	 * @param roleCode
	 * @return
	 * @
	 */
	public int roleCodeIsExist(@Param("roleCode")String roleCode);
	
}
