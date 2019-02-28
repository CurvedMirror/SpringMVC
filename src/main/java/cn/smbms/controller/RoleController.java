package cn.smbms.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.smbms.pojo.User;
import cn.smbms.tools.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import cn.smbms.pojo.Role;
import cn.smbms.service.role.RoleService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("sys/role")
public class RoleController {
    private Logger logger = Logger.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/rolelist.html")
    public String getRoleList(Model model) {
        List<Role> roleList = roleService.getRoleList();
        model.addAttribute("roleList", roleList);
        return "role/rolelist";
    }

    @RequestMapping(value = "/addRole.html")
    public String addRole() {
        return "role/roleadd";
    }

    @RequestMapping(value = "/addSave.html")
    public String add(Role role, Model model, HttpSession session) {
        role.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        role.setCreationDate(new Date());
        roleService.add(role);
        return "redirect:/sys/role/rolelist.html";
    }

    @RequestMapping(value = "/rcexist",method = RequestMethod.POST)
    @ResponseBody
    public Object rcexist(String roleCode) {
        HashMap<String, String> resultMap = new HashMap<>();
        int result = roleService.roleCodeIsExist(roleCode);
        if (result != 0) {
            resultMap.put("roleCode", "exist");
        }
        return resultMap;
    }

    @RequestMapping(value = "/modify/{id}")
    public String modify(@PathVariable int id, Model model) {
        Role role = roleService.getRoleById(id);
        model.addAttribute(role);
        return "role/rolemodify";
    }

    @RequestMapping(value = "/modifySave")
    public String modifySave(Role role) {
        int result = roleService.modify(role);
        if (result > 0) {
            return "redirect:/sys/role/rolelist.html";
        } else {
            return "role/rolemodify";
        }
    }

    @RequestMapping(value = "/delrole")
    @ResponseBody
    public Object delrole(int id) {
        HashMap<String, String> resultMap = new HashMap<>();
        Role role = roleService.getRoleById(id);
        if (role == null) {
            resultMap.put("delResult", "notexist");
        } else {
            if (roleService.deleteRoleById(id) > 0) {
                resultMap.put("delResult", "true");
            } else {
                resultMap.put("delResult", "false");
            }
        }
        return resultMap;
    }
}
