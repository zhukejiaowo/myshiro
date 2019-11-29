package com.coderman.rbac.workflow.controller;

import com.coderman.rbac.sys.enums.ResultEnum;
import com.coderman.rbac.sys.vo.LoginLogVo;
import com.coderman.rbac.sys.vo.PageVo;
import com.coderman.rbac.sys.vo.ResultVo;
import com.coderman.rbac.workflow.service.WorkFlowService;
import com.coderman.rbac.workflow.vo.DeploymentEntityVo;
import com.coderman.rbac.workflow.vo.WorkFlowVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * 流程管理模块前端控制器
 * Created by zhangyukang on 2019/11/28 16:12
 */
@Slf4j
@Controller
@RequestMapping("/workFlow")
public class WorkFlowController {

    @Autowired
    private WorkFlowService workFlowService;

    /**
     * 跳转到流程部署管理
     * @return
     */
    @RequestMapping(value = "/workFlow",method = RequestMethod.GET)
    public String workDeploy(){
        return "workflow/work/workFlow";
    }


    @RequestMapping(value = "/workFlowAdd",method = RequestMethod.GET)
    public String workFlowAdd(){return "workflow/work/workFlowAdd";}

    @RequestMapping(value = "/processImage",method = RequestMethod.GET)
    public String workFlowImage(String id,Map<String,Object> map){
        map.put("deployId",id);
        return "workflow/work/processImage";
    }

    /**
     * 查询所有流程部署信息
     * @param workFlowVo
     * @return
     */
    @ResponseBody
    @RequestMapping("/listAllProcessDeploy")
    public PageVo<DeploymentEntityVo> listAllProcessDeploy(WorkFlowVo workFlowVo){
        PageVo pageVo = workFlowService.listAllProcessDeploy(workFlowVo);
        return pageVo;
    }

    /**
     *查询所有流程定义信息
     * @param workFlowVo
     * @return
     */
    @ResponseBody
    @RequestMapping("/listAllProcessDefine")
    public PageVo listAllProcessDefine(WorkFlowVo workFlowVo){
        PageVo pageVo = workFlowService.listAllProcessDefine(workFlowVo);
        return pageVo;
    }

    /**
     * 流程部署
     * @param file
     * @param deploymentEntityVo
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/deployProcess")
    public ResultVo deployProcess(MultipartFile file,DeploymentEntityVo deploymentEntityVo) throws IOException {
        InputStream inputStream = file.getInputStream();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        try {
            workFlowService.deployProcess(zipInputStream,deploymentEntityVo.getName());
            return ResultVo.OK(ResultEnum.DEPLOY_PROCESS_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.ERROR(ResultEnum.DEPLOY_PROCESS_FAIL);
        }finally {
            zipInputStream.close();
            inputStream.close();
        }
    }

    /**
     * 删除流程
     * @param workFlowVo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/delete")
    public ResultVo delete(WorkFlowVo workFlowVo){
        try {
            String id = workFlowVo.getId();
            if(id!=null&&!"".equals(id))
            workFlowService.delete(workFlowVo);
            return ResultVo.OK(ResultEnum.DELETE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("【删除流程失败】=【流程正在运行中】");
            return ResultVo.ERROR(ResultEnum.DELETE_FAIL);
        }
    }

    /**
     * 批量删除登入日志
     * @param workFlowVo
     * @return
     */
    @ResponseBody
    @GetMapping("/batchDelete")
    public ResultVo batchDelete(WorkFlowVo workFlowVo){
        try {
            workFlowService.batchDelete(workFlowVo);
            return ResultVo.OK(ResultEnum.DELETE_SUCCESS);
        } catch (Exception e) {
            log.info("【删除流程失败】=【流程正在运行中】");
            return ResultVo.ERROR(ResultEnum.DELETE_FAIL);
        }
    }

    /**
     * 流程图
     * @param workFlowVo
     * @param response
     */
    @GetMapping("/workFlowImage")
    public void workFlowImage(WorkFlowVo  workFlowVo, HttpServletResponse response){
        try {
            InputStream inputStream=workFlowService.workFlowImage(workFlowVo);
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedImage img=ImageIO.read(inputStream);
            ImageIO.write(img,"PNG",outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
