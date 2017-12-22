package cn.sinjinsong.eshop.common.domain.dto.mail;


import cn.sinjinsong.eshop.common.enumeration.mail.MailStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Created by SinjinSong on 2017/10/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailsUpdatedDTO implements Serializable{
    @NotNull
    private List<Long> ids;
    @NotNull
    private MailStatus mailStatus;
}
