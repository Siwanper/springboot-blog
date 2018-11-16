package com.swp.springboot.dto;

import com.swp.springboot.modal.vo.MetaVo;

/**
 * 描述:
 *
 * @outhor ios
 * @create 2018-10-31 3:06 PM
 */
public class MetaDto extends MetaVo {

    /**
     * 类别下的文章个数
     */
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
