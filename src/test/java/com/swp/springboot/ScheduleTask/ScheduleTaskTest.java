package com.swp.springboot.ScheduleTask;

import com.swp.springboot.scheduetask.ScheduleTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * DESCRIPTION：   ${DESCRIPTION}
 *
 * @ProjectName: springboot-blog
 * @Package: com.swp.springboot.ScheduleTask
 * @Author: Siwanper
 * @CreateDate: 2018/11/9 下午11:07
 * @Version: 1.0
 * <p>Copyright: Copyright (c) 2018</p>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleTaskTest {

    @Resource
    private ScheduleTask scheduleTask;

    @Test
    public void testSendString(){
        scheduleTask.process();
    }

}
