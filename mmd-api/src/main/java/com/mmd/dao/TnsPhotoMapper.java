package com.mmd.dao;

import com.mmd.domain.dto.massagist.PhotoDTO;
import com.mmd.entity.TnsPhoto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dsccc
 * @since 2020-03-29
 */
public interface TnsPhotoMapper extends BaseMapper<TnsPhoto> {

    List<PhotoDTO> findPhotosOfMassager(String massagerId);
}
