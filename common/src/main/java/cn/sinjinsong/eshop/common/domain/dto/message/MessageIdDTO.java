package cn.sinjinsong.eshop.common.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author sinjinsong
 * @date 2017/12/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageIdDTO {
    private List<Long> ids;
}
