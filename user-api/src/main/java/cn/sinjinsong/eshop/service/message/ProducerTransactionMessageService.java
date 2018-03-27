package cn.sinjinsong.eshop.service.message;

import cn.sinjinsong.eshop.common.domain.dto.message.MessageQueryConditionDTO;
import cn.sinjinsong.eshop.common.domain.entity.message.ProducerTransactionMessageDO;
import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author sinjinsong
 * @date 2017/12/26
 */
public interface ProducerTransactionMessageService {
    void save(ProducerTransactionMessageDO message);

    void check();

    void reSend(List<ProducerTransactionMessageDO> messages);

    void delete(Long id);

    List<ProducerTransactionMessageDO> findByIds(List<Long> ids);

    PageInfo<ProducerTransactionMessageDO> findByQueryDTO(MessageQueryConditionDTO dto);

    void update(ProducerTransactionMessageDO message);

    void updateStatusAndReSend(Map<Long, MessageStatus> statuses);
}
