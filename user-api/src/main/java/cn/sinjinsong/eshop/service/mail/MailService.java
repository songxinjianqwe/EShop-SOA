package cn.sinjinsong.eshop.service.mail;


import cn.sinjinsong.eshop.common.domain.entity.mail.MailDO;
import cn.sinjinsong.eshop.common.enumeration.mail.MailStatus;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by SinjinSong on 2017/5/4.
 */
public interface MailService {
    PageInfo<MailDO> findByReceiver(Long receiver, int pageNum, int pageSize, MailStatus mailStatus);

    PageInfo<MailDO> findBySender(Long sender, int pageNum, int pageSize);

    Integer findCountByReceiver(Long receiver, MailStatus mailStatus);

    Integer findCountBySender(Long sender);

    void send(Long sender, List<Long> receivers, String text);

    void broadcast(Long sender, String text);

    void deleteMail(Long id);
    void updateStatus(List<Long> ids, MailStatus mailStatus);
}
