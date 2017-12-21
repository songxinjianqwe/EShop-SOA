package cn.sinjinsong.eshop.controller.news;

import cn.sinjinsong.eshop.common.base.exception.RestValidationException;
import cn.sinjinsong.eshop.common.domain.entity.news.NewsDO;
import cn.sinjinsong.eshop.common.properties.PageProperties;
import cn.sinjinsong.eshop.service.news.NewsService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by SinjinSong on 2017/10/6.
 */
@RestController
@RequestMapping("/news")
@Api(value = "news", description = "新闻API")
@Slf4j
public class NewsController {

    @Autowired
    private NewsService newsService;
    
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "获取所有新闻", response = PageInfo.class)
    public PageInfo<NewsDO> findAllNews(@RequestParam(value = "pageNum", required = false, defaultValue = PageProperties.DEFAULT_PAGE_NUM) @ApiParam(value = "页码，从1开始", defaultValue = PageProperties.DEFAULT_PAGE_NUM) Integer pageNum, @RequestParam(value = "pageSize", required = false, defaultValue = PageProperties.DEFAULT_PAGE_SIZE) @ApiParam(value = "每页记录数", defaultValue = PageProperties.DEFAULT_PAGE_SIZE) Integer pageSize) {
        return newsService.findAllNews(pageNum,pageSize);
    }

    @RequestMapping(value = "/query/latest", method = RequestMethod.GET)
    @ApiOperation(value = "获取最新的指定条新闻", response = NewsDO.class)
    public List<NewsDO> findLatestNews(@RequestParam(value = "count", required = false, defaultValue = "5")
                                       @ApiParam(value = "最新的count条新闻，默认值为5") Integer count) {
        return newsService.findLatestNews(count);
    }
    
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "添加一条新闻")
    @PreAuthorize("hasRole('ADMIN')")
    public void saveNews(@RequestBody @Valid @ApiParam(value = "新闻对象", required = true) NewsDO news, BindingResult result) {
        log.info("{}", news);
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }
        news.setTime(LocalDateTime.now());
        newsService.save(news);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ApiOperation(value = "修改一条新闻")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateNews(@RequestBody @Valid @ApiParam(value = "新闻对象", required = true) NewsDO news, BindingResult result) {
        log.info("{}", news);
        if (result.hasErrors()) {
            throw new RestValidationException(result.getFieldErrors());
        }
        news.setTime(LocalDateTime.now());
        newsService.update(news);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "按id查询新闻", response = NewsDO.class)
    public NewsDO findNewsById(@PathVariable("id") @ApiParam("新闻id") Long id) {
        return newsService.findById(id);
    }

}
