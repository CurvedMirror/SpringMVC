package cn.smbms.service.role;

import java.util.List;

import cn.smbms.dao.role.RoleMapper;
import cn.smbms.pojo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Administrator
 */
@Service
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleMapper roleMapper;


	@Override
	public List<Role> getRoleList() {
		return roleMapper.getRoleList();
	}

    @Override
    public int add(Role role) {
        return roleMapper.add(role);
    }

    @Override
    public int deleteRoleById(Integer delId) {
        return roleMapper.deleteRoleById(delId);
    }

    @Override
    public int modify(Role role) {
        return roleMapper.modify(role);
    }

    @Override
    public Role getRoleById(Integer id) {
        return roleMapper.getRoleById(id);
    }

    @Override
    public int roleCodeIsExist(String roleCode) {
        return roleMapper.roleCodeIsExist(roleCode);
    }

}
