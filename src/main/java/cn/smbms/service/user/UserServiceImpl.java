package cn.smbms.service.user;

import java.io.File;
import java.util.List;


import cn.smbms.dao.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.smbms.pojo.User;

/**
 * service层捕获异常，进行事务处理
 * 事务处理：调用不同dao的多个方法，必须使用同一个connection（connection作为参数传递）
 * 事务完成之后，需要在service层进行connection的关闭，在dao层关闭（PreparedStatement和ResultSet对象）
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;


    @Override
    public User login(String userCode, String userPassword)  {
        User user = null;
        user = userMapper.getLoginUser(userCode);
        //匹配密码
        if(null != user){
            if(!user.getUserPassword().equals(userPassword)) {
                user = null;
            }
        }
        return user;
    }

    @Override
    public boolean smbmsAdd(User user) {
        boolean flag = false;
        if(userMapper.add(user) > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public List<User> getUserList(String queryUserName, Integer queryUserRole, Integer currentPageNo, Integer pageSize)  {
        currentPageNo = (currentPageNo-1)*pageSize;
        return userMapper.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
    }

    @Override
    public int getUserCount(String queryUserName, Integer queryUserRole)  {
        return userMapper.getUserCount(queryUserName, queryUserRole);
    }

    @Override
    public User selectUserCodeExist(String userCode)  {
        return userMapper.getLoginUser(userCode);
    }

    @Override
    public boolean deleteUserById(Integer delId)  {
        boolean flag = true;
        String path = "F:\\SMBMS\\SMBMS\\target\\SMBMS\\statics\\uploadfiles\\";
        //先删除该条记录的上传附件
        User user = userMapper.getUserById(delId);
        if(user.getIdPicPath() != null && !"".equals(user.getIdPicPath())){
            //删除服务器上个人证件照
            File file = new File(path+user.getIdPicPath());
            if(file.exists()){
                flag = file.delete();
            }
        }
        if(flag && user.getWorkPicPath() != null && !"".equals(user.getWorkPicPath())){
            //删除服务器上个人工作证照片
            File file = new File(path+user.getWorkPicPath());
            if(file.exists()){
                flag = file.delete();
            }
        }
        if(flag){
            if(userMapper.deleteUserById(delId) < 1) {
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public User getUserById(Integer id)  {
        return userMapper.getUserById(id);
    }

    @Override
    public boolean modify(User user)  {
        boolean flag = false;
        if(userMapper.modify(user) > 0) {
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean updatePwd(Integer id, String pwd)  {
        boolean flag = false;
        if(userMapper.updatePwd(id, pwd) > 0) {
            flag = true;
        }
        return flag;
    }
}