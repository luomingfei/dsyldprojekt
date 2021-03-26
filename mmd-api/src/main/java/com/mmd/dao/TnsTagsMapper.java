package com.mmd.dao;

import com.mmd.domain.dto.comment.Tag;
import com.mmd.domain.dto.massagist.MassagistDTO;
import com.mmd.entity.TnsTags;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TnsTagsMapper extends BaseMapper<TnsTags> {
    public List<MassagistDTO> findTagsAndCount(@Param("tid") String tid);

    public List<MassagistDTO> findTagsGrouped(@Param("tid") String tid);

    public int findTagCount(@Param("tid") String tid, @Param("tag") String tag);

    int addTag(@Param("t") Tag tag);
}
