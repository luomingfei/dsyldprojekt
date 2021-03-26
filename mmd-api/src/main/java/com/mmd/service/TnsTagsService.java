package com.mmd.service;

import com.mmd.domain.dto.comment.Tag;
import com.mmd.domain.dto.massagist.MassagistDTO;
import com.mmd.entity.TnsTags;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TnsTagsService extends IService<TnsTags> {
    List<MassagistDTO> massagistdtolist(String massagerId, String type);

    List<MassagistDTO> findTagsOfMassager(String massagerId);

    int addTag(Tag tag);
}
