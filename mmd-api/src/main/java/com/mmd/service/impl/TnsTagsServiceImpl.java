package com.mmd.service.impl;

import com.mmd.domain.dto.comment.Tag;
import com.mmd.domain.dto.massagist.MassagistDTO;
import com.mmd.entity.TnsTags;
import com.mmd.dao.TnsTagsMapper;
import com.mmd.service.TnsTagsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TnsTagsServiceImpl extends ServiceImpl<TnsTagsMapper, TnsTags> implements TnsTagsService {
    private final TnsTagsMapper tnsTagsMapper;

    @Override
    public List<MassagistDTO> massagistdtolist(String massagerId, String type) {
        if (type == null) {
            return tnsTagsMapper.findTagsAndCount(massagerId);
        }
        return null;
    }

    @Override
    public List<MassagistDTO> findTagsOfMassager(String tid) {
        List<MassagistDTO> result = tnsTagsMapper.findTagsGrouped(tid);
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                result.get(i).setCount(tnsTagsMapper.findTagCount(tid, result.get(i).getTag()));
            }
        }
        return result;
    }

    @Override
    public int addTag(Tag tag) {
        return tnsTagsMapper.addTag(tag);
    }
}
