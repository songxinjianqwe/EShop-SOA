package cn.sinjinsong.eshop.controller.mail;

import cn.sinjinsong.eshop.common.base.exception.RestValidationException;
import cn.sinjinsong.eshop.common.domain.dto.mail.MailDTO;
import cn.sinjinsong.eshop.common.domain.dto.mail.MailsUpdatedDTO;
import cn.sinjinsong.eshop.common.domain.entity.mail.MailDO;
import cn.sinjinsong.eshop.common.enumeration.mail.MailStatus;
import cn.sinjinsong.eshop.common.enumeration.mail.QueryMailTarget;
import cn.sinjinsong.eshop.common.enumeration.mail.SendMode;
import cn.sinjinsong.eshop.exception.mail.MailStatusNotFoundException;
import cn.sinjinsong.eshop.exception.mail.MailTargetNotFoundException;
import cn.sinjinsong.common.web.security.domain.JWTUser;
import cn.sinjinsong.eshop.service.mail.MailService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

/**
 * Created by SinjinSong on 2017/5/5.
 */
@RestController
@RequestMapping("mails")
@Api(value = "mails", description = "站内信模块")
@Slf4j
public class MailController {
    @Autowired
    private MailService mailService;

    /**
     * List必须放在RequestBody中!
     * 如果是只有一个List参数，那么在List上加@RequestBody注解，前端ajax需要传一个json数组对象
     */
    @RequestMapping(value = "/status", method = RequestMethod.PUT)
    @ApiOperation(value = "批量更新站内信的状态")
    public void updateMailsStatus(@RequestBody @Valid MailsUpdatedDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }
        mailService.updateStatus(dto.getIds(), dto.getMailStatus());
    }

    @RequestMapping(value = "/by_target/{targetId}", method = RequestMethod.GET)
    @ApiOperation(value = "按照发信人或收信人的id以及发信状态查询站内信信息", notes = "target是指定按照收信人还是发信人查询，可选值是sender和receiver；如果是按照收信人查询，那么必须指定mail_status，可选值为ALL、NOT_VIEWED、VIEWED；如果是按照发信人查询，则不需要给出该参数", response = PageInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "按收信人查询时未给出mail_status"),
            @ApiResponse(code = 404, message = "未指定target")
    })
    public PageInfo<MailDO> findMails(@PathVariable("targetId") @ApiParam(value = "发信人或收信人的id", required = true) Long targetId,
                                      @RequestParam("target") @ApiParam(value = "按照收信人还是发信人查询，可选值是sender和receiver", required = true) String target,
                                      @RequestParam(value = "pageNum", required = false, defaultValue = "1") @ApiParam(value = "页码，从1开始，默认为1", required = false) Integer pageNum,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "5") @ApiParam(value = "页的大小，默认为5") Integer pageSize,
                                      @RequestParam(value = "mail_status", required = false) @ApiParam(value = "递信状态，如果是按照收信人查询，那么必须指定，可选值为ALL、NOT_VIEWED、VIEWED", required = false) String mailStatus) {
        QueryMailTarget queryTarget = QueryMailTarget.valueOf(StringUtils.upperCase(target));
        if (queryTarget == QueryMailTarget.RECEIVER) {
            if (StringUtils.isEmpty(mailStatus)) {
                throw new MailStatusNotFoundException(targetId);
            }
            return mailService.findByReceiver(targetId, pageNum, pageSize, MailStatus.valueOf(mailStatus.toUpperCase()));
        } else if (queryTarget == QueryMailTarget.SENDER) {
            return mailService.findBySender(targetId, pageNum, pageSize);
        }
        throw new MailTargetNotFoundException(target);
    }

    @RequestMapping(value = "/by_target/{targetId}/size", method = RequestMethod.GET)
    @ApiOperation(value = "按照发信人或收信人的id以及发信状态查询站内信数量", notes = "target是指定按照收信人还是发信人查询，可选值是sender和receiver；如果是按照收信人查询，那么必须指定mail_status，可选值为ALL、NOT_VIEWED、VIEWED；如果是按照发信人查询，则不需要给出该参数", response = PageInfo.class)
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "按收信人查询时未给出mail_status"),
            @ApiResponse(code = 404, message = "未指定target")
    })
    public Integer findMailsSize(@PathVariable("targetId") @ApiParam(value = "发信人或收信人的id", required = true) Long targetId,
                                 @RequestParam("target") @ApiParam(value = "按照收信人还是发信人查询，可选值是sender和receiver", required = true) String target,
                                 @RequestParam(value = "mail_status", required = false) @ApiParam(value = "递信状态，如果是按照收信人查询，那么必须指定，可选值为ALL、NOT_VIEWED、VIEWED", required = false) String mailStatus) {
        QueryMailTarget queryTarget = QueryMailTarget.valueOf(StringUtils.upperCase(target));
        if (queryTarget == QueryMailTarget.RECEIVER) {
            if (StringUtils.isEmpty(mailStatus)) {
                throw new MailStatusNotFoundException(targetId);
            }
            return mailService.findCountByReceiver(targetId, MailStatus.valueOf(mailStatus.toUpperCase()));
        } else if (queryTarget == QueryMailTarget.SENDER) {
            return mailService.findCountBySender(targetId);
        }
        throw new MailTargetNotFoundException(target);
    }


    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #mailDTO.sendMode.toString() != 'BROADCAST' )")
    @ApiOperation(value = "发送站内信，可以单独发送、批量或广播", notes = "如果是单独发送或批量，那么必须指定receivers，并将sendMode置为SINGLE/BATCH.SINGLE只会给第一个receiver发送；如果是广播，那么无需指定receivers，并将SendMode置为BROADCAST")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "mail对象属性校验失败"),
            @ApiResponse(code = 403, message = "广播功能仅支持管理员"),
    })
    public void sendMails(@RequestBody @Valid @ApiParam(value = "站内信对象", required = true) MailDTO mailDTO, BindingResult result, @AuthenticationPrincipal JWTUser user) {
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }
        Long senderId = user.getId();
        log.info("senderId:{}", senderId);
        if (mailDTO.getSendMode() == SendMode.SINGLE) {
            mailService.send(senderId, Arrays.asList(mailDTO.getReceivers().get(0)), mailDTO.getText());
        } else if (mailDTO.getSendMode() == SendMode.BATCH) {
            mailService.send(senderId, mailDTO.getReceivers(), mailDTO.getText());
        } else if (mailDTO.getSendMode() == SendMode.BROADCAST) {
            mailService.broadcast(senderId, mailDTO.getText());
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除某条站内信")
    public void deleteMail(@PathVariable("id") @ApiParam(value = "站内信的id", required = true) Long id) {
        mailService.deleteMail(id);
    }
}
