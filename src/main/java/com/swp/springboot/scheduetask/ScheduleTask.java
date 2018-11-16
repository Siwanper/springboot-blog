package com.swp.springboot.scheduetask;

import com.sun.management.OperatingSystemMXBean;
import com.swp.springboot.modal.vo.LogVo;
import com.swp.springboot.service.ILogService;
import com.swp.springboot.service.IMailService;
import com.swp.springboot.util.DateKit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * DESCRIPTION：   ${DESCRIPTION}
 *
 * @ProjectName: springboot-blog
 * @Package: com.swp.springboot.scheduetask
 * @Author: Siwanper
 * @CreateDate: 2018/11/9 下午10:55
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018</p>
 */
@Component
public class ScheduleTask {

    @Resource
    private IMailService mailService;

    @Resource
    private ILogService logService;

    @Value("${spring.mail.username}")
    private String mailTo;

    @Scheduled(fixedRate = 86400000)
    public void process(){

        StringBuffer result = new StringBuffer();
        long totalMemory = Runtime.getRuntime().totalMemory();
        result.append("使用的总内存为："+totalMemory/(1024*1024)+"MB").append("\n");
        result.append("内存使用率为："+getMemery()).append("\n");
        List<LogVo> logVoList = logService.getLogs(0,5);
        for (LogVo logVo:logVoList){
            result.append(" 时间: ").append(DateKit.formatDateByUnixTime(logVo.getCreated()));
            result.append(" 操作: ").append(logVo.getAction());
            result.append(" IP： ").append(logVo.getIp()).append("\n");
        }
        mailService.sendSimpleEmail(mailTo,"博客系统运行情况",result.toString());
    }

    public static String getMemery(){
        OperatingSystemMXBean osmxb = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
        long totalvirtualMemory = osmxb.getTotalPhysicalMemorySize(); // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        Double compare = (Double) (1 - freePhysicalMemorySize * 1.0 / totalvirtualMemory) * 100;
        String str = compare.intValue() + "%";
        return str;
    }

}
