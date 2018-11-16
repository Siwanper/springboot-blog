package com.swp.springboot.controller.admin;

import com.swp.springboot.constant.WebConst;
import com.swp.springboot.controller.AbstractController;
import com.swp.springboot.controller.helper.ExceptionHelper;
import com.swp.springboot.dto.LogActions;
import com.swp.springboot.dto.Types;
import com.swp.springboot.exception.TipException;
import com.swp.springboot.modal.bo.RestResponseBo;
import com.swp.springboot.modal.vo.OptionVo;
import com.swp.springboot.service.ILogService;
import com.swp.springboot.service.IOptionService;
import com.swp.springboot.util.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 设置控制器
 *
 * @version 1.0.0
 * @outhor ios
 * @create 2018-11-09 4:19 PM
 */
@Controller
@RequestMapping("admin/setting")
public class SettingController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(SettingController.class);

    @Resource
    private IOptionService optionService;

    @Resource
    private ILogService logService;

    /**
     * 设置页面
     * @param request
     * @return
     */
    @GetMapping("")
    public String index(HttpServletRequest request){
        List<OptionVo> optionVos = optionService.getOptions();
        Map<String, String> options = new HashMap<>();
        optionVos.forEach((optionVo) -> {
            options.put(optionVo.getName(), optionVo.getValue());
        });
        if (options.get("site_title") == null) {
            options.put("site_title", "");
        }
        if (options.get("site_description") == null) {
            options.put("site_description", "");
        }
        if (options.get("site_record") == null) {
            options.put("site_record", "");
        }
        if (options.get("site_theme") == null) {
            options.put("site_theme", "default");
        }
        if (options.get("site_block_ips") == null) {
            options.put("site_block_ips", "");
        }
        request.setAttribute("options", options);
        return "admin/setting";
    }

    /**
     * 保存设置
     * @param request
     * @param site_theme
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo save(HttpServletRequest request, @RequestParam(required = false) String site_theme){
        try {
            Map<String, String[]> parameter = request.getParameterMap();
            Map<String, String> query = new HashMap<>();
            parameter.forEach((key, value) -> {
                query.put(key, join(value));
            });
            optionService.saveOption(query);

            WebConst.initConfig = query;
            if (StringUtils.isNotBlank(site_theme)) {
                if (site_theme.equals("default")) {
                    AbstractController.THEME = "themes/siwanper";
                } else {
                    AbstractController.THEME = "themes/" + site_theme;
                }
            }
            logService.insertLog(LogActions.SYS_SETTING.getAction(), GsonUtils.toJson(query) , this.getUid(request), request.getRemoteAddr());
            return RestResponseBo.ok();
        } catch (Exception e) {
            return ExceptionHelper.handlerException(logger, "保存失败" ,e);
        }

    }

    /**
     * 保存高级设置
     * @param cache_key
     * @param block_ips
     * @return
     */
    @PostMapping("/advanced")
    @ResponseBody
    @Transactional(rollbackFor = TipException.class)
    public RestResponseBo saveAdvanced(@RequestParam String cache_key, @RequestParam String block_ips) {
        if (StringUtils.isNotBlank(cache_key)) {
            if (cache_key.equals("*")) {
                cache.clean();
            } else {
                cache.del(cache_key);
            }
        }
        if (StringUtils.isNotBlank(block_ips)) {
            optionService.insertOption(Types.BLOCK_IPS.getType(), block_ips);
            WebConst.BLOCK_IPS.addAll(Arrays.asList(block_ips.split(",")));
        } else {
            optionService.insertOption(Types.BLOCK_IPS.getType(), "");
            WebConst.BLOCK_IPS.clear();
        }

        return RestResponseBo.ok();
    }


    /**
     * 数组转字符串
     *
     * @param arr
     * @return
     */
    private String join(String[] arr) {
        StringBuilder ret = new StringBuilder();
        String[] var3 = arr;
        int var4 = arr.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            String item = var3[var5];
            ret.append(',').append(item);
        }
        return ret.length() > 0 ? ret.substring(1) : ret.toString();
    }
}
