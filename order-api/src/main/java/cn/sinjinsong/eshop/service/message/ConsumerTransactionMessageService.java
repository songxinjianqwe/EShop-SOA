package cn.sinjinsong.eshop.service.message;

import cn.sinjinsong.eshop.common.domain.entity.message.ConsumerTransactionMessageDO;
import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;

import java.util.List;
import java.util.Map;

/**
 * @author sinjinsong
 * @date 2017/12/26
 */
public interface ConsumerTransactionMessageService {
    Map<Long,MessageStatus>  findConsumerMessageStatuses(List<Long> ids);
    ConsumerTransactionMessageDO selectByPrimaryKey(Long id);
    void insert(ConsumerTransactionMessageDO record);
    void insertOrUpdate(ConsumerTransactionMessageDO record);
    void insertIfNotExists(ConsumerTransactionMessageDO record);
    void update(ConsumerTransactionMessageDO record);
    boolean isMessageConsumedSuccessfully(Long id);
}
