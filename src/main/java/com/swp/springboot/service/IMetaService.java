package com.swp.springboot.service;

import com.swp.springboot.dto.MetaDto;
import com.swp.springboot.modal.vo.MetaVo;

import java.util.List;

public interface IMetaService {

    /**
     * 保存多条分类和标签
     *
     * @param type
     * @param names
     * @param cid
     */
    void saveMetas(String type, String names, Integer cid);

    /**
     *  保存分类和标签
     *
     * @param type
     * @param name
     * @param mid
     */
    void saveMeta(String type, String name, Integer mid);

    /**
     * 保存分类和标签
     *
     * @param metaVo
     */
    void saveMeta(MetaVo metaVo);

    /**
     * 更新分类和标签
     * @param metaVo
     */
    void updateMeta(MetaVo metaVo);

    /**
     * 获取分类和标签列表，包含文章个数
     *
     * @param type 分类或者标签
     * @param order 排序
     * @param limit
     * @return
     */
    List<MetaDto> getMetaList(String type, String order, int limit);

    /**
     * 根据类型获取分类和标签
     *
     * @param type
     * @return
     */
    List<MetaVo> getMetaByType(String type);

    /**
     * 根据类型和名字查询项
     *
     * @param type
     * @param name
     * @return
     */
    MetaDto getMeta(String type, String name);

    /**
     * 删除分类或标签
     *
     * @param mid
     */
    void delete(Integer mid);

}
