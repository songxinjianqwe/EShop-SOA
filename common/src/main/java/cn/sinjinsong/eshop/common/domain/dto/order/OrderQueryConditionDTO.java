package cn.sinjinsong.eshop.common.domain.dto.order;

import cn.sinjinsong.eshop.common.enumeration.order.OrderStatus;
import cn.sinjinsong.eshop.common.properties.DateTimeProperties;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by SinjinSong on 2017/10/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderQueryConditionDTO {
    private Long userId;
    private Long categoryId;
    @JsonFormat(pattern = DateTimeProperties.LOCAL_DATE_TIME_PATTERN)
    private LocalDateTime begin;
    @JsonFormat(pattern = DateTimeProperties.LOCAL_DATE_TIME_PATTERN)
    private LocalDateTime end;
    private OrderStatus status;
    private Integer pageNum;
    private Integer pageSize;
}
