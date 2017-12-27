package cn.sinjinsong.eshop.service.message;

import cn.sinjinsong.eshop.common.domain.entity.message.ProducerTransactionMessageDO;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2017/12/26
 */
public interface ProducerTransactionMessageService {
    void save(ProducerTransactionMessageDO message);
    void check();
    void reSend(List<ProducerTransactionMessageDO> messages) ;
    void delete(Long id);
}
