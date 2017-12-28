package cn.sinjinsong.eshop.common.domain.dto.message;

import cn.sinjinsong.eshop.common.enumeration.message.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author sinjinsong
 * @date 2017/12/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageQueryConditionDTO implements Serializable{
    private MessageStatus status;
    private Long id;
    private String topic;
    private Integer pageNum;
    private Integer pageSize;
}
