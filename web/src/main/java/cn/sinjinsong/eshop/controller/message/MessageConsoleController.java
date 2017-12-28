package cn.sinjinsong.eshop.controller.message;

import cn.sinjinsong.eshop.common.domain.dto.message.MessageIdDTO;
import cn.sinjinsong.eshop.common.domain.dto.message.MessageQueryConditionDTO;
import cn.sinjinsong.eshop.common.domain.entity.message.ProducerTransactionMessageDO;
import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;
import cn.sinjinsong.eshop.common.properties.PageProperties;
import cn.sinjinsong.eshop.service.message.ProducerTransactionMessageService;
import cn.sinjinsong.eshop.service.pay.AccountService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2017/12/28
 */
@RestController
@RequestMapping("/message_console")
public class MessageConsoleController {
    @Autowired
    private ProducerTransactionMessageService messageService;
    @Autowired
    private AccountService accountService;
    
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public PageInfo<ProducerTransactionMessageDO> findByQueryDTO(@RequestBody MessageQueryConditionDTO queryDTO) {
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(Integer.valueOf(PageProperties.DEFAULT_PAGE_NUM));
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(Integer.valueOf(PageProperties.DEFAULT_PAGE_SIZE));
        }
        return messageService.findByQueryDTO(queryDTO);
    }
    
    @RequestMapping(value = "/reSend", method = RequestMethod.POST)
    public void reSend(@RequestBody MessageIdDTO dto) {
        List<ProducerTransactionMessageDO> messages = messageService.findByIds(dto.getIds());
        for (ProducerTransactionMessageDO messageDO : messages) {
            messageDO.setMessageStatus(MessageStatus.UNCONSUMED);
            messageDO.setSendTimes(0);
        }
        messageService.reSend(messages);
    }
    
    @RequestMapping(value = "/rollback", method = RequestMethod.POST)
    public void rollback(@RequestBody MessageIdDTO dto) {
        for (ProducerTransactionMessageDO message : messageService.findByIds(dto.getIds())) {
            accountService.rollback(message);
        }
    }
}
