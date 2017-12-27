package cn.sinjinsong.eshop.service.order;

import cn.sinjinsong.eshop.common.domain.entity.order.EnterpriseDO;

/**
 * @author sinjinsong
 * @date 2017/12/23
 */
public interface EnterpriseService {
    EnterpriseDO find();
    int update(EnterpriseDO enterpriseDO);
}
