package cn.smbms.controller;

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author jie
 * @date 2019/2/16 -8:27
 */
@Controller
@RequestMapping("/sys/provider")
public class ProviderController {

    private Logger logger = Logger.getLogger(ProviderController.class);

    @Autowired
    private ProviderService providerService;

    @Autowired
    private BillService billService;

    @RequestMapping("/providerList.html")
    public String getProviderList(Model model,
                                  @RequestParam(value="queryProCode",required=false) String queryProCode,
                                  @RequestParam(value="queryProName",required=false) String queryProName,
                                  @RequestParam(value="pageIndex",required=false) String pageIndex){
        logger.info("getProviderList ---- > queryProCode: " + queryProCode);
        logger.info("getProviderList ---- > queryProName: " + queryProName);
        logger.info("getProviderList ---- > pageIndex: " + pageIndex);
        List<Provider> providerList = null;
        //设置页面容量
        int pageSize = Constants.pageSize;
        //当前页码
        int currentPageNo = 1;

        if(queryProCode == null){
            queryProCode = "";
        }
        if(queryProName == null){
            queryProName = "";
        }
        if(pageIndex != null){
            try{
                currentPageNo = Integer.valueOf(pageIndex);
            }catch(NumberFormatException e){
                return "redirect:/sys/provider/syserror.html";
            }
        }
        //总数量（表）
        int totalCount = 0;
        try {
            totalCount = providerService.getProviderCount(queryProCode,queryProName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //总页数
        PageSupport pages=new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if(currentPageNo < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }
        try {
            providerList = providerService.getProviderList(queryProName,queryProCode,currentPageNo, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("providerList", providerList);
        model.addAttribute("queryProCode", queryProCode);
        model.addAttribute("queryProName", queryProName);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPageNo", currentPageNo);
        return "provider/providerlist";
    }


    @RequestMapping(value = "/syserror.html")
    public String sysError() {
        return "syserror";
    }


    @RequestMapping(value = "/add.html", method = RequestMethod.GET)
    public String add(@ModelAttribute("provider") Provider provider) {
        return "provider/provideradd";
    }

    @RequestMapping(value = "/add.html", method = RequestMethod.POST)
    public String addSave(@Valid Provider provider, BindingResult bindingResult, HttpSession session,
                          HttpServletRequest request,
                          @RequestParam(value = "attachs", required = false) MultipartFile[] attachs) {

        if (bindingResult.hasErrors()) {
            logger.debug("add provider validated has error=============================");
            return "provider/provideradd";
        }

        String companyLicPicPath = null;
        String orgCodePicPath = null;
        String errorInfo = null;
        boolean flag = true;
        String path = session.getServletContext().getRealPath("statics" + File.separator + "uploadfiles");

        for (int i = 0; i < attachs.length; i++) {
            MultipartFile attach = attachs[i];
            if (!attach.isEmpty()) {
                if (i == 0) {
                    errorInfo = "uploadFileError";
                }
                if (i == 1) {
                    errorInfo = "uploadOcError";
                }
                String oldFileName = attach.getOriginalFilename();
                int fileSize = 500000;
                String prefix = FilenameUtils.getExtension(oldFileName);

                if (attach.getSize() > fileSize) {
                    request.setAttribute(errorInfo, "*上传大小不得超过500kb");
                    return "provider/provideradd";
                } else if ("jpg".equalsIgnoreCase(prefix)
                        || "png".equalsIgnoreCase(prefix)
                        || "jpeg".equalsIgnoreCase(prefix)
                        || "pneg".equalsIgnoreCase(prefix)) {
                    String fileName = System.currentTimeMillis() +
                            RandomUtils.nextInt(1000000) + "_company.jpg";
                    File targetFile = new File(path, fileName);
                    if (!targetFile.exists()) {
                        targetFile.mkdirs();
                    }
                    try {
                        attach.transferTo(targetFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        request.setAttribute(errorInfo, "上传失败！");
                        return "provider/provideradd";
                    }
                    if (i == 0) {
                        companyLicPicPath = fileName;
                    } else if (i == 1) {
                        orgCodePicPath = fileName;
                    }

                } else {
                    request.setAttribute(errorInfo, "*上传图片格式不正确");
                    return "provider/provideradd";
                }
            }
        }
        if (flag){
            provider.setOrgCodePicPath(orgCodePicPath);
            provider.setCompanyLicPicPath(companyLicPicPath);
            provider.setCreatedBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
            provider.setCreationDate(new Date());
            if (providerService.add(provider)) {
                return "redirect:/sys/provider/providerList.html";
            }
        }
        return "provider/provideradd";
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable int id, Model model) {
        logger.debug("view id===============" + id);
        Provider provider = providerService.getProviderById(id);
        model.addAttribute(provider);
        return "provider/providerview";
    }

    @RequestMapping(value = "/modify/{id}", method = RequestMethod.GET)
    public String modify(@PathVariable int id, Model model) {
        logger.debug("view id===============" + id);
        Provider provider = providerService.getProviderById(id);
        model.addAttribute(provider);
        return "provider/providermodify";
    }

    @RequestMapping(value = "/providermodifysava.html", method = RequestMethod.POST)
    public String modifyUserSave(Provider provider) {
        logger.debug("modifyUserSave userid===============" + provider.getId());
        provider.setModifyDate(new Date());
        if (providerService.modify(provider)) {
            return "redirect:/sys/provider/providerList.html";
        }
        return "provider/providermodify";
    }

    @RequestMapping(value = "/delprovider",method = RequestMethod.POST)
    @ResponseBody
    public Object delprovider(int id){
        HashMap<String,String> resultMap = new HashMap<>();
        if(!StringUtils.isNullOrEmpty(String.valueOf(id))){

            int billCountByProviderId = billService.getBillCountByProviderId(id);
            if (billCountByProviderId != 0){
                resultMap.put("delResult", String.valueOf(billCountByProviderId));
            }else{
                if (providerService.smbmsDeleteProviderById(id)){
                    resultMap.put("delResult","true");
                }else{
                    resultMap.put("delResult","false");
                }
            }
        }else{
            resultMap.put("delResult","notexist");
        }
        return resultMap;
    }

}
