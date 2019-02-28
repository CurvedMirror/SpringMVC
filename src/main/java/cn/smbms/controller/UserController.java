package cn.smbms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.smbms.pojo.Role;
import cn.smbms.service.role.RoleService;
import cn.smbms.tools.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import cn.smbms.pojo.User;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/sys/user")
public class UserController {
    private Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /*@RequestMapping(value = "/login.html")
    public String login() {
        logger.debug("UserController welcome SMBMS==================");
        return "login";
    }*/


    /*@RequestMapping(value = "/dologin.html", method = RequestMethod.POST)
    public String doLogin(@RequestParam String userCode, @RequestParam String userPassword, HttpServletRequest request, HttpSession session) {
        logger.debug("doLogin====================================");
        User user = userService.login(userCode, userPassword);
        if (null == user) {
            throw new RuntimeException("用户名不存在");
        } else if (null != user && !user.getUserPassword().equals(userPassword)) {
            throw new RuntimeException("密码输入错误");
        }
        session.setAttribute(Constants.USER_SESSION, user);
        return "redirect:/user/main.html";
    }*/

  /*  @RequestMapping(value = "/main.html")
    public String main(HttpSession session) {
        if (session.getAttribute(Constants.USER_SESSION) == null) {
            return "redirect:/user/login.html";
        }
        return "frame";
    }*/

 /*   @RequestMapping(value = "/exlogin.html", method = RequestMethod.GET)
    public String exLogin(@RequestParam String userCode, @RequestParam String userPassword) {
        logger.debug("exLogin====================================");
        //调用service方法，进行用户匹配
        User user = userService.login(userCode, userPassword);
        //登录失败
        if (null == user) {
            throw new RuntimeException("用户名或者密码不正确！");
        }
        return "redirect:/user/main.html";
    }*/


	/*@ExceptionHandler(value = {RuntimeException.class})
	public String handlerException(RuntimeException e,HttpServletRequest request){
		request.setAttribute("error",e);
		return "login";
	}*/

 /*   @RequestMapping(value = "/logout.html")
    public String logout(HttpSession session) {
        //清除session
        session.invalidate();
        return "login";
    }*/

    @RequestMapping(value = "/userlist.html")
    public String getUserList(Model model,
                              @RequestParam(value = "queryname", required = false) String queryUserName,
                              @RequestParam(value = "queryUserRole", required = false) String queryUserRole,
                              @RequestParam(value = "pageIndex", required = false) String pageIndex) {
        int _queryUserRole = 0;
        List<User> userList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;
        /**
         * http://localhost:8090/SMBMS/userlist.do
         * ----queryUserName --NULL
         * http://localhost:8090/SMBMS/userlist.do?queryname=
         * --queryUserName ---""
         */
        System.out.println("queryUserName servlet--------" + queryUserName);
        System.out.println("queryUserRole servlet--------" + queryUserRole);
        System.out.println("query pageIndex--------- > " + pageIndex);
        if (queryUserName == null) {
            queryUserName = "";
        }
        if (queryUserRole != null && !"".equals(queryUserRole)) {
            _queryUserRole = Integer.parseInt(queryUserRole);
        }
        if (pageIndex != null) {
            try {
                currentPageNo = Integer.valueOf(pageIndex);
            } catch (NumberFormatException e) {
                return "redirect:/user/syserror.html";
            }
        }
        //总数量（表）
        int totalCount = userService.getUserCount(queryUserName, _queryUserRole);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);

        int totalPageCount = pages.getTotalPageCount();

        //控制首页和尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }


        userList = userService.getUserList(queryUserName, _queryUserRole, currentPageNo, pageSize);
        model.addAttribute("userList", userList);
        List<Role> roleList = null;
        roleList = roleService.getRoleList();
        model.addAttribute("roleList", roleList);
        model.addAttribute("queryUserName", queryUserName);
        model.addAttribute("queryUserRole", queryUserRole);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", currentPageNo);
        return "user/userlist";
    }


    @RequestMapping(value = "/syserror.html")
    public String sysError() {
        return "syserror";
    }

    @RequestMapping(value = "/useradd.html", method = RequestMethod.GET)
    public String addUser(@ModelAttribute("user") User user) {
        return "user/useradd";
    }

    @RequestMapping(value = "/addsave.html", method = RequestMethod.POST)
    public String addUserSave(User user, HttpSession session, HttpServletRequest request,
                              @RequestParam(value = "attachs", required = false) MultipartFile[] attachs) {
        String idPicPath = null;
        String workPicPath = null;
        String errorInfo = null;
        boolean flag = true;
        String path =
                request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
        logger.info("uploadFile path==========>" + path);
        for (int i = 0; i < attachs.length; i++) {
            MultipartFile attach = attachs[i];
            if (!attach.isEmpty()) {
                if (i == 0) {
                    errorInfo = "uploadFileError";
                } else if (i == 1) {
                    errorInfo = "uploadWpError";
                }
                String oldFileName = attach.getOriginalFilename();
                String prefix = FilenameUtils.getExtension(oldFileName);
                int filesize = 500000;
                if (attach.getSize() > filesize) {
                    request.setAttribute(errorInfo, "*上传的大小不能超过500kb");
                    flag = false;
                } else if ("jpg".equalsIgnoreCase(prefix)
                        || "png".equalsIgnoreCase(prefix)
                        || "jpeg".equalsIgnoreCase(prefix)
                        || "pneg".equalsIgnoreCase(prefix)) {
                    String fileName = System.currentTimeMillis()
                            + RandomUtils.nextInt(1000000) + "_Personal.jpg";
                    File targetFile = new File(path, fileName);
                    if (!targetFile.exists()) {
                        targetFile.mkdirs();
                    }
                    try {
                        attach.transferTo(targetFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        request.setAttribute(errorInfo, "*上传失败！");
                        flag = false;
                    }
                    if (i == 0) {
                        idPicPath = fileName;

                    } else if (i == 1) {
                        workPicPath = fileName;
                    }
                    logger.debug("idPicPath:" + idPicPath);
                    logger.debug("workPicPath:" + workPicPath);
                } else {
                    request.setAttribute(errorInfo, "*上传图片的格式不正确");
                    flag = false;
                }
            }

        }
        if (flag) {
            User attribute = (User) session.getAttribute(Constants.USER_SESSION);
            user.setCreatedBy(attribute.getId());
            user.setCreationDate(new Date());
            user.setIdPicPath(idPicPath);
            user.setWorkPicPath(workPicPath);
            try {
                if (userService.smbmsAdd(user)) {
                    return "redirect:/sys/user/userlist.html";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "user/useradd";
    }

    @RequestMapping(value = "/ucexist", method = RequestMethod.POST)
    @ResponseBody
    public Object userCodeIsExit(@RequestParam String userCode) {
        HashMap<String, String> resultMap = new HashMap<>();
        if (StringUtils.isNullOrEmpty(userCode)) {
            resultMap.put("userCode", "exist");
        } else {
            User user = userService.selectUserCodeExist(userCode);
            if (null != user) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "noexist");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping(value = "/pwdmodify.html", method = RequestMethod.GET)
    public String pwdModify(HttpSession session) {
        if (session.getAttribute(Constants.USER_SESSION) == null) {
            return "redirect:/user/login.html";
        }
        return "user/pwdmodify";
    }

    @RequestMapping(value = "/pwdmodify.json", method = RequestMethod.POST)
    @ResponseBody
    public Object getPwdByUserID(@RequestParam String oldpassword, HttpSession session) {
        HashMap<String, String> resultMap = new HashMap<>();
        if (null == session.getAttribute(Constants.USER_SESSION)) {
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)) {
            resultMap.put("result", "error");
        } else {
            String sessionPwd = ((User) session.getAttribute(Constants.USER_SESSION)).getUserPassword();
            if (!sessionPwd.equals(oldpassword)) {
                resultMap.put("result", "false");
            } else {
                resultMap.put("result", "true");
            }
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping(value = "/pwdsave.html")
    public String pwdSave(@RequestParam(value = "newpassword") String newPassword, HttpSession session) {
        User user = ((User) session.getAttribute(Constants.USER_SESSION));
        if (null != user) {
            boolean flag = userService.updatePwd(user.getId(), newPassword);
            if (flag) {
                return "login";
            } else {
                return "user/pwdmodify";
            }
        } else {
            return "login";
        }
    }

    @RequestMapping(value = "/getrolelist", method = RequestMethod.POST)
    @ResponseBody
    public Object getrolelist() {
        List<Role> roleList = roleService.getRoleList();
        return roleList;

    }

    @RequestMapping(value = "/delUser", method = RequestMethod.POST)
    @ResponseBody
    public Object delUser(Integer id, HttpServletRequest request) {
        HashMap<String, String> resultMap = new HashMap<>();
        boolean flag = userService.deleteUserById(id);
        if (flag) {
            resultMap.put("delResult", "true");
        } else {
            resultMap.put("delResult", "false");
        }
        return resultMap;
    }

    @RequestMapping(value = "/modify/{id}", method = RequestMethod.GET)
    public String getUserById(@PathVariable int id, Model model, HttpServletRequest request) {
        logger.debug("getProviderById id===================== " + id);
        User user = userService.getUserById(id);
        model.addAttribute(user);
        return "user/usermodify";
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable int id, Model model, HttpServletRequest request) {
        logger.debug("view id===================== " + id);
        User user = userService.getUserById(id);
        model.addAttribute(user);
        return "user/userview";
    }

    @RequestMapping(value = "/modifysave.html", method = RequestMethod.POST)
    public String modify(User user, HttpSession session, HttpServletRequest request,
                         @RequestParam(value = "attachs", required = false) MultipartFile[] attachs) {

        String idPicPath = null;
        String workPicPath = null;
        String errorInfo = null;
        boolean flag = true;
        String path = session.getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
        if (attachs != null) {
            for (int i = 0; i < attachs.length; i++) {
                MultipartFile attach = attachs[i];
                if (!attach.isEmpty()) {
                    if (i == 0) {
                        errorInfo = "uploadFileError";
                    } else {
                        errorInfo = "uploadWpError";
                    }
                    String oldFileName = attach.getOriginalFilename();
                    int fileSize = 5000000;
                    String prefix = FilenameUtils.getExtension(oldFileName);

                    if (attach.getSize() > fileSize) {
                        request.setAttribute(errorInfo, "*上传大小不得超过500kb");
                        return "user/usermodify";
                    } else if ("jpg".equalsIgnoreCase(prefix)
                            || "png".equalsIgnoreCase(prefix)
                            || "jpeg".equalsIgnoreCase(prefix)
                            || "pneg".equalsIgnoreCase(prefix)) {
                        String fileName = System.currentTimeMillis() +
                                RandomUtils.nextInt(1000000) + "_user.jpg";
                        File targetFile = new File(path, fileName);

                        if (!targetFile.exists()) {
                            targetFile.mkdirs();
                        } else {
                            if (user.getIdPicPath() != null && !"".equals(user.getIdPicPath())) {
                                //删除服务器上个人证件照
                                File file = new File(path + user.getIdPicPath());
                                if (file.exists()) {
                                    flag = file.delete();
                                }
                            }
                            if (flag && user.getWorkPicPath() != null && !"".equals(user.getWorkPicPath())) {
                                //删除服务器上个人工作证照片
                                File file = new File(path + user.getWorkPicPath());
                                if (file.exists()) {
                                    flag = file.delete();
                                }
                            }
                        }
                        try {
                            attach.transferTo(targetFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                            request.setAttribute(errorInfo, "上传失败！");
                            return "user/usermodify";
                        }
                        if (i == 0) {
                            idPicPath = fileName;
                        } else {
                            workPicPath = fileName;
                        }
                    } else {
                        request.setAttribute(errorInfo, "*上传图片格式不正确");
                        return "user/usermodify";
                    }
                }
            }
        }
        if (flag) {
            user.setModifyDate(new Date());
            user.setModifyBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
            user.setIdPicPath(idPicPath);
            user.setWorkPicPath(workPicPath);
            if (userService.modify(user)) {
                return "redirect:/sys/user/userlist.html";
            }
        }
        return "user/usermodify";
    }

}
